package com.pleiades.pleione.kittencare.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.pleiades.pleione.kittencare.Converter;
import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.controller.AdvertisementController;
import com.pleiades.pleione.kittencare.controller.PrefsController;
import com.pleiades.pleione.kittencare.controller.ToastController;

import java.util.Random;

import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_GIFT;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_JUMP_DISTANCE;
import static com.pleiades.pleione.kittencare.Config.DIRECTION_TO_LEFT;
import static com.pleiades.pleione.kittencare.Config.DIRECTION_TO_RIGHT;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_COSTUME_FOUND;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_GIFT;
import static com.pleiades.pleione.kittencare.Config.KEY_JUMP_DISTANCE;
import static com.pleiades.pleione.kittencare.Config.KEY_REWARD_EARNED_COUNT;
import static com.pleiades.pleione.kittencare.Config.PREFS;
import static com.pleiades.pleione.kittencare.Config.RANDOM_BOUND_REWARD;
import static com.pleiades.pleione.kittencare.Config.REPEAT_COUNT_BOX_SHIVER;
import static com.pleiades.pleione.kittencare.Config.REWARD_TYPE_ADVERTISEMENT_COSTUME;
import static com.pleiades.pleione.kittencare.Config.REWARD_TYPE_ADVERTISEMENT_ITEM;
import static com.pleiades.pleione.kittencare.controller.AnimationController.calculateDurationGravity;

public class AdvertisementActivity extends AppCompatActivity {
    private Activity activity;

    private int rewardType, rewardReference;
    private boolean isEarned;

    private ImageView boxImageView;
    private TextView titleTextView, contentsTextView;
    private Button button;
    private float boxOriginPositionX, boxOriginPositionY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement);

        // initialize activity
        activity = AdvertisementActivity.this;

        // set navigation color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
            getWindow().setNavigationBarColor(ContextCompat.getColor(activity, R.color.color_navigation_background));

        // initialize appbar
        View appbar = findViewById(R.id.appbar_advertisement);
        Toolbar toolbar = appbar.findViewById(R.id.toolbar_sub);
        setSupportActionBar(toolbar);

        // initialize action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // initialize box image view
        boxImageView = findViewById(R.id.box_advertisement);
        boxImageView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // initialize origin box position
                        boxOriginPositionX = boxImageView.getX();
                        boxOriginPositionY = boxImageView.getY();

                        // remove layout listener
                        boxImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

        // initialize text views
        titleTextView = findViewById(R.id.title_advertisement);
        contentsTextView = findViewById(R.id.contents_advertisement);


        // initialize ad button
        button = findViewById(R.id.button_advertisement);
        button.setOnClickListener(view -> {
            // case random box rewarded ad is not loaded
            if (AdvertisementController.randomBoxRewardedAd == null) {
                // load rewarded ad
                AdvertisementController.loadRandomBoxRewardedAd(activity, true);

                // show error toast
                String message = getString(R.string.toast_error_load) + "\n" + getString(R.string.toast_try_later);
                new ToastController(activity).showToast(message, Toast.LENGTH_SHORT);
            }
            // case random box rewarded ad is loaded
            else {
                // show random box rewarded ad
                AdvertisementController.randomBoxRewardedAd.show(activity, rewardItem -> {
                    SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    PrefsController prefsController = new PrefsController(activity);

                    // apply reward earned count
                    editor.putInt(KEY_REWARD_EARNED_COUNT, prefs.getInt(KEY_REWARD_EARNED_COUNT, 0) + 1);
                    editor.apply();

                    // unlock gift costume
                    if (prefs.getInt(KEY_REWARD_EARNED_COUNT, 0) == 10) {
                        editor.putBoolean(KEY_COSTUME_GIFT, true);
                        editor.apply();

                        new PrefsController(activity).addHistoryPrefs(HISTORY_TYPE_COSTUME_FOUND, COSTUME_CODE_GIFT);
                    }

                    // case costume reward
                    if ((new Random()).nextInt(RANDOM_BOUND_REWARD) == 0) {
                        rewardType = REWARD_TYPE_ADVERTISEMENT_COSTUME;
                        rewardReference = prefsController.unlockRandomRewardCostumePrefs();

                        // case all paid costumes unlocked
                        if (rewardReference == -1) {
                            rewardType = REWARD_TYPE_ADVERTISEMENT_ITEM;
                            rewardReference = prefsController.addRandomItemPrefs(REWARD_TYPE_ADVERTISEMENT_ITEM);
                        }
                    }
                    // case item reward
                    else {
                        rewardType = REWARD_TYPE_ADVERTISEMENT_ITEM;
                        rewardReference = prefsController.addRandomItemPrefs(REWARD_TYPE_ADVERTISEMENT_ITEM);
                    }

                    // set is earned true
                    isEarned = true;
                });
            }
        });
    }

    private void animateBoxShiver(final int direction, final int iteration) {
        // initialize distance
        float shiverDistance = getSharedPreferences(PREFS, MODE_PRIVATE).getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE);

        // initialize duration
        long duration = iteration * calculateDurationGravity(activity, shiverDistance, false) / 10; // if iteration down, speed up

        // case first movement
        if (iteration == REPEAT_COUNT_BOX_SHIVER)
            duration /= 2; // move half distance
        else
            duration = Math.max(duration, 25); // duration lower bound

        // initialize value animator
        ValueAnimator valueAnimator;
        if (iteration == -1) {
            // case move stop
            valueAnimator = ValueAnimator.ofFloat(boxImageView.getX(), boxOriginPositionX);
        } else if ((direction == DIRECTION_TO_LEFT)) {
            // case move left
            valueAnimator = ValueAnimator.ofFloat(boxImageView.getX(), boxOriginPositionX - shiverDistance);
        } else {
            // case move right
            valueAnimator = ValueAnimator.ofFloat(boxImageView.getX(), boxOriginPositionX + shiverDistance);
        }

        // set value animator attributes
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> boxImageView.setX((Float) animation.getAnimatedValue()));
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (iteration == -1)
                    animateBoxJumpUp();
                else if (iteration == 0)
                    animateBoxShiver(DIRECTION_TO_LEFT, iteration - 1); // direction not effect
                else {
                    if (direction == DIRECTION_TO_LEFT)
                        animateBoxShiver(DIRECTION_TO_RIGHT, iteration - 1);
                    else
                        animateBoxShiver(DIRECTION_TO_LEFT, iteration - 1);
                }
            }
        });

        // start animation
        valueAnimator.start();

        // set title
        if (iteration == REPEAT_COUNT_BOX_SHIVER)
            titleTextView.setText(R.string.ads_title_drum_1);
        else {
            String title;
            if (iteration % 2 == 0)
                title = titleTextView.getText() + getString(R.string.ads_title_drum_1);
            else
                title = titleTextView.getText() + getString(R.string.ads_title_drum_2);

            titleTextView.setText(title);
        }

        // set contents
        contentsTextView.setText(R.string.ads_contents_unboxing);
    }

    private void animateBoxJumpUp() {
        // initialize distance
        float shiverDistance = 2 * getSharedPreferences(PREFS, MODE_PRIVATE).getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(boxOriginPositionY, boxOriginPositionY - shiverDistance);
        valueAnimator.setDuration(calculateDurationGravity(activity, shiverDistance, false));
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> boxImageView.setY((Float) animation.getAnimatedValue()));
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateBoxJumpDown();
            }
        });

        // start animation
        valueAnimator.start();

        // set title
        String title;
        if (rewardType == REWARD_TYPE_ADVERTISEMENT_COSTUME)
            title = getString(R.string.history_title_costume_get);
        else
            title = getString(R.string.history_title_item_get);
        title = title + "!";
        titleTextView.setText(title);

        // set contents
        String contents;
        if (rewardType == REWARD_TYPE_ADVERTISEMENT_COSTUME)
            contents = String.format(getString(R.string.history_contents_costume_reward), Converter.getCostumeName(activity, rewardReference));
        else
            contents = String.format(getString(R.string.history_contents_item_reward), Converter.getItemName(activity, rewardReference));
        contentsTextView.setText(contents);

        // set button
        button.setText(R.string.button_again);
    }

    private void animateBoxJumpDown() {
        // initialize distance
        float shiverDistance = 2 * getSharedPreferences(PREFS, MODE_PRIVATE).getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(boxOriginPositionY - shiverDistance, boxOriginPositionY);
        valueAnimator.setDuration(calculateDurationGravity(activity, shiverDistance, false));
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> boxImageView.setY((Float) animation.getAnimatedValue()));

        // start animation
        valueAnimator.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // create and load rewarded ad again
        if (isEarned) {
            // load random box rewarded ad
            AdvertisementController.loadRandomBoxRewardedAd(activity, false);

            // animate box shiver
            animateBoxShiver(DIRECTION_TO_LEFT, REPEAT_COUNT_BOX_SHIVER);

            // reset is earned
            isEarned = false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
