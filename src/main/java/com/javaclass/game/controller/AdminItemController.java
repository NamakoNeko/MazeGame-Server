package com.javaclass.game.controller;

import com.javaclass.game.dto.UpsertItemRequest;
import com.javaclass.game.service.ItemService;
import com.javaclass.game.utility.ApiResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/items")
public class AdminItemController {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final ItemService itemService;

    public AdminItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    @PreAuthorize("hasRole('SuperAdmin') or hasRole('Admin')")
    public ResponseEntity<ApiResponse<?>> getItemList(
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(itemService.getItemList(keyword, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SuperAdmin') or hasRole('Admin')")
    public ResponseEntity<ApiResponse<?>> getItem(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(itemService.getItem(id)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(404, exception.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('SuperAdmin')")
    public ResponseEntity<ApiResponse<?>> createItem(@RequestBody UpsertItemRequest request) {
        ResponseEntity<ApiResponse<?>> invalidResponse = validateRequest(request);
        if (invalidResponse != null) {
            return invalidResponse;
        }

        return ResponseEntity.ok(ApiResponse.success(itemService.createItem(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SuperAdmin') or hasRole('Admin')")
    public ResponseEntity<ApiResponse<?>> updateItem(
        @PathVariable Long id,
        @RequestBody UpsertItemRequest request
    ) {
        ResponseEntity<ApiResponse<?>> invalidResponse = validateRequest(request);
        if (invalidResponse != null) {
            return invalidResponse;
        }

        try {
            return ResponseEntity.ok(ApiResponse.success(itemService.updateItem(id, request)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(400, exception.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SuperAdmin')")
    public ResponseEntity<ApiResponse<?>> deleteItem(@PathVariable Long id) {
        try {
            itemService.deleteItem(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(400, exception.getMessage()));
        }
    }

    private ResponseEntity<ApiResponse<?>> validateRequest(UpsertItemRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(400, "道具名稱為必填欄位"));
        }
        if (request.getType() == null) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(400, "道具類型為必填欄位"));
        }
        if (request.getMaxAmount() == null || request.getMaxAmount() <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(400, "堆疊上限必須大於 0"));
        }
        return null;
    }
}
