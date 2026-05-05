package com.javaclass.game.config;

import com.javaclass.game.constants.MenuPermissionDefiner;
import com.javaclass.game.constants.MenuPermissionDefiner.RoleLevel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MenuPermissionConfig {

    public List<MenuItemConfig> getMenuList() {
        return List.of(

            new MenuItemConfig(
                MenuPermissionDefiner.PLAYER_MENU_KEY,
                MenuPermissionDefiner.PLAYER_MENU_NAME,
                MenuPermissionDefiner.PLAYER_PATH,
                RoleLevel.Operator,
                Map.of(
                    MenuPermissionDefiner.QUERY_TEXT,  RoleLevel.Operator,
                    MenuPermissionDefiner.EDIT_TEXT,   RoleLevel.Admin,
                    MenuPermissionDefiner.BAN_TEXT,    RoleLevel.Admin,
                    MenuPermissionDefiner.UNBAN_TEXT,  RoleLevel.Admin
                )
            ),

            new MenuItemConfig(
                MenuPermissionDefiner.ITEM_MENU_KEY,
                MenuPermissionDefiner.ITEM_MENU_NAME,
                MenuPermissionDefiner.ITEM_PATH,
                RoleLevel.Admin,
                Map.of(
                    MenuPermissionDefiner.QUERY_TEXT,  RoleLevel.Admin,
                    MenuPermissionDefiner.CREATE_TEXT, RoleLevel.SuperAdmin,
                    MenuPermissionDefiner.EDIT_TEXT,   RoleLevel.Admin,
                    MenuPermissionDefiner.DELETE_TEXT, RoleLevel.SuperAdmin
                )
            ),

            new MenuItemConfig(
                MenuPermissionDefiner.INVENTORY_MENU_KEY,
                MenuPermissionDefiner.INVENTORY_MENU_NAME,
                MenuPermissionDefiner.INVENTORY_PATH,
                RoleLevel.Operator,
                Map.of(
                    MenuPermissionDefiner.QUERY_TEXT,  RoleLevel.Operator,
                    MenuPermissionDefiner.GRANT_TEXT,  RoleLevel.Admin,
                    MenuPermissionDefiner.REMOVE_TEXT, RoleLevel.Admin
                )
            ),

            new MenuItemConfig(
                MenuPermissionDefiner.OPERATION_LOG_MENU_KEY,
                MenuPermissionDefiner.OPERATION_LOG_MENU_NAME,
                MenuPermissionDefiner.OPERATION_LOG_PATH,
                RoleLevel.Operator,
                Map.of(
                    MenuPermissionDefiner.QUERY_SELF_TEXT, RoleLevel.Operator,
                    MenuPermissionDefiner.QUERY_ALL_TEXT,  RoleLevel.Admin
                )
            ),

            new MenuItemConfig(
                MenuPermissionDefiner.ACCOUNT_MANAGEMENT_MENU_KEY,
                MenuPermissionDefiner.ACCOUNT_MANAGEMENT_MENU_NAME,
                MenuPermissionDefiner.ACCOUNT_MANAGEMENT_PATH,
                RoleLevel.SuperAdmin,
                Map.of(
                    MenuPermissionDefiner.CREATE_TEXT, RoleLevel.SuperAdmin
                )
            )
        );
    }
}