package com.javaclass.game.service;

import com.javaclass.game.constants.MenuPermissionDefiner.RoleLevel;
import com.javaclass.game.dao.AdminDao;
import com.javaclass.game.dto.AdminAccountResult;
import com.javaclass.game.dto.CreateAdminAccountRequest;
import com.javaclass.game.model.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminAccountService {

    private final AdminDao adminDao;
    private final BCryptPasswordEncoder passwordEncoder;
    private final OperationLogService operationLogService;

    public AdminAccountService(
        AdminDao adminDao,
        BCryptPasswordEncoder passwordEncoder,
        OperationLogService operationLogService
    ) {
        this.adminDao = adminDao;
        this.passwordEncoder = passwordEncoder;
        this.operationLogService = operationLogService;
    }

    public Page<AdminAccountResult> getAccountList(String keyword, Pageable pageable) {
        Page<Admin> adminPage = adminDao.findByKeyword(keyword, pageable);
        List<AdminAccountResult> resultList = adminPage.getContent().stream()
            .map(this::toResult)
            .toList();
        return new PageImpl<>(resultList, pageable, adminPage.getTotalElements());
    }

    @Transactional
    public AdminAccountResult createAccount(CreateAdminAccountRequest request) {
        if (adminDao.existsByAccount(request.getAccount())) {
            throw new IllegalArgumentException("管理員帳號已存在");
        }

        Admin admin = new Admin();
        admin.setAccount(request.getAccount().trim());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setRole(RoleLevel.valueOf(request.getRole()).name());
        admin.setCreatedAt(LocalDateTime.now());

        Admin savedAdmin = adminDao.save(admin);
        operationLogService.record("CREATE_ADMIN_ACCOUNT", "ADMIN", String.valueOf(savedAdmin.getId()), savedAdmin.getAccount());
        return toResult(savedAdmin);
    }

    private AdminAccountResult toResult(Admin admin) {
        return AdminAccountResult.builder()
            .id(admin.getId())
            .account(admin.getAccount())
            .role(admin.getRole())
            .createdAt(admin.getCreatedAt())
            .build();
    }
}
