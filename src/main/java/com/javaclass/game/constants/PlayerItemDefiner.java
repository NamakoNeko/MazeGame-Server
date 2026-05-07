package com.javaclass.game.constants;

public class PlayerItemDefiner {

    public static final int ITEM_TYPE_EQUIPMENT  = 1;
    public static final int ITEM_TYPE_CONSUMABLE = 2;

    public static final String STORAGE_BASE_URL  = "/api/game/storage";
    public static final String GAIN_PATH         = "/gain";
    public static final String CONSUME_PATH      = "/consume";
    public static final String MOVE_PATH         = "/move";

    public static final int LOCATION_INVENTORY   = 1;
    public static final int LOCATION_WAREHOUSE   = 2;

    public static final String ERROR_PLAYER_NOT_FOUND       = "玩家不存在";
    public static final String ERROR_ITEM_NOT_FOUND         = "道具不存在";
    public static final String ERROR_ITEM_NOT_CONSUMABLE    = "此道具無法消耗";
    public static final String ERROR_AMOUNT_INVALID         = "數量必須大於 0";
    public static final String ERROR_AMOUNT_NOT_ENOUGH      = "道具數量不足";
    public static final String ERROR_MAX_AMOUNT_EXCEEDED    = "超過道具持有上限";
    public static final String ERROR_AMOUNT_REQUIRED        = "數量為必填欄位";
    public static final String ERROR_ITEM_ID_REQUIRED       = "道具 ID 為必填欄位";
    public static final String ERROR_POSITION_REQUIRED      = "格子位置為必填欄位";
    public static final String ERROR_LOCATION_REQUIRED      = "位置類型為必填欄位";
}