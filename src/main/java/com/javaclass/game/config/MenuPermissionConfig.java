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
                "player", "玩家管理", "/players", RoleLevel.Operator,
                Map.of(
                    MenuPermissionDefiner.QUERY_TEXT,  RoleLevel.Operator,
                    MenuPermissionDefiner.EDIT_TEXT,   RoleLevel.Admin,
                    MenuPermissionDefiner.BAN_TEXT,    RoleLevel.Admin,
                    MenuPermissionDefiner.UNBAN_TEXT,  RoleLevel.Admin
                )
            ),

            new MenuItemConfig(
                "item", "道具管理", "/items", RoleLevel.Admin,
                Map.of(
                    MenuPermissionDefiner.QUERY_TEXT,  RoleLevel.Admin,
                    MenuPermissionDefiner.CREATE_TEXT, RoleLevel.SuperAdmin,
                    MenuPermissionDefiner.EDIT_TEXT,   RoleLevel.Admin,
                    MenuPermissionDefiner.DELETE_TEXT, RoleLevel.SuperAdmin
                )
            ),

            new MenuItemConfig(
                "inventory", "背包管理", "/inventory", RoleLevel.Operator,
                Map.of(
                    MenuPermissionDefiner.QUERY_TEXT,  RoleLevel.Operator,
                    MenuPermissionDefiner.GRANT_TEXT,  RoleLevel.Admin,
                    MenuPermissionDefiner.REMOVE_TEXT, RoleLevel.Admin
                )
            ),

            new MenuItemConfig(
                "operationLog", "操作紀錄", "/logs", RoleLevel.Operator,
                Map.of(
                    MenuPermissionDefiner.QUERY_SELF_TEXT, RoleLevel.Operator,
                    MenuPermissionDefiner.QUERY_ALL_TEXT,  RoleLevel.Admin
                )
            ),

            new MenuItemConfig(
                "accountManagement", "帳號管理", "/accounts", RoleLevel.SuperAdmin,
                Map.of(
                    MenuPermissionDefiner.CREATE_TEXT, RoleLevel.SuperAdmin
                )
            )
        );
    }
}