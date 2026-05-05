package com.javaclass.game.constants;

public class AuthDefiner {

    public static final int    TOKEN_VALID_HOURS               = 8;

    public static final String ADMIN_AUTH_BASE_URL             = "/api/admin/auth";
    public static final String PLAYER_AUTH_BASE_URL            = "/api/player/auth";
    public static final String LOGIN_PATH                      = "/login";
    public static final String REGISTER_PATH                   = "/register";

    public static final String LOGIN_URL                       = ADMIN_AUTH_BASE_URL + LOGIN_PATH;
    public static final String PLAYER_LOGIN_URL                = PLAYER_AUTH_BASE_URL + LOGIN_PATH;
    public static final String PLAYER_REGISTER_URL             = PLAYER_AUTH_BASE_URL + REGISTER_PATH;

    public static final String ERROR_ACCOUNT_REQUIRED          = "帳號為必填欄位";
    public static final String ERROR_PASSWORD_REQUIRED         = "密碼為必填欄位";
    public static final String ERROR_NICKNAME_REQUIRED         = "暱稱為必填欄位";
    public static final String ERROR_EMAIL_REQUIRED            = "Email為必填欄位";
    public static final String ERROR_INVALID_CREDENTIALS       = "帳號或密碼錯誤";
    public static final String ERROR_ACCOUNT_ALREADY_EXISTS    = "帳號已被使用";
}