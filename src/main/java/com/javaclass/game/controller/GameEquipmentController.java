package com.javaclass.game.controller;

import com.javaclass.game.constants.GameApiDefiner;
import com.javaclass.game.dto.EquipItemRequest;
import com.javaclass.game.dto.UnequipItemRequest;
import com.javaclass.game.service.GameEquipmentService;
import com.javaclass.game.utility.ApiResponse;
import com.javaclass.game.utility.JwtUtility;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(GameApiDefiner.EQUIPMENT_BASE_URL)
public class GameEquipmentController {

    private final GameEquipmentService gameEquipmentService;
    private final JwtUtility jwtUtility;

    public GameEquipmentController(GameEquipmentService gameEquipmentService, JwtUtility jwtUtility) {
        this.gameEquipmentService = gameEquipmentService;
        this.jwtUtility = jwtUtility;
    }

    @PostMapping(GameApiDefiner.EQUIP_PATH)
    public ResponseEntity<ApiResponse<?>> equip(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestBody EquipItemRequest request
    ) {
        if (request.getPlayerItemId() == null) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(400, GameApiDefiner.ERROR_ITEM_NOT_FOUND));
        }
        try {
            Long playerId = jwtUtility.extractPlayerId(jwtUtility.extractToken(authorizationHeader));
            return ResponseEntity.ok(ApiResponse.success(gameEquipmentService.equip(playerId, request)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(400, exception.getMessage()));
        }
    }

    @PostMapping(GameApiDefiner.UNEQUIP_PATH)
    public ResponseEntity<ApiResponse<?>> unequip(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestBody UnequipItemRequest request
    ) {
        try {
            Long playerId = jwtUtility.extractPlayerId(jwtUtility.extractToken(authorizationHeader));
            return ResponseEntity.ok(ApiResponse.success(gameEquipmentService.unequip(playerId, request)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(400, exception.getMessage()));
        }
    }
}
