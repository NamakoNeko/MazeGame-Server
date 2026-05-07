package com.javaclass.game.controller;

import com.javaclass.game.constants.PlayerItemDefiner;
import com.javaclass.game.dto.ConsumePlayerItemRequest;
import com.javaclass.game.dto.ConsumePlayerItemResponse;
import com.javaclass.game.dto.GainPlayerItemRequest;
import com.javaclass.game.dto.GainPlayerItemResponse;
import com.javaclass.game.dto.MovePlayerItemRequest;
import com.javaclass.game.dto.MovePlayerItemResponse;
import com.javaclass.game.dto.PlayerItemResult;
import com.javaclass.game.service.PlayerItemService;
import com.javaclass.game.utility.ApiResponse;
import com.javaclass.game.utility.JwtUtility;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(PlayerItemDefiner.STORAGE_BASE_URL)
public class PlayerItemController {

    private final PlayerItemService playerItemsService;
    private final JwtUtility jwtUtility;

    public PlayerItemController(PlayerItemService playerItemsService, JwtUtility jwtUtility) {
        this.playerItemsService = playerItemsService;
        this.jwtUtility = jwtUtility;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getPlayerItems(
        @RequestHeader("Authorization") String authorizationHeader
    ) {
        String accountId = jwtUtility.extractPlayerAccountId(jwtUtility.extractToken(authorizationHeader));
        try {
            List<PlayerItemResult> playerItemsList = playerItemsService.getPlayerItemsByAccountId(accountId);
            return ResponseEntity.ok(ApiResponse.success(playerItemsList));
        } catch (IllegalArgumentException illegalArgumentException) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, illegalArgumentException.getMessage()));
        }
    }

    @PostMapping(PlayerItemDefiner.GAIN_PATH)
    public ResponseEntity<ApiResponse<?>> gainItems(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestBody GainPlayerItemRequest gainPlayerItemRequest
    ) {
        if (gainPlayerItemRequest.getItems() == null || gainPlayerItemRequest.getItems().isEmpty()) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, PlayerItemDefiner.ERROR_ITEM_ID_REQUIRED));
        }

        try {
            String accountId = jwtUtility.extractPlayerAccountId(jwtUtility.extractToken(authorizationHeader));
            GainPlayerItemResponse response = playerItemsService.gainItems(accountId, gainPlayerItemRequest);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException illegalArgumentException) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, illegalArgumentException.getMessage()));
        }
    }

    @PostMapping(PlayerItemDefiner.CONSUME_PATH)
    public ResponseEntity<ApiResponse<?>> consumeItems(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestBody ConsumePlayerItemRequest consumePlayerItemRequest
    ) {
        if (consumePlayerItemRequest.getItems() == null || consumePlayerItemRequest.getItems().isEmpty()) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, PlayerItemDefiner.ERROR_ITEM_ID_REQUIRED));
        }

        try {
            String accountId = jwtUtility.extractPlayerAccountId(jwtUtility.extractToken(authorizationHeader));
            ConsumePlayerItemResponse response = playerItemsService.consumeItems(accountId, consumePlayerItemRequest);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException illegalArgumentException) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, illegalArgumentException.getMessage()));
        }
    }

    @PostMapping(PlayerItemDefiner.MOVE_PATH)
    public ResponseEntity<ApiResponse<?>> moveItem(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestBody MovePlayerItemRequest movePlayerItemRequest
    ) {
        if (movePlayerItemRequest.getItemId() == null) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, PlayerItemDefiner.ERROR_ITEM_ID_REQUIRED));
        }

        if (movePlayerItemRequest.getAmount() == null || movePlayerItemRequest.getAmount() <= 0) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, PlayerItemDefiner.ERROR_AMOUNT_INVALID));
        }

        if (movePlayerItemRequest.getBeforeLocation() == null || movePlayerItemRequest.getBeforePosition() == null) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, PlayerItemDefiner.ERROR_POSITION_REQUIRED));
        }

        if (movePlayerItemRequest.getAfterLocation() == null || movePlayerItemRequest.getAfterPosition() == null) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, PlayerItemDefiner.ERROR_POSITION_REQUIRED));
        }

        try {
            String accountId = jwtUtility.extractPlayerAccountId(jwtUtility.extractToken(authorizationHeader));
            MovePlayerItemResponse response = playerItemsService.moveItem(accountId, movePlayerItemRequest);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException illegalArgumentException) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, illegalArgumentException.getMessage()));
        }
    }
}