package com.javaclass.game.constants;

public class PlayerItemDefiner {

    public enum ItemType {
        HEAD(1),
        CHEST(2),
        WEAPON(3),
        OFF_HAND(4),
        SHOES(5),
        CONSUMABLE(6);

        private final int value;

        ItemType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static ItemType fromValue(int value) {
            for (ItemType itemType : values()) {
                if (itemType.value == value) {
                    return itemType;
                }
            }
            throw new IllegalArgumentException("無效的道具類型：" + value);
        }

        public boolean isEquipment() {
            return this != CONSUMABLE;
        }
    }

    public static final String STORAGE_BASE_URL  = "/api/game/storage";
    public static final String GAIN_PATH         = "/gain";
    public static final String CONSUME_PATH      = "/consume";
    public static final String MOVE_PATH         = "/move";
    public static final String SELL_PATH         = "/sell";
    public static final String CLEAR_LOCATION_PATH = "/location/{location}";
    public static final String REPLACE_LOCATION_PATH = "/location/{location}/replace";

    public static final String EQUIPMENT_BASE_URL = "/api/game/equipment";
    public static final String EQUIP_PATH          = "/equip";
    public static final String UNEQUIP_PATH        = "/unequip";

    public static final int LOCATION_INVENTORY   = 1;
    public static final int LOCATION_WAREHOUSE   = 2;

    public static final String ERROR_PLAYER_NOT_FOUND        = "玩家不存在";
    public static final String ERROR_ITEM_NOT_FOUND          = "道具不存在";
    public static final String ERROR_ITEM_NOT_CONSUMABLE     = "此道具無法消耗";
    public static final String ERROR_ITEM_NOT_EQUIPMENT      = "此道具不是裝備";
    public static final String ERROR_ITEM_NOT_IN_INVENTORY   = "背包中沒有此道具";
    public static final String ERROR_AMOUNT_INVALID          = "數量必須大於 0";
    public static final String ERROR_AMOUNT_NOT_ENOUGH       = "道具數量不足";
    public static final String ERROR_MAX_AMOUNT_EXCEEDED     = "超過道具持有上限";
    public static final String ERROR_AMOUNT_REQUIRED         = "數量為必填欄位";
    public static final String ERROR_ITEM_ID_REQUIRED        = "道具 ID 為必填欄位";
    public static final String ERROR_POSITION_REQUIRED       = "格子位置為必填欄位";
    public static final String ERROR_LOCATION_REQUIRED       = "位置類型為必填欄位";
    public static final String ERROR_SLOT_NOT_EQUIPPED       = "該部位尚未裝備道具";
}
