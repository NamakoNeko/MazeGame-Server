package com.javaclass.game.controller;

import com.javaclass.game.constants.PlayerItemDefiner;
import com.javaclass.game.dto.EquipRequest;
import com.javaclass.game.dto.EquipmentResult;
import com.javaclass.game.dto.UnequipRequest;
import com.javaclass.game.service.PlayerEquipmentService;
import com.javaclass.game.utility.ApiResponse;
import com.javaclass.game.utility.JwtUtility;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(PlayerItemDefiner.EQUIPMENT_BASE_URL)
public class PlayerEquipmentController {

    private final PlayerEquipmentService playerEquipmentService;
    private final JwtUtility jwtUtility;

    public PlayerEquipmentController(PlayerEquipmentService playerEquipmentService, JwtUtility jwtUtility) {
        this.playerEquipmentService = playerEquipmentService;
        this.jwtUtility = jwtUtility;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getEquipment(
        @RequestHeader("Authorization") String authorizationHeader
    ) {
        Long playerId = jwtUtility.extractPlayerId(jwtUtility.extractToken(authorizationHeader));
        try {
            EquipmentResult equipmentResult = playerEquipmentService.getEquipment(playerId);
            return ResponseEntity.ok(ApiResponse.success(equipmentResult));
        } catch (IllegalArgumentException illegalArgumentException) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, illegalArgumentException.getMessage()));
        }
    }

    @PostMapping(PlayerItemDefiner.EQUIP_PATH)
    public ResponseEntity<ApiResponse<?>> equip(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestBody EquipRequest equipRequest
    ) {
        if (equipRequest.getItemId() == null) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, PlayerItemDefiner.ERROR_ITEM_ID_REQUIRED));
        }

        if (equipRequest.getLocation() == null || equipRequest.getPosition() == null) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, PlayerItemDefiner.ERROR_POSITION_REQUIRED));
        }

        try {
            Long playerId = jwtUtility.extractPlayerId(jwtUtility.extractToken(authorizationHeader));
            EquipmentResult equipmentResult = playerEquipmentService.equip(playerId, equipRequest);
            return ResponseEntity.ok(ApiResponse.success(equipmentResult));
        } catch (IllegalArgumentException illegalArgumentException) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, illegalArgumentException.getMessage()));
        }
    }

    @PostMapping(PlayerItemDefiner.UNEQUIP_PATH)
    public ResponseEntity<ApiResponse<?>> unequip(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestBody UnequipRequest unequipRequest
    ) {
        if (unequipRequest.getItemId() == null) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, PlayerItemDefiner.ERROR_ITEM_ID_REQUIRED));
        }

        if (unequipRequest.getTargetLocation() == null || unequipRequest.getTargetPosition() == null) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, PlayerItemDefiner.ERROR_POSITION_REQUIRED));
        }

        try {
            Long playerId = jwtUtility.extractPlayerId(jwtUtility.extractToken(authorizationHeader));
            EquipmentResult equipmentResult = playerEquipmentService.unequip(playerId, unequipRequest);
            return ResponseEntity.ok(ApiResponse.success(equipmentResult));
        } catch (IllegalArgumentException illegalArgumentException) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, illegalArgumentException.getMessage()));
        }
    }
}