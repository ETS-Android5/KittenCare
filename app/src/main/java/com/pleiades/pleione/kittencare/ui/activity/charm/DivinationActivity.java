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

import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.controller.PrefsController;
import com.pleiades.pleione.kittencare.controller.ToastController;

import java.util.Random;

import static com.pleiades.pleione.kittencare.Config.BUFF_CODE_EXPERIENCE;
import static com.pleiades.pleione.kittencare.Config.BUFF_CODE_FAIL;
import static com.pleiades.pleione.kittencare.Config.BUFF_CODE_ITEM;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_JUMP_ALTITUDE;
import static com.pleiades.pleione.kittencare.Config.DELAY_FACE_MAINTAIN_LONG;
import static com.pleiades.pleione.kittencare.Config.DELAY_GAME_READ_CHAR;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_1;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_2;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_3;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_DEFAULT;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SPARKLE;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SWEAT_1;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_BUFF;
import static com.pleiades.pleione.kittencare.Config.KEY_BUFF;
import static com.pleiades.pleione.kittencare.Config.KEY_JUMP_ALTITUDE;
import static com.pleiades.pleione.kittencare.Config.PREFS;
import static com.pleiades.pleione.kittencare.Converter.getFaceResourceId;
import static com.pleiades.pleione.kittencare.controller.AnimationController.calculateDurationGravity;

// TODO update buff
public class DivinationActivity extends AppCompatActivity {
    private Activity activity;

    // kitten
    private View kittenView;
    private ImageView faceImageView;

    // cloud
    private ImageView cloudImageView;

    // crystal ball
    private ImageView crystalBallOutlineImageView, crystalBallImageView;

    // text
    private TextView titleTextView, contentsTextView;

    private int buffCode;
    private int fadeCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_divination);

        // initialize activity
        activity = DivinationActivity.this;

        // set navigation color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
            getWindow().setNavigationBarColor(ContextCompat.getColor(activity, R.color.color_navigation_background));

        // initialize appbar
        View appbar = findViewById(R.id.appbar_divination);
        Toolbar toolbar = appbar.findViewById(R.id.toolbar_sub);
        setSupportActionBar(toolbar);

        // initialize action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // initialize kitten view
        kittenView = findViewById(R.id.kitten_divination);
        faceImageView = kittenView.findViewById(R.id.face_kitten);
        ((ImageView) kittenView.findViewById(R.id.body_kitten)).setImageResource(R.drawable.image_body_crop);
        ((ImageView) kittenView.findViewById(R.id.costume_kitten)).setImageResource(R.drawable.image_costume_seer);

        // initialize cloud view
        cloudImageView = findViewById(R.id.cloud_divination);

        // initialize crystal ball views
        crystalBallOutlineImageView = findViewById(R.id.crystal_ball_outline_divination);
        crystalBallImageView = findViewById(R.id.crystal_ball_divination);

        // initialize text views
        titleTextView = findViewById(R.id.title_divination);
        contentsTextView = findViewById(R.id.contents_divination);

        // initialize alchemy button
        Button button = findViewById(R.id.button_divination);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                // case buff removed
                if (prefs.getInt(KEY_BUFF, -1) == -1) {
                    // initialize buff
                    buffCode = new Random().nextInt(3);

                    // apply buff
                    editor.putInt(KEY_BUFF, buffCode);
                    editor.apply();

                    // add history
                    if (buffCode != BUFF_CODE_FAIL)
                        new PrefsController(activity).addHistoryPrefs(HISTORY_TYPE_BUFF, buffCode);

                    // start divination animation
                    startDivinationAnimation();
                } else {
                    new ToastController(activity).showToast(getString(R.string.toast_divination_once), Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void startDivinationAnimation() {
        // concentrate seer
        concentrateSeer();

        // animate cloud alpha
        animateCloudFadeOut();
    }

    private void concentrateSeer() {
        // set seer face blink
        faceImageView.setImageResource(getFaceResourceId(FACE_CODE_BLINK_1));
        setFaceDelayed(FACE_CODE_BLINK_2, 40);
        setFaceDelayed(FACE_CODE_BLINK_3, 80);

        // set title, contents
        final CharSequence titleCharSequence = getString(R.string.divination_title_concentrate);
        for (int i = 0; i < titleCharSequence.length(); i++)
            setTextDelayed(titleTextView, titleCharSequence.subSequence(0, i + 1), i * DELAY_GAME_READ_CHAR);
        contentsTextView.setText(R.string.divination_contents_concentrate);
    }

    private void animateCloudFadeOut() {
        // initialize fade in animation
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(activity, R.anim.anim_scale_fade_out);

        // set animation attributes
        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // add fade count
                fadeCount++;
                if (fadeCount == 2) {
                    // open seer eye
                    openSeerEye();
                }

                // animate cloud fade in
                animateCloudFadeIn();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // start animation
        cloudImageView.startAnimation(fadeOutAnimation);
    }

    private void animateCloudFadeIn() {
        // initialize fade in animation
        Animation fadeInAnimation = AnimationUtils.loadAnimation(activity, R.anim.anim_scale_fade_in);

        // set animation attributes
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // animate cloud fade out
                animateCloudFadeOut();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // start animation
        cloudImageView.startAnimation(fadeInAnimation);
    }

    private void openSeerEye() {
        // set seer face default
        faceImageView.setImageResource(getFaceResourceId(FACE_CODE_BLINK_2));
        setFaceDelayed(FACE_CODE_BLINK_1, 40);
        setFaceDelayed(FACE_CODE_DEFAULT, 80);

        // set title
        final CharSequence titleCharSequence = getString(R.string.divination_title_eye_open);
        for (int i = 0; i < titleCharSequence.length(); i++)
            setTextDelayed(titleTextView, titleCharSequence.subSequence(0, i + 1), i * DELAY_GAME_READ_CHAR);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (buffCode == BUFF_CODE_FAIL) {
                    // animate crystal ball fall
                    animateCrystalBallFall();
                } else {
                    // animate seer jump up
                    animateSeerJumpUp();
                }
            }
        }, DELAY_GAME_READ_CHAR * titleCharSequence.length() + DELAY_FACE_MAINTAIN_LONG);
    }

    private void animateCrystalBallFall() {
        // initialize from, to
        final float fromOutlineY = crystalBallOutlineImageView.getY();
        final float fromInlineY = crystalBallImageView.getY();
        float toOutlineY = kittenView.getY() + kittenView.getHeight() - crystalBallOutlineImageView.getHeight();

        // initialize distance, duration
        float distance = Math.abs(toOutlineY - fromOutlineY);
        long duration = calculateDurationGravity(activity, distance, false);

        // initialize value animator
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, distance);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (Float) animation.getAnimatedValue();
                crystalBallOutlineImageView.setY(fromOutlineY + animatedValue);
                crystalBallImageView.setY(fromInlineY + animatedValue);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // animate crystal ball roll
                animateCrystalBallRoll();

                // animate seer rotate
                animateSeerRotate();
            }
        });

        // start animation
        valueAnimator.start();
    }

    private void animateCrystalBallRoll(){
        // initialize from, to
        final float fromOutlineX = crystalBallOutlineImageView.getX();
        final float fromInlineX = crystalBallImageView.getX();
        float toOutlineX = findViewById(R.id.layout_divination).getWidth();

        // initialize distance, duration
        float distance = Math.abs(toOutlineX - fromOutlineX);
        long duration = calculateDurationGravity(activity, distance, true);

        // initialize value animator
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, distance);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (Float) animation.getAnimatedValue();
                crystalBallOutlineImageView.setX(fromOutlineX + animatedValue);
                crystalBallImageView.setX(fromInlineX + animatedValue);
            }
        });

        // start animation
        valueAnimator.start();
    }

    private void animateSeerRotate() {
        // initialize rotate animation
        Animation rotateAnimation = AnimationUtils.loadAnimation(activity, R.anim.anim_rotate_kitten_right);

        // set animation attributes
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // change alchemist face
                faceImageView.setImageResource(getFaceResourceId(FACE_CODE_SWEAT_1));

                // set title, contents
                final CharSequence titleCharSequence = getString(R.string.divination_title_rotate);
                for (int i = 0; i < titleCharSequence.length(); i++)
                    setTextDelayed(titleTextView, titleCharSequence.subSequence(0, i + 1), i * DELAY_GAME_READ_CHAR);
                contentsTextView.setText(R.string.divination_contents_rotate);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // start animation
        kittenView.startAnimation(rotateAnimation);
    }

    private void animateSeerJumpUp() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        // initialize from, to, distance
        float from = kittenView.getY();
        float distance = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        float to = from - distance;

        // initialize duration
        long duration = calculateDurationGravity(activity, distance, false);

        // initialize value animator
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                kittenView.setY((Float) animation.getAnimatedValue());
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                // set seer face sparkle
                faceImageView.setImageResource(getFaceResourceId(FACE_CODE_SPARKLE));

                // set title, contents
                CharSequence titleCharSequence = getString(R.string.history_title_buff_get) + "!";
                for (int i = 0; i < titleCharSequence.length(); i++)
                    setTextDelayed(titleTextView, titleCharSequence.subSequence(0, i + 1), i * DELAY_GAME_READ_CHAR);
                if (buffCode == BUFF_CODE_EXPERIENCE)
                    contentsTextView.setText(R.string.history_contents_buff_get_experience);
                else if (buffCode == BUFF_CODE_ITEM)
                    contentsTextView.setText(R.string.history_contents_buff_get_item);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animateSeerJumpDown();
            }
        });

        // start animation
        valueAnimator.start();
    }

    private void animateSeerJumpDown() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        // initialize from, to, distance
        float from = kittenView.getY();
        float distance = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        float to = from + distance;

        // initialize duration
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

        // start animation
        valueAnimator.start();
    }

    private void setFaceDelayed(final int faceCode, int delay) {
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
                if (textView != null) {
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
