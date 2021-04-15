package com.pleiades.pleione.kittencare;

import android.content.Context;

import java.util.Locale;

import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_2021;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_ALCHEMIST;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_ALCYONE;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_ASTRONAUT;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_BEE;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_BLAZER;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_CHOCO;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_COAT;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_DEFAULT;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_DINOSAUR;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_FLEECE;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_GAME_MACHINE;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_GIFT;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_HANBOK;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_HOODIE;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_JACKET;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_MAGICIAN;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_MAID;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_PAJAMAS;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_PENGUIN;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_PLEIONE;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_SAILOR;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_SEER;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_SHIRTS;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_SUNFLOWER;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_SWEATER;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_UNIFORM;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_ANGRY;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_1;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_2;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_3;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_DEFAULT;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_MEOW_1;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_MEOW_2;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SLEEP;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SMILE;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SPARKLE;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SURPRISED;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SWEAT_1;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SWEAT_2;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_ALCHEMY;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_CHERRY_CAKE;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_CHERRY_ICE_CREAM;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_CHOCOLATE_CAKE;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_CHOCOLATE_ICE_CREAM;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_CRYSTAL_BALL;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_GREEN_TEA_CAKE;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_GREEN_TEA_ICE_CREAM;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_ICE_CREAM_CAKE;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_MELON_CAKE;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_MELON_ICE_CREAM;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_MINT_CAKE;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_MINT_ICE_CREAM;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_PLEIONE_DOLL;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_SCRATCHER;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_TOWER;
import static com.pleiades.pleione.kittencare.Config.ITEM_TYPE_CHARM;
import static com.pleiades.pleione.kittencare.Config.ITEM_TYPE_CONSUMPTION;
import static com.pleiades.pleione.kittencare.Config.ITEM_TYPE_TOY;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_2021;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_ALCHEMIST;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_ALCYONE;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_ASTRONAUT;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_BEE;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_BLAZER;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_CHOCO;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_COAT;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_DEFAULT;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_DINOSAUR;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_FLEECE;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_GAME_MACHINE;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_GIFT;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_HANBOK;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_HOODIE;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_JACKET;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_MAGICIAN;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_MAID;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_PAJAMAS;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_PENGUIN;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_PLEIONE;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_SAILOR;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_SEER;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_SHIRTS;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_SUNFLOWER;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_SWEATER;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_UNIFORM;
import static com.pleiades.pleione.kittencare.Config.KEY_ITEM_ALCHEMY;
import static com.pleiades.pleione.kittencare.Config.KEY_ITEM_CRYSTAL_BALL;
import static com.pleiades.pleione.kittencare.Config.KEY_ITEM_PLEIONE_DOLL;
import static com.pleiades.pleione.kittencare.Config.KEY_ITEM_SCRATCHER;
import static com.pleiades.pleione.kittencare.Config.KEY_ITEM_TOWER;

// TODO update all
public class Converter {
    // history
    public static String getHistoryMonth(Context context, int month, boolean isLimited) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();

        if (language.contains("ko"))
            return Integer.toString(month);

        switch (month) {
            case 1:
                return isLimited ? "Jan" : "January";
            case 2:
                return isLimited ? "Feb" : "February";
            case 3:
                return isLimited ? "Mar" : "March";
            case 4:
                return isLimited ? "Apr" : "April";
            case 5:
                return "May";
            case 6:
                return isLimited ? "Jun" : "June";
            case 7:
                return isLimited ? "Jul" : "July";
            case 8:
                return isLimited ? "Aug" : "August";
            case 9:
                return isLimited ? "Sep" : "September";
            case 10:
                return isLimited ? "Oct" : "October";
            case 11:
                return isLimited ? "Nov" : "November";
            case 12:
                return isLimited ? "Dec" : "December";
            default:
                return "";
        }
    }

    // item
    public static String getItemName(Context context, int itemCode) {
        switch (itemCode) {
            case ITEM_CODE_ICE_CREAM_CAKE:
                return context.getString(R.string.item_title_ice_cream_cake);
            case ITEM_CODE_GREEN_TEA_ICE_CREAM:
                return context.getString(R.string.item_title_green_tea_ice_cream);
            case ITEM_CODE_CHOCOLATE_ICE_CREAM:
                return context.getString(R.string.item_title_chocolate_ice_cream);
            case ITEM_CODE_CHERRY_ICE_CREAM:
                return context.getString(R.string.item_title_cherry_ice_cream);
            case ITEM_CODE_MELON_ICE_CREAM:
                return context.getString(R.string.item_title_melon_ice_cream);
            case ITEM_CODE_MINT_ICE_CREAM:
                return context.getString(R.string.item_title_mint_ice_cream);
            case ITEM_CODE_GREEN_TEA_CAKE:
                return context.getString(R.string.item_title_green_tea_cake);
            case ITEM_CODE_CHOCOLATE_CAKE:
                return context.getString(R.string.item_title_chocolate_cake);
            case ITEM_CODE_CHERRY_CAKE:
                return context.getString(R.string.item_title_cherry_cake);
            case ITEM_CODE_MELON_CAKE:
                return context.getString(R.string.item_title_melon_cake);
            case ITEM_CODE_MINT_CAKE:
                return context.getString(R.string.item_title_mint_cake);
            case ITEM_CODE_ALCHEMY:
                return context.getString(R.string.item_title_alchemy);
            case ITEM_CODE_CRYSTAL_BALL:
                return context.getString(R.string.item_title_crystal_ball);
            case ITEM_CODE_PLEIONE_DOLL:
                return context.getString(R.string.item_title_pleione_doll);
            case ITEM_CODE_SCRATCHER:
                return context.getString(R.string.item_title_scratcher);
            case ITEM_CODE_TOWER:
                return context.getString(R.string.item_title_tower);
        }
        return "";
    }

    public static String getItemPrefsKey(int itemCode) {
        switch (itemCode) {
            case ITEM_CODE_ALCHEMY:
                return KEY_ITEM_ALCHEMY;
            case ITEM_CODE_CRYSTAL_BALL:
                return KEY_ITEM_CRYSTAL_BALL;
            case ITEM_CODE_PLEIONE_DOLL:
                return KEY_ITEM_PLEIONE_DOLL;
            case ITEM_CODE_SCRATCHER:
                return KEY_ITEM_SCRATCHER;
            case ITEM_CODE_TOWER:
                return KEY_ITEM_TOWER;
            default:
                return null;
        }
    }

    public static String getItemEffect(Context context, int itemCode) {
        switch (itemCode) {
            case ITEM_CODE_ICE_CREAM_CAKE:
                return context.getString(R.string.item_contents_ice_cream_cake);
            case ITEM_CODE_GREEN_TEA_ICE_CREAM:
                return context.getString(R.string.item_contents_green_tea_ice_cream);
            case ITEM_CODE_CHOCOLATE_ICE_CREAM:
                return context.getString(R.string.item_contents_chocolate_ice_cream);
            case ITEM_CODE_CHERRY_ICE_CREAM:
                return context.getString(R.string.item_contents_cherry_ice_cream);
            case ITEM_CODE_MELON_ICE_CREAM:
                return context.getString(R.string.item_contents_melon_ice_cream);
            case ITEM_CODE_MINT_ICE_CREAM:
                return context.getString(R.string.item_contents_mint_ice_cream);
            case ITEM_CODE_GREEN_TEA_CAKE:
                return context.getString(R.string.item_contents_green_tea_cake);
            case ITEM_CODE_CHOCOLATE_CAKE:
                return context.getString(R.string.item_contents_chocolate_cake);
            case ITEM_CODE_CHERRY_CAKE:
                return context.getString(R.string.item_contents_cherry_cake);
            case ITEM_CODE_MELON_CAKE:
                return context.getString(R.string.item_contents_melon_cake);
            case ITEM_CODE_MINT_CAKE:
                return context.getString(R.string.item_contents_mint_cake);
            case ITEM_CODE_ALCHEMY:
                return context.getString(R.string.item_contents_alchemy);
            case ITEM_CODE_CRYSTAL_BALL:
                return context.getString(R.string.item_contents_crystal_ball);
            case ITEM_CODE_PLEIONE_DOLL:
                return context.getString(R.string.item_contents_pleione_doll);
            case ITEM_CODE_SCRATCHER:
                return context.getString(R.string.item_contents_scratcher);
            case ITEM_CODE_TOWER:
                return context.getString(R.string.item_contents_tower);
            default:
                return "";
        }
    }

    public static int getItemType(int itemCode) {
        switch (itemCode) {
            case ITEM_CODE_ALCHEMY:
            case ITEM_CODE_CRYSTAL_BALL:
                return ITEM_TYPE_CHARM;
            case ITEM_CODE_PLEIONE_DOLL:
            case ITEM_CODE_SCRATCHER:
            case ITEM_CODE_TOWER:
                return ITEM_TYPE_TOY;
            default:
                return ITEM_TYPE_CONSUMPTION;
        }
    }

    public static int getConsumptionItemRank(int itemCode) {
        switch (itemCode) {
            case ITEM_CODE_ICE_CREAM_CAKE:
                return 6;
            case ITEM_CODE_GREEN_TEA_ICE_CREAM:
            case ITEM_CODE_GREEN_TEA_CAKE:
                return 5;
            case ITEM_CODE_CHOCOLATE_ICE_CREAM:
            case ITEM_CODE_CHOCOLATE_CAKE:
                return 4;
            case ITEM_CODE_CHERRY_ICE_CREAM:
            case ITEM_CODE_CHERRY_CAKE:
                return 3;
            case ITEM_CODE_MELON_ICE_CREAM:
            case ITEM_CODE_MELON_CAKE:
                return 2;
            case ITEM_CODE_MINT_ICE_CREAM:
            case ITEM_CODE_MINT_CAKE:
                return 1;
            default:
                return 0;
        }
    }

    public static int getItemIconResourceId(int itemType, int itemCode) {
        if (itemType == ITEM_TYPE_CHARM)
            return R.drawable.icon_charm;
        if (itemType == ITEM_TYPE_TOY)
            return R.drawable.icon_toy;

        switch (getConsumptionItemRank(itemCode)) {
            case 6:
                return R.drawable.icon_star_6;
            case 5:
                return R.drawable.icon_star_5;
            case 4:
                return R.drawable.icon_star_4;
            case 3:
                return R.drawable.icon_star_3;
            case 2:
                return R.drawable.icon_star_2;
            case 1:
                return R.drawable.icon_star;
            default:
                return 0;
        }
    }

    // costume
    public static String getCostumeName(Context context, int costumeCode) {
        switch (costumeCode) {
            case COSTUME_CODE_DEFAULT:
                return context.getString(R.string.costume_title_default);
            case COSTUME_CODE_PAJAMAS:
                return context.getString(R.string.costume_title_pajamas);
            case COSTUME_CODE_SWEATER:
                return context.getString(R.string.costume_title_sweater);
            case COSTUME_CODE_SHIRTS:
                return context.getString(R.string.costume_title_shirts);
            case COSTUME_CODE_BLAZER:
                return context.getString(R.string.costume_title_blazer);
            case COSTUME_CODE_UNIFORM:
                return context.getString(R.string.costume_title_uniform);
            case COSTUME_CODE_HANBOK:
                return context.getString(R.string.costume_title_hanbok);
            case COSTUME_CODE_FLEECE:
                return context.getString(R.string.costume_title_fleece);
            case COSTUME_CODE_HOODIE:
                return context.getString(R.string.costume_title_hoodie);
            case COSTUME_CODE_JACKET:
                return context.getString(R.string.costume_title_jacket);
            case COSTUME_CODE_COAT:
                return context.getString(R.string.costume_title_coat);

            case COSTUME_CODE_ALCYONE:
                return context.getString(R.string.costume_title_alcyone);
            case COSTUME_CODE_PLEIONE:
                return context.getString(R.string.costume_title_pleione);
            case COSTUME_CODE_GIFT:
                return context.getString(R.string.costume_title_gift);
            case COSTUME_CODE_CHOCO:
                return context.getString(R.string.costume_title_choco);
            case COSTUME_CODE_2021:
                return context.getString(R.string.costume_title_2021);
            case COSTUME_CODE_GAME_MACHINE:
                return context.getString(R.string.costume_title_game_machine);
            case COSTUME_CODE_SAILOR:
                return context.getString(R.string.costume_title_sailor);

            case COSTUME_CODE_DINOSAUR:
                return context.getString(R.string.costume_title_dinosaur);
            case COSTUME_CODE_BEE:
                return context.getString(R.string.costume_title_bee);
            case COSTUME_CODE_PENGUIN:
                return context.getString(R.string.costume_title_penguin);
            case COSTUME_CODE_SUNFLOWER:
                return context.getString(R.string.costume_title_sunflower);
            case COSTUME_CODE_MAGICIAN:
                return context.getString(R.string.costume_title_magician);
            case COSTUME_CODE_ASTRONAUT:
                return context.getString(R.string.costume_title_astronaut);
            case COSTUME_CODE_MAID:
                return context.getString(R.string.costume_title_maid);
            case COSTUME_CODE_ALCHEMIST:
                return context.getString(R.string.costume_title_alchemist);
            case COSTUME_CODE_SEER:
                return context.getString(R.string.costume_title_seer);
        }
        return null;
    }

    public static String getCostumePrefsKey(int costumeCode) {
        switch (costumeCode) {
            case COSTUME_CODE_DEFAULT:
                return KEY_COSTUME_DEFAULT;
            case COSTUME_CODE_PAJAMAS:
                return KEY_COSTUME_PAJAMAS;
            case COSTUME_CODE_SWEATER:
                return KEY_COSTUME_SWEATER;
            case COSTUME_CODE_SHIRTS:
                return KEY_COSTUME_SHIRTS;
            case COSTUME_CODE_BLAZER:
                return KEY_COSTUME_BLAZER;
            case COSTUME_CODE_UNIFORM:
                return KEY_COSTUME_UNIFORM;
            case COSTUME_CODE_HANBOK:
                return KEY_COSTUME_HANBOK;
            case COSTUME_CODE_FLEECE:
                return KEY_COSTUME_FLEECE;
            case COSTUME_CODE_HOODIE:
                return KEY_COSTUME_HOODIE;
            case COSTUME_CODE_JACKET:
                return KEY_COSTUME_JACKET;
            case COSTUME_CODE_COAT:
                return KEY_COSTUME_COAT;

            case COSTUME_CODE_ALCYONE:
                return KEY_COSTUME_ALCYONE;
            case COSTUME_CODE_PLEIONE:
                return KEY_COSTUME_PLEIONE;
            case COSTUME_CODE_GIFT:
                return KEY_COSTUME_GIFT;
            case COSTUME_CODE_CHOCO:
                return KEY_COSTUME_CHOCO;
            case COSTUME_CODE_2021:
                return KEY_COSTUME_2021;
            case COSTUME_CODE_GAME_MACHINE:
                return KEY_COSTUME_GAME_MACHINE;
            case COSTUME_CODE_SAILOR:
                return KEY_COSTUME_SAILOR;

            case COSTUME_CODE_DINOSAUR:
                return KEY_COSTUME_DINOSAUR;
            case COSTUME_CODE_BEE:
                return KEY_COSTUME_BEE;
            case COSTUME_CODE_PENGUIN:
                return KEY_COSTUME_PENGUIN;
            case COSTUME_CODE_SUNFLOWER:
                return KEY_COSTUME_SUNFLOWER;
            case COSTUME_CODE_MAGICIAN:
                return KEY_COSTUME_MAGICIAN;
            case COSTUME_CODE_ASTRONAUT:
                return KEY_COSTUME_ASTRONAUT;
            case COSTUME_CODE_MAID:
                return KEY_COSTUME_MAID;
            case COSTUME_CODE_ALCHEMIST:
                return KEY_COSTUME_ALCHEMIST;
            case COSTUME_CODE_SEER:
                return KEY_COSTUME_SEER;
            default:
                return null;
        }
    }

    public static int getCostumeCode(String key) {
        switch (key) {
            case KEY_COSTUME_DEFAULT:
                return COSTUME_CODE_DEFAULT;
            case KEY_COSTUME_PAJAMAS:
                return COSTUME_CODE_PAJAMAS;
            case KEY_COSTUME_SWEATER:
                return COSTUME_CODE_SWEATER;
            case KEY_COSTUME_SHIRTS:
                return COSTUME_CODE_SHIRTS;
            case KEY_COSTUME_BLAZER:
                return COSTUME_CODE_BLAZER;
            case KEY_COSTUME_UNIFORM:
                return COSTUME_CODE_UNIFORM;
            case KEY_COSTUME_HANBOK:
                return COSTUME_CODE_HANBOK;
            case KEY_COSTUME_FLEECE:
                return COSTUME_CODE_FLEECE;
            case KEY_COSTUME_HOODIE:
                return COSTUME_CODE_HOODIE;
            case KEY_COSTUME_JACKET:
                return COSTUME_CODE_JACKET;
            case KEY_COSTUME_COAT:
                return COSTUME_CODE_COAT;

            case KEY_COSTUME_ALCYONE:
                return COSTUME_CODE_ALCYONE;
            case KEY_COSTUME_PLEIONE:
                return COSTUME_CODE_PLEIONE;
            case KEY_COSTUME_GIFT:
                return COSTUME_CODE_GIFT;
            case KEY_COSTUME_CHOCO:
                return COSTUME_CODE_CHOCO;
            case KEY_COSTUME_2021:
                return COSTUME_CODE_2021;
            case KEY_COSTUME_GAME_MACHINE:
                return COSTUME_CODE_GAME_MACHINE;
            case KEY_COSTUME_SAILOR:
                return COSTUME_CODE_SAILOR;

            case KEY_COSTUME_DINOSAUR:
                return COSTUME_CODE_DINOSAUR;
            case KEY_COSTUME_BEE:
                return COSTUME_CODE_BEE;
            case KEY_COSTUME_PENGUIN:
                return COSTUME_CODE_PENGUIN;
            case KEY_COSTUME_SUNFLOWER:
                return COSTUME_CODE_SUNFLOWER;
            case KEY_COSTUME_MAGICIAN:
                return COSTUME_CODE_MAGICIAN;
            case KEY_COSTUME_ASTRONAUT:
                return COSTUME_CODE_ASTRONAUT;
            case KEY_COSTUME_MAID:
                return COSTUME_CODE_MAID;
            case KEY_COSTUME_ALCHEMIST:
                return COSTUME_CODE_ALCHEMIST;
            case KEY_COSTUME_SEER:
                return COSTUME_CODE_SEER;
            default:
                return 0;
        }
    }

    public static int getCostumeLevel(int costumeCode) {
        switch (costumeCode) {
            // unlock according to level
            case COSTUME_CODE_PAJAMAS:
                return 5;
            case COSTUME_CODE_SWEATER:
                return 10;
            case COSTUME_CODE_SHIRTS:
                return 20;
            case COSTUME_CODE_BLAZER:
                return 30;
            case COSTUME_CODE_UNIFORM:
                return 40;
            case COSTUME_CODE_HANBOK:
                return 50;
            case COSTUME_CODE_FLEECE:
                return 60;
            case COSTUME_CODE_HOODIE:
                return 70;
            case COSTUME_CODE_JACKET:
                return 80;
            case COSTUME_CODE_COAT:
                return 90;
        }
        return Integer.MAX_VALUE;
    }

    public static int getCostumeResourceId(int costumeCode) {
        switch (costumeCode) {
            case COSTUME_CODE_PAJAMAS:
                return R.drawable.image_costume_pajamas;
            case COSTUME_CODE_SWEATER:
                return R.drawable.image_costume_sweater;
            case COSTUME_CODE_SHIRTS:
                return R.drawable.image_costume_shirts;
            case COSTUME_CODE_BLAZER:
                return R.drawable.image_costume_blazer;
            case COSTUME_CODE_UNIFORM:
                return R.drawable.image_costume_uniform;
            case COSTUME_CODE_HANBOK:
                return R.drawable.image_costume_hanbok;
            case COSTUME_CODE_FLEECE:
                return R.drawable.image_costume_fleece;
            case COSTUME_CODE_HOODIE:
                return R.drawable.image_costume_hoodie;
            case COSTUME_CODE_JACKET:
                return R.drawable.image_costume_jacket;
            case COSTUME_CODE_COAT:
                return R.drawable.image_costume_coat;

            case COSTUME_CODE_ALCYONE:
                return R.drawable.image_costume_alcyone;
            case COSTUME_CODE_PLEIONE:
                return R.drawable.image_costume_pleione;
            case COSTUME_CODE_GIFT:
                return R.drawable.image_costume_gift;
            case COSTUME_CODE_CHOCO:
                return R.drawable.image_costume_choco;
            case COSTUME_CODE_2021:
                return R.drawable.image_costume_2021;
            case COSTUME_CODE_GAME_MACHINE:
                return R.drawable.image_costume_game_machine;
            case COSTUME_CODE_SAILOR:
                return R.drawable.image_costume_sailor;

            case COSTUME_CODE_DINOSAUR:
                return R.drawable.image_costume_dinosaur;
            case COSTUME_CODE_BEE:
                return R.drawable.image_costume_bee;
            case COSTUME_CODE_PENGUIN:
                return R.drawable.image_costume_penguin;
            case COSTUME_CODE_SUNFLOWER:
                return R.drawable.image_costume_sunflower;
            case COSTUME_CODE_MAGICIAN:
                return R.drawable.image_costume_magician;
            case COSTUME_CODE_ASTRONAUT:
                return R.drawable.image_costume_astronaut;
            case COSTUME_CODE_MAID:
                return R.drawable.image_costume_maid;
            case COSTUME_CODE_ALCHEMIST:
                return R.drawable.image_costume_alchemist;
            case COSTUME_CODE_SEER:
                return R.drawable.image_costume_seer;
        }
        return 0;
    }

    // others
    public static int getFaceResourceId(int faceCode) {
        switch (faceCode) {
            case FACE_CODE_DEFAULT:
                return R.drawable.image_face_default;
            case FACE_CODE_BLINK_1:
                return R.drawable.image_face_blink_1;
            case FACE_CODE_BLINK_2:
                return R.drawable.image_face_blink_2;
            case FACE_CODE_BLINK_3:
                return R.drawable.image_face_blink_3;
            case FACE_CODE_SMILE:
                return R.drawable.image_face_smile;
            case FACE_CODE_ANGRY:
                return R.drawable.image_face_angry;
            case FACE_CODE_SURPRISED:
                return R.drawable.image_face_surprised;
            case FACE_CODE_SPARKLE:
                return R.drawable.image_face_sparkle;
            case FACE_CODE_SLEEP:
                return R.drawable.image_face_sleep;
            case FACE_CODE_SWEAT_1:
                return R.drawable.image_face_sweat_1;
            case FACE_CODE_SWEAT_2:
                return R.drawable.image_face_sweat_2;
            case FACE_CODE_MEOW_1:
                return R.drawable.image_face_meow_1;
            case FACE_CODE_MEOW_2:
                return R.drawable.image_face_meow_2;
        }
        return 0;
    }

    public static int getCakeCode(int iceCreamCode) {
        switch (iceCreamCode) {
            case ITEM_CODE_GREEN_TEA_ICE_CREAM:
                return ITEM_CODE_GREEN_TEA_CAKE;
            case ITEM_CODE_CHOCOLATE_ICE_CREAM:
                return ITEM_CODE_CHOCOLATE_CAKE;
            case ITEM_CODE_CHERRY_ICE_CREAM:
                return ITEM_CODE_CHERRY_CAKE;
            case ITEM_CODE_MELON_ICE_CREAM:
                return ITEM_CODE_MELON_CAKE;
            case ITEM_CODE_MINT_ICE_CREAM:
                return ITEM_CODE_MINT_CAKE;
            default:
                return 0;
        }
    }

    public static boolean isItemIceCream(int itemCode) {
        switch (itemCode) {
            case ITEM_CODE_GREEN_TEA_ICE_CREAM:
            case ITEM_CODE_CHOCOLATE_ICE_CREAM:
            case ITEM_CODE_CHERRY_ICE_CREAM:
            case ITEM_CODE_MELON_ICE_CREAM:
            case ITEM_CODE_MINT_ICE_CREAM:
                return true;
            default:
                return false;
        }
    }
}
