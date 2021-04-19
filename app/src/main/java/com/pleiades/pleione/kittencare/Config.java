package com.pleiades.pleione.kittencare;

public class Config {
    // argument
    public static final String ARGUMENT_KEY_POSITION = "position";

    // reward type
    public static final int REWARD_TYPE_ADVERTISEMENT_ITEM = 0;
    public static final int REWARD_TYPE_ADVERTISEMENT_COSTUME = 1;
    public static final int REWARD_TYPE_GAME_ITEM_EASY = 2;
    public static final int REWARD_TYPE_GAME_ITEM_HARD = 3;
    public static final int REWARD_TYPE_EXPLORE = 4;

    // bound
    public static final int RANDOM_BOUND_REWARD = 20;
    public static final int RANDOM_BOUND_ITEM_FOUND = 100;

    // status
    public static final int LEVEL_MAX = 99;
    public static final double EXPERIENCE_MAGNIFICATION = 1.1;

    // toast
    public static final int TOAST_POSITION_DEFAULT = 0;
    public static final int TOAST_POSITION_HIGH = 1;
    public static final int TOAST_POSITION_VERY_HIGH = 2;

    // animation
    public static final int DIRECTION_TO_LEFT = 0;
    public static final int DIRECTION_TO_RIGHT = 1;
    public static final int DIRECTION_LEFT_TO_RIGHT = 2;
    public static final int DIRECTION_RIGHT_TO_LEFT = 3;
    public static final int DIRECTION_LEFT_TO_CENTER = 4;
    public static final int DIRECTION_RIGHT_TO_CENTER = 5;
    public static final int REPEAT_COUNT_BOX_SHIVER = 14;

    // notification
    public static final int NOTIFICATION_ID_FOREGROUND = 1004;
    public static final String NOTIFICATION_CHANNEL_ID = "channel_id_notification";
    public static final String NOTIFICATION_CHANNEL_NAME = "channel_name_notification";

    // url
    public static final String URL_ALCYONE = "https://play.google.com/store/apps/details?id=com.pleiades.pleione.alcyone";
    public static final String URL_KITTEN = "https://play.google.com/store/apps/details?id=com.pleiades.pleione.kittencare";
    public static final String URL_CLUSTER_PLEIADES = "https://play.google.com/store/apps/developer?id=Pleione+Pleiades";

    // dialog
    public static final int DIALOG_TYPE_OVERLAY = 0;
    public static final int DIALOG_TYPE_ALCYONE = 1;
    public static final int DIALOG_TYPE_PLEIONE = 2;
    public static final int DIALOG_TYPE_COMPLETE_TUTORIAL = 3;
    public static final int DIALOG_TYPE_SKIP_TUTORIAL = 4;
    public static final int DIALOG_TYPE_ESCAPE = 5;
    public static final int DIALOG_TYPE_RENAME = 6;
    public static final int DIALOG_TYPE_2021 = 7;
    public static final int DIALOG_TYPE_FINISH_GAME = 8;
    public static final int DIALOG_TYPE_SKIP_OPENING = 9;
    public static final int DIALOG_TYPE_START_PAJAMAS = 10;
    public static final int DIALOG_TYPE_WIN_PAJAMAS = 11;
    public static final int DIALOG_TYPE_START_PLEIADES = 12;
    public static final int DIALOG_TYPE_WIN_PLEIADES = 13;
    public static final int DIALOG_TYPE_START_DINOSAUR = 14;
    public static final int DIALOG_TYPE_WIN_DINOSAUR = 15;
    public static final int DIALOG_TYPE_TICKET = 16;
    public static final int DIALOG_TYPE_HAPPINESS = 17;
    public static final int DIALOG_TYPE_COMPLETE_HAPPINESS_TUTORIAL = 18;

    // animation kitten
    public static final int PERIOD_EXPLORE = 3000;
    public static final int COORDINATE_X = 0;
    public static final int COORDINATE_Y = 1;
    public static final int DELAY_DEFAULT = 150;
    public static final int DELAY_ANIMATION = 1000;
    public static final int DELAY_FACE_MAINTAIN = 1500;
    public static final int DELAY_FACE_MAINTAIN_LONG = 2000;
    public static final int DELAY_UNLOCK_SCREEN = 500;
    public static final float SNIFF_ANGLE_LEFT = -12f;
    public static final float SNIFF_ANGLE_RIGHT = 12f;
    public static final float SNIFF_ANGLE_LEFT_SHALLOW = -4f;
    public static final float SNIFF_ANGLE_RIGHT_SHALLOW = 4f;

    // history
    public static final int HISTORY_SIZE_MAX = 100;
    public static final int HISTORY_SIZE_LIMIT = 10;
    public static final int HISTORY_TYPE_ITEM_FOUND = 0;
    public static final int HISTORY_TYPE_ITEM_USED = 1;
    public static final int HISTORY_TYPE_ITEM_REWARD = 2;
    public static final int HISTORY_TYPE_ITEM_UPDATE_REWARD = 3;
    public static final int HISTORY_TYPE_COSTUME_FOUND = 10;
    public static final int HISTORY_TYPE_COSTUME_REWARD = 11;
    public static final int HISTORY_TYPE_LEVEL_UP = 20;
    public static final int HISTORY_TYPE_FIRST_EXPLORE = 21;
    public static final int HISTORY_TYPE_BUFF = 22;
    public static final int FILTER_POSITION_HISTORY_ALL = 0;
    public static final int FILTER_POSITION_HISTORY_ITEM = 1;
    public static final int FILTER_POSITION_HISTORY_COSTUME = 2;
    public static final int FILTER_POSITION_HISTORY_STATE = 3;

    // item
    public static final int ITEM_TYPE_CONSUMPTION = 0;
    public static final int ITEM_TYPE_TOY = 1;
    public static final int ITEM_TYPE_CHARM = 2;
    public static final int FILTER_POSITION_ITEM_ALL = 0;
    public static final int FILTER_POSITION_ITEM_CONSUMPTION = 1;
    public static final int FILTER_POSITION_ITEM_CHARM = 2;
    public static final int FILTER_POSITION_ITEM_TOY = 3;

    public static final int ITEM_CODE_ICE_CREAM_CAKE = -10;    // ★★★★★★
    public static final int ITEM_CODE_GREEN_TEA_ICE_CREAM = 0; // ★★★★★
    public static final int ITEM_CODE_CHOCOLATE_ICE_CREAM = 1; // ★★★★
    public static final int ITEM_CODE_CHERRY_ICE_CREAM = 2;    // ★★★
    public static final int ITEM_CODE_MELON_ICE_CREAM = 3;     // ★★
    public static final int ITEM_CODE_MINT_ICE_CREAM = 4;      // ★
    public static final int ITEM_CODE_GREEN_TEA_CAKE = 5;      // ★★★★★
    public static final int ITEM_CODE_CHOCOLATE_CAKE = 6;      // ★★★★
    public static final int ITEM_CODE_CHERRY_CAKE = 7;         // ★★★
    public static final int ITEM_CODE_MELON_CAKE = 8;          // ★★
    public static final int ITEM_CODE_MINT_CAKE = 9;           // ★
    public static final int ITEM_CODE_CRYSTAL_BALL = 49;
    public static final int ITEM_CODE_ALCHEMY = 50;
    public static final int ITEM_CODE_PLEIONE_DOLL = 100;
    public static final int ITEM_CODE_SCRATCHER = 101;
    public static final int ITEM_CODE_TOWER = 102;

    public static final int BUFF_CODE_FAIL = 0;
    public static final int BUFF_CODE_EXPERIENCE = 1;
    public static final int BUFF_CODE_ITEM = 2;

    // face
    public static final int FACE_CODE_DEFAULT = 0;
    public static final int FACE_CODE_BLINK_1 = 1;
    public static final int FACE_CODE_BLINK_2 = 2;
    public static final int FACE_CODE_BLINK_3 = 3;
    public static final int FACE_CODE_SMILE = 4;
    public static final int FACE_CODE_ANGRY = 5;
    public static final int FACE_CODE_SURPRISED = 6;
    public static final int FACE_CODE_SPARKLE = 7;
    public static final int FACE_CODE_SLEEP = 8;
    public static final int FACE_CODE_SWEAT_1 = 9;
    public static final int FACE_CODE_SWEAT_2 = 10;
    public static final int FACE_CODE_MEOW_1 = 11;
    public static final int FACE_CODE_MEOW_2 = 12;

    // costume
    public static final int COSTUME_TYPE_FREE = 0;
    public static final int COSTUME_TYPE_SPECIAL = 1;
    public static final int COSTUME_TYPE_PAID = 2;

    public static final int COSTUME_CODE_DEFAULT = 0;
    public static final int COSTUME_CODE_PAJAMAS = 1;
    public static final int COSTUME_CODE_SWEATER = 2;
    public static final int COSTUME_CODE_BLAZER = 3;
    public static final int COSTUME_CODE_UNIFORM = 4;
    public static final int COSTUME_CODE_HANBOK = 5;
    public static final int COSTUME_CODE_HOODIE = 6;
    public static final int COSTUME_CODE_JACKET = 7;
    public static final int COSTUME_CODE_SHIRTS = 8;
    public static final int COSTUME_CODE_FLEECE = 9;
    public static final int COSTUME_CODE_COAT = 10;

    public static final int COSTUME_CODE_ALCYONE = 11;
    public static final int COSTUME_CODE_PLEIONE = 12;
    public static final int COSTUME_CODE_GIFT = 13;
    public static final int COSTUME_CODE_CHOCO = 14;
    public static final int COSTUME_CODE_2021 = 15;
    public static final int COSTUME_CODE_GAME_MACHINE = 16;
    public static final int COSTUME_CODE_SAILOR = 17;

    public static final int COSTUME_CODE_DINOSAUR = 20;
    public static final int COSTUME_CODE_BEE = 21;
    public static final int COSTUME_CODE_PENGUIN = 22;
    public static final int COSTUME_CODE_SUNFLOWER = 23;
    public static final int COSTUME_CODE_MAGICIAN = 24;
    public static final int COSTUME_CODE_ASTRONAUT = 25;
    public static final int COSTUME_CODE_MAID = 26;
    public static final int COSTUME_CODE_ALCHEMIST = 27;
    public static final int COSTUME_CODE_SEER = 28;

    // settings
    public static final float PROGRESS_TERM_MAGNET_PERCENTAGE = 0.05f;
    public static final int PROGRESS_TERM_JUMP_ALTITUDE = 10;
    public static final int PROGRESS_TERM_JUMP_DISTANCE = 20;
    public static final int PROGRESS_TERM_SHOW_POSITION = 500;
    public static final float PROGRESS_TERM_SCALE_PERCENTAGE = 0.1f;

    public static final int POSITION_APPLICATION = 0;
    public static final int POSITION_DETAIL = 1;
    public static final int APPLICATION_POSITION_TUTORIAL = 0;
    public static final int APPLICATION_POSITION_HAPPINESS = 1;
    public static final int APPLICATION_POSITION_SHARE = 2;
    public static final int APPLICATION_POSITION_CHECK_UPDATE = 3;
    public static final int APPLICATION_POSITION_BACKUP = 4;
    public static final int APPLICATION_POSITION_OTHER_APPS = 5;
    public static final int DETAIL_POSITION_RENAME = 0;
    public static final int DETAIL_POSITION_MODIFY_COORDINATES = 1;
    public static final int DETAIL_POSITION_DRESS_RANDOM_COSTUME = 2;
    public static final int DETAIL_POSITION_RECOGNIZE_KEYBOARD = 3;

    // prefs
    public static final String PREFS = "prefs";

    public static final String KEY_BACKUP = "userPrefs";
    public static final String KEY_USER_LAST_VERSION_CODE = "userLastVersionCode";
    public static final String KEY_LAST_LAUNCH_DAY = "last_launch_day";
    public static final String KEY_REWARD_EARNED_COUNT = "adsCount";
    public static final String KEY_IS_TUTORIAL_COMPLETED = "tutorial_complete";
    public static final String KEY_IS_HAPPINESS_TUTORIAL_COMPLETED = "happiness_tutorial_complete";
    public static final String KEY_IS_EXPLORED = "firstExploring";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_EXPERIENCE = "experience";
    public static final String KEY_HAPPINESS = "happiness";
    public static final String KEY_HISTORY_ARRAY_LIST = "historyList";
    public static final String KEY_ITEM_ARRAY_LIST = "itemList";
    public static final String KEY_WORN_ARRAY_LIST = "wornList";
    public static final String KEY_SCREEN_WIDTH = "screen_width";
    public static final String KEY_SCREEN_HEIGHT = "screen_height";
    public static final String KEY_HISTORY_SIZE_LIMIT = "history_size_limit";
    public static final String KEY_LAST_HIDE_DATE_STRING = "last_hide_date_string";
    public static final String KEY_LAST_CONSUMPTION_DATE_STRING = "last_consumption_date_string";

    public static final String KEY_ITEM_PLEIONE_DOLL = "pleioneDoll";
    public static final String KEY_ITEM_SCRATCHER = "scratcher";
    public static final String KEY_ITEM_TOWER = "tower";
    public static final String KEY_ITEM_ALCHEMY = "alchemy";
    public static final String KEY_ITEM_CRYSTAL_BALL = "crystal_ball";
    public static final String KEY_USE_ICE_CREAM_CAKE = "use_ice_cream_cake";
    public static final String KEY_USE_GREEN_TEA_ICE_CREAM = "use_green_tea";
    public static final String KEY_USE_CHOCOLATE_ICE_CREAM = "use_chocolate";
    public static final String KEY_USE_CHERRY_ICE_CREAM = "use_cherry";
    public static final String KEY_USE_MELON_ICE_CREAM = "use_melon";
    public static final String KEY_USE_MINT_ICE_CREAM = "use_mint";

    public static final String KEY_BUFF = "buff";

    public static final String KEY_WEARING_COSTUME = "wearingCostume";

    public static final String KEY_COSTUME_DEFAULT = "default_costume";
    public static final String KEY_COSTUME_PAJAMAS = "pajamas_costume";
    public static final String KEY_COSTUME_SWEATER = "sweater_costume";
    public static final String KEY_COSTUME_SHIRTS = "shirts_costume";
    public static final String KEY_COSTUME_BLAZER = "blazer_costume";
    public static final String KEY_COSTUME_UNIFORM = "uniform_costume";
    public static final String KEY_COSTUME_HANBOK = "hanbok_costume";
    public static final String KEY_COSTUME_FLEECE = "fleece_costume";
    public static final String KEY_COSTUME_HOODIE = "hoodie_costume";
    public static final String KEY_COSTUME_JACKET = "jacket_costume";
    public static final String KEY_COSTUME_COAT = "coat_costume";

    public static final String KEY_COSTUME_ALCYONE = "alcyone_costume";
    public static final String KEY_COSTUME_PLEIONE = "pleione_costume";
    public static final String KEY_COSTUME_GIFT = "gift_costume";
    public static final String KEY_COSTUME_CHOCO = "choco_costume";
    public static final String KEY_COSTUME_2021 = "2021_costume";
    public static final String KEY_COSTUME_GAME_MACHINE = "game_machine_costume";
    public static final String KEY_COSTUME_SAILOR = "sailor_costume";

    public static final String KEY_COSTUME_DINOSAUR = "dinosaur_costume";
    public static final String KEY_COSTUME_BEE = "bee_costume";
    public static final String KEY_COSTUME_PENGUIN = "penguin_costume";
    public static final String KEY_COSTUME_SUNFLOWER = "sunflower_costume";
    public static final String KEY_COSTUME_MAGICIAN = "magician_costume";
    public static final String KEY_COSTUME_ASTRONAUT = "astronaut_costume";
    public static final String KEY_COSTUME_MAID = "maid_costume";
    public static final String KEY_COSTUME_ALCHEMIST = "alchemist_costume";
    public static final String KEY_COSTUME_SEER = "seer_costume";

    public static final String KEY_IS_RECOGNIZING_KEYBOARD = "recognize_keyboard";
    public static final String KEY_IS_DRESS_RANDOMLY = "dress_random_costume";
    public static final String KEY_MAGNET_PERCENTAGE_WIDTH = "magnet_width_percentage";
    public static final String KEY_MAGNET_PERCENTAGE_HEIGHT = "magnet_height_percentage";
    public static final String KEY_JUMP_ALTITUDE = "jump_altitude";
    public static final String KEY_JUMP_DISTANCE = "jump_distance";
    public static final String KEY_SHOW_POSITION = "show_position";
    public static final String KEY_ANIMATOR_DURATION_SCALE = "animator_duration_scale";
    public static final String KEY_NAME = "kittenName";
    public static final float DEFAULT_MAGNET_PERCENTAGE_WIDTH = 0.85f;
    public static final float DEFAULT_MAGNET_PERCENTAGE_HEIGHT = 0.75f;
    public static final int DEFAULT_JUMP_ALTITUDE = 70;
    public static final int DEFAULT_JUMP_DISTANCE = 150;
    public static final int DEFAULT_SHOW_POSITION = 1000;

    public static final String KEY_GAME_TICKET_PAJAMAS = "pajamas_ticket";
    public static final String KEY_GAME_TICKET_PLEIADES = "pleiades_ticket";
    public static final String KEY_GAME_TICKET_DINOSAUR = "dinosaur_ticket";
    public static final String KEY_GAME_WIN_COUNT = "game_win_count";
    public static final String KEY_IS_GAME_DIFFICULTY_HARD = "is_game_difficulty_hard";

    // game
    public static final int TICKET_MAX = 2;
    public static final int GAME_CODE_PAJAMAS = 0;
    public static final int GAME_CODE_PLEIADES = 1;
    public static final int GAME_CODE_DINOSAUR = 2;

    public static final long PERIOD_CHANGE_FACE_GAME_FRAGMENT = 4000;
    public static final long DELAY_GAME_READ_CHAR = 75;
    public static final long DELAY_GAME_READ_CHAR_SLOW = 200;
    public static final long DELAY_GAME_ANIMATION_SHORT = 500;
    public static final long DELAY_GAME_ANIMATION_DEFAULT = 1000;
    public static final long DELAY_GAME_ANIMATION_LONG = 2000;
}
