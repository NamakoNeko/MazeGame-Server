package com.javaclass.game.constants;

public class GameApiDefiner {

    public static final String EQUIPMENT_BASE_URL = "/api/game/equipment";
    public static final String HOTKEY_BASE_URL    = "/api/game/hotkeys";
    public static final String SHOP_BASE_URL      = "/api/game/shop";
    public static final String ITEM_BASE_URL      = "/api/game/items";
    public static final String ITEM_PRICE_BASE_URL = "/api/game/item-prices";
    public static final String RUN_BASE_URL       = "/api/game/run";
    public static final String MONSTER_BASE_URL   = "/api/game/monsters";
    public static final String DROP_BASE_URL      = "/api/game/drop";
    public static final String HUB_BASE_URL       = "/api/game/hub";

    public static final String EQUIP_PATH         = "/equip";
    public static final String UNEQUIP_PATH       = "/unequip";
    public static final String OFFERS_PATH        = "/offers";
    public static final String REFRESH_PATH       = "/refresh";
    public static final String BUY_PATH           = "/buy";
    public static final String SETTLE_PATH        = "/settle";
    public static final String SELL_PATH          = "/sell";
    public static final String BED_SAVE_PATH      = "/bed/save";
    public static final String PORTAL_VALIDATE_PATH = "/portal/validate";
    public static final String STORAGE_OPEN_PATH  = "/storage/open";

    public static final String SLOT_HEAD          = "HEAD";
    public static final String SLOT_CHEST         = "CHEST";
    public static final String SLOT_WEAPON        = "WEAPON";
    public static final String SLOT_OFFHAND       = "OFFHAND";
    public static final String SLOT_SHOES         = "SHOES";

    public static final String ERROR_PLAYER_NOT_FOUND      = "player not found";
    public static final String ERROR_ITEM_NOT_FOUND        = "item not found";
    public static final String ERROR_PRICE_NOT_FOUND       = "item price not found";
    public static final String ERROR_STATS_NOT_FOUND       = "player stats not found";
    public static final String ERROR_EQUIPMENT_NOT_FOUND   = "player equipment not found";
    public static final String ERROR_INVALID_SLOT          = "invalid equipment slot";
    public static final String ERROR_INVALID_AMOUNT        = "amount must be greater than 0";
    public static final String ERROR_INVALID_POSITION      = "position is required";
    public static final String ERROR_MONEY_NOT_ENOUGH      = "money is not enough";
    public static final String ERROR_KEY_INDEX_INVALID     = "key index must be between 1 and 9";
    public static final String ERROR_DROP_POOL_EMPTY       = "drop pool is empty";
    public static final String ERROR_TARGET_SLOT_OCCUPIED  = "target slot is occupied";
}
