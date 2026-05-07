package com.javaclass.game.service;

import com.javaclass.game.constants.GameApiDefiner;
import com.javaclass.game.dao.PlayerDao;
import com.javaclass.game.dao.PlayerEquipmentDao;
import com.javaclass.game.dao.PlayerItemDao;
import com.javaclass.game.dto.EquipItemRequest;
import com.javaclass.game.dto.EquipmentResponse;
import com.javaclass.game.dto.UnequipItemRequest;
import com.javaclass.game.model.Player;
import com.javaclass.game.model.PlayerEquipment;
import com.javaclass.game.model.PlayerItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameEquipmentService {

    private final PlayerDao playerDao;
    private final PlayerEquipmentDao playerEquipmentDao;
    private final PlayerItemDao playerItemDao;

    public GameEquipmentService(PlayerDao playerDao, PlayerEquipmentDao playerEquipmentDao, PlayerItemDao playerItemDao) {
        this.playerDao = playerDao;
        this.playerEquipmentDao = playerEquipmentDao;
        this.playerItemDao = playerItemDao;
    }

    @Transactional
    public EquipmentResponse equip(String accountId, EquipItemRequest request) {
        Player player = getPlayer(accountId);
        PlayerItem playerItem = playerItemDao.findById(request.getPlayerItemId())
            .filter(item -> item.getPlayerId().equals(player.getId()))
            .orElseThrow(() -> new IllegalArgumentException(GameApiDefiner.ERROR_ITEM_NOT_FOUND));
        PlayerEquipment equipment = getEquipment(player);
        setSlot(equipment, request.getSlot(), playerItem.getId());
        playerEquipmentDao.save(equipment);
        return toResponse(equipment);
    }

    @Transactional
    public EquipmentResponse unequip(String accountId, UnequipItemRequest request) {
        Player player = getPlayer(accountId);
        PlayerEquipment equipment = getEquipment(player);
        setSlot(equipment, request.getSlot(), null);
        playerEquipmentDao.save(equipment);
        return toResponse(equipment);
    }

    private Player getPlayer(String accountId) {
        return playerDao.findByAccountId(accountId)
            .orElseThrow(() -> new IllegalArgumentException(GameApiDefiner.ERROR_PLAYER_NOT_FOUND));
    }

    private PlayerEquipment getEquipment(Player player) {
        return playerEquipmentDao.findById(player.getId())
            .orElseThrow(() -> new IllegalArgumentException(GameApiDefiner.ERROR_EQUIPMENT_NOT_FOUND));
    }

    private static void setSlot(PlayerEquipment equipment, String slot, Long playerItemId) {
        switch ((slot == null ? "" : slot.trim().toUpperCase())) {
            case GameApiDefiner.SLOT_HEAD -> equipment.setHeadId(playerItemId);
            case GameApiDefiner.SLOT_CHEST -> equipment.setChestId(playerItemId);
            case GameApiDefiner.SLOT_WEAPON -> equipment.setWeaponId(playerItemId);
            case GameApiDefiner.SLOT_OFFHAND -> equipment.setOffHandId(playerItemId);
            case GameApiDefiner.SLOT_SHOES -> equipment.setShoesId(playerItemId);
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
