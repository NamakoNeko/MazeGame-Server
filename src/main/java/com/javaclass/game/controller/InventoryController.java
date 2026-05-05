package com.javaclass.game.controller;

import com.javaclass.game.dao.ItemDao;
import com.javaclass.game.dao.PlayerItemDao;
import com.javaclass.game.dto.InventoryItemDto;
import com.javaclass.game.model.Item;
import com.javaclass.game.model.PlayerItem;
import com.javaclass.game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired private PlayerItemDao playerItemDao;
    @Autowired private ItemDao itemDao;
    @Autowired private GameService gameService;

    @GetMapping("/list")
    public ResponseEntity<List<InventoryItemDto>> getList(
            @RequestParam Long playerId, @RequestParam String location) {
        
        List<PlayerItem> items = playerItemDao.findByPlayerIdAndLocation(playerId, location);
        
        List<InventoryItemDto> dtoList = items.stream().map(pi -> {
            InventoryItemDto dto = new InventoryItemDto();
            Item t = itemDao.findById(pi.getItemId()).orElse(null);
            dto.setId(pi.getId());
            dto.setItemName(t != null ? t.getName() : "未知物品");
            dto.setInstanceAtk(pi.getInstanceAtk());
            dto.setInstanceDef(pi.getInstanceDef());
            dto.setInstanceHp(pi.getInstanceHp());
            dto.setEquipped(pi.isEquipped());
            dto.setSlot(t != null ? t.getSlot() : "NONE");
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @DeleteMapping("/sell/{playerId}/{pItemId}")
    public ResponseEntity<?> sell(@PathVariable Long playerId, @PathVariable Long pItemId) {
        gameService.sellItem(playerId, pItemId);
        return ResponseEntity.ok(Map.of("message", "出售成功"));
    }
}