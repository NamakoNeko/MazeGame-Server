package com.javaclass.game.constants;

public class InventoryDefiner {

    public static final String INVENTORY_BASE_URL        = "/api/players/{accountId}/inventory";
    public static final String GRANT_PATH                = "/grant";
    public static final String REMOVE_PATH               = "/remove";

    public static final int    DEFAULT_PAGE              = 0;
    public static final int    DEFAULT_PAGE_SIZE         = 20;

    public static final String ERROR_ITEM_NOT_FOUND      = "道具不存在";
    public static final String ERROR_PLAYER_NOT_FOUND    = "玩家不存在";
    public static final String ERROR_ITEM_ID_REQUIRED    = "道具 ID 為必填欄位";
    public static final String ERROR_QUANTITY_REQUIRED   = "數量為必填欄位";
    public static final String ERROR_QUANTITY_INVALID    = "數量必須大於 0";
    public static final String ERROR_QUANTITY_NOT_ENOUGH = "背包中道具數量不足";
}