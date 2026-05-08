package com.javaclass.game.service;

import com.javaclass.game.constants.PlayerItemDefiner;
import com.javaclass.game.dao.GameItemAttributeDao;
import com.javaclass.game.dao.ItemDao;
import com.javaclass.game.dao.ItemPriceDao;
import com.javaclass.game.dao.PlayerDao;
import com.javaclass.game.dao.PlayerItemDao;
import com.javaclass.game.dao.PlayerStatsDao;
import com.javaclass.game.dto.ConsumePlayerItemRequest;
import com.javaclass.game.dto.ConsumePlayerItemResponse;
import com.javaclass.game.dto.GainPlayerItemRequest;
import com.javaclass.game.dto.GainPlayerItemResponse;
import com.javaclass.game.dto.MovePlayerItemRequest;
import com.javaclass.game.dto.MovePlayerItemResponse;
import com.javaclass.game.dto.PlayerItemResult;
import com.javaclass.game.dto.ItemAttributeResult;
import com.javaclass.game.dto.ReplaceLocationItemsRequest;
import com.javaclass.game.dto.SellPlayerItemRequest;
import com.javaclass.game.dto.SellPlayerItemResponse;
import com.javaclass.game.model.Item;
import com.javaclass.game.model.ItemPrice;
import com.javaclass.game.model.GameItemAttribute;
import com.javaclass.game.model.Player;
import com.javaclass.game.model.PlayerItem;
import com.javaclass.game.model.PlayerStats;
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
    private final ItemPriceDao itemPriceDao;
    private final PlayerStatsDao playerStatsDao;
    private final GameItemAttributeDao gameItemAttributeDao;

    public PlayerItemService(
        PlayerItemDao playerItemDao,
        ItemDao itemDao,
        PlayerDao playerDao,
        ItemPriceDao itemPriceDao,
        PlayerStatsDao playerStatsDao,
        GameItemAttributeDao gameItemAttributeDao
    ) {
        this.playerItemDao = playerItemDao;
        this.itemDao = itemDao;
        this.playerDao = playerDao;
        this.itemPriceDao = itemPriceDao;
        this.playerStatsDao = playerStatsDao;
        this.gameItemAttributeDao = gameItemAttributeDao;
    }

    public List<PlayerItemResult> getPlayerItemsByAccountId(String accountId) {
        Player player = getPlayerByAccountId(accountId);

        return playerItemDao.findByPlayerId(player.getId()).stream()
            .filter(record -> record.getAmount() > 0)
            .map((PlayerItem record) -> {
                Item item = itemDao.findById(record.getItemId()).orElse(null);

                return toPlayerItemResult(record, item);
            })
            .toList();
    }

    @Transactional
    public GainPlayerItemResponse gainItems(String accountId, GainPlayerItemRequest gainPlayerItemRequest) {
        Player player = getPlayerByAccountId(accountId);

        List<GainPlayerItemRequest.GainPlayerItemEntry> entryList = gainPlayerItemRequest.getItems();

        // ── 第一階段：全部驗證 ──
        for (GainPlayerItemRequest.GainPlayerItemEntry entry : entryList) {
            Item item = itemDao.findById(entry.getItemId())
                .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_FOUND));

            Optional<PlayerItem> existingPlayerItem = playerItemDao
                .findByPlayerIdAndLocationAndPosition(player.getId(), entry.getLocation(), entry.getPosition());

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
                .findByPlayerIdAndLocationAndPosition(player.getId(), entry.getLocation(), entry.getPosition());

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
                newPlayerItem.setPlayerId(player.getId());
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
        Player player = getPlayerByAccountId(accountId);

        List<ConsumePlayerItemRequest.ConsumePlayerItemEntry> entryList = consumePlayerItemRequest.getItems();

        // ── 第一階段：全部驗證 ──
        for (ConsumePlayerItemRequest.ConsumePlayerItemEntry entry : entryList) {
            PlayerItem playerItem = playerItemDao
                .findByPlayerIdAndLocationAndPosition(player.getId(), entry.getLocation(), entry.getPosition())
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
                .findByPlayerIdAndLocationAndPosition(player.getId(), entry.getLocation(), entry.getPosition())
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
        Player player = getPlayerByAccountId(accountId);

        PlayerItem sourcePlayerItem = playerItemDao
            .findByPlayerIdAndLocationAndPosition(
                player.getId(),
                movePlayerItemRequest.getBeforeLocation(),
                movePlayerItemRequest.getBeforePosition()
            )
            .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_FOUND));

        boolean isAmountEnough = sourcePlayerItem.getAmount() >= movePlayerItemRequest.getAmount();
        if (!isAmountEnough) {
            throw new IllegalArgumentException(PlayerItemDefiner.ERROR_AMOUNT_NOT_ENOUGH);
        }

        Optional<PlayerItem> targetPlayerItem = playerItemDao
            .findByPlayerIdAndLocationAndPosition(
                player.getId(),
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
            newPlayerItem.setPlayerId(player.getId());
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

    @Transactional
    public void clearLocation(String accountId, Integer location) {
        if (location == null) {
            throw new IllegalArgumentException(PlayerItemDefiner.ERROR_LOCATION_REQUIRED);
        }
        Player player = getPlayerByAccountId(accountId);
        playerItemDao.deleteByPlayerIdAndLocation(player.getId(), location);
    }

    @Transactional
    public void replaceLocation(String accountId, Integer location, ReplaceLocationItemsRequest request) {
        if (location == null) {
            throw new IllegalArgumentException(PlayerItemDefiner.ERROR_LOCATION_REQUIRED);
        }
        Player player = getPlayerByAccountId(accountId);
        playerItemDao.deleteByPlayerIdAndLocation(player.getId(), location);
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            return;
        }

        for (ReplaceLocationItemsRequest.ReplaceLocationItemEntry entry : request.getItems()) {
            if (entry.getItemId() == null) {
                throw new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_ID_REQUIRED);
            }
            int amount = entry.getAmount() == null ? 1 : entry.getAmount();
            if (amount <= 0) {
                throw new IllegalArgumentException(PlayerItemDefiner.ERROR_AMOUNT_INVALID);
            }
            if (entry.getPosition() == null || entry.getPosition() < 0) {
                throw new IllegalArgumentException(PlayerItemDefiner.ERROR_POSITION_REQUIRED);
            }

            Item item = itemDao.findById(entry.getItemId())
                .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_FOUND));
            int maxAmount = item.getMaxAmount() == null ? amount : item.getMaxAmount();
            if (amount > maxAmount) {
                throw new IllegalArgumentException(PlayerItemDefiner.ERROR_MAX_AMOUNT_EXCEEDED);
            }

            PlayerItem playerItem = new PlayerItem();
            playerItem.setPlayerId(player.getId());
            playerItem.setItemId(entry.getItemId());
            playerItem.setLocation(location);
            playerItem.setPosition(entry.getPosition());
            playerItem.setAmount(amount);
            playerItemDao.save(playerItem);
        }
    }

    @Transactional
    public SellPlayerItemResponse sellItem(String accountId, SellPlayerItemRequest request) {
        Player player = getPlayerByAccountId(accountId);
        int amount = request.getAmount() == null ? 1 : request.getAmount();
        if (amount <= 0) {
            throw new IllegalArgumentException(PlayerItemDefiner.ERROR_AMOUNT_INVALID);
        }

        PlayerItem playerItem;
        if (request.getPlayerItemId() != null) {
            playerItem = playerItemDao.findById(request.getPlayerItemId())
                .filter(item -> item.getPlayerId().equals(player.getId()))
                .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_FOUND));
        } else {
            if (request.getItemId() == null) {
                throw new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_ID_REQUIRED);
            }
            if (request.getLocation() == null || request.getPosition() == null) {
                throw new IllegalArgumentException(PlayerItemDefiner.ERROR_POSITION_REQUIRED);
            }
            playerItem = playerItemDao.findByPlayerIdAndLocationAndPosition(player.getId(), request.getLocation(), request.getPosition())
                .filter(item -> item.getItemId().equals(request.getItemId()))
                .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_FOUND));
        }
        if (playerItem.getAmount() < amount) {
            throw new IllegalArgumentException(PlayerItemDefiner.ERROR_AMOUNT_NOT_ENOUGH);
        }

        ItemPrice price = itemPriceDao.findById(playerItem.getItemId())
            .orElseThrow(() -> new IllegalArgumentException("item price not found"));
        PlayerStats stats = playerStatsDao.findById(player.getId())
            .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_PLAYER_NOT_FOUND));

        long gainedMoney = price.getSellPrice() * amount;
        stats.setMoney((stats.getMoney() == null ? 0L : stats.getMoney()) + gainedMoney);
        playerStatsDao.save(stats);

        playerItem.setAmount(playerItem.getAmount() - amount);
        int remaining = playerItem.getAmount();
        if (remaining <= 0) {
            playerItemDao.delete(playerItem);
            remaining = 0;
        } else {
            playerItemDao.save(playerItem);
        }

        return SellPlayerItemResponse.builder()
            .playerItemId(request.getPlayerItemId())
            .itemId(playerItem.getItemId())
            .soldAmount(amount)
            .remainingAmount(remaining)
            .gainedMoney(gainedMoney)
            .money(stats.getMoney())
            .build();
    }

    private Player getPlayerByAccountId(String accountId) {
        return playerDao.findByAccountId(accountId)
            .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_PLAYER_NOT_FOUND));
    }

    public PlayerItemResult toPlayerItemResult(PlayerItem record) {
        Item item = itemDao.findById(record.getItemId()).orElse(null);
        return toPlayerItemResult(record, item);
    }

    private PlayerItemResult toPlayerItemResult(PlayerItem record, Item item) {
        ItemPrice price = itemPriceDao.findById(record.getItemId()).orElse(null);
        List<ItemAttributeResult> attributes = gameItemAttributeDao.findByItemId(record.getItemId()).stream()
            .map(this::toAttributeResult)
            .toList();
        return PlayerItemResult.builder()
            .playerItemId(record.getId())
            .itemId(record.getItemId())
            .name(item != null ? item.getName() : "")
            .description(item != null ? item.getDescription() : "")
            .effect("")
            .rare(item != null ? item.getRare() : "")
            .type(item != null ? item.getType() : null)
            .modelPath(item != null ? item.getModelPath() : "")
            .maxAmount(item != null ? item.getMaxAmount() : null)
            .location(record.getLocation())
            .position(record.getPosition())
            .amount(record.getAmount())
            .buyPrice(price != null ? price.getBuyPrice() : 0L)
            .sellPrice(price != null ? price.getSellPrice() : 0L)
            .attributes(attributes)
            .build();
    }

    private ItemAttributeResult toAttributeResult(GameItemAttribute attribute) {
        return ItemAttributeResult.builder()
            .effectType(attribute.getEffectType())
            .value(attribute.getValue())
            .duration(attribute.getDuration())
            .build();
    }
}
