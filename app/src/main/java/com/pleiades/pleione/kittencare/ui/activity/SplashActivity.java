package com.pleiades.pleione.kittencare.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pleiades.pleione.kittencare.Converter;
import com.pleiades.pleione.kittencare.controller.AdvertisementController;
import com.pleiades.pleione.kittencare.controller.PrefsController;
import com.pleiades.pleione.kittencare.object.Costume;
import com.pleiades.pleione.kittencare.object.Item;

import java.util.ArrayList;

import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_ITEM_UPDATE_REWARD;
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
import static com.pleiades.pleione.kittencare.Config.KEY_BUFF;
import static com.pleiades.pleione.kittencare.Config.KEY_EXPERIENCE;
import static com.pleiades.pleione.kittencare.Config.KEY_GAME_TICKET_DINOSAUR;
import static com.pleiades.pleione.kittencare.Config.KEY_GAME_TICKET_PAJAMAS;
import static com.pleiades.pleione.kittencare.Config.KEY_GAME_TICKET_PLEIADES;
import static com.pleiades.pleione.kittencare.Config.KEY_HISTORY_SIZE_LIMIT;
import static com.pleiades.pleione.kittencare.Config.KEY_LEVEL;
import static com.pleiades.pleione.kittencare.Config.KEY_USER_LAST_VERSION_CODE;
import static com.pleiades.pleione.kittencare.Config.LEVEL_MAX;
import static com.pleiades.pleione.kittencare.Config.PREFS;
import static com.pleiades.pleione.kittencare.Config.TICKET_MAX;

public class SplashActivity extends AppCompatActivity {
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize activity
        activity = SplashActivity.this;

        // create and load rewarded advertisements
        AdvertisementController.loadRandomBoxRewardedAd(activity, false);
        AdvertisementController.loadTicketRewardedAd(activity, false);

        // update app version
        updateAppVersion();

        // set developer mode
        setDeveloperMode();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        finish();
    }

    // TODO delete
    private void setDeveloperMode() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

//        editor.putInt(KEY_HAPPINESS, 40);
//        editor.putString(KEY_LAST_HIDE_DATE_STRING, "21/04/15 14:45:04");

        PrefsController prefsController = new PrefsController(activity);
        prefsController.addItemPrefs(ITEM_CODE_ICE_CREAM_CAKE, 50);
        prefsController.addItemPrefs(ITEM_CODE_GREEN_TEA_ICE_CREAM, 1);
        prefsController.addItemPrefs(ITEM_CODE_CHOCOLATE_ICE_CREAM, 1);
        prefsController.addItemPrefs(ITEM_CODE_CHERRY_ICE_CREAM, 1);
        prefsController.addItemPrefs(ITEM_CODE_MELON_ICE_CREAM, 1);
        prefsController.addItemPrefs(ITEM_CODE_MINT_ICE_CREAM, 1);
        prefsController.addItemPrefs(ITEM_CODE_GREEN_TEA_CAKE, 1);
        prefsController.addItemPrefs(ITEM_CODE_CHOCOLATE_CAKE, 1);
        prefsController.addItemPrefs(ITEM_CODE_CHERRY_CAKE, 1);
        prefsController.addItemPrefs(ITEM_CODE_MELON_CAKE, 1);
        prefsController.addItemPrefs(ITEM_CODE_MINT_CAKE, 1);
        prefsController.addItemPrefs(ITEM_CODE_ALCHEMY, 1);
        prefsController.addItemPrefs(ITEM_CODE_CRYSTAL_BALL, 1);
        prefsController.addItemPrefs(ITEM_CODE_PLEIONE_DOLL, 1);
        prefsController.addItemPrefs(ITEM_CODE_SCRATCHER, 1);
        prefsController.addItemPrefs(ITEM_CODE_TOWER, 1);
        editor.putInt(KEY_GAME_TICKET_PAJAMAS, TICKET_MAX);
        editor.putInt(KEY_GAME_TICKET_PLEIADES, TICKET_MAX);
        editor.putInt(KEY_GAME_TICKET_DINOSAUR, TICKET_MAX);
        editor.remove(KEY_BUFF);
        editor.apply();
    }

    // TODO check
    private void updateAppVersion() {
        try {
            SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            // initialize version code
            int existVersionCode = prefs.getInt(KEY_USER_LAST_VERSION_CODE, 1);
            int latestVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;

            // case version code is not latest
            if (existVersionCode != latestVersionCode) {
                // respond to app version
                respond(existVersionCode);

                // add update reward items
                PrefsController prefsController = new PrefsController(activity);
                prefsController.addItemPrefs(ITEM_CODE_ICE_CREAM_CAKE, 1);
                prefsController.addHistoryPrefs(HISTORY_TYPE_ITEM_UPDATE_REWARD, ITEM_CODE_ICE_CREAM_CAKE);

                // apply app version
                editor.putInt(KEY_USER_LAST_VERSION_CODE, latestVersionCode);
                editor.apply();
            }
        } catch (Exception e) {
            // ignore exception block
        }
    }

    private void respond(int existVersionCode) {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        PrefsController prefsController = new PrefsController(activity);

        // case ~ 1.1.0
        if (existVersionCode <= 19) {
            for (Costume costume : prefsController.getInitializedCostumeArrayList()) {
                if (costume.isUnlocked)
                    prefsController.addWornPrefs(costume.costumeCode);
            }
        }

        // case ~ 1.4.1
        if (existVersionCode <= 24) {
            ArrayList<Item> itemArrayList = prefsController.getItemArrayListPrefs();
            for (int i = 0; i < itemArrayList.size(); i++)
                itemArrayList.get(i).itemType = Converter.getItemType(itemArrayList.get(i).itemCode);
            prefsController.putItemArrayListPrefs(itemArrayList);
        }

        // case ~ 1.4.2
        if (existVersionCode <= 25) {
            int level = prefs.getInt(KEY_LEVEL, 1);
            if (level == LEVEL_MAX) {
                editor.putInt(KEY_EXPERIENCE, 0);
                editor.apply();
            }
        }

        // case ~ 1.7.0
        if (existVersionCode <= 30) {
            editor.remove(KEY_HISTORY_SIZE_LIMIT);
            editor.apply();
        }
    }
}
