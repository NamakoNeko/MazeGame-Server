package com.javaclass.game.service;

import com.javaclass.game.dao.*;
import com.javaclass.game.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipmentService {

    @Autowired private PlayerDao playerDao;
    @Autowired private PlayerStatsDao playerStatsDao;
    @Autowired private PlayerEquipmentDao playerEquipmentDao;
    @Autowired private PlayerItemDao playerItemDao;
    @Autowired private ItemDao itemDao;

    // 基礎數值定義 (未來可移至 Constants)
    private final int BASE_HP = 100;
    private final double BASE_ATK = 10.0;
    private final int BASE_DEF = 5;

    /**
     * 穿上裝備
     */
    @Transactional
    public void equipItem(Long playerId, Long playerItemId) {
        PlayerEquipment equipRecord = playerEquipmentDao.findById(playerId)
                .orElseThrow(() -> new RuntimeException("找不到裝備紀錄"));
        
        PlayerItem pItem = playerItemDao.findById(playerItemId)
                .orElseThrow(() -> new RuntimeException("找不到該物品"));

        Item template = itemDao.findById(pItem.getItemId()).orElseThrow();
        String slot = template.getSlot();

        if (slot == null || slot.equals("NONE")) {
            throw new RuntimeException("此物品不可裝備");
        }

        // 1. 處理替換邏輯：如果該部位已有裝備，先卸下
        Long oldPItemId = getSlotValue(equipRecord, slot);
        if (oldPItemId != null) {
            playerItemDao.findById(oldPItemId).ifPresent(old -> {
                old.setEquipped(false);
                playerItemDao.save(old);
            });
        }

        // 2. 更新新裝備狀態
        pItem.setEquipped(true);
        playerItemDao.save(pItem);

        // 3. 更新 PlayerEquipment 表
        setSlotValue(equipRecord, slot, pItem.getId());
        playerEquipmentDao.save(equipRecord);

        // 4. 重新計算並更新 PlayerStats 表
        refreshPlayerStats(playerId);
    }

    /**
     * 脫下裝備
     */
    @Transactional
    public void unequipItem(Long playerId, String slot) {
        PlayerEquipment equipRecord = playerEquipmentDao.findById(playerId).orElseThrow();
        Long pItemId = getSlotValue(equipRecord, slot);

        if (pItemId != null) {
            playerItemDao.findById(pItemId).ifPresent(item -> {
                item.setEquipped(false);
                playerItemDao.save(item);
            });
            setSlotValue(equipRecord, slot, null);
            playerEquipmentDao.save(equipRecord);
            
            // 重新計算數值
            refreshPlayerStats(playerId);
        }
    }

    /**
     * 核心邏輯：掃描所有已裝備物品並更新 Stats 表
     */
    public void refreshPlayerStats(Long playerId) {
        PlayerStats stats = playerStatsDao.findById(playerId)
                .orElseThrow(() -> new RuntimeException("找不到數值紀錄"));
        
        List<PlayerItem> equippedItems = playerItemDao.findByPlayerIdAndIsEquippedTrue(playerId);

        double totalAtk = BASE_ATK;
        int totalDef = BASE_DEF;
        int totalHp = BASE_HP;

        for (PlayerItem pi : equippedItems) {
            totalAtk += pi.getInstanceAtk();
            totalDef += pi.getInstanceDef();
            totalHp += pi.getInstanceHp();
        }

        stats.setAtk(totalAtk);
        stats.setDef(totalDef);
        stats.setHp(totalHp);
        
        playerStatsDao.save(stats);
    }

    // --- 輔助方法：處理 Slot 字串與欄位的對應 ---
    private Long getSlotValue(PlayerEquipment e, String slot) {
        return switch (slot) {
            case "HEAD" -> e.getHeadId();
            case "CHEST" -> e.getChestId();
            case "WEAPON" -> e.getWeaponId();
            case "OFF_HAND" -> e.getOffHandId();
            case "SHOES" -> e.getShoesId();
            default -> null;
        };
    }

    private void setSlotValue(PlayerEquipment e, String slot, Long value) {
        switch (slot) {
            case "HEAD" -> e.setHeadId(value);
            case "CHEST" -> e.setChestId(value);
            case "WEAPON" -> e.setWeaponId(value);
            case "OFF_HAND" -> e.setOffHandId(value);
            case "SHOES" -> e.setShoesId(value);
        }
    }
}