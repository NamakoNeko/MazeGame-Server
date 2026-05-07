package com.javaclass.game.service;

import com.javaclass.game.constants.PlayerItemDefiner;
import com.javaclass.game.constants.PlayerItemDefiner.ItemType;
import com.javaclass.game.dao.ItemDao;
import com.javaclass.game.dao.PlayerDao;
import com.javaclass.game.dao.PlayerEquipmentDao;
import com.javaclass.game.dao.PlayerItemDao;
import com.javaclass.game.dto.EquipRequest;
import com.javaclass.game.dto.EquipmentResult;
import com.javaclass.game.dto.UnequipRequest;
import com.javaclass.game.model.Item;

import com.javaclass.game.model.PlayerEquipment;
import com.javaclass.game.model.PlayerItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayerEquipmentService {

    private final PlayerEquipmentDao playerEquipmentDao;
    private final PlayerItemDao playerItemDao;
    private final ItemDao itemDao;
    private final PlayerDao playerDao;

    public PlayerEquipmentService(
        PlayerEquipmentDao playerEquipmentDao,
        PlayerItemDao playerItemDao,
        ItemDao itemDao,
        PlayerDao playerDao
    ) {
        this.playerEquipmentDao = playerEquipmentDao;
        this.playerItemDao = playerItemDao;
        this.itemDao = itemDao;
        this.playerDao = playerDao;
    }

    public EquipmentResult getEquipment(String accountId) {
        validatePlayer(accountId);

        PlayerEquipment playerEquipment = playerEquipmentDao.findByAccountId(accountId)
            .orElseGet(() -> buildEmptyEquipment(accountId));

        return toEquipmentResult(playerEquipment);
    }

    @Transactional
    public EquipmentResult equip(String accountId, EquipRequest equipRequest) {
        validatePlayer(accountId);

        Item item = itemDao.findById(equipRequest.getItemId())
            .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_FOUND));

        ItemType itemType = ItemType.fromValue(item.getType());
        if (!itemType.isEquipment()) {
            throw new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_EQUIPMENT);
        }

        PlayerItem playerItem = playerItemDao
            .findByAccountIdAndLocationAndPosition(accountId, equipRequest.getLocation(), equipRequest.getPosition())
            .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_IN_INVENTORY));

        boolean isCorrectItem = playerItem.getItemId().equals(equipRequest.getItemId());
        if (!isCorrectItem || playerItem.getAmount() <= 0) {
            throw new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_IN_INVENTORY);
        }

        PlayerEquipment playerEquipment = playerEquipmentDao.findByAccountId(accountId)
            .orElseGet(() -> buildEmptyEquipment(accountId));

        setEquipmentSlot(playerEquipment, itemType, equipRequest.getItemId());

        playerItem.setAmount(playerItem.getAmount() - 1);
        if (playerItem.getAmount() == 0) {
            playerItemDao.delete(playerItem);
        } else {
            playerItemDao.save(playerItem);
        }

        playerEquipmentDao.save(playerEquipment);
        return toEquipmentResult(playerEquipment);
    }

    @Transactional
    public EquipmentResult unequip(String accountId, UnequipRequest unequipRequest) {
        validatePlayer(accountId);

        Item item = itemDao.findById(unequipRequest.getItemId())
            .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_FOUND));

        ItemType itemType = ItemType.fromValue(item.getType());

        PlayerEquipment playerEquipment = playerEquipmentDao.findByAccountId(accountId)
            .orElseThrow(() -> new IllegalArgumentException(PlayerItemDefiner.ERROR_SLOT_NOT_EQUIPPED));

        boolean isSlotEquipped = getEquipmentSlot(playerEquipment, itemType) != null;
        if (!isSlotEquipped) {
            throw new IllegalArgumentException(PlayerItemDefiner.ERROR_SLOT_NOT_EQUIPPED);
        }

        PlayerItem targetPlayerItem = playerItemDao
            .findByAccountIdAndLocationAndPosition(
                accountId,
                unequipRequest.getTargetLocation(),
                unequipRequest.getTargetPosition()
            )
            .orElseGet(() -> {
                PlayerItem newPlayerItem = new PlayerItem();
                newPlayerItem.setAccountId(accountId);
                newPlayerItem.setItemId(unequipRequest.getItemId());
                newPlayerItem.setLocation(unequipRequest.getTargetLocation());
                newPlayerItem.setPosition(unequipRequest.getTargetPosition());
                newPlayerItem.setAmount(0);
                return newPlayerItem;
            });

        boolean isEmptySlot = targetPlayerItem.getAmount() == 0;
        if (isEmptySlot) {
            targetPlayerItem.setItemId(unequipRequest.getItemId());
        }

        targetPlayerItem.setAmount(targetPlayerItem.getAmount() + 1);
        playerItemDao.save(targetPlayerItem);

        setEquipmentSlot(playerEquipment, itemType, null);
        playerEquipmentDao.save(playerEquipment);

        return toEquipmentResult(playerEquipment);
    }

    private void validatePlayer(String accountId) {
        boolean isPlayerExists = playerDao.findByAccountId(accountId).isPresent();
        if (!isPlayerExists) {
            throw new IllegalArgumentException(PlayerItemDefiner.ERROR_PLAYER_NOT_FOUND);
        }
    }

    private PlayerEquipment buildEmptyEquipment(String accountId) {
        PlayerEquipment playerEquipment = new PlayerEquipment();
        playerEquipment.setAccountId(accountId);
        return playerEquipment;
    }

    private void setEquipmentSlot(PlayerEquipment playerEquipment, ItemType itemType, Long itemId) {
        switch (itemType) {
            case HEAD     -> playerEquipment.setHeadId(itemId);
            case CHEST    -> playerEquipment.setChestId(itemId);
            case WEAPON   -> playerEquipment.setWeaponId(itemId);
            case OFF_HAND -> playerEquipment.setOffHandId(itemId);
            case SHOES    -> playerEquipment.setShoesId(itemId);
            default -> throw new IllegalArgumentException(PlayerItemDefiner.ERROR_ITEM_NOT_EQUIPMENT);
        }
    }

    private Long getEquipmentSlot(PlayerEquipment playerEquipment, ItemType itemType) {
        return switch (itemType) {
            case HEAD     -> playerEquipment.getHeadId();
            case CHEST    -> playerEquipment.getChestId();
            case WEAPON   -> playerEquipment.getWeaponId();
            case OFF_HAND -> playerEquipment.getOffHandId();
            case SHOES    -> playerEquipment.getShoesId();
            default       -> null;
        };
    }

    private EquipmentResult toEquipmentResult(PlayerEquipment playerEquipment) {
        return EquipmentResult.builder()
            .headId(playerEquipment.getHeadId())
            .chestId(playerEquipment.getChestId())
            .weaponId(playerEquipment.getWeaponId())
            .offHandId(playerEquipment.getOffHandId())
            .shoesId(playerEquipment.getShoesId())
            .build();
    }
}