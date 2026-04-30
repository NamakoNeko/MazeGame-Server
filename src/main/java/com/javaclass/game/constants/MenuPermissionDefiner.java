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

    public static final int TOKEN_VALID_HOURS   = 8;

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