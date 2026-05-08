package com.javaclass.game.controller;

import com.javaclass.game.constants.InventoryDefiner;
import com.javaclass.game.dao.PlayerDao;
import com.javaclass.game.dto.GrantItemRequest;
import com.javaclass.game.dto.InventoryItemResult;
import com.javaclass.game.dto.RemoveItemRequest;
import com.javaclass.game.service.InventoryService;
import com.javaclass.game.utility.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(InventoryDefiner.INVENTORY_BASE_URL)
public class AdminInventoryController {

    private final InventoryService inventoryService;
    private final PlayerDao playerDao;

    public AdminInventoryController(InventoryService inventoryService, PlayerDao playerDao) {
        this.inventoryService = inventoryService;
        this.playerDao = playerDao;
    }

    private Long resolvePlayerId(String accountId) {
        return playerDao.findByAccountId(accountId)
            .orElseThrow(() -> new IllegalArgumentException(InventoryDefiner.ERROR_PLAYER_NOT_FOUND))
            .getId();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getInventory(
        @PathVariable String accountId,
        @RequestParam(defaultValue = "" + InventoryDefiner.DEFAULT_PAGE)      int page,
        @RequestParam(defaultValue = "" + InventoryDefiner.DEFAULT_PAGE_SIZE) int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<InventoryItemResult> inventoryPage = inventoryService.getInventory(resolvePlayerId(accountId), pageable);
            return ResponseEntity.ok(ApiResponse.success(inventoryPage));
        } catch (IllegalArgumentException illegalArgumentException) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, illegalArgumentException.getMessage()));
        }
    }

    @PostMapping(InventoryDefiner.GRANT_PATH)
    public ResponseEntity<ApiResponse<?>> grantItem(
        @PathVariable String accountId,
        @RequestBody GrantItemRequest grantItemRequest
    ) {
        if (grantItemRequest.getItemId() == null) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, InventoryDefiner.ERROR_ITEM_ID_REQUIRED));
        }

        if (grantItemRequest.getQuantity() == null) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, InventoryDefiner.ERROR_QUANTITY_REQUIRED));
        }

        if (grantItemRequest.getQuantity() <= 0) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, InventoryDefiner.ERROR_QUANTITY_INVALID));
        }

        try {
            inventoryService.grantItem(resolvePlayerId(accountId), grantItemRequest);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException illegalArgumentException) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, illegalArgumentException.getMessage()));
        }
    }

    @PostMapping(InventoryDefiner.REMOVE_PATH)
    public ResponseEntity<ApiResponse<?>> removeItem(
        @PathVariable String accountId,
        @RequestBody RemoveItemRequest removeItemRequest
    ) {
        if (removeItemRequest.getItemId() == null) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, InventoryDefiner.ERROR_ITEM_ID_REQUIRED));
        }

        if (removeItemRequest.getQuantity() == null) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, InventoryDefiner.ERROR_QUANTITY_REQUIRED));
        }

        if (removeItemRequest.getQuantity() <= 0) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, InventoryDefiner.ERROR_QUANTITY_INVALID));
        }

        try {
            inventoryService.removeItem(resolvePlayerId(accountId), removeItemRequest);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException illegalArgumentException) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, illegalArgumentException.getMessage()));
        }
    }
}
