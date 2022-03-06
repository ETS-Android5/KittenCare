package com.pleiades.pleione.kittencare.controller;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.pleiades.pleione.kittencare.R;

import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INTERNAL_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INVALID_REQUEST;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NETWORK_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL;

public class AdvertisementController {
    public static RewardedAd randomBoxRewardedAd, ticketRewardedAd;

    public static void loadRandomBoxRewardedAd(final Context context, final boolean pushErrorToast) {
        // initialize full screen content callback
        final FullScreenContentCallback fullScreenContentCallback =
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        randomBoxRewardedAd = null;
                    }
                };

        RewardedAd.load(context, context.getString(R.string.ad_unit_id_random_box), new AdRequest.Builder().build(), new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                super.onAdLoaded(rewardedAd);

                // initialize random box rewarded ad
                randomBoxRewardedAd = rewardedAd;
                randomBoxRewardedAd.setFullScreenContentCallback(fullScreenContentCallback);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);

                if (pushErrorToast)
                    pushErrorToast(context, loadAdError.getCode());
            }
        });
    }

    public static void loadTicketRewardedAd(final Context context, final boolean pushErrorToast) {
        // initialize full screen content callback
        final FullScreenContentCallback fullScreenContentCallback =
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        ticketRewardedAd = null;
                    }
                };

        RewardedAd.load(context, context.getString(R.string.ad_unit_id_ticket), new AdRequest.Builder().build(), new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                super.onAdLoaded(rewardedAd);

                // initialize ticket rewarded ad
                ticketRewardedAd = rewardedAd;
                ticketRewardedAd.setFullScreenContentCallback(fullScreenContentCallback);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);

                if (pushErrorToast)
                    pushErrorToast(context, loadAdError.getCode());
            }
        });
    }

    private static void pushErrorToast(Context context, int errorCode) {
        String message;

        if (errorCode == ERROR_CODE_INTERNAL_ERROR)
            message = context.getString(R.string.toast_error_internal);
        else if (errorCode == ERROR_CODE_INVALID_REQUEST)
            message = context.getString(R.string.toast_error_request);
        else if (errorCode == ERROR_CODE_NETWORK_ERROR)
            message = context.getString(R.string.toast_error_network);
        else if (errorCode == ERROR_CODE_NO_FILL)
            message = context.getString(R.string.toast_error_no_fill);
        else
            message = context.getString(R.string.toast_error);

        message = message + "\n" + context.getString(R.string.toast_try_later);
        new ToastController(context).showToast(message, Toast.LENGTH_SHORT);
    }
}
