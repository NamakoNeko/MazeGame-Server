package com.javaclass.game.service;

import com.javaclass.game.constants.InventoryDefiner;
import com.javaclass.game.dao.InventoryDao;
import com.javaclass.game.dao.ItemDao;
import com.javaclass.game.dto.GrantItemRequest;
import com.javaclass.game.dto.InventoryItemResult;
import com.javaclass.game.dto.RemoveItemRequest;
import com.javaclass.game.model.Inventory;
import com.javaclass.game.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryDao inventoryDao;
    private final ItemDao itemDao;

    public InventoryService(InventoryDao inventoryDao, ItemDao itemDao) {
        this.inventoryDao = inventoryDao;
        this.itemDao = itemDao;
    }

    public Page<InventoryItemResult> getInventory(Long playerId, Pageable pageable) {
        Page<Inventory> inventoryPage = inventoryDao.findByPlayerId(playerId, pageable);

        List<InventoryItemResult> inventoryItemResultList = inventoryPage.getContent().stream()
            .map(inventory -> {
                String itemName = itemDao.findById(inventory.getItemId())
                    .map(Item::getName)
                    .orElse("");

                return InventoryItemResult.builder()
                    .id(inventory.getId())
                    .itemId(inventory.getItemId())
                    .itemName(itemName)
                    .quantity(inventory.getQuantity())
                    .updatedAt(inventory.getUpdatedAt())
                    .build();
            })
            .toList();

        return new PageImpl<>(inventoryItemResultList, pageable, inventoryPage.getTotalElements());
    }

    @Transactional
    public void grantItem(Long playerId, GrantItemRequest grantItemRequest) {
        boolean isItemExists = itemDao.existsById(grantItemRequest.getItemId());
        if (!isItemExists) {
            throw new IllegalArgumentException(InventoryDefiner.ERROR_ITEM_NOT_FOUND);
        }

        Inventory inventory = inventoryDao
            .findByPlayerIdAndItemId(playerId, grantItemRequest.getItemId())
            .orElseGet(() -> {
                Inventory newInventory = new Inventory();
                newInventory.setPlayerId(playerId);
                newInventory.setItemId(grantItemRequest.getItemId());
                newInventory.setQuantity(0);
                return newInventory;
            });

        inventory.setQuantity(inventory.getQuantity() + grantItemRequest.getQuantity());
        inventoryDao.save(inventory);
    }

    @Transactional
    public void removeItem(Long playerId, RemoveItemRequest removeItemRequest) {
        Inventory inventory = inventoryDao
            .findByPlayerIdAndItemId(playerId, removeItemRequest.getItemId())
            .orElseThrow(() -> new IllegalArgumentException(InventoryDefiner.ERROR_ITEM_NOT_FOUND));

        boolean isQuantityEnough = inventory.getQuantity() >= removeItemRequest.getQuantity();
        if (!isQuantityEnough) {
            throw new IllegalArgumentException(InventoryDefiner.ERROR_QUANTITY_NOT_ENOUGH);
        }

        inventory.setQuantity(inventory.getQuantity() - removeItemRequest.getQuantity());
        inventoryDao.save(inventory);
    }
}
