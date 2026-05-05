package com.javaclass.game.service;

import com.javaclass.game.dao.*;
import com.javaclass.game.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class GameService {

    @Autowired private PlayerDao playerDao;
    @Autowired private PlayerStatsDao playerStatsDao;
    @Autowired private PlayerEquipmentDao playerEquipmentDao;
    @Autowired private PlayerItemDao playerItemDao;
    @Autowired private ItemDao itemDao;
    
    private final Random random = new Random();

    /**
     * 建立新角色 (組長要求的拆分初始化)
     */
    @Transactional
    public Player createNewPlayer(String accountId, String nickname) {
        // 1. 建立基礎身分
        Player player = new Player();
        player.setAccountId(accountId);
        player.setNickname(nickname);
        player.setMoney(500L); // 初始金幣
        player = playerDao.save(player);

        // 2. 建立數值表 (使用 MapsId 會自動對應 ID)
        PlayerStats stats = new PlayerStats();
        stats.setPlayer(player);
        playerStatsDao.save(stats);

        // 3. 建立裝備表
        PlayerEquipment equip = new PlayerEquipment();
        equip.setPlayer(player);
        playerEquipmentDao.save(equip);

        return player;
    }

    /**
     * 處理掉落邏輯
     */
    @Transactional
    public PlayerItem processDrop(Long playerId, String source) {
        List<Item> templates = itemDao.findAll();
        if (templates.isEmpty()) return null;
        
        Item template = templates.get(random.nextInt(templates.size()));

        // 隨機產生 3~8 點屬性 (範例)
        PlayerItem newItem = new PlayerItem();
        newItem.setPlayerId(playerId);
        newItem.setItemId(template.getId());
        newItem.setInstanceAtk((double)(random.nextInt(6) + 3));
        newItem.setInstanceDef(random.nextInt(6) + 5);
        newItem.setInstanceHp(random.nextInt(11) + 10);
        
        return playerItemDao.save(newItem);
    }

    /**
     * 售出物品
     */
    @Transactional
    public void sellItem(Long playerId, Long playerItemId) {
        Player player = playerDao.findById(playerId).orElseThrow();
        PlayerItem pItem = playerItemDao.findById(playerItemId).orElseThrow();
        
        if (pItem.isEquipped()) throw new RuntimeException("裝備中不可售出");
        
        player.setMoney(player.getMoney() + 50); // 賣 50 塊
        playerItemDao.delete(pItem);
        playerDao.save(player);
    }

    @Transactional
    public void transferLocation(Long playerItemId, String target) {
        PlayerItem pItem = playerItemDao.findById(playerItemId).orElseThrow();
        if (pItem.isEquipped()) throw new RuntimeException("裝備中不可移動");
        pItem.setLocation(target);
        playerItemDao.save(pItem);
    }
}