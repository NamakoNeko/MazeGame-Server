package com.javaclass.game.controller;

import com.javaclass.game.model.PlayerItem;
import com.javaclass.game.service.EquipmentService;
import com.javaclass.game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameActionController {

    @Autowired private GameService gameService;
    @Autowired private EquipmentService equipmentService;

    @PostMapping("/drop")
    public ResponseEntity<?> handleDrop(@RequestParam Long playerId, @RequestParam String source) {
        PlayerItem dropped = gameService.processDrop(playerId, source);
        return ResponseEntity.ok(Map.of("message", "掉落成功", "data", dropped));
    }

    @PostMapping("/equip")
    public ResponseEntity<?> handleEquip(@RequestParam Long playerId, @RequestParam Long playerItemId) {
        try {
            equipmentService.equipItem(playerId, playerItemId);
            return ResponseEntity.ok(Map.of("message", "穿戴成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/unequip")
    public ResponseEntity<?> handleUnequip(@RequestParam Long playerId, @RequestParam String slot) {
        equipmentService.unequipItem(playerId, slot);
        return ResponseEntity.ok(Map.of("message", "已卸下裝備"));
    }

    @PatchMapping("/transfer")
    public ResponseEntity<?> handleTransfer(@RequestParam Long playerItemId, @RequestParam String target) {
        gameService.transferLocation(playerItemId, target);
        return ResponseEntity.ok(Map.of("message", "轉移成功"));
    }
}