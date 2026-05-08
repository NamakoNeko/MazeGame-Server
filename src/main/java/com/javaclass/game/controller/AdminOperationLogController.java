package com.javaclass.game.controller;

import com.javaclass.game.service.OperationLogService;
import com.javaclass.game.utility.ApiResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/logs")
public class AdminOperationLogController {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final OperationLogService operationLogService;

    public AdminOperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @GetMapping
    @PreAuthorize("hasRole('SuperAdmin') or hasRole('Admin') or hasRole('Operator')")
    public ResponseEntity<ApiResponse<?>> getLogs(
        @RequestParam(defaultValue = "false") boolean includeAll,
        @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiResponse.success(operationLogService.getLogs(includeAll, pageable)));
    }
}
