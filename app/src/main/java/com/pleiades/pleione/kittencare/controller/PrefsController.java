package com.pleiades.pleione.kittencare.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pleiades.pleione.kittencare.Converter;
import com.pleiades.pleione.kittencare.object.Costume;
import com.pleiades.pleione.kittencare.object.History;
import com.pleiades.pleione.kittencare.object.Item;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;
import static com.pleiades.pleione.kittencare.Config.BUFF_CODE_EXPERIENCE;
import static com.pleiades.pleione.kittencare.Config.BUFF_CODE_FAIL;
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
import static com.pleiades.pleione.kittencare.Config.COSTUME_TYPE_FREE;
import static com.pleiades.pleione.kittencare.Config.COSTUME_TYPE_PAID;
import static com.pleiades.pleione.kittencare.Config.COSTUME_TYPE_SPECIAL;
import static com.pleiades.pleione.kittencare.Config.EXPERIENCE_MAGNIFICATION;
import static com.pleiades.pleione.kittencare.Config.HISTORY_SIZE_MAX;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_COSTUME_FOUND;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_COSTUME_REWARD;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_ITEM_FOUND;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_ITEM_REWARD;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_ITEM_USED;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_LEVEL_UP;
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
import static com.pleiades.pleione.kittencare.Config.ITEM_TYPE_CONSUMPTION;
import static com.pleiades.pleione.kittencare.Config.KEY_BUFF;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_CHOCO;
import static com.pleiades.pleione.kittencare.Config.KEY_EXPERIENCE;
import static com.pleiades.pleione.kittencare.Config.KEY_HAPPINESS;
import static com.pleiades.pleione.kittencare.Config.KEY_HISTORY_ARRAY_LIST;
import static com.pleiades.pleione.kittencare.Config.KEY_ITEM_ARRAY_LIST;
import static com.pleiades.pleione.kittencare.Config.KEY_ITEM_PLEIONE_DOLL;
import static com.pleiades.pleione.kittencare.Config.KEY_ITEM_SCRATCHER;
import static com.pleiades.pleione.kittencare.Config.KEY_LEVEL;
import static com.pleiades.pleione.kittencare.Config.KEY_USE_CHERRY_ICE_CREAM;
import static com.pleiades.pleione.kittencare.Config.KEY_USE_CHOCOLATE_ICE_CREAM;
import static com.pleiades.pleione.kittencare.Config.KEY_USE_GREEN_TEA_ICE_CREAM;
import static com.pleiades.pleione.kittencare.Config.KEY_USE_ICE_CREAM_CAKE;
import static com.pleiades.pleione.kittencare.Config.KEY_USE_MELON_ICE_CREAM;
import static com.pleiades.pleione.kittencare.Config.KEY_USE_MINT_ICE_CREAM;
import static com.pleiades.pleione.kittencare.Config.KEY_WORN_ARRAY_LIST;
import static com.pleiades.pleione.kittencare.Config.LEVEL_MAX;
import static com.pleiades.pleione.kittencare.Config.PREFS;
import static com.pleiades.pleione.kittencare.Config.RANDOM_BOUND_ITEM_FOUND;
import static com.pleiades.pleione.kittencare.Config.REWARD_TYPE_ADVERTISEMENT_ITEM;
import static com.pleiades.pleione.kittencare.Config.REWARD_TYPE_GAME_ITEM_EASY;
import static com.pleiades.pleione.kittencare.Config.REWARD_TYPE_GAME_ITEM_HARD;

// TODO update all
public class PrefsController {
    private final Context context;

    public PrefsController(Context context) {
        this.context = context;
    }

    // put
    private void putHistoryArrayListPrefs(ArrayList<History> historyArrayList) {
        putJsonPrefs(KEY_HISTORY_ARRAY_LIST, new Gson().toJson(historyArrayList));
    }

    public void putItemArrayListPrefs(ArrayList<Item> itemArrayList) {
        putJsonPrefs(KEY_ITEM_ARRAY_LIST, new Gson().toJson(itemArrayList));
    }

    private void putWornArrayListPrefs(ArrayList<Integer> wornArrayList) {
        putJsonPrefs(KEY_WORN_ARRAY_LIST, new Gson().toJson(wornArrayList));
    }

    private void putJsonPrefs(String key, String json) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key, json);
        editor.apply();
    }

    // get
    public ArrayList<History> getHistoryArrayListPrefs() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = prefs.getString(KEY_HISTORY_ARRAY_LIST, null);

        if (json != null) {
            Type type = new TypeToken<ArrayList<History>>() {
            }.getType();

            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }

    public ArrayList<Item> getItemArrayListPrefs() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = prefs.getString(KEY_ITEM_ARRAY_LIST, null);

        if (json != null) {
            Type type = new TypeToken<ArrayList<Item>>() {
            }.getType();

            ArrayList<Item> itemArrayList = gson.fromJson(json, type);
            Iterator<Item> iterator = itemArrayList.iterator();
            while (iterator.hasNext()) {
                Item item = iterator.next();
                if (item.itemType == ITEM_TYPE_CONSUMPTION && item.quantity <= 0)
                    iterator.remove();
            }

            return itemArrayList;
        } else
            return new ArrayList<>();
    }

    public ArrayList<Costume> getInitializedCostumeArrayList() {
        ArrayList<Costume> costumeArrayList = new ArrayList<>();

        addCostume(costumeArrayList, COSTUME_CODE_DEFAULT, COSTUME_TYPE_FREE);
        addCostume(costumeArrayList, COSTUME_CODE_PAJAMAS, COSTUME_TYPE_FREE);
        addCostume(costumeArrayList, COSTUME_CODE_SWEATER, COSTUME_TYPE_FREE);
        addCostume(costumeArrayList, COSTUME_CODE_SHIRTS, COSTUME_TYPE_FREE);
        addCostume(costumeArrayList, COSTUME_CODE_BLAZER, COSTUME_TYPE_FREE);
        addCostume(costumeArrayList, COSTUME_CODE_UNIFORM, COSTUME_TYPE_FREE);
        addCostume(costumeArrayList, COSTUME_CODE_HANBOK, COSTUME_TYPE_FREE);
        addCostume(costumeArrayList, COSTUME_CODE_FLEECE, COSTUME_TYPE_FREE);
        addCostume(costumeArrayList, COSTUME_CODE_HOODIE, COSTUME_TYPE_FREE);
        addCostume(costumeArrayList, COSTUME_CODE_JACKET, COSTUME_TYPE_FREE);
        addCostume(costumeArrayList, COSTUME_CODE_COAT, COSTUME_TYPE_FREE);

        addCostume(costumeArrayList, COSTUME_CODE_ALCYONE, COSTUME_TYPE_SPECIAL);
        addCostume(costumeArrayList, COSTUME_CODE_PLEIONE, COSTUME_TYPE_SPECIAL);
        addCostume(costumeArrayList, COSTUME_CODE_SAILOR, COSTUME_TYPE_SPECIAL);
        addCostume(costumeArrayList, COSTUME_CODE_GAME_MACHINE, COSTUME_TYPE_SPECIAL);
        addCostume(costumeArrayList, COSTUME_CODE_GIFT, COSTUME_TYPE_SPECIAL);
        addCostume(costumeArrayList, COSTUME_CODE_CHOCO, COSTUME_TYPE_SPECIAL);
        addCostume(costumeArrayList, COSTUME_CODE_2021, COSTUME_TYPE_SPECIAL);

        addCostume(costumeArrayList, COSTUME_CODE_DINOSAUR, COSTUME_TYPE_PAID);
        addCostume(costumeArrayList, COSTUME_CODE_BEE, COSTUME_TYPE_PAID);
        addCostume(costumeArrayList, COSTUME_CODE_PENGUIN, COSTUME_TYPE_PAID);
        addCostume(costumeArrayList, COSTUME_CODE_SUNFLOWER, COSTUME_TYPE_PAID);
        addCostume(costumeArrayList, COSTUME_CODE_MAGICIAN, COSTUME_TYPE_PAID);
        addCostume(costumeArrayList, COSTUME_CODE_ASTRONAUT, COSTUME_TYPE_PAID);
        addCostume(costumeArrayList, COSTUME_CODE_MAID, COSTUME_TYPE_PAID);
        addCostume(costumeArrayList, COSTUME_CODE_ALCHEMIST, COSTUME_TYPE_PAID);
        addCostume(costumeArrayList, COSTUME_CODE_SEER, COSTUME_TYPE_PAID);

        return costumeArrayList;
    }

    public ArrayList<Costume> getUnlockedCostumeArrayList() {
        ArrayList<Costume> costumeArrayList = getInitializedCostumeArrayList();

        for (int i = costumeArrayList.size() - 1; i >= 0; i--) {
            Costume costume = costumeArrayList.get(i);
            if (!costume.isUnlocked)
                costumeArrayList.remove(costume);
        }

        return costumeArrayList;
    }

    public ArrayList<Integer> getWornArrayListPrefs() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = prefs.getString(KEY_WORN_ARRAY_LIST, null);

        if (json != null) {
            Type type = new TypeToken<ArrayList<Integer>>() {
            }.getType();

            return gson.fromJson(json, type);
        } else
            return new ArrayList<>();
    }

    // add
    public void addHistoryPrefs(int historyType, int reference) {
        ArrayList<History> historyArrayList = getHistoryArrayListPrefs();

        // initialize history object
        History history = new History(historyType, reference);
        Calendar calendar = Calendar.getInstance();
        history.month = calendar.get(Calendar.MONTH) + 1;
        history.date = calendar.get(Calendar.DATE);
        history.hour = calendar.get(Calendar.HOUR_OF_DAY);
        history.minute = calendar.get(Calendar.MINUTE);

        if (historyArrayList.size() == HISTORY_SIZE_MAX)
            historyArrayList.remove(HISTORY_SIZE_MAX - 1);

        historyArrayList.add(0, history);
        putHistoryArrayListPrefs(historyArrayList);
    }

    public void addItemPrefs(int itemCode, int quantity) {
        // initialize item array list
        ArrayList<Item> itemArrayList = getItemArrayListPrefs();

        // initialize item type
        int itemType = Converter.getItemType(itemCode);

        // initialize item index
        int itemIndex = -1; // default is item not found
        for (int i = 0; i < itemArrayList.size(); i++) {
            if (itemArrayList.get(i).itemCode == itemCode) {
                itemIndex = i;
                break;
            }
        }

        // case item found
        if (itemIndex != -1) {
            // case consumption
            if (itemType == ITEM_TYPE_CONSUMPTION)
                itemArrayList.get(itemIndex).quantity += quantity;
        }
        // case item not found
        else {
            // case charm or toy
            if (itemType != ITEM_TYPE_CONSUMPTION) {
                SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                editor.putBoolean(Converter.getItemPrefsKey(itemCode), true);
                editor.apply();
            }

            // initialize and add item
            Item item = new Item(itemCode, itemType, quantity);
            itemArrayList.add(item);
        }

        // put item array list prefs
        putItemArrayListPrefs(itemArrayList);
    }

    public int addRandomItemPrefs(int rewardType) {
        Random random = new Random();
        int worthRandomValue = random.nextInt(RANDOM_BOUND_ITEM_FOUND);
        int kindsRandomValue = random.nextInt(5);
        int specialPercentage, star5Percentage, star4Percentage, star3Percentage, star2Percentage;
        int itemCode;

        // initialize percentage according to reward type
        switch (rewardType) {
            case REWARD_TYPE_ADVERTISEMENT_ITEM:
            case REWARD_TYPE_GAME_ITEM_HARD:
                specialPercentage = 5;
                star5Percentage = 5 + 10;
                star4Percentage = 5 + 10 + 15;
                star3Percentage = 5 + 10 + 15 + 20;
                star2Percentage = 5 + 10 + 15 + 20 + 50;
                break;
            default:
                specialPercentage = 1;
                star5Percentage = 1 + 5;
                star4Percentage = 1 + 5 + 10;
                star3Percentage = 1 + 5 + 10 + 15;
                star2Percentage = 1 + 5 + 10 + 15 + 20;
                break;
        }

        // initialize item code to get
        if (worthRandomValue < specialPercentage) {
            if (kindsRandomValue == 0)
                itemCode = ITEM_CODE_PLEIONE_DOLL;
            else if (kindsRandomValue == 1)
                itemCode = ITEM_CODE_SCRATCHER;
            else if (kindsRandomValue == 2)
                itemCode = ITEM_CODE_TOWER;
            else if (kindsRandomValue == 3)
                itemCode = ITEM_CODE_ALCHEMY;
            else
                itemCode = ITEM_CODE_CRYSTAL_BALL;
        } else if (worthRandomValue < star5Percentage) {
            itemCode = kindsRandomValue < 1 ? ITEM_CODE_GREEN_TEA_CAKE : ITEM_CODE_GREEN_TEA_ICE_CREAM;
        } else if (worthRandomValue < star4Percentage) {
            itemCode = kindsRandomValue < 1 ? ITEM_CODE_CHOCOLATE_CAKE : ITEM_CODE_CHOCOLATE_ICE_CREAM;
        } else if (worthRandomValue < star3Percentage) {
            itemCode = kindsRandomValue < 1 ? ITEM_CODE_CHERRY_CAKE : ITEM_CODE_CHERRY_ICE_CREAM;
        } else if (worthRandomValue < star2Percentage) {
            itemCode = kindsRandomValue < 1 ? ITEM_CODE_MELON_CAKE : ITEM_CODE_MELON_ICE_CREAM;
        } else {
            itemCode = kindsRandomValue < 1 ? ITEM_CODE_MINT_CAKE : ITEM_CODE_MINT_ICE_CREAM;
        }

        // add history
        switch (rewardType) {
            case REWARD_TYPE_ADVERTISEMENT_ITEM:
            case REWARD_TYPE_GAME_ITEM_HARD:
            case REWARD_TYPE_GAME_ITEM_EASY:
                addHistoryPrefs(HISTORY_TYPE_ITEM_REWARD, itemCode);
                break;
            default:
                addHistoryPrefs(HISTORY_TYPE_ITEM_FOUND, itemCode);
                break;
        }

        // add item prefs
        addItemPrefs(itemCode, 1);

        // return item code
        return itemCode;
    }

    public void addWornPrefs(int costumeCode) {
        ArrayList<Integer> wornArrayList = getWornArrayListPrefs();

        if (!wornArrayList.contains(costumeCode))
            wornArrayList.add(costumeCode);
        putWornArrayListPrefs(wornArrayList);
    }

    private void addCostume(ArrayList<Costume> costumeArrayList, int costumeCode, int costumeType) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // initialize costume object
        Costume costume = new Costume(costumeCode, costumeType);

        // case prefs true
        if (prefs.getBoolean(Converter.getCostumePrefsKey(costumeCode), false)) {
            costume.isUnlocked = true;
        } else {
            // case default
            if (costumeCode == COSTUME_CODE_DEFAULT) {
                costume.isUnlocked = true;
            }

            // case alcyone
            else if (costumeCode == COSTUME_CODE_ALCYONE) {
                Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.pleiades.pleione.alcyone");
                if (intent != null)
                    costume.isUnlocked = true;
            }

            // case free
            else if (costumeType == COSTUME_TYPE_FREE) {
                if (prefs.getInt(KEY_LEVEL, 1) >= Converter.getCostumeLevel(costumeCode))
                    costume.isUnlocked = true;
            }

            // case just unlocked
            if (costume.isUnlocked) {
                // apply costume
                editor.putBoolean(Converter.getCostumePrefsKey(costumeCode), true);
                editor.apply();

                if (costumeCode != COSTUME_CODE_DEFAULT) {
                    // add history
                    addHistoryPrefs(HISTORY_TYPE_COSTUME_FOUND, costumeCode);
                }
            }
        }

        // add costume to array list
        costumeArrayList.add(costume);
    }

    // others
    public void useItem(ArrayList<Item> itemArrayList, int position) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // discount item quantity
        itemArrayList.get(position).quantity--;

        // put item array list prefs
        putItemArrayListPrefs(itemArrayList);

        // add item use history
        addHistoryPrefs(HISTORY_TYPE_ITEM_USED, itemArrayList.get(position).itemCode);

        // initialize level, experience, max experience
        int level = prefs.getInt(KEY_LEVEL, 1);
        int experience = prefs.getInt(KEY_EXPERIENCE, 0);
        int maxExperience = (int) (100 * (Math.pow(EXPERIENCE_MAGNIFICATION, level - 1)));

        // initialize increasing experience
        double increasingExperience;
        switch (itemArrayList.get(position).itemCode) {
            case ITEM_CODE_ICE_CREAM_CAKE:
                increasingExperience = maxExperience;
                editor.putBoolean(KEY_USE_ICE_CREAM_CAKE, true);
                break;
            case ITEM_CODE_GREEN_TEA_ICE_CREAM:
                increasingExperience = 300;
                editor.putBoolean(KEY_USE_GREEN_TEA_ICE_CREAM, true);
                break;
            case ITEM_CODE_CHOCOLATE_ICE_CREAM:
                increasingExperience = 200;
                editor.putBoolean(KEY_USE_CHOCOLATE_ICE_CREAM, true);
                break;
            case ITEM_CODE_CHERRY_ICE_CREAM:
                increasingExperience = 100;
                editor.putBoolean(KEY_USE_CHERRY_ICE_CREAM, true);
                break;
            case ITEM_CODE_MELON_ICE_CREAM:
                increasingExperience = 50;
                editor.putBoolean(KEY_USE_MELON_ICE_CREAM, true);
                break;
            case ITEM_CODE_MINT_ICE_CREAM:
                increasingExperience = 30;
                editor.putBoolean(KEY_USE_MINT_ICE_CREAM, true);
                break;
            case ITEM_CODE_GREEN_TEA_CAKE:
                increasingExperience = Math.ceil((double) maxExperience / 100 * 50);
                break;
            case ITEM_CODE_CHOCOLATE_CAKE:
                increasingExperience = Math.ceil((double) maxExperience / 100 * 30);
                break;
            case ITEM_CODE_CHERRY_CAKE:
                increasingExperience = Math.ceil((double) maxExperience / 100 * 20);
                break;
            case ITEM_CODE_MELON_CAKE:
                increasingExperience = Math.ceil((double) maxExperience / 100 * 10);
                break;
            case ITEM_CODE_MINT_CAKE:
                increasingExperience = Math.ceil((double) maxExperience / 100 * 5);
                break;
            default:
                increasingExperience = 0;
                break;
        }
        if (prefs.getBoolean(KEY_ITEM_PLEIONE_DOLL, false))
            increasingExperience += increasingExperience / 100 * 15;
        if (prefs.getBoolean(KEY_ITEM_SCRATCHER, false))
            increasingExperience += increasingExperience / 100 * 10;
        if (prefs.getInt(KEY_BUFF, BUFF_CODE_FAIL) == BUFF_CODE_EXPERIENCE)
            increasingExperience += increasingExperience / 100 * 10;

        // add increasing experience
        experience += increasingExperience;

        // case level up
        while (experience >= maxExperience) {
            if (level == LEVEL_MAX) {
                experience = 0;
                break;
            } else {
                // count level
                level++;

                // calculate experience
                experience = experience - maxExperience;

                // calculate max experience
                maxExperience = (int) (100 * (Math.pow(EXPERIENCE_MAGNIFICATION, level - 1)));

                // add level up history
                addHistoryPrefs(HISTORY_TYPE_LEVEL_UP, level);

                // initialize costume
                initializeFreeCostumeArrayList();
            }
        }

        // apply level, experience
        editor.putInt(KEY_LEVEL, level);
        editor.putInt(KEY_EXPERIENCE, level == LEVEL_MAX ? 0 : experience);
        editor.apply();

        // apply happiness
        int happiness = prefs.getInt(KEY_HAPPINESS, 100);
        happiness = Math.min(100, happiness + Converter.getConsumptionItemRank(itemArrayList.get(position).itemCode));
        editor.putInt(KEY_HAPPINESS, happiness);
        editor.apply();

        // unlock choco costume
        if (prefs.getBoolean(KEY_USE_GREEN_TEA_ICE_CREAM, false))
            if (prefs.getBoolean(KEY_USE_CHOCOLATE_ICE_CREAM, false))
                if (prefs.getBoolean(KEY_USE_CHERRY_ICE_CREAM, false))
                    if (prefs.getBoolean(KEY_USE_MELON_ICE_CREAM, false))
                        if (prefs.getBoolean(KEY_USE_MINT_ICE_CREAM, false)) {
                            if (!prefs.getBoolean(KEY_COSTUME_CHOCO, false)) {
                                editor.putBoolean(KEY_COSTUME_CHOCO, true);
                                editor.apply();

                                addHistoryPrefs(HISTORY_TYPE_COSTUME_FOUND, COSTUME_CODE_CHOCO);
                            }
                        }
    }

    public int alchemyItem(int iceCreamCode, int quantity) {
        // initialize item array list
        ArrayList<Item> itemArrayList = getItemArrayListPrefs();

        for (int i = 0; i < itemArrayList.size(); i++) {
            Item item = itemArrayList.get(i);

            if (item.itemCode == iceCreamCode) {
                // use ice cream
                itemArrayList.get(i).quantity -= quantity;
                putItemArrayListPrefs(itemArrayList);

                // get cake
                int cakeCode = Converter.getCakeCode(iceCreamCode);
                addItemPrefs(cakeCode, 1);
                addHistoryPrefs(HISTORY_TYPE_ITEM_REWARD, cakeCode);

                // return cake item code
                return cakeCode;
            }
        }

        return -1;
    }

    public void wasteItem(int itemCode, int quantity) {
        // initialize item array list
        ArrayList<Item> itemArrayList = getItemArrayListPrefs();

        for (int i = 0; i < itemArrayList.size(); i++) {
            Item item = itemArrayList.get(i);

            if (item.itemCode == itemCode) {
                // use ice cream
                itemArrayList.get(i).quantity -= quantity;
                putItemArrayListPrefs(itemArrayList);
                break;
            }
        }
    }

    public int unlockRandomRewardCostumePrefs() {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        ArrayList<Integer> lockedPaidCostumePositionList = new ArrayList<>();
        ArrayList<Costume> costumeList = getInitializedCostumeArrayList();

        // find locked paid costumes
        for (int i = 0; i < costumeList.size(); i++) {
            Costume costume = costumeList.get(i);
            if (costume.costumeType == COSTUME_TYPE_PAID && !costume.isUnlocked)
                lockedPaidCostumePositionList.add(i);
        }

        // case all paid costumes unlocked
        if (lockedPaidCostumePositionList.size() == 0)
            return -1;

        // decide unlock position
        int unlockPosition = lockedPaidCostumePositionList.get((new Random()).nextInt(lockedPaidCostumePositionList.size()));

        // apply costume
        int costumeCode = costumeList.get(unlockPosition).costumeCode;
        editor.putBoolean(Converter.getCostumePrefsKey(costumeCode), true);
        editor.apply();

        // add history
        addHistoryPrefs(HISTORY_TYPE_COSTUME_REWARD, costumeCode);

        return costumeCode;
    }

    public void initializeFreeCostumeArrayList() {
        ArrayList<Costume> costumeArrayList = new ArrayList<>();

        addCostume(costumeArrayList, COSTUME_CODE_DEFAULT, COSTUME_TYPE_FREE);
        addCostume(costumeArrayList, COSTUME_CODE_PAJAMAS, COSTUME_TYPE_FREE);
        addCostume(costumeArrayList, COSTUME_CODE_SWEATER, COSTUME_TYPE_FREE);
        addCostume(costumeArrayList, COSTUME_CODE_SHIRTS, COSTUME_TYPE_FREE);
        addCostume(costumeArrayList, COSTUME_CODE_BLAZER, COSTUME_TYPE_FREE);
        addCostume(costumeArrayList, COSTUME_CODE_UNIFORM, COSTUME_TYPE_FREE);
        addCostume(costumeArrayList, COSTUME_CODE_HANBOK, COSTUME_TYPE_FREE);
        addCostume(costumeArrayList, COSTUME_CODE_FLEECE, COSTUME_TYPE_FREE);
        addCostume(costumeArrayList, COSTUME_CODE_HOODIE, COSTUME_TYPE_FREE);
        addCostume(costumeArrayList, COSTUME_CODE_JACKET, COSTUME_TYPE_FREE);
        addCostume(costumeArrayList, COSTUME_CODE_COAT, COSTUME_TYPE_FREE);
    }
}