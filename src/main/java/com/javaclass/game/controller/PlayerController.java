package com.javaclass.game.controller;

import com.javaclass.game.dao.PlayerDao;
import com.javaclass.game.model.Player;
import com.javaclass.game.model.PlayerStats;
import com.javaclass.game.model.PlayerEquipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    @Autowired
    private PlayerDao playerDao;

    /**
     * 獲取大廳基本資料 (只包含 ID, 帳號, 暱稱, 錢)
     * GET /api/player/1/lobby
     */
    @GetMapping("/{id}/lobby")
    public ResponseEntity<?> getLobbyInfo(@PathVariable Long id) {
        Player player = playerDao.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到玩家"));
        // 實務上建議這裡也用 DTO，這邊先回傳實體以便測試
        return ResponseEntity.ok(player); 
    }

    /**
     * 獲取純戰鬥數值 (HP, ATK, DEF)
     * GET /api/player/1/stats
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<PlayerStats> getPlayerStats(@PathVariable Long id) {
        Player player = playerDao.findById(id).orElseThrow();
        return ResponseEntity.ok(player.getStats());
    }

    /**
     * 獲取身上穿戴的裝備 ID
     * GET /api/player/1/equipment
     */
    @GetMapping("/{id}/equipment")
    public ResponseEntity<PlayerEquipment> getPlayerEquipment(@PathVariable Long id) {
        Player player = playerDao.findById(id).orElseThrow();
        return ResponseEntity.ok(player.getEquipment());
    }
}