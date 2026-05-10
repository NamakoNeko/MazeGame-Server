package com.javaclass.game.service;

import com.javaclass.game.constants.InventoryDefiner;
import com.javaclass.game.constants.PlayerItemDefiner;
import com.javaclass.game.dao.ItemDao;
import com.javaclass.game.dao.PlayerItemDao;
import com.javaclass.game.dto.GrantItemRequest;
import com.javaclass.game.dto.InventoryItemResult;
import com.javaclass.game.dto.RemoveItemRequest;
import com.javaclass.game.model.Item;
import com.javaclass.game.model.PlayerItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private static final int DEFAULT_INVENTORY_SIZE = 50;

    private final PlayerItemDao playerItemDao;
    private final ItemDao itemDao;

    public InventoryService(PlayerItemDao playerItemDao, ItemDao itemDao) {
        this.playerItemDao = playerItemDao;
        this.itemDao = itemDao;
    }

    public Page<InventoryItemResult> getInventory(Long playerId, Pageable pageable) {
        Map<Long, List<PlayerItem>> playerItemsByItemId = playerItemDao.findByPlayerId(playerId).stream()
            .filter(playerItem -> playerItem.getAmount() != null && playerItem.getAmount() > 0)
            .collect(Collectors.groupingBy(PlayerItem::getItemId));

        List<InventoryItemResult> inventoryItemResultList = playerItemsByItemId.entrySet().stream()
            .map(entry -> toInventoryItemResult(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(InventoryItemResult::getItemId))
            .toList();

        int start = (int) Math.min(pageable.getOffset(), inventoryItemResultList.size());
        int end = Math.min(start + pageable.getPageSize(), inventoryItemResultList.size());
        return new PageImpl<>(inventoryItemResultList.subList(start, end), pageable, inventoryItemResultList.size());
    }

    @Transactional
    public void grantItem(Long playerId, GrantItemRequest grantItemRequest) {
        Item item = itemDao.findById(grantItemRequest.getItemId())
            .orElseThrow(() -> new IllegalArgumentException(InventoryDefiner.ERROR_ITEM_NOT_FOUND));

        int remainingQuantity = grantItemRequest.getQuantity();
        int maxAmount = item.getMaxAmount() == null || item.getMaxAmount() <= 0
            ? remainingQuantity
            : item.getMaxAmount();

        for (PlayerItem playerItem : getStackableInventoryItems(playerId, item.getId(), maxAmount)) {
            if (remainingQuantity <= 0) {
                return;
            }

            int availableAmount = maxAmount - playerItem.getAmount();
            int addedAmount = Math.min(availableAmount, remainingQuantity);
            playerItem.setAmount(playerItem.getAmount() + addedAmount);
            playerItemDao.save(playerItem);
            remainingQuantity -= addedAmount;
        }

        while (remainingQuantity > 0) {
            int position = findFirstEmptyPosition(playerId, PlayerItemDefiner.LOCATION_INVENTORY);
            int addedAmount = Math.min(maxAmount, remainingQuantity);

            PlayerItem playerItem = new PlayerItem();
            playerItem.setPlayerId(playerId);
            playerItem.setItemId(item.getId());
            playerItem.setLocation(PlayerItemDefiner.LOCATION_INVENTORY);
            playerItem.setPosition(position);
            playerItem.setAmount(addedAmount);
            playerItemDao.save(playerItem);

            remainingQuantity -= addedAmount;
        }
    }

    @Transactional
    public void removeItem(Long playerId, RemoveItemRequest removeItemRequest) {
        List<PlayerItem> playerItems = playerItemDao.findByPlayerId(playerId).stream()
            .filter(playerItem -> removeItemRequest.getItemId().equals(playerItem.getItemId()))
            .filter(playerItem -> playerItem.getAmount() != null && playerItem.getAmount() > 0)
            .sorted(Comparator
                .comparing(PlayerItem::getLocation)
                .thenComparing(PlayerItem::getPosition))
            .toList();

        int currentQuantity = playerItems.stream().mapToInt(PlayerItem::getAmount).sum();
        if (currentQuantity < removeItemRequest.getQuantity()) {
            throw new IllegalArgumentException(InventoryDefiner.ERROR_QUANTITY_NOT_ENOUGH);
        }

        int remainingQuantity = removeItemRequest.getQuantity();
        for (PlayerItem playerItem : playerItems) {
            if (remainingQuantity <= 0) {
                return;
            }

            int removedAmount = Math.min(playerItem.getAmount(), remainingQuantity);
            playerItem.setAmount(playerItem.getAmount() - removedAmount);
            remainingQuantity -= removedAmount;

            if (playerItem.getAmount() <= 0) {
                playerItemDao.delete(playerItem);
            } else {
                playerItemDao.save(playerItem);
            }
        }
    }

    private InventoryItemResult toInventoryItemResult(Long itemId, List<PlayerItem> playerItems) {
        String itemName = itemDao.findById(itemId)
            .map(Item::getName)
            .orElse("");
        int quantity = playerItems.stream().mapToInt(PlayerItem::getAmount).sum();
        Long firstPlayerItemId = playerItems.stream()
            .map(PlayerItem::getId)
            .min(Long::compareTo)
            .orElse(null);

        return InventoryItemResult.builder()
            .id(firstPlayerItemId)
            .itemId(itemId)
            .itemName(itemName)
            .quantity(quantity)
            .updatedAt(null)
            .build();
    }

    private List<PlayerItem> getStackableInventoryItems(Long playerId, Long itemId, int maxAmount) {
        return playerItemDao.findByPlayerId(playerId).stream()
            .filter(playerItem -> itemId.equals(playerItem.getItemId()))
            .filter(playerItem -> PlayerItemDefiner.LOCATION_INVENTORY == playerItem.getLocation())
            .filter(playerItem -> playerItem.getAmount() != null && playerItem.getAmount() > 0)
            .filter(playerItem -> playerItem.getAmount() < maxAmount)
            .sorted(Comparator.comparing(PlayerItem::getPosition))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    private int findFirstEmptyPosition(Long playerId, Integer location) {
        for (int position = 0; position < DEFAULT_INVENTORY_SIZE; position++) {
            if (playerItemDao.findByPlayerIdAndLocationAndPosition(playerId, location, position).isEmpty()) {
                return position;
            }
        }

        throw new IllegalArgumentException(PlayerItemDefiner.ERROR_MAX_AMOUNT_EXCEEDED);
    }
}
