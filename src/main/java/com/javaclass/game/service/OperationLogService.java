package com.javaclass.game.service;

import com.javaclass.game.dao.AdminDao;
import com.javaclass.game.dao.OperationLogDao;
import com.javaclass.game.dto.OperationLogResult;
import com.javaclass.game.model.Admin;
import com.javaclass.game.model.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationLogService {

    private final OperationLogDao operationLogDao;
    private final AdminDao adminDao;

    public OperationLogService(OperationLogDao operationLogDao, AdminDao adminDao) {
        this.operationLogDao = operationLogDao;
        this.adminDao = adminDao;
    }

    public void record(String action, String targetType, String targetId, String detail) {
        Long adminId = getCurrentAdminId();
        Admin admin = adminDao.findById(adminId)
            .orElseThrow(() -> new IllegalArgumentException("管理員不存在"));

        OperationLog operationLog = new OperationLog();
        operationLog.setAdminId(admin.getId());
        operationLog.setAdminAccount(admin.getAccount());
        operationLog.setAction(action);
        operationLog.setTargetType(targetType);
        operationLog.setTargetId(targetId);
        operationLog.setDetail(detail);
        operationLogDao.save(operationLog);
    }

    public Page<OperationLogResult> getLogs(boolean includeAll, Pageable pageable) {
        boolean canQueryAll = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(authority -> authority.equals("ROLE_Admin") || authority.equals("ROLE_SuperAdmin"));
        boolean shouldIncludeAll = includeAll && canQueryAll;

        Page<OperationLog> logPage = shouldIncludeAll
            ? operationLogDao.findAll(pageable)
            : operationLogDao.findByAdminId(getCurrentAdminId(), pageable);

        List<OperationLogResult> resultList = logPage.getContent().stream()
            .map(this::toResult)
            .toList();

        return new PageImpl<>(resultList, pageable, logPage.getTotalElements());
    }

    private Long getCurrentAdminId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Long adminId) {
            return adminId;
        }
        if (principal instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(String.valueOf(principal));
    }

    private OperationLogResult toResult(OperationLog operationLog) {
        return OperationLogResult.builder()
            .id(operationLog.getId())
            .adminId(operationLog.getAdminId())
            .adminAccount(operationLog.getAdminAccount())
            .action(operationLog.getAction())
            .targetType(operationLog.getTargetType())
            .targetId(operationLog.getTargetId())
            .detail(operationLog.getDetail())
            .createdAt(operationLog.getCreatedAt())
            .build();
    }
}
