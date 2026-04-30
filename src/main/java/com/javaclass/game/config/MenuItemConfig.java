package com.javaclass.game.config;

import com.javaclass.game.constants.MenuPermissionDefiner.RoleLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class MenuItemConfig {

    private final String menuKey;
    private final String menuName;
    private final String path;
    private final RoleLevel requiredLevel;
    private final Map<String, RoleLevel> operations;
}