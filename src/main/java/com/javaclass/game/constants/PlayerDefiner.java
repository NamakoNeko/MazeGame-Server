package com.javaclass.game.constants;

public class PlayerDefiner {

    public static final String PLAYER_BASE_URL             = "/api/players";
    public static final String PLAYER_ACCOUNT_ID_PATH      = "/{accountId}";
    public static final String PLAYER_ID_PATH              = "/{id}";
    public static final String PLAYER_STATUS_PATH          = "/{accountId}/status";

    public static final String STATUS_ACTIVE               = "ACTIVE";
    public static final String STATUS_BANNED               = "BANNED";

    public static final int    DEFAULT_PAGE                = 0;
    public static final int    DEFAULT_PAGE_SIZE           = 20;

    public static final String ERROR_PLAYER_NOT_FOUND      = "玩家不存在";
    public static final String ERROR_NICKNAME_REQUIRED     = "暱稱為必填欄位";
    public static final String ERROR_LEVEL_INVALID         = "等級必須大於 0";
    public static final String ERROR_STATUS_INVALID        = "狀態值無效，允許值為 ACTIVE 或 BANNED";
}