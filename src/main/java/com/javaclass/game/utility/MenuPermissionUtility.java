package com.javaclass.game.utility;

import com.javaclass.game.config.MenuItemConfig;
import com.javaclass.game.config.MenuPermissionConfig;
import com.javaclass.game.constants.MenuPermissionDefiner.RoleLevel;
import com.javaclass.game.dto.MenuItemResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MenuPermissionUtility {

    private final MenuPermissionConfig menuPermissionConfig;

    public MenuPermissionUtility(MenuPermissionConfig menuPermissionConfig) {
        this.menuPermissionConfig = menuPermissionConfig;
    }

    public List<MenuItemResult> buildFullMenuList(RoleLevel userRoleLevel) {
        return menuPermissionConfig.getMenuList().stream()
            .map(menuItem -> buildMenuItemResult(menuItem, userRoleLevel))
            .toList();
    }

    private MenuItemResult buildMenuItemResult(MenuItemConfig menuItem, RoleLevel userRoleLevel) {
        boolean isMenuEnabled = menuItem.getRequiredLevel().isGrantedTo(userRoleLevel);

        Map<String, Boolean> resolvedOperations = new HashMap<>();
        menuItem.getOperations().forEach((operationKey, requiredLevel) ->
            resolvedOperations.put(operationKey, requiredLevel.isGrantedTo(userRoleLevel))
        );

        return new MenuItemResult(
            menuItem.getMenuKey(),
            menuItem.getMenuName(),
            menuItem.getPath(),
            isMenuEnabled,
            resolvedOperations
        );
    }
}