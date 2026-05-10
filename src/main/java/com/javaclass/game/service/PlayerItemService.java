package com.javaclass.game.service;

import com.javaclass.game.constants.PlayerItemDefiner;
import com.javaclass.game.dao.GameItemAttributeDao;
import com.javaclass.game.dao.ItemDao;
import com.javaclass.game.dao.ItemPriceDao;
import com.javaclass.game.dao.PlayerEquipmentDao;
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
import com.javaclass.game.model.PlayerEquipment;
import com.javaclass.game.model.PlayerItem;
import com.javaclass.game.model.PlayerStats;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PlayerItemService {

    private final PlayerItemDao playerItemDao;
    private final ItemDao itemDao;
    private final ItemPriceDao itemPriceDao;
    private final PlayerStatsDao playerStatsDao;
    private final GameItemAttributeDao gameItemAttributeDao;
    private final PlayerEquipmentDao playerEquipmentDao;

    public PlayerItemService(
        PlayerItemDao playerItemDao,
        ItemDao itemDao,
        ItemPriceDao itemPriceDao,
        PlayerStatsDao playerStatsDao,
        GameItemAttributeDao gameItemAttributeDao,
        PlayerEquipmentDao playerEquipmentDao
    ) {
        this.playerItemDao = playerItemDao;
        this.itemDao = itemDao;
        this.itemPriceDao = itemPriceDao;
        this.playerStatsDao = playerStatsDao;
        this.gameItemAttributeDao = gameItemAttributeDao;
        this.playerEquipmentDao = playerEquipmentDao;
    }

    public List<PlayerItemResult> getPlayerItems(Long playerId) {
        

        return playerItemDao.findByPlayerId(playerId).stream()
            .filter(record -> record.getAmount() > 0)
            .map((PlayerItem record) -> {
                Item item = itemDao.findById(record.getItemId()).orElse(null);

                return toPlayerItemResult(record, item);
            })
            .toList();
    }

    @Transactional
    public GainPlayerItemResponse gainItems(Long playerId, GainPlayerItemRequest gainPlayerItemRequest) {
        

        List<GainPlayerItemRequest.GainPlayerItemEntry> entryList = gainPlayerItemRequest.getItems();

        // ── 第一階段：全部驗證 ──
        for (GainPlayerItemRequest.GainPlayerItemEntry entry : entryList) {
            Item item = itemDao.findById(entry.getItemId())
                .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_FOUND));

            Optional<PlayerItem> existingPlayerItem = playerItemDao
                .findByPlayerIdAndLocationAndPosition(playerId, entry.getLocation(), entry.getPosition());

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
                .findByPlayerIdAndLocationAndPosition(playerId, entry.getLocation(), entry.getPosition());

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
                newPlayerItem.setPlayerId(playerId);
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
    public ConsumePlayerItemResponse consumeItems(Long playerId, ConsumePlayerItemRequest consumePlayerItemRequest) {
        

        List<ConsumePlayerItemRequest.ConsumePlayerItemEntry> entryList = consumePlayerItemRequest.getItems();

        // ── 第一階段：全部驗證 ──
        for (ConsumePlayerItemRequest.ConsumePlayerItemEntry entry : entryList) {
            PlayerItem playerItem = playerItemDao
                .findByPlayerIdAndLocationAndPosition(playerId, entry.getLocation(), entry.getPosition())
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
                .findByPlayerIdAndLocationAndPosition(playerId, entry.getLocation(), entry.getPosition())
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
    public MovePlayerItemResponse moveItem(Long playerId, MovePlayerItemRequest movePlayerItemRequest) {
        

        PlayerItem sourcePlayerItem;
        if (movePlayerItemRequest.getPlayerItemId() != null) {
            sourcePlayerItem = playerItemDao.findById(movePlayerItemRequest.getPlayerItemId())
                .filter(item -> item.getPlayerId().equals(playerId))
                .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_FOUND));
        } else {
            sourcePlayerItem = playerItemDao
                .findByPlayerIdAndLocationAndPosition(
                    playerId,
                    movePlayerItemRequest.getBeforeLocation(),
                    movePlayerItemRequest.getBeforePosition()
                )
                .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_FOUND));
        }

        boolean isAmountEnough = sourcePlayerItem.getAmount() >= movePlayerItemRequest.getAmount();
        if (!isAmountEnough) {
            throw new IllegalArgumentException(PlayerItemDefiner.ERROR_AMOUNT_NOT_ENOUGH);
        }

        boolean isMovingWholeStack = sourcePlayerItem.getAmount().equals(movePlayerItemRequest.getAmount());

        Optional<PlayerItem> targetPlayerItem = playerItemDao
            .findByPlayerIdAndLocationAndPosition(
                playerId,
                movePlayerItemRequest.getAfterLocation(),
                movePlayerItemRequest.getAfterPosition()
            );

        if (targetPlayerItem.isPresent()) {
            PlayerItem target = targetPlayerItem.get();

            if (target.getId().equals(sourcePlayerItem.getId())) {
                return MovePlayerItemResponse.builder()
                    .itemId(sourcePlayerItem.getItemId())
                    .amount(movePlayerItemRequest.getAmount())
                    .beforeLocation(movePlayerItemRequest.getBeforeLocation())
                    .beforePosition(movePlayerItemRequest.getBeforePosition())
                    .afterLocation(movePlayerItemRequest.getAfterLocation())
                    .afterPosition(movePlayerItemRequest.getAfterPosition())
                    .build();
            }

            boolean isEmptySlot = target.getAmount() == 0;
            if (isEmptySlot) {
                if (isMovingWholeStack) {
                    playerItemDao.delete(target);
                    sourcePlayerItem.setLocation(movePlayerItemRequest.getAfterLocation());
                    sourcePlayerItem.setPosition(movePlayerItemRequest.getAfterPosition());
                    playerItemDao.save(sourcePlayerItem);

                    return MovePlayerItemResponse.builder()
                        .itemId(sourcePlayerItem.getItemId())
                        .amount(movePlayerItemRequest.getAmount())
                        .beforeLocation(movePlayerItemRequest.getBeforeLocation())
                        .beforePosition(movePlayerItemRequest.getBeforePosition())
                        .afterLocation(movePlayerItemRequest.getAfterLocation())
                        .afterPosition(movePlayerItemRequest.getAfterPosition())
                        .build();
                } else {
                    target.setItemId(sourcePlayerItem.getItemId());
                    target.setAmount(movePlayerItemRequest.getAmount());
                }
            } else if (!target.getItemId().equals(sourcePlayerItem.getItemId())) {
                if (!isMovingWholeStack) {
                    throw new IllegalArgumentException("target slot occupied");
                }

                Integer sourceLocation = sourcePlayerItem.getLocation();
                Integer sourcePosition = sourcePlayerItem.getPosition();
                sourcePlayerItem.setLocation(target.getLocation());
                sourcePlayerItem.setPosition(target.getPosition());
                target.setLocation(sourceLocation);
                target.setPosition(sourcePosition);
                playerItemDao.save(target);
                playerItemDao.save(sourcePlayerItem);

                return MovePlayerItemResponse.builder()
                    .itemId(sourcePlayerItem.getItemId())
                    .amount(movePlayerItemRequest.getAmount())
                    .beforeLocation(movePlayerItemRequest.getBeforeLocation())
                    .beforePosition(movePlayerItemRequest.getBeforePosition())
                    .afterLocation(movePlayerItemRequest.getAfterLocation())
                    .afterPosition(movePlayerItemRequest.getAfterPosition())
                    .build();
            } else {
                Item item = itemDao.findById(sourcePlayerItem.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_FOUND));

                int newAmount = target.getAmount() + movePlayerItemRequest.getAmount();
                int maxAmount = item.getMaxAmount() == null || item.getMaxAmount() <= 0
                    ? newAmount
                    : item.getMaxAmount();
                boolean isExceedMaxAmount = newAmount > maxAmount;
                if (isExceedMaxAmount) {
                    throw new IllegalArgumentException(PlayerItemDefiner.ERROR_MAX_AMOUNT_EXCEEDED);
                }

                if (isMovingWholeStack) {
                    sourcePlayerItem.setLocation(target.getLocation());
                    sourcePlayerItem.setPosition(target.getPosition());
                    sourcePlayerItem.setAmount(newAmount);
                    playerItemDao.delete(target);
                    playerItemDao.save(sourcePlayerItem);

                    return MovePlayerItemResponse.builder()
                        .itemId(sourcePlayerItem.getItemId())
                        .amount(movePlayerItemRequest.getAmount())
                        .beforeLocation(movePlayerItemRequest.getBeforeLocation())
                        .beforePosition(movePlayerItemRequest.getBeforePosition())
                        .afterLocation(movePlayerItemRequest.getAfterLocation())
                        .afterPosition(movePlayerItemRequest.getAfterPosition())
                        .build();
                } else {
                    target.setAmount(newAmount);
                }
            }
            playerItemDao.save(target);
        } else {
            if (isMovingWholeStack) {
                sourcePlayerItem.setLocation(movePlayerItemRequest.getAfterLocation());
                sourcePlayerItem.setPosition(movePlayerItemRequest.getAfterPosition());
                playerItemDao.save(sourcePlayerItem);

                return MovePlayerItemResponse.builder()
                    .itemId(sourcePlayerItem.getItemId())
                    .amount(movePlayerItemRequest.getAmount())
                    .beforeLocation(movePlayerItemRequest.getBeforeLocation())
                    .beforePosition(movePlayerItemRequest.getBeforePosition())
                    .afterLocation(movePlayerItemRequest.getAfterLocation())
                    .afterPosition(movePlayerItemRequest.getAfterPosition())
                    .build();
            } else {
                PlayerItem newPlayerItem = new PlayerItem();
                newPlayerItem.setPlayerId(playerId);
                newPlayerItem.setItemId(sourcePlayerItem.getItemId());
                newPlayerItem.setLocation(movePlayerItemRequest.getAfterLocation());
                newPlayerItem.setPosition(movePlayerItemRequest.getAfterPosition());
                newPlayerItem.setAmount(movePlayerItemRequest.getAmount());
                playerItemDao.save(newPlayerItem);
            }
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
    public void clearLocation(Long playerId, Integer location) {
        if (location == null) {
            throw new IllegalArgumentException(PlayerItemDefiner.ERROR_LOCATION_REQUIRED);
        }
        
        playerItemDao.deleteByPlayerIdAndLocation(playerId, location);
    }

    @Transactional
    public void replaceLocation(Long playerId, Integer location, ReplaceLocationItemsRequest request) {
        if (location == null) {
            throw new IllegalArgumentException(PlayerItemDefiner.ERROR_LOCATION_REQUIRED);
        }

        Set<Long> equippedIds = getEquippedPlayerItemIds(playerId);
        Set<Long> keptIds = new HashSet<>();
        List<PlayerItem> existingLocationItems = playerItemDao.findByPlayerId(playerId).stream()
            .filter(item -> location.equals(item.getLocation()))
            .toList();
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            for (PlayerItem existing : existingLocationItems) {
                if (equippedIds.contains(existing.getId())) {
                    existing.setLocation(0);
                    existing.setPosition(toDetachedPosition(existing));
                    playerItemDao.save(existing);
                } else {
                    playerItemDao.delete(existing);
                }
            }
            playerItemDao.flush();
            return;
        }

        validateReplaceLocationRequest(request);
        moveExistingLocationItemsToTemporaryPositions(existingLocationItems);

        for (ReplaceLocationItemsRequest.ReplaceLocationItemEntry entry : request.getItems()) {
            int amount = entry.getAmount() == null ? 1 : entry.getAmount();

            PlayerItem playerItem = null;
            if (entry.getPlayerItemId() != null) {
                playerItem = playerItemDao.findById(entry.getPlayerItemId())
                    .filter(itemRecord -> itemRecord.getPlayerId().equals(playerId))
                    .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_FOUND));
            }
            if (playerItem == null) {
                playerItem = new PlayerItem();
            }
            playerItem.setPlayerId(playerId);
            playerItem.setItemId(entry.getItemId());
            playerItem.setLocation(location);
            playerItem.setPosition(entry.getPosition());
            playerItem.setAmount(amount);
            PlayerItem saved = playerItemDao.save(playerItem);
            keptIds.add(saved.getId());
        }

        for (PlayerItem existing : existingLocationItems) {
            if (keptIds.contains(existing.getId())) {
                continue;
            }
            if (equippedIds.contains(existing.getId())) {
                existing.setLocation(0);
                existing.setPosition(toDetachedPosition(existing));
                playerItemDao.save(existing);
            } else {
                playerItemDao.delete(existing);
            }
        }
    }

    private void validateReplaceLocationRequest(ReplaceLocationItemsRequest request) {
        Set<Integer> positions = new HashSet<>();
        Set<Long> playerItemIds = new HashSet<>();

        for (ReplaceLocationItemsRequest.ReplaceLocationItemEntry entry : request.getItems()) {
            if (entry.getItemId() == null) {
                throw new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_ID_REQUIRED);
            }
            int amount = entry.getAmount() == null ? 1 : entry.getAmount();
            if (amount <= 0) {
                throw new IllegalArgumentException(PlayerItemDefiner.ERROR_AMOUNT_INVALID);
            }
            if (entry.getPosition() == null || entry.getPosition() < 0 || !positions.add(entry.getPosition())) {
                throw new IllegalArgumentException(PlayerItemDefiner.ERROR_POSITION_REQUIRED);
            }
            if (entry.getPlayerItemId() != null && !playerItemIds.add(entry.getPlayerItemId())) {
                throw new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_FOUND);
            }

            Item item = itemDao.findById(entry.getItemId())
                .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_FOUND));
            int maxAmount = item.getMaxAmount() == null || item.getMaxAmount() <= 0 ? amount : item.getMaxAmount();
            if (amount > maxAmount) {
                throw new IllegalArgumentException(PlayerItemDefiner.ERROR_MAX_AMOUNT_EXCEEDED);
            }
        }
    }

    private void moveExistingLocationItemsToTemporaryPositions(List<PlayerItem> existingLocationItems) {
        int temporaryPosition = -100000;
        for (PlayerItem existing : existingLocationItems) {
            existing.setPosition(temporaryPosition--);
            playerItemDao.save(existing);
        }
        playerItemDao.flush();
    }

    private int toDetachedPosition(PlayerItem playerItem) {
        return -Math.toIntExact(playerItem.getId());
    }

    private Set<Long> getEquippedPlayerItemIds(Long playerId) {
        Set<Long> ids = new HashSet<>();
        Optional<PlayerEquipment> equipment = playerEquipmentDao.findById(playerId);
        if (equipment.isEmpty()) {
            return ids;
        }

        addIfPresent(ids, equipment.get().getHeadId());
        addIfPresent(ids, equipment.get().getChestId());
        addIfPresent(ids, equipment.get().getWeaponId());
        addIfPresent(ids, equipment.get().getOffHandId());
        addIfPresent(ids, equipment.get().getShoesId());
        return ids;
    }

    private static void addIfPresent(Set<Long> ids, Long id) {
        if (id != null) {
            ids.add(id);
        }
    }

    @Transactional
    public SellPlayerItemResponse sellItem(Long playerId, SellPlayerItemRequest request) {
        
        int amount = request.getAmount() == null ? 1 : request.getAmount();
        if (amount <= 0) {
            throw new IllegalArgumentException(PlayerItemDefiner.ERROR_AMOUNT_INVALID);
        }

        PlayerItem playerItem;
        if (request.getPlayerItemId() != null) {
            playerItem = playerItemDao.findById(request.getPlayerItemId())
                .filter(item -> item.getPlayerId().equals(playerId))
                .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_FOUND));
        } else {
            if (request.getItemId() == null) {
                throw new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_ID_REQUIRED);
            }
            if (request.getLocation() == null || request.getPosition() == null) {
                throw new IllegalArgumentException(PlayerItemDefiner.ERROR_POSITION_REQUIRED);
            }
            playerItem = playerItemDao.findByPlayerIdAndLocationAndPosition(playerId, request.getLocation(), request.getPosition())
                .filter(item -> item.getItemId().equals(request.getItemId()))
                .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_FOUND));
        }
        if (playerItem.getAmount() < amount) {
            throw new IllegalArgumentException(PlayerItemDefiner.ERROR_AMOUNT_NOT_ENOUGH);
        }

        ItemPrice price = itemPriceDao.findById(playerItem.getItemId())
            .orElseThrow(() -> new IllegalArgumentException("item price not found"));
        PlayerStats stats = playerStatsDao.findById(playerId)
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
