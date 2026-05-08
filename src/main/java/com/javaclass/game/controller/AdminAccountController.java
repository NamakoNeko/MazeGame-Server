package com.javaclass.game.controller;

import com.javaclass.game.constants.MenuPermissionDefiner.RoleLevel;
import com.javaclass.game.dto.CreateAdminAccountRequest;
import com.javaclass.game.service.AdminAccountService;
import com.javaclass.game.utility.ApiResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/admin/accounts")
@PreAuthorize("hasRole('SuperAdmin')")
public class AdminAccountController {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final AdminAccountService adminAccountService;

    public AdminAccountController(AdminAccountService adminAccountService) {
        this.adminAccountService = adminAccountService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAccountList(
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(adminAccountService.getAccountList(keyword, pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createAccount(@RequestBody CreateAdminAccountRequest request) {
        if (request.getAccount() == null || request.getAccount().isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(400, "帳號為必填欄位"));
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(400, "密碼為必填欄位"));
        }
        if (request.getRole() == null || Arrays.stream(RoleLevel.values()).noneMatch(role -> role.name().equals(request.getRole()))) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(400, "角色值無效"));
        }

        try {
            return ResponseEntity.ok(ApiResponse.success(adminAccountService.createAccount(request)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(400, exception.getMessage()));
        }
    }
}
