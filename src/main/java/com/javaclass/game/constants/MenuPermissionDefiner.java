package com.javaclass.game.constants;

public class MenuPermissionDefiner {

    public enum RoleLevel {
        Operator,
        Admin,
        SuperAdmin;

        public boolean isGrantedTo(RoleLevel userLevel) {
            return userLevel.ordinal() >= this.ordinal();
        }
    }

    // ── 選單 Key ──
    public static final String PLAYER_MENU_KEY              = "player";
    public static final String ITEM_MENU_KEY                = "item";
    public static final String INVENTORY_MENU_KEY           = "inventory";
    public static final String OPERATION_LOG_MENU_KEY       = "operationLog";
    public static final String ACCOUNT_MANAGEMENT_MENU_KEY  = "accountManagement";

    // ── 選單名稱 ──
    public static final String PLAYER_MENU_NAME             = "玩家管理";
    public static final String ITEM_MENU_NAME               = "道具管理";
    public static final String INVENTORY_MENU_NAME          = "背包管理";
    public static final String OPERATION_LOG_MENU_NAME      = "操作紀錄";
    public static final String ACCOUNT_MANAGEMENT_MENU_NAME = "帳號管理";

    // ── 選單路徑 ──
    public static final String PLAYER_PATH                  = "/players";
    public static final String ITEM_PATH                    = "/items";
    public static final String INVENTORY_PATH               = "/inventory";
    public static final String OPERATION_LOG_PATH           = "/logs";
    public static final String ACCOUNT_MANAGEMENT_PATH      = "/accounts";

    // ── 操作 Key ──
    public static final String QUERY_TEXT       = "query";
    public static final String EDIT_TEXT        = "edit";
    public static final String BAN_TEXT         = "ban";
    public static final String UNBAN_TEXT       = "unban";
    public static final String CREATE_TEXT      = "create";
    public static final String DELETE_TEXT      = "delete";
    public static final String GRANT_TEXT       = "grant";
    public static final String REMOVE_TEXT      = "remove";
    public static final String QUERY_SELF_TEXT  = "querySelf";
    public static final String QUERY_ALL_TEXT   = "queryAll";
}