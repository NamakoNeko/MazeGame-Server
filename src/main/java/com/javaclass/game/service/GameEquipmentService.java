package com.javaclass.game.service;

import com.javaclass.game.constants.GameApiDefiner;
import com.javaclass.game.dao.PlayerEquipmentDao;
import com.javaclass.game.dao.PlayerItemDao;
import com.javaclass.game.dto.EquipItemRequest;
import com.javaclass.game.dto.EquipmentResponse;
import com.javaclass.game.dto.UnequipItemRequest;
import com.javaclass.game.model.PlayerEquipment;
import com.javaclass.game.model.PlayerItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameEquipmentService {

    private final PlayerEquipmentDao playerEquipmentDao;
    private final PlayerItemDao playerItemDao;

    public GameEquipmentService(PlayerEquipmentDao playerEquipmentDao, PlayerItemDao playerItemDao) {
        this.playerEquipmentDao = playerEquipmentDao;
        this.playerItemDao = playerItemDao;
    }

    @Transactional
    public EquipmentResponse equip(Long playerId, EquipItemRequest request) {
        PlayerItem playerItem = playerItemDao.findById(request.getPlayerItemId())
            .filter(item -> item.getPlayerId().equals(playerId))
            .orElseThrow(() -> new IllegalArgumentException(GameApiDefiner.ERROR_ITEM_NOT_FOUND));
        PlayerEquipment equipment = getEquipment(playerId);
        setSlot(equipment, request.getSlot(), playerItem.getId());
        playerEquipmentDao.save(equipment);
        return toResponse(equipment);
    }

    @Transactional
    public EquipmentResponse unequip(Long playerId, UnequipItemRequest request) {
        PlayerEquipment equipment = getEquipment(playerId);
        setSlot(equipment, request.getSlot(), null);
        playerEquipmentDao.save(equipment);
        return toResponse(equipment);
    }

    private PlayerEquipment getEquipment(Long playerId) {
        return playerEquipmentDao.findById(playerId)
            .orElseThrow(() -> new IllegalArgumentException(GameApiDefiner.ERROR_EQUIPMENT_NOT_FOUND));
    }

    private static void setSlot(PlayerEquipment equipment, String slot, Long playerItemId) {
        switch ((slot == null ? "" : slot.trim().toUpperCase())) {
            case GameApiDefiner.SLOT_HEAD    -> equipment.setHeadId(playerItemId);
            case GameApiDefiner.SLOT_CHEST   -> equipment.setChestId(playerItemId);
            case GameApiDefiner.SLOT_WEAPON  -> equipment.setWeaponId(playerItemId);
            case GameApiDefiner.SLOT_OFFHAND -> equipment.setOffHandId(playerItemId);
            case GameApiDefiner.SLOT_SHOES   -> equipment.setShoesId(playerItemId);
            default -> throw new IllegalArgumentException(GameApiDefiner.ERROR_INVALID_SLOT);
        }
    }

    private static EquipmentResponse toResponse(PlayerEquipment equipment) {
        return EquipmentResponse.builder()
            .playerId(equipment.getPlayerId())
            .headId(equipment.getHeadId())
            .chestId(equipment.getChestId())
            .weaponId(equipment.getWeaponId())
            .offHandId(equipment.getOffHandId())
            .shoesId(equipment.getShoesId())
            .build();
    }
}
