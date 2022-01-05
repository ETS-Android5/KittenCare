package com.pleiades.pleione.kittencare.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pleiades.pleione.kittencare.Converter;
import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.controller.AdvertisementController;
import com.pleiades.pleione.kittencare.controller.PrefsController;
import com.pleiades.pleione.kittencare.controller.ToastController;
import com.pleiades.pleione.kittencare.object.Costume;
import com.pleiades.pleione.kittencare.ui.activity.settings.TutorialActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.pleiades.pleione.kittencare.Config.COSTUME_TYPE_PAID;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_COSTUME_FOUND;
import static com.pleiades.pleione.kittencare.Config.KEY_BUFF;
import static com.pleiades.pleione.kittencare.Config.KEY_GAME_TICKET_DINOSAUR;
import static com.pleiades.pleione.kittencare.Config.KEY_GAME_TICKET_PAJAMAS;
import static com.pleiades.pleione.kittencare.Config.KEY_GAME_TICKET_PLEIADES;
import static com.pleiades.pleione.kittencare.Config.KEY_IS_TUTORIAL_COMPLETED;
import static com.pleiades.pleione.kittencare.Config.KEY_LAST_LAUNCH_DAY;
import static com.pleiades.pleione.kittencare.Config.PREFS;
import static com.pleiades.pleione.kittencare.Config.TICKET_MAX;

// TODO update game
public class MainActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private Activity activity;
    private BillingProcessor billingProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        // initialize activity
        activity = MainActivity.this;

        // initialize billing processor
        String RSAkey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg+sKsztXEixOxdSP20Sbk7ghcVxVhl8fBC1CIo+lAf4X+crDP2KSW7RSfUSZM/qDfn25uEfzLubhJ0KoPkkYVtM4IzaWwLOSTxvyswumiVEPe/bVGFOzT2AfHFzgodL+S6eGeC6Ai5BwWmXW8GNBOHLvZ8gtBO3cwad3JS2Jy9yEdaak6EJn3J8riJXCQYoAskMb/EeO9juHht6CzrzRrhj5/8Ac2MY/IvKQXUnkl3+uhjaziuHPuaG5DL0JVWcK10BBUQOT0CZZPrFvqn57pV7y9dFI4+Ncwil11A0Dt5G5kSSxedtNGLbyNcTOoSCWEhYpcJD/a9PkdToFLz/jhQIDAQAB";
        billingProcessor = new BillingProcessor(activity, RSAkey, this);
        billingProcessor.initialize();

        // set navigation color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
            getWindow().setNavigationBarColor(ContextCompat.getColor(activity, R.color.color_navigation_background));

        // initialize appbar
        View appbar = findViewById(R.id.appbar_main);
        Toolbar toolbar = appbar.findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // initialize nav controller
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);

        // set navigation UI with nav controller
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_costume,
                R.id.navigation_item,
                R.id.navigation_setting).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController((BottomNavigationView) findViewById(R.id.navigation_main), navController);

        // start tutorial activity
        if (!prefs.getBoolean(KEY_IS_TUTORIAL_COMPLETED, false)) {
            Intent intent = new Intent(activity, TutorialActivity.class);
            startActivity(intent);
        }
    }

    public void requestTicketRewardedAd() {
        // case ticket rewarded ad is not loaded
        if (AdvertisementController.ticketRewardedAd == null) {
            // load rewarded ad
            AdvertisementController.loadTicketRewardedAd(activity, true);

            // show error toast
            String message = getString(R.string.toast_error_load) + "\n" + getString(R.string.toast_try_later);
            new ToastController(activity).showToast(message, Toast.LENGTH_SHORT);
        }
        // case ticket rewarded ad is loaded
        else {
            AdvertisementController.ticketRewardedAd.show(activity, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // initialize game ticket array list
                    ArrayList<GameTicket> gameTicketArrayList = new ArrayList<>();
                    gameTicketArrayList.add(new GameTicket(KEY_GAME_TICKET_PAJAMAS));
                    gameTicketArrayList.add(new GameTicket(KEY_GAME_TICKET_PLEIADES));
                    gameTicketArrayList.add(new GameTicket(KEY_GAME_TICKET_DINOSAUR));

                    // initialize minimum index
                    int minimumIndex = 0;
                    for (int i = 0; i < gameTicketArrayList.size(); i++) {
                        if (gameTicketArrayList.get(i).count < gameTicketArrayList.get(minimumIndex).count)
                            minimumIndex = i;
                    }

                    // add game ticket
                    gameTicketArrayList.get(minimumIndex).addGameTicket();
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int date = cal.get(Calendar.DATE);

        String lastLaunchDay = prefs.getString(KEY_LAST_LAUNCH_DAY, "");
        String today = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, date);

        // case today is not last launch day
        if (!today.equals(lastLaunchDay)) {
            // remove buff
            editor.remove(KEY_BUFF);
            editor.apply();

            // check auto time
            if (android.provider.Settings.Global.getInt(getContentResolver(), android.provider.Settings.Global.AUTO_TIME, 0) == 1) {
                // initialize game tickets
                int pajamasTickets = prefs.getInt(KEY_GAME_TICKET_PAJAMAS, TICKET_MAX);
                int pleiadesTickets = prefs.getInt(KEY_GAME_TICKET_PLEIADES, TICKET_MAX);
                int dinosaurTickets = prefs.getInt(KEY_GAME_TICKET_DINOSAUR, TICKET_MAX);

                // recharge game tickets
                if (pajamasTickets < TICKET_MAX)
                    editor.putInt(KEY_GAME_TICKET_PAJAMAS, TICKET_MAX);
                if (pleiadesTickets < TICKET_MAX)
                    editor.putInt(KEY_GAME_TICKET_PLEIADES, TICKET_MAX);
                if (dinosaurTickets < TICKET_MAX)
                    editor.putInt(KEY_GAME_TICKET_DINOSAUR, TICKET_MAX);

                // apply last launch day
                editor.putString(KEY_LAST_LAUNCH_DAY, today);
                editor.apply();
            }
        }
    }

    public void purchaseCostume(int costumeCode) {
        billingProcessor.purchase(activity, Converter.getCostumePrefsKey(costumeCode));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (billingProcessor != null)
            billingProcessor.release();

        super.onDestroy();
    }

    @Override
    public void onBillingInitialized() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // load own purchases
        billingProcessor.loadOwnedPurchasesFromGoogle();

        for (Costume costume : new PrefsController(activity).getInitializedCostumeArrayList()) {
            if (costume.costumeType == COSTUME_TYPE_PAID) {
                String key = Converter.getCostumePrefsKey(costume.costumeCode);
                if (billingProcessor.isPurchased(key)) {
                    editor.putBoolean(key, true);
                }
            }
        }

        editor.apply();
    }

    @Override
    public void onPurchaseHistoryRestored() {
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();

        // apply purchased costume prefs
        editor.putBoolean(productId, true); // product Id is same with prefs key
        editor.apply();

        // add history
        new PrefsController(activity).addHistoryPrefs(HISTORY_TYPE_COSTUME_FOUND, Converter.getCostumeCode(productId));
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        new ToastController(activity).showToast(getString(R.string.toast_flutter), Toast.LENGTH_SHORT);
    }

    private class GameTicket {
        public String key;
        public int count;

        public GameTicket(String key) {
            SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

            this.key = key;
            this.count = prefs.getInt(key, TICKET_MAX);
        }

        public void addGameTicket() {
            SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putInt(key, count + 1);
            editor.apply();
        }
    }
}
