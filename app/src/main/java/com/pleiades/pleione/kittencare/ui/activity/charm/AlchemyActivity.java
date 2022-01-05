package com.pleiades.pleione.kittencare.ui.activity.charm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.pleiades.pleione.kittencare.Converter;
import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.controller.PrefsController;
import com.pleiades.pleione.kittencare.controller.ToastController;
import com.pleiades.pleione.kittencare.object.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static com.pleiades.pleione.kittencare.Config.DEFAULT_JUMP_ALTITUDE;
import static com.pleiades.pleione.kittencare.Config.DELAY_FACE_MAINTAIN_LONG;
import static com.pleiades.pleione.kittencare.Config.DELAY_GAME_READ_CHAR;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_1;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_2;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_3;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_MEOW_1;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_MEOW_2;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SLEEP;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SPARKLE;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SWEAT_1;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SWEAT_2;
import static com.pleiades.pleione.kittencare.Config.KEY_JUMP_ALTITUDE;
import static com.pleiades.pleione.kittencare.Config.PREFS;
import static com.pleiades.pleione.kittencare.Converter.getFaceResourceId;
import static com.pleiades.pleione.kittencare.controller.AnimationController.calculateDurationGravity;

public class AlchemyActivity extends AppCompatActivity {
    private Activity activity;

    // kitten
    private View kittenView;
    private ImageView faceImageView;
    private float kittenOriginPositionY;

    // sun
    private ImageView bloomImageView, sunImageView, shadowImageView;
    private float sunOriginPositionX, shadowOriginPositionX;

    // text
    private TextView titleTextView, contentsTextView;
    private Button button;

    private boolean isButtonLocked = false;
    private boolean isSucceed = true; // prevent rotate back animation
    private int cakeCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alchemy);

        // initialize activity
        activity = AlchemyActivity.this;

        // set navigation color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
            getWindow().setNavigationBarColor(ContextCompat.getColor(activity, R.color.color_navigation_background));

        // initialize appbar
        View appbar = findViewById(R.id.appbar_alchemy);
        Toolbar toolbar = appbar.findViewById(R.id.toolbar_sub);
        setSupportActionBar(toolbar);

        // initialize action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // initialize kitten view
        kittenView = findViewById(R.id.kitten_alchemy);
        kittenView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // initialize origin kitten position
                        kittenOriginPositionY = kittenView.getY();

                        // remove layout listener
                        kittenView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
        faceImageView = kittenView.findViewById(R.id.face_kitten);
        ((ImageView) kittenView.findViewById(R.id.body_kitten)).setImageResource(R.drawable.image_body_crop);
        ((ImageView) kittenView.findViewById(R.id.costume_kitten)).setImageResource(R.drawable.image_costume_alchemist);

        // initialize sun image views
        bloomImageView = findViewById(R.id.bloom_alchemy);
        sunImageView = findViewById(R.id.sun_alchemy);
        sunImageView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // initialize origin sun position
                        sunOriginPositionX = sunImageView.getX();

                        // remove layout listener
                        sunImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
        shadowImageView = findViewById(R.id.shadow_alchemy);
        shadowImageView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // initialize origin shadow position
                        shadowOriginPositionX = shadowImageView.getX();

                        // remove layout listener
                        shadowImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

        // initialize text views
        titleTextView = findViewById(R.id.title_alchemy);
        contentsTextView = findViewById(R.id.contents_alchemy);

        // initialize alchemy button
        button = findViewById(R.id.button_alchemy);
        button.setOnClickListener(v -> {
            // case button is locked
            if (isButtonLocked)
                return;

            // lock button
            isButtonLocked = true;

            // initialize origin item array list
            ArrayList<Item> originItemArrayList = new PrefsController(activity).getItemArrayListPrefs();

            // initialize ice cream item array list
            ArrayList<Item> iceCreamItemArrayList = new ArrayList<>();
            for (Item item : originItemArrayList) {
                if (Converter.isItemIceCream(item.itemCode))
                    iceCreamItemArrayList.add(item);
            }

            // case no ice cream found
            if (iceCreamItemArrayList.size() == 0) {
                new ToastController(activity).showToast(getString(R.string.toast_need_alchemy_material), Toast.LENGTH_SHORT);

                // unlock button
                isButtonLocked = false;
                return;
            }

            // sort and reverse ice cream item array list
            Collections.sort(iceCreamItemArrayList); // green to mint
            Collections.reverse(iceCreamItemArrayList); // mint to green

            // initialize quantity and success rate
            Item item = iceCreamItemArrayList.get(0);
            int quantity = Math.min(item.quantity, 5);
            int successRate = 20 * quantity; // 0 ~ 100

            // case kitten rotated
            if (!isSucceed)
                kittenView.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.anim_rotate_kitten_right_back));

            // initialize is succeed
            int randomValue = new Random().nextInt(100); // 0 ~ 99
            isSucceed = randomValue < successRate;

            // initialize prefs controller
            PrefsController prefsController = new PrefsController(activity);

            // alchemy item
            if (isSucceed)
                cakeCode = prefsController.alchemyItem(item.itemCode, quantity);
            else
                prefsController.wasteItem(item.itemCode, quantity);

            // start animation
            startAlchemyAnimation();
        });
    }

    private void startAlchemyAnimation() {
        // change alchemist face blink
        concentrateAlchemist();

        // animate shadow right
        animateShadowRight();
    }

    private void concentrateAlchemist() {
        // change alchemist face
        faceImageView.setImageResource(getFaceResourceId(FACE_CODE_BLINK_1));
        changeAlchemistFace(FACE_CODE_BLINK_2, 40);
        changeAlchemistFace(FACE_CODE_BLINK_3, 80);

        // set title, contents
        final CharSequence titleCharSequence = getString(R.string.alchemy_title_concentrate);
        for (int i = 0; i < titleCharSequence.length(); i++)
            setTextDelayed(titleTextView, titleCharSequence.subSequence(0, i + 1), i * DELAY_GAME_READ_CHAR);
        contentsTextView.setText(R.string.alchemy_contents_concentrate);
    }

    private void animateShadowRight() {
        // initialize from, to
        final float from = shadowOriginPositionX;
        final float to = sunOriginPositionX;

        // initialize duration
        long duration = 10 * calculateDurationGravity(activity, Math.abs(to - from), true);

        // initialize value animator
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);

        // set value animator attributes
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> shadowImageView.setX((Float) animation.getAnimatedValue()));
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    if (isSucceed)
                        animateBloomZoomIn();
                    else
                        animateAlchemistRotate();
                }, DELAY_FACE_MAINTAIN_LONG);
            }
        });

        // start animation
        valueAnimator.start();
    }

    private void animateBloomZoomIn() {
        // initialize enlarge animation
        Animation zoomAnimation = AnimationUtils.loadAnimation(activity, R.anim.anim_alchemy_scale_zoom_in);

        // set animation attributes
        zoomAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // change alchemist face
                changeAlchemistFace(FACE_CODE_BLINK_2, 0);
                changeAlchemistFace(FACE_CODE_BLINK_1, 40);
                changeAlchemistFace(FACE_CODE_MEOW_2, 80);
                changeAlchemistFace(FACE_CODE_MEOW_1, 120);
                changeAlchemistFace(FACE_CODE_SWEAT_1, 160);
                changeAlchemistFace(FACE_CODE_SWEAT_2, 200);

                // set title
                final CharSequence titleCharSequence = getString(R.string.alchemy_title_bloom);
                for (int i = 0; i < titleCharSequence.length(); i++)
                    setTextDelayed(titleTextView, titleCharSequence.subSequence(0, i + 1), i * DELAY_GAME_READ_CHAR);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    // animate bloom zoom out
                    bloomImageView.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.anim_alchemy_scale_zoom_out));

                    // animate alchemist jump up
                    animateAlchemistJumpUp();
                }, DELAY_FACE_MAINTAIN_LONG);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // start animation
        bloomImageView.startAnimation(zoomAnimation);
    }

    private void animateAlchemistJumpUp() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        // initialize from, to
        final float from = kittenOriginPositionY;
        final float to = from - prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);

        // initialize duration
        float distance = Math.abs(to - from);
        long duration = calculateDurationGravity(activity, distance, false);

        // initialize value animator
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> kittenView.setY((Float) animation.getAnimatedValue()));
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                // change alchemist face
                changeAlchemistFace(FACE_CODE_SPARKLE, 0);

                // set title, contents
                CharSequence titleCharSequence = getString(R.string.history_title_item_get) + "!";
                for (int i = 0; i < titleCharSequence.length(); i++)
                    setTextDelayed(titleTextView, titleCharSequence.subSequence(0, i + 1), i * DELAY_GAME_READ_CHAR);
                contentsTextView.setText(String.format(getString(R.string.history_contents_item_reward), Converter.getItemName(activity, cakeCode)));

                // set button
                button.setText(R.string.button_again);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animateAlchemistJumpDown();
            }
        });

        // start animation
        valueAnimator.start();
    }

    private void animateAlchemistJumpDown() {
        // initialize from, to
        final float from = kittenView.getY();
        final float to = kittenOriginPositionY;

        // initialize duration
        float distance = Math.abs(to - from);
        long duration = calculateDurationGravity(activity, distance, false);

        // initialize value animator
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                kittenView.setY((Float) animation.getAnimatedValue());
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // unlock button
                isButtonLocked = false;
            }
        });

        // start animation
        valueAnimator.start();
    }

    private void animateAlchemistRotate() {
        // initialize rotate animation
        Animation rotateAnimation = AnimationUtils.loadAnimation(activity, R.anim.anim_rotate_kitten_right);

        // set animation attributes
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // change alchemist face
                changeAlchemistFace(FACE_CODE_SLEEP, 0);

                // set title, contents
                final CharSequence titleCharSequence = getString(R.string.alchemy_title_rotate);
                for (int i = 0; i < titleCharSequence.length(); i++)
                    setTextDelayed(titleTextView, titleCharSequence.subSequence(0, i + 1), i * DELAY_GAME_READ_CHAR);
                contentsTextView.setText(R.string.alchemy_contents_rotate);

                // set button
                button.setText(R.string.button_again);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // unlock button
                isButtonLocked = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // start animation
        kittenView.startAnimation(rotateAnimation);
    }

    private void changeAlchemistFace(final int faceCode, int delay) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (faceImageView != null)
                    faceImageView.setImageResource(getFaceResourceId(faceCode));
            }
        }, delay);
    }

    private void setTextDelayed(final TextView textView, final CharSequence charSequence, long delay) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(textView != null){
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(charSequence);
                }
            }
        }, delay);
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
