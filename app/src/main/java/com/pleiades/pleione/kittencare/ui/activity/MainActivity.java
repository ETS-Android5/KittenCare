package com.pleiades.pleione.kittencare.ui.activity;

import static android.widget.Toast.LENGTH_SHORT;
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

import android.app.Activity;
import android.content.Context;
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

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pleiades.pleione.kittencare.Converter;
import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.controller.AdvertisementController;
import com.pleiades.pleione.kittencare.controller.DeviceController;
import com.pleiades.pleione.kittencare.controller.PrefsController;
import com.pleiades.pleione.kittencare.controller.ToastController;
import com.pleiades.pleione.kittencare.object.Costume;
import com.pleiades.pleione.kittencare.ui.activity.settings.TutorialActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// TODO update game
public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    private Activity activity;
    private BillingClient billingClient;
    private ArrayList<String> paidCostumeKeyArrayList;
    private SkuDetails[] paidCostumeSkuDetailsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        // initialize activity
        activity = MainActivity.this;

        // initialize paid costume key arraylist
        paidCostumeKeyArrayList = new PrefsController(activity).getPaidCostumeKeyArrayList();

        // initialize billing client
        billingClient = BillingClient.newBuilder(activity).setListener(this).enablePendingPurchases().build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    initializePaidCostumeSkuDetailsArray();
                    initializePurchased();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
            }
        });

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

    private void initializePaidCostumeSkuDetailsArray() {
        // initialize sku details array
        paidCostumeSkuDetailsArray = new SkuDetails[paidCostumeKeyArrayList.size()];
        SkuDetailsParams.Builder skuDetailsParamsBuilder = SkuDetailsParams.newBuilder();
        skuDetailsParamsBuilder.setSkusList(paidCostumeKeyArrayList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(skuDetailsParamsBuilder.build(), (billingResult, skuDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
                if (skuDetailsList != null)
                    for (SkuDetails skuDetails : skuDetailsList) {
                        String productId = skuDetails.getSku();
                        for (int i = 0; i < paidCostumeKeyArrayList.size(); i++) {
                            if (productId.equals(paidCostumeKeyArrayList.get(i))) {
                                paidCostumeSkuDetailsArray[i] = skuDetails;
                                break;
                            }
                        }
                    }
        });
    }

    private void initializePurchased() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();

        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, (billingResult, purchaseList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
                for (Purchase purchase : purchaseList) {
                    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                        if (purchase.isAcknowledged()) {
                            String productId = purchase.getSkus().get(0);
                            for (int i = 0; i < paidCostumeKeyArrayList.size(); i++) {
                                if (productId.equals(paidCostumeKeyArrayList.get(i))) {
                                    editor.putBoolean(productId, true);
                                    editor.apply();
                                    break;
                                }
                            }
                        }
                    }
                }
        });
    }

    public void purchaseCostume(int costumeCode) {
        String costumeKey = Converter.getCostumePrefsKey(costumeCode);
        for (int i = 0; i < paidCostumeKeyArrayList.size(); i++) {
            if (paidCostumeKeyArrayList.get(i).equals(costumeKey)) {
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(paidCostumeSkuDetailsArray[i]).build();
                billingClient.launchBillingFlow(activity, billingFlowParams);
                break;
            }
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
            AdvertisementController.ticketRewardedAd.show(activity, rewardItem -> {
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
            });
        }
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchaseList) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchaseList != null) {
            for (Purchase purchase : purchaseList) {
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    if (!purchase.isAcknowledged()) {
                        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
                        billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult1 -> {
                            if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                                String productId = purchase.getSkus().get(0);
                                for (int i = 0; i < paidCostumeKeyArrayList.size(); i++) {
                                    if (productId.equals(paidCostumeKeyArrayList.get(i))) {
                                        // apply purchased costume prefs
                                        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                                        editor.putBoolean(productId, true);
                                        editor.apply();

                                        // add history
                                        new PrefsController(activity).addHistoryPrefs(HISTORY_TYPE_COSTUME_FOUND, Converter.getCostumeCode(productId));

                                        break;
                                    }
                                }
                            }
                        });
                    }
                }
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED)
            Toast.makeText(activity, R.string.toast_flutter, LENGTH_SHORT).show();
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
