package com.javaclass.game.controller;

import com.javaclass.game.dao.PlayerDao;
import com.javaclass.game.dao.PlayerEquipmentDao;
import com.javaclass.game.dao.PlayerStatsDao;
import com.javaclass.game.model.Player;
import com.javaclass.game.model.PlayerStats;
import com.javaclass.game.model.PlayerEquipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    @Autowired
    private PlayerDao playerDao;

    @Autowired
    private PlayerStatsDao playerStatsDao;

    @Autowired
    private PlayerEquipmentDao playerEquipmentDao;

    /**
     * 獲取大廳基本資料 (只包含 ID, 帳號, 暱稱, 錢)
     * GET /api/player/1/lobby
     */
    @GetMapping("/{id}/lobby")
    public ResponseEntity<?> getLobbyInfo(@PathVariable Long id) {
        Player player = playerDao.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到玩家"));
        // 實務上建議這裡也用 DTO，這邊先回傳實體以便測試
        Long money = player.getStats() != null ? player.getStats().getMoney() : 0L;
        return ResponseEntity.ok(Map.of(
            "id", player.getId(),
            "accountId", player.getAccountId(),
            "nickname", player.getNickname() == null ? "" : player.getNickname(),
            "money", money
        )); 
    }

    /**
     * 獲取純戰鬥數值 (HP, ATK, DEF)
     * GET /api/player/1/stats
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getPlayerStats(@PathVariable Long id) {
        Player player = playerDao.findById(id).orElseThrow();
        PlayerStats stats = player.getStats();
        if (stats == null) {
            stats = new PlayerStats();
            stats.setPlayer(player);
            stats = playerStatsDao.save(stats);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("playerId", stats.getPlayerId());
        result.put("hp", stats.getHp());
        result.put("maxHp", stats.getHp());
        result.put("atk", stats.getAtk());
        result.put("def", stats.getDef());
        result.put("money", stats.getMoney());
        return ResponseEntity.ok(result);
    }

    /**
     * 獲取身上穿戴的裝備 ID
     * GET /api/player/1/equipment
     */
    @GetMapping("/{id}/equipment")
    public ResponseEntity<Map<String, Object>> getPlayerEquipment(@PathVariable Long id) {
        Player player = playerDao.findById(id).orElseThrow();
        PlayerEquipment equipment = player.getEquipment();
        if (equipment == null) {
            equipment = new PlayerEquipment();
            equipment.setPlayer(player);
            equipment = playerEquipmentDao.save(equipment);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("playerId", equipment.getPlayerId());
        result.put("headId", equipment.getHeadId());
        result.put("chestId", equipment.getChestId());
        result.put("rightHand", equipment.getWeaponId());
        result.put("weaponId", equipment.getWeaponId());
        result.put("leftHand", equipment.getOffHandId());
        result.put("offHandId", equipment.getOffHandId());
        result.put("shoesId", equipment.getShoesId());
        return ResponseEntity.ok(result);
    }
}
