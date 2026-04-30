package com.javaclass.game.model;

import com.javaclass.game.dto.MenuItemResult;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class LoginResponse {

    private String token;
    private String role;
    private LocalDateTime expiresAt;
    private List<MenuItemResult> menuList;
}