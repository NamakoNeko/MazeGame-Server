package com.javaclass.game.service;

import com.javaclass.game.constants.PlayerItemDefiner;
import com.javaclass.game.dao.ItemDao;
import com.javaclass.game.dao.PlayerDao;
import com.javaclass.game.dao.PlayerItemDao;
import com.javaclass.game.dto.ConsumePlayerItemRequest;
import com.javaclass.game.dto.ConsumePlayerItemResponse;
import com.javaclass.game.dto.GainPlayerItemRequest;
import com.javaclass.game.dto.GainPlayerItemResponse;
import com.javaclass.game.dto.MovePlayerItemRequest;
import com.javaclass.game.dto.MovePlayerItemResponse;
import com.javaclass.game.dto.PlayerItemResult;
import com.javaclass.game.model.Item;
import com.javaclass.game.model.PlayerItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerItemService {

    private final PlayerItemDao playerItemDao;
    private final ItemDao itemDao;
    private final PlayerDao playerDao;

    public PlayerItemService(
        PlayerItemDao playerItemDao,
        ItemDao itemDao,
        PlayerDao playerDao
    ) {
        this.playerItemDao = playerItemDao;
        this.itemDao = itemDao;
        this.playerDao = playerDao;
    }

    public List<PlayerItemResult> getPlayerItemsByAccountId(String accountId) {
        boolean isPlayerExists = playerDao.findByAccountId(accountId).isPresent();
        if (!isPlayerExists) {
            throw new IllegalArgumentException(PlayerItemDefiner.ERROR_PLAYER_NOT_FOUND);
        }

        return playerItemDao.findByAccountId(accountId).stream()
            .filter(record -> record.getAmount() > 0)
            .map((PlayerItem record) -> {
                Item item = itemDao.findById(record.getItemId()).orElse(null);

                return PlayerItemResult.builder()
                    .playerItemId(record.getId())
                    .itemId(record.getItemId())
                    .name(item != null ? item.getName() : "")
                    .description(item != null ? item.getDescription() : "")
                    .effect(item != null ? item.getEffect() : "")
                    .rare(item != null ? item.getRare() : "")
                    .type(item != null ? item.getType() : null)
                    .maxAmount(item != null ? item.getMaxAmount() : null)
                    .location(record.getLocation())
                    .position(record.getPosition())
                    .amount(record.getAmount())
                    .build();
            })
            .toList();
    }

    @Transactional
    public GainPlayerItemResponse gainItems(String accountId, GainPlayerItemRequest gainPlayerItemRequest) {
        boolean isPlayerExists = playerDao.findByAccountId(accountId).isPresent();
        if (!isPlayerExists) {
            throw new IllegalArgumentException(PlayerItemDefiner.ERROR_PLAYER_NOT_FOUND);
        }

        List<GainPlayerItemRequest.GainPlayerItemEntry> entryList = gainPlayerItemRequest.getItems();

        // ── 第一階段：全部驗證 ──
        for (GainPlayerItemRequest.GainPlayerItemEntry entry : entryList) {
            Item item = itemDao.findById(entry.getItemId())
                .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_FOUND));

            Optional<PlayerItem> existingPlayerItem = playerItemDao
                .findByAccountIdAndLocationAndPosition(accountId, entry.getLocation(), entry.getPosition());

            if (existingPlayerItem.isPresent() && existingPlayerItem.get().getAmount() > 0) {
                int newAmount = existingPlayerItem.get().getAmount() + entry.getAmount();
                boolean isExceedMaxAmount = newAmount > item.getMaxAmount();
                if (isExceedMaxAmount) {
                    throw new IllegalArgumentException(PlayerItemDefiner.ERROR_MAX_AMOUNT_EXCEEDED);
                }
            }
        }

        // ── 第二階段：全部寫入 ──
        List<GainPlayerItemResponse.PlayerItemEntry> resultList = new ArrayList<>();

        for (GainPlayerItemRequest.GainPlayerItemEntry entry : entryList) {
            Optional<PlayerItem> existingPlayerItem = playerItemDao
                .findByAccountIdAndLocationAndPosition(accountId, entry.getLocation(), entry.getPosition());

            if (existingPlayerItem.isPresent()) {
                PlayerItem playerItem = existingPlayerItem.get();

                boolean isEmptySlot = playerItem.getAmount() == 0;
                if (isEmptySlot) {
                    playerItem.setItemId(entry.getItemId());
                    playerItem.setAmount(entry.getAmount());
                } else {
                    playerItem.setAmount(playerItem.getAmount() + entry.getAmount());
                }
                playerItemDao.save(playerItem);

                resultList.add(GainPlayerItemResponse.PlayerItemEntry.builder()
                    .itemId(playerItem.getItemId())
                    .amount(playerItem.getAmount())
                    .location(playerItem.getLocation())
                    .position(playerItem.getPosition())
                    .build());
            } else {
                PlayerItem newPlayerItem = new PlayerItem();
                newPlayerItem.setAccountId(accountId);
                newPlayerItem.setItemId(entry.getItemId());
                newPlayerItem.setLocation(entry.getLocation());
                newPlayerItem.setPosition(entry.getPosition());
                newPlayerItem.setAmount(entry.getAmount());
                playerItemDao.save(newPlayerItem);

                resultList.add(GainPlayerItemResponse.PlayerItemEntry.builder()
                    .itemId(entry.getItemId())
                    .amount(entry.getAmount())
                    .location(entry.getLocation())
                    .position(entry.getPosition())
                    .build());
            }
        }

        return GainPlayerItemResponse.builder().items(resultList).build();
    }

    @Transactional
    public ConsumePlayerItemResponse consumeItems(String accountId, ConsumePlayerItemRequest consumePlayerItemRequest) {
        boolean isPlayerExists = playerDao.findByAccountId(accountId).isPresent();
        if (!isPlayerExists) {
            throw new IllegalArgumentException(PlayerItemDefiner.ERROR_PLAYER_NOT_FOUND);
        }

        List<ConsumePlayerItemRequest.ConsumePlayerItemEntry> entryList = consumePlayerItemRequest.getItems();

        // ── 第一階段：全部驗證 ──
        for (ConsumePlayerItemRequest.ConsumePlayerItemEntry entry : entryList) {
            PlayerItem playerItem = playerItemDao
                .findByAccountIdAndLocationAndPosition(accountId, entry.getLocation(), entry.getPosition())
                .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_FOUND));

            boolean isAmountEnough = playerItem.getAmount() >= entry.getAmount();
            if (!isAmountEnough) {
                throw new IllegalArgumentException(PlayerItemDefiner.ERROR_AMOUNT_NOT_ENOUGH);
            }
        }

        // ── 第二階段：全部寫入 ──
        List<ConsumePlayerItemResponse.PlayerItemEntry> resultList = new ArrayList<>();

        for (ConsumePlayerItemRequest.ConsumePlayerItemEntry entry : entryList) {
            PlayerItem playerItem = playerItemDao
                .findByAccountIdAndLocationAndPosition(accountId, entry.getLocation(), entry.getPosition())
                .orElseThrow();

            playerItem.setAmount(playerItem.getAmount() - entry.getAmount());
            if (playerItem.getAmount() == 0) {
                playerItemDao.delete(playerItem);
            } else {
                playerItemDao.save(playerItem);
            }

            resultList.add(ConsumePlayerItemResponse.PlayerItemEntry.builder()
                .itemId(playerItem.getItemId())
                .amount(playerItem.getAmount())
                .location(playerItem.getLocation())
                .position(playerItem.getPosition())
                .build());
        }

        return ConsumePlayerItemResponse.builder().items(resultList).build();
    }

    @Transactional
    public MovePlayerItemResponse moveItem(String accountId, MovePlayerItemRequest movePlayerItemRequest) {
        boolean isPlayerExists = playerDao.findByAccountId(accountId).isPresent();
        if (!isPlayerExists) {
            throw new IllegalArgumentException(PlayerItemDefiner.ERROR_PLAYER_NOT_FOUND);
        }

        PlayerItem sourcePlayerItem = playerItemDao
            .findByAccountIdAndLocationAndPosition(
                accountId,
                movePlayerItemRequest.getBeforeLocation(),
                movePlayerItemRequest.getBeforePosition()
            )
            .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_FOUND));

        boolean isAmountEnough = sourcePlayerItem.getAmount() >= movePlayerItemRequest.getAmount();
        if (!isAmountEnough) {
            throw new IllegalArgumentException(PlayerItemDefiner.ERROR_AMOUNT_NOT_ENOUGH);
        }

        Optional<PlayerItem> targetPlayerItem = playerItemDao
            .findByAccountIdAndLocationAndPosition(
                accountId,
                movePlayerItemRequest.getAfterLocation(),
                movePlayerItemRequest.getAfterPosition()
            );

        if (targetPlayerItem.isPresent()) {
            PlayerItem target = targetPlayerItem.get();

            boolean isEmptySlot = target.getAmount() == 0;
            if (isEmptySlot) {
                target.setItemId(sourcePlayerItem.getItemId());
                target.setAmount(movePlayerItemRequest.getAmount());
            } else {
                Item item = itemDao.findById(sourcePlayerItem.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_FOUND));

                int newAmount = target.getAmount() + movePlayerItemRequest.getAmount();
                boolean isExceedMaxAmount = newAmount > item.getMaxAmount();
                if (isExceedMaxAmount) {
                    throw new IllegalArgumentException(PlayerItemDefiner.ERROR_MAX_AMOUNT_EXCEEDED);
                }

                target.setAmount(newAmount);
            }
            playerItemDao.save(target);
        } else {
            PlayerItem newPlayerItem = new PlayerItem();
            newPlayerItem.setAccountId(accountId);
            newPlayerItem.setItemId(sourcePlayerItem.getItemId());
            newPlayerItem.setLocation(movePlayerItemRequest.getAfterLocation());
            newPlayerItem.setPosition(movePlayerItemRequest.getAfterPosition());
            newPlayerItem.setAmount(movePlayerItemRequest.getAmount());
            playerItemDao.save(newPlayerItem);
        }

        sourcePlayerItem.setAmount(sourcePlayerItem.getAmount() - movePlayerItemRequest.getAmount());
        if (sourcePlayerItem.getAmount() == 0) {
            playerItemDao.delete(sourcePlayerItem);
        } else {
            playerItemDao.save(sourcePlayerItem);
        }

        return MovePlayerItemResponse.builder()
            .itemId(sourcePlayerItem.getItemId())
            .amount(movePlayerItemRequest.getAmount())
            .beforeLocation(movePlayerItemRequest.getBeforeLocation())
            .beforePosition(movePlayerItemRequest.getBeforePosition())
            .afterLocation(movePlayerItemRequest.getAfterLocation())
            .afterPosition(movePlayerItemRequest.getAfterPosition())
            .build();
    }
}