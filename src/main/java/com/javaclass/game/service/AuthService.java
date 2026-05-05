package com.javaclass.game.service;

import com.javaclass.game.constants.AuthDefiner;
import com.javaclass.game.constants.MenuPermissionDefiner.RoleLevel;
import com.javaclass.game.dao.AdminDao;
import com.javaclass.game.model.Admin;
import com.javaclass.game.model.LoginResponse;
import com.javaclass.game.utility.JwtUtility;
import com.javaclass.game.utility.MenuPermissionUtility;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final AdminDao adminDao;
    private final JwtUtility jwtUtility;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MenuPermissionUtility menuPermissionUtility;

    public AuthService(
        AdminDao adminDao,
        JwtUtility jwtUtility,
        BCryptPasswordEncoder passwordEncoder,
        MenuPermissionUtility menuPermissionUtility
    ) {
        this.adminDao = adminDao;
        this.jwtUtility = jwtUtility;
        this.passwordEncoder = passwordEncoder;
        this.menuPermissionUtility = menuPermissionUtility;
    }

    public LoginResponse login(String account, String password) {
        Admin admin = adminDao.findByAccount(account)
            .orElseThrow(() -> new IllegalArgumentException("帳號或密碼錯誤"));

        boolean isPasswordCorrect = passwordEncoder.matches(password, admin.getPassword());
        if (!isPasswordCorrect) {
            throw new IllegalArgumentException("帳號或密碼錯誤");
        }

        String token = jwtUtility.generateToken(admin.getId(), admin.getRole());
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(AuthDefiner.TOKEN_VALID_HOURS);
        RoleLevel userRoleLevel = RoleLevel.valueOf(admin.getRole());

        return LoginResponse.builder()
            .token(token)
            .role(admin.getRole())
            .expiresAt(expiresAt)
            .menuList(menuPermissionUtility.buildFullMenuList(userRoleLevel))
            .build();
    }
}