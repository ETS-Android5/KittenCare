package com.pleiades.pleione.kittencare.ui.activity.game;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.pleiades.pleione.kittencare.Converter;
import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.controller.DeviceController;
import com.pleiades.pleione.kittencare.controller.PrefsController;
import com.pleiades.pleione.kittencare.object.Scenario;
import com.pleiades.pleione.kittencare.ui.fragment.dialog.DefaultDialogFragment;

import java.util.Random;

import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_DINOSAUR;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_JUMP_ALTITUDE;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_JUMP_DISTANCE;
import static com.pleiades.pleione.kittencare.Config.DELAY_GAME_ANIMATION_DEFAULT;
import static com.pleiades.pleione.kittencare.Config.DELAY_GAME_ANIMATION_LONG;
import static com.pleiades.pleione.kittencare.Config.DELAY_GAME_ANIMATION_SHORT;
import static com.pleiades.pleione.kittencare.Config.DELAY_GAME_READ_CHAR;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_FINISH_GAME;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_SKIP_OPENING;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_START_DINOSAUR;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_WIN_DINOSAUR;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_DEFAULT;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_MEOW_1;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_MEOW_2;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SURPRISED;
import static com.pleiades.pleione.kittencare.Config.KEY_ANIMATOR_DURATION_SCALE;
import static com.pleiades.pleione.kittencare.Config.KEY_IS_GAME_DIFFICULTY_HARD;
import static com.pleiades.pleione.kittencare.Config.KEY_JUMP_ALTITUDE;
import static com.pleiades.pleione.kittencare.Config.KEY_JUMP_DISTANCE;
import static com.pleiades.pleione.kittencare.Config.PREFS;
import static com.pleiades.pleione.kittencare.Config.REWARD_TYPE_GAME_ITEM_EASY;
import static com.pleiades.pleione.kittencare.Config.REWARD_TYPE_GAME_ITEM_HARD;
import static com.pleiades.pleione.kittencare.Config.SNIFF_ANGLE_RIGHT;
import static com.pleiades.pleione.kittencare.Converter.getFaceResourceId;

public class DinosaurActivity extends AppCompatActivity {
    private Context context;

    // translation
    private TextView translationSpeakerTextView;

    // cloud
    private ImageView cloudImageView;

    // dino
    private View dinoView;
    private ImageView dinoFaceImageView;
    private TextView dinoSpeakerTextView;

    // sunflower
    private View sunflowerView;
    private ImageView sunflowerFaceImageView;
    private TextView sunflowerSpeakerTextView;

    // game object
    private ProgressBar progressBar;
    private View targetView;
    private int targetXMin, targetXMax, targetRangeMin, targetRangeMax;
    private ImageView leafImageView;

    // animator
    private ValueAnimator valueAnimatorX, valueAnimatorY;
    private ValueAnimator valueAnimatorCloud, valueAnimatorProgress;
    private AnimatorSet animatorSet;

    public boolean isOpeningSkipped;
    private boolean isOpeningFinished;
    private boolean isGameDifficultyHard, isCloudMoving, isDinoFalling, isDinoLocked, isDinoSucceed;
    private long readCharDelay, shortAnimationDelay, defaultAnimationDelay, longAnimationDelay;
    private int animationCount, successCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_dinosaur);

        // initialize context
        context = DinosaurActivity.this;

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // set navigation color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
            getWindow().setNavigationBarColor(ContextCompat.getColor(context, R.color.color_navigation_background));

        // initialize appbar
        View appbar = findViewById(R.id.appbar_dinosaur);
        Toolbar toolbar = appbar.findViewById(R.id.toolbar_sub);
        setSupportActionBar(toolbar);

        // initialize action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // initialize difficulty
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        isGameDifficultyHard = prefs.getBoolean(KEY_IS_GAME_DIFFICULTY_HARD, false);

        // initialize translation view
        translationSpeakerTextView = findViewById(R.id.speaker_translation_dinosaur);

        // initialize cloud view
        cloudImageView = findViewById(R.id.cloud_dinosaur);

        // initialize dino kitten views
        dinoView = findViewById(R.id.dino_dinosaur);
        ImageView dinoBodyImageView = dinoView.findViewById(R.id.body_kitten);
        dinoBodyImageView.setImageResource(R.drawable.image_body_crop);
        ImageView dinoCostumeImageView = dinoView.findViewById(R.id.costume_kitten);
        dinoCostumeImageView.setImageResource(R.drawable.image_costume_dinosaur);
        dinoFaceImageView = dinoView.findViewById(R.id.face_kitten);
        dinoSpeakerTextView = findViewById(R.id.speaker_dino_dinosaur);

        // initialize sunflower kitten views
        sunflowerView = findViewById(R.id.sunflower_dinosaur);
        ImageView sunflowerBodyImageView = sunflowerView.findViewById(R.id.body_kitten);
        sunflowerBodyImageView.setImageResource(R.drawable.image_body_crop);
        ImageView sunflowerCostumeImageView = sunflowerView.findViewById(R.id.costume_kitten);
        sunflowerCostumeImageView.setImageResource(R.drawable.image_costume_sunflower);
        sunflowerFaceImageView = sunflowerView.findViewById(R.id.face_kitten);
        sunflowerSpeakerTextView = findViewById(R.id.speaker_sunflower_dinosaur);

        // initialize game objects
        progressBar = findViewById(R.id.progress_dinosaur);
        targetView = findViewById(R.id.progress_target_dinosaur);
        leafImageView = findViewById(R.id.leaf_dinosaur);

        // start story
        startStory();
    }

    private void startStory() {
        // initialize delays
        initializeDelays();

        // initialize count
        animationCount = 0;
        successCount = 0;

        // change kittens face
        dinoFaceImageView.setImageResource(R.drawable.image_face_sparkle);
        sunflowerFaceImageView.setImageResource(R.drawable.image_face_surprised);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // case layout is not loaded yet
                if (dinoView.getX() == 0) {
                    startStory();
                } else {
                    // animate kittens long jump up right
                    animateKittensLongJumpUpRight();
                }
            }
        }, shortAnimationDelay);
    }

    private void animateKittensLongJumpUpRight() {
        // cancel animator
        cancelAnimators();

        // initialize from
        final float fromDinoX = dinoView.getX();
        final float fromDinoY = dinoView.getY();
        final float fromSunflowerX = sunflowerView.getX();
        final float fromSunflowerY = sunflowerView.getY();

        // initialize distance, duration
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float distanceX = prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE);
        final float distanceY = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        long duration = calculateDuration(distanceY);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(0, distanceX);
        valueAnimatorY = ValueAnimator.ofFloat(0, distanceY);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animationCount >= 3)
                    dinoView.setX(fromDinoX + (Float) animation.getAnimatedValue());
                sunflowerView.setX(fromSunflowerX + (Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animationCount >= 3)
                    dinoView.setY(fromDinoY - (Float) animation.getAnimatedValue());
                sunflowerView.setY(fromSunflowerY - (Float) animation.getAnimatedValue());
            }
        });

        // initialize animator set
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // animate kittens long jump down right
                animateKittensLongJumpDownRight();
            }
        });

        // start animation
        animatorSet.start();
    }

    private void animateKittensLongJumpDownRight() {
        // cancel animator
        cancelAnimators();

        // initialize from
        final float fromDinoX = dinoView.getX();
        final float fromDinoY = dinoView.getY();
        final float fromSunflowerX = sunflowerView.getX();
        final float fromSunflowerY = sunflowerView.getY();

        // initialize distance, duration
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float distanceX = prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE);
        final float distanceY = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        long duration = calculateDuration(distanceY);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(0, distanceX);
        valueAnimatorY = ValueAnimator.ofFloat(0, distanceY);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animationCount >= 3)
                    dinoView.setX(fromDinoX + (Float) animation.getAnimatedValue());
                sunflowerView.setX(fromSunflowerX + (Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animationCount >= 3)
                    dinoView.setY(fromDinoY + (Float) animation.getAnimatedValue());
                sunflowerView.setY(fromSunflowerY + (Float) animation.getAnimatedValue());
            }
        });

        // initialize animator set
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new AccelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // add animation count
                animationCount++;

                int layoutWidth = findViewById(R.id.layout_dinosaur).getWidth();
                if (dinoView.getX() >= layoutWidth) {
                    // set kittens X
                    dinoView.setX(layoutWidth);
                    sunflowerView.setX(layoutWidth);

                    // initialize animation count
                    animationCount = 0;

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // animate kittens long jump up left
                            animateKittensLongJumpUpLeft();
                        }
                    }, longAnimationDelay);
                } else {
                    // animate kittens long jump up right
                    animateKittensLongJumpUpRight();
                }
            }
        });

        // start animation
        animatorSet.start();
    }

    private void animateKittensLongJumpUpLeft() {
        // cancel animator
        cancelAnimators();

        // initialize from
        final float fromDinoX = dinoView.getX();
        final float fromDinoY = dinoView.getY();
        final float fromSunflowerX = sunflowerView.getX();
        final float fromSunflowerY = sunflowerView.getY();

        // initialize distance, duration
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float distanceX = prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE);
        final float distanceY = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        long duration = calculateDuration(distanceY);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(0, distanceX);
        valueAnimatorY = ValueAnimator.ofFloat(0, distanceY);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animationCount >= 2)
                    dinoView.setX(fromDinoX - (Float) animation.getAnimatedValue());
                sunflowerView.setX(fromSunflowerX - (Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animationCount >= 2)
                    dinoView.setY(fromDinoY - (Float) animation.getAnimatedValue());
                sunflowerView.setY(fromSunflowerY - (Float) animation.getAnimatedValue());
            }
        });

        // initialize animator set
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // animate kittens long jump down
                animateKittensLongJumpDownLeft();
            }
        });

        // start animation
        animatorSet.start();
    }

    private void animateKittensLongJumpDownLeft() {
        // cancel animator
        cancelAnimators();

        // initialize from
        final float fromDinoX = dinoView.getX();
        final float fromDinoY = dinoView.getY();
        final float fromSunflowerX = sunflowerView.getX();
        final float fromSunflowerY = sunflowerView.getY();

        // initialize distance, duration
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float distanceX = prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE);
        final float distanceY = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        long duration = calculateDuration(distanceY);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(0, distanceX);
        valueAnimatorY = ValueAnimator.ofFloat(0, distanceY);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animationCount >= 2)
                    dinoView.setX(fromDinoX - (Float) animation.getAnimatedValue());
                sunflowerView.setX(fromSunflowerX - (Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animationCount >= 2)
                    dinoView.setY(fromDinoY + (Float) animation.getAnimatedValue());
                sunflowerView.setY(fromSunflowerY + (Float) animation.getAnimatedValue());
            }
        });

        // initialize animator set
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new AccelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // add animation count
                animationCount++;

                int kittenWidth = dinoView.getWidth();
                if (dinoView.getX() + kittenWidth <= 0) {
                    // set kittens X
                    dinoView.setX(-1 * kittenWidth);
                    sunflowerView.setX(-1 * kittenWidth);

                    // initialize animation count
                    animationCount = 0;

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // animate kittens long jump up right again
                            animateKittensLongJumpUpRightAgain();
                        }
                    }, longAnimationDelay);
                } else {
                    // animate kittens long jump up left
                    animateKittensLongJumpUpLeft();
                }
            }
        });

        // start animation
        animatorSet.start();
    }

    private void animateKittensLongJumpUpRightAgain() {
        // cancel animator
        cancelAnimators();

        // initialize to x max (center of layout)
        final float toXMax = ((float) findViewById(R.id.layout_dinosaur).getWidth() / 2) - ((float) dinoView.getWidth() / 2);

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float fromDinoX = dinoView.getX();
        final float fromDinoY = dinoView.getY();
        final float fromSunflowerX = sunflowerView.getX();
        final float fromSunflowerY = sunflowerView.getY();
        final float toDinoX = Math.min(fromDinoX + prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE), toXMax);

        // initialize distance, duration
        float distanceX = toDinoX - fromDinoX;
        float distanceY = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        long duration = calculateDuration(distanceY);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(0, distanceX);
        valueAnimatorY = ValueAnimator.ofFloat(0, distanceY);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animationCount >= 1)
                    dinoView.setX(fromDinoX + (Float) animation.getAnimatedValue());
                sunflowerView.setX(fromSunflowerX + (Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animationCount >= 1)
                    dinoView.setY(fromDinoY - (Float) animation.getAnimatedValue());
                sunflowerView.setY(fromSunflowerY - (Float) animation.getAnimatedValue());
            }
        });

        // initialize animator set
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // animate cloud left
                if (dinoView.getX() == toXMax && !isCloudMoving)
                    animateCloudLeft();

                // animate kittens long jump down right again
                animateKittensLongJumpDownRightAgain();
            }
        });

        // start animation
        animatorSet.start();
    }

    private void animateKittensLongJumpDownRightAgain() {
        // cancel animator
        cancelAnimators();

        // initialize to x max (center of layout)
        final float toXMax = ((float) findViewById(R.id.layout_dinosaur).getWidth() / 2) - ((float) dinoView.getWidth() / 2);

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float fromDinoX = dinoView.getX();
        final float fromDinoY = dinoView.getY();
        final float fromSunflowerX = sunflowerView.getX();
        final float fromSunflowerY = sunflowerView.getY();
        final float toDinoX = Math.min(fromDinoX + prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE), toXMax);

        // initialize distance, duration
        final float distanceX = toDinoX - fromDinoX;
        final float distanceY = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        long duration = calculateDuration(distanceY);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(0, distanceX);
        valueAnimatorY = ValueAnimator.ofFloat(0, distanceY);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animationCount >= 1)
                    dinoView.setX(fromDinoX + (Float) animation.getAnimatedValue());
                sunflowerView.setX(fromSunflowerX + (Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animationCount >= 1)
                    dinoView.setY(fromDinoY + (Float) animation.getAnimatedValue());
                sunflowerView.setY(fromSunflowerY + (Float) animation.getAnimatedValue());
            }
        });

        // initialize animator set
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new AccelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // add animation count
                animationCount++;

                if (dinoView.getX() == toXMax) {
                    // animate cloud left
                    if (!isCloudMoving)
                        animateCloudLeft();

                    // set speaker x, y
                    dinoSpeakerTextView.setX(dinoView.getX() - dinoSpeakerTextView.getWidth());
                    dinoSpeakerTextView.setY(dinoView.getY() - distanceY - dinoSpeakerTextView.getHeight());
                    sunflowerSpeakerTextView.setX(sunflowerView.getX() - sunflowerSpeakerTextView.getWidth());
                    sunflowerSpeakerTextView.setY(sunflowerView.getY() - distanceY - sunflowerSpeakerTextView.getHeight());

                    // initialize animation count
                    animationCount = 0;

                    // animate kittens jump up
                    animateKittensJumpUp();
                } else {
                    // animate kittens long jump up again
                    animateKittensLongJumpUpRightAgain();
                }
            }
        });

        // start animation
        animatorSet.start();
    }

    private void animateCloudLeft() {
        isCloudMoving = true;

        // cancel animator cloud
        cancelAnimatorCloud();

        // initialize from to
        final float from = cloudImageView.getX();
        final float to = -1 * cloudImageView.getWidth();

        // initialize distance, duration
        final float distance = Math.abs(to - from);
        final long duration = calculateKittenJumpRatioDuration(distance);

        // initialize value animator x
        valueAnimatorCloud = ValueAnimator.ofFloat(from, to);
        valueAnimatorCloud.setDuration(duration);
        valueAnimatorCloud.setInterpolator(new LinearInterpolator());
        valueAnimatorCloud.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // set cloud x
                cloudImageView.setX((Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorCloud.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // initialize cloud x
                cloudImageView.setX(findViewById(R.id.layout_dinosaur).getWidth());

                // recursive call
                animateCloudLeft();
            }
        });

        // start animation
        valueAnimatorCloud.start();
    }

    private void animateKittensJumpUp() {
        // cancel animator y
        cancelAnimatorY();

        // initialize from
        final float from = sunflowerView.getY();

        // initialize distance, duration
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float distanceY = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        long duration = calculateDuration(distanceY);

        // initialize is dino up
        final boolean isDinoUp = !isDinoFalling;

        // initialize value animator
        valueAnimatorY = ValueAnimator.ofFloat(0, distanceY);
        valueAnimatorY.setDuration(duration);
        valueAnimatorY.setInterpolator(new DecelerateInterpolator());
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // set dino y
                if (isDinoUp)
                    dinoView.setY(from - (Float) animation.getAnimatedValue());

                // set sunflower y
                sunflowerView.setY(from - (Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                // case dino is falling
                if (!isDinoUp && !isDinoLocked) {
                    // remove button click listener
                    Button button = findViewById(R.id.button_dinosaur);
                    button.setOnClickListener(null);

                    // change dino face
                    dinoFaceImageView.setImageResource(R.drawable.image_face_surprised);

                    // animate dino rotate
                    animateDinoRotate();

                    // animate dino left
                    animateDinoLeft();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // animate kittens jump down
                animateKittensJumpDown(isDinoUp);
            }
        });

        // start animation
        valueAnimatorY.start();
    }

    private void animateKittensJumpDown(final boolean isDinoUp) {
        // cancel animator y
        cancelAnimatorY();

        // initialize from
        final float from = sunflowerView.getY();

        // initialize distance, duration
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float distanceY = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        long duration = calculateDuration(distanceY);

        // initialize value animator
        valueAnimatorY = ValueAnimator.ofFloat(0, distanceY);
        valueAnimatorY.setDuration(duration);
        valueAnimatorY.setInterpolator(new AccelerateInterpolator());
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // set kittens y
                if (isDinoUp)
                    dinoView.setY(from + (Float) animation.getAnimatedValue());
                sunflowerView.setY(from + (Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isDinoSucceed) {
                    // remove button click listener
                    Button button = findViewById(R.id.button_dinosaur);
                    button.setOnClickListener(null);

                    // set leaf attributes
                    leafImageView.setX(sunflowerView.getX());
                    leafImageView.setVisibility(View.VISIBLE);

                    // cancel animator cloud
                    cancelAnimatorCloud();

                    // animate sunflower long jump up right
                    animateSunflowerLongJumpUpRight();

                    // animate dino long jump up to leaf
                    animateDinoLongJumpUpToLeaf();
                } else {
                    // animate kittens jump down
                    animateKittensJumpUp();

                    // add animation count
                    animationCount++;
                    if (animationCount == (isOpeningSkipped ? 1 : 4))
                        readChaseScripts();
                }
            }
        });

        // start animation
        valueAnimatorY.start();
    }

    private void readChaseScripts() {
        // initialize dino scenario
        Scenario chaseScenarioDino = new Scenario(context, R.array.scripts_dinosaur_chase_dino, R.array.scripts_dinosaur_chase_dino_translation);
        final CharSequence dinoScript = chaseScenarioDino.removeFirstScript();
        final CharSequence translatedDinoScript = chaseScenarioDino.removeFirstTranslatedScript();
        long dinoDuration = chaseScenarioDino.removeFirstDuration();

        // initialize sunflower scenario
        Scenario chaseScenarioSunflower = new Scenario(context, R.array.scripts_dinosaur_chase_sunflower, R.array.scripts_dinosaur_chase_sunflower_translation);
        final CharSequence sunflowerScript = chaseScenarioSunflower.removeFirstScript();
        final CharSequence translatedSunflowerScript = chaseScenarioSunflower.removeFirstTranslatedScript();
        long sunflowerDuration = chaseScenarioSunflower.removeFirstDuration();

        // initialize total duration
        long totalDuration = 0;

        // initialize handler
        Handler handler = new Handler(Looper.getMainLooper());

        // read dino chase script
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= dinoScript.length(); i++)
                    setTextDelayed(dinoSpeakerTextView, dinoScript.subSequence(0, i), i * readCharDelay);
                setTextDelayed(translationSpeakerTextView, translatedDinoScript, readCharDelay);
            }
        }, isOpeningSkipped ? 0 : totalDuration);

        // add total duration
        totalDuration += dinoDuration;

        // read sunflower chase script
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dinoSpeakerTextView.setVisibility(View.INVISIBLE);

                for (int i = 1; i <= sunflowerScript.length(); i++)
                    setTextDelayed(sunflowerSpeakerTextView, sunflowerScript.subSequence(0, i), i * readCharDelay);
                setTextDelayed(translationSpeakerTextView, translatedSunflowerScript, readCharDelay);
            }
        }, isOpeningSkipped ? 0 : totalDuration);

        // add total duration
        totalDuration += sunflowerDuration;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sunflowerSpeakerTextView.setVisibility(View.INVISIBLE);
                translationSpeakerTextView.setVisibility(View.INVISIBLE);

                // animate game objects appear
                animateGameObjectsAppear();
            }
        }, isOpeningSkipped ? 0 : totalDuration);
    }

    private void animateGameObjectsAppear() {
        // initialize progress bar
        ProgressBar progressBackground = findViewById(R.id.progress_background_dinosaur);

        // initialize appear animation
        Animation appearAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_scale_appear);
        appearAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isOpeningSkipped)
                    startGame();
                else {
                    DefaultDialogFragment defaultDialogFragment = new DefaultDialogFragment(DIALOG_TYPE_START_DINOSAUR);
                    defaultDialogFragment.show(getSupportFragmentManager(), Integer.toString(DIALOG_TYPE_START_DINOSAUR));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // start animation
        progressBackground.startAnimation(appearAnimation);
    }

    // start game
    public void startGame() {
        // initialize is opening finished
        isOpeningFinished = true;

        // initialize delays
        initializeDelays();

        // initialize button touch listener
        Button button = findViewById(R.id.button_dinosaur);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDinoFalling || (successCount == 20))
                    return;

                // cancel animator progress
                cancelAnimatorProgress();

                // initialize progress
                int progressX = progressBar.getProgress() + (int) progressBar.getX();
                if (progressX >= targetRangeMin && progressX <= targetRangeMax) {
                    // count success
                    successCount++;

                    // case success
                    if (successCount == 20) {
                        // set is dino succeed true
                        isDinoSucceed = true;

                        // animate game objects disappear
                        animateGameObjectsDisappear(true);
                    } else {
                        // animate progress bar
                        animateProgressBar(); // initialize progress bar animation
                    }
                } else {
                    // set is dino falling true
                    isDinoFalling = true;
                }
            }
        });

        // initialize progress, target
        progressBar.setMax(progressBar.getWidth());
        int dpToPx4 = new DeviceController(context).dpToPx(4);
        targetXMin = (int) progressBar.getX() + (progressBar.getWidth() / 2) - targetView.getWidth();
        targetXMax = (int) progressBar.getX() + progressBar.getWidth() - dpToPx4 - targetView.getWidth();

        // animate progress bar
        animateProgressBar();
    }

    private void animateProgressBar() {
        // cancel animator
        cancelAnimatorProgress();

        // initialize distance, duration
        final float distance = (float) progressBar.getWidth();
        final long duration = isGameDifficultyHard ? (long) (defaultAnimationDelay / 1.25) : defaultAnimationDelay;

        // set target x
        Random random = new Random();
        int bound = (int) distance / 2;
        int targetRandomX;
        do {
            targetRandomX = random.nextInt(bound) + bound; // half to full of progress bar
        } while (targetRandomX < targetXMin || targetRandomX > targetXMax);
        targetRangeMin = targetRandomX;
        targetRangeMax = targetRangeMin + targetView.getWidth();
        targetView.setX(targetRandomX);
        targetView.setVisibility(View.VISIBLE);

        // initialize value animator
        valueAnimatorProgress = ValueAnimator.ofFloat(0, distance);
        valueAnimatorProgress.setDuration(duration);
        valueAnimatorProgress.setInterpolator(new LinearInterpolator());
        valueAnimatorProgress.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // initialize progress
                float animatedValue = (Float) animation.getAnimatedValue(); // convert Float to float
                progressBar.setProgress((int) animatedValue);
            }
        });
        valueAnimatorProgress.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // set is kitten falling true
                isDinoFalling = true;
            }
        });

        // start animation
        valueAnimatorProgress.start();
    }

    private void animateDinoRotate() {
        // set is dino locked (prevent call rotate multi times)
        isDinoLocked = true;

        // cancel animator
        cancelAnimatorProgress();

        // initialize duration
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        long duration = calculateDuration(prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE));

        // initialize dino view pivot
        dinoView.setPivotX(85.71f * dinoView.getWidth() / 100);
        dinoView.setPivotY(dinoView.getHeight());

        // initialize rotate object animator
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(dinoView, "rotation", 0, 90);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateInterpolator());

        // start kitten animation
        objectAnimator.start();
    }

    private void animateDinoLeft() {
        // cancel animator x
        cancelAnimatorX();

        // initialize from, to
        final float from = dinoView.getX();
        final float to = -1 * (dinoView.getWidth() + dinoView.getHeight());

        // initialize distance, duration
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        long rotateDuration = calculateDuration(prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE));
        float distance = Math.abs(to - from);
        long duration = Math.max(calculateKittenJumpRatioDuration(distance), 2 * rotateDuration);

        // initialize value animator
        valueAnimatorX = ValueAnimator.ofFloat(0, distance);
        valueAnimatorX.setDuration(duration);
        valueAnimatorX.setInterpolator(new LinearInterpolator());
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // set dino x
                dinoView.setX(from - (Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // animate game objects disappear
                animateGameObjectsDisappear(false);
            }
        });

        // start animation
        valueAnimatorX.start();
    }

    private void animateSunflowerLongJumpUpRight() {
        // cancel animator
        cancelAnimators();

        // initialize distance, duration
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float distanceX = prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE);
        final float distanceY = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        long duration = calculateDuration(distanceY);

        // initialize from
        final float fromX = sunflowerView.getX();
        final float fromY = sunflowerView.getY();

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(0, distanceX);
        valueAnimatorY = ValueAnimator.ofFloat(0, distanceY);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sunflowerView.setX(fromX + (Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sunflowerView.setY(fromY - (Float) animation.getAnimatedValue());
            }
        });

        // initialize animator set
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // animate sunflower long jump down right
                animateSunflowerLongJumpDownRight();
            }
        });

        // start animation
        animatorSet.start();
    }

    private void animateSunflowerLongJumpDownRight() {
        // cancel animator
        cancelAnimators();

        // initialize distance, duration
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float distanceX = prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE);
        final float distanceY = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        long duration = calculateDuration(distanceY);

        // initialize from
        final float fromX = sunflowerView.getX();
        final float fromY = sunflowerView.getY();

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(0, distanceX);
        valueAnimatorY = ValueAnimator.ofFloat(0, distanceY);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sunflowerView.setX(fromX + (Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sunflowerView.setY(fromY + (Float) animation.getAnimatedValue());
            }
        });

        // initialize animator set
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new AccelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                int layoutWidth = findViewById(R.id.layout_dinosaur).getWidth();
                if (sunflowerView.getX() < layoutWidth) {
                    // animate sunflower long jump up right
                    animateSunflowerLongJumpUpRight();
                }
            }
        });

        // start animation
        animatorSet.start();
    }

    private void animateDinoLongJumpUpToLeaf() {
        // initialize from
        final float fromX = dinoView.getX();
        final float fromY = dinoView.getY();

        // initialize distance, duration
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        float distanceX = prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE);
        float distanceDifference = (leafImageView.getX() - fromX - dinoView.getWidth());
        if (distanceX > distanceDifference)
            distanceX = distanceDifference / 2;
        float distanceY = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        long duration = calculateDuration(distanceY);

        // initialize value animator x, y
        ValueAnimator valueAnimatorX = ValueAnimator.ofFloat(0, distanceX);
        ValueAnimator valueAnimatorY = ValueAnimator.ofFloat(0, distanceY);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dinoView.setX(fromX + (Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dinoView.setY(fromY - (Float) animation.getAnimatedValue());
            }
        });

        // initialize animator set
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // animate dino long jump down to leaf
                animateDinoLongJumpDownToLeaf();
            }
        });

        // start animation
        animatorSet.start();
    }

    private void animateDinoLongJumpDownToLeaf() {
        // initialize from
        final float fromX = dinoView.getX();
        final float fromY = dinoView.getY();

        // initialize distance, duration
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        float distanceX = prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE);
        float distanceDifference = (leafImageView.getX() - fromX - dinoView.getWidth());
        if (distanceX > distanceDifference)
            distanceX = distanceDifference;
        float distanceY = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        long duration = calculateDuration(distanceY);

        // initialize value animator x, y
        ValueAnimator valueAnimatorX = ValueAnimator.ofFloat(0, distanceX);
        ValueAnimator valueAnimatorY = ValueAnimator.ofFloat(0, distanceY);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dinoView.setX(fromX + (Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dinoView.setY(fromY + (Float) animation.getAnimatedValue());
            }
        });

        // initialize animator set
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // change face default
                        dinoFaceImageView.setImageResource(getFaceResourceId(FACE_CODE_SURPRISED));
                        setFaceDelayed(FACE_CODE_MEOW_1, 40);
                        setFaceDelayed(FACE_CODE_MEOW_2, 80);
                        setFaceDelayed(FACE_CODE_DEFAULT, 120);

                        // animate dino sniff
                        animateDinoSniff();
                    }
                }, defaultAnimationDelay);
            }
        });

        // start animation
        animatorSet.start();
    }

    private void animateDinoSniff() {
        // initialize dino view pivot
        dinoView.setPivotX(85.71f * dinoView.getWidth() / 100);
        dinoView.setPivotY(dinoView.getHeight());

        // initialize duration
        long duration = calculateDuration(4 * dinoView.getHeight());

        // initialize rotate object animator
        final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(dinoView, "rotation", 0, SNIFF_ANGLE_RIGHT);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // animate dino sniff back
                        animateDinoSniffBack();
                    }
                }, defaultAnimationDelay);
            }
        });


        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // start kitten animation
                objectAnimator.start();
            }
        }, defaultAnimationDelay);
    }

    private void animateDinoSniffBack() {
        // initialize duration
        long duration = calculateDuration(4 * dinoView.getHeight());

        // initialize rotate object animator
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(dinoView, "rotation", SNIFF_ANGLE_RIGHT, 0);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // set dino face sparkle
                        dinoFaceImageView.setImageResource(R.drawable.image_face_sparkle);

                        // animate dino jump up
                        animateDinoJumpUp();
                    }
                }, defaultAnimationDelay);
            }
        });

        // start kitten animation
        objectAnimator.start();
    }

    private void animateDinoJumpUp() {
        // initialize from, to
        final float from = dinoView.getY();

        // initialize distance, duration
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        float distance = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        long duration = calculateDuration(distance);

        // initialize value animator
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, distance);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // set dino y
                dinoView.setY(from - (Float) animation.getAnimatedValue());
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // animate dino jump down
                animateDinoJumpDown();
            }
        });

        // start animation
        valueAnimator.start();
    }

    private void animateDinoJumpDown(){
        // initialize from, to
        final float from = dinoView.getY();

        // initialize distance, duration
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        float distance = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        long duration = calculateDuration(distance);

        // initialize value animator
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, distance);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // set dino y
                dinoView.setY(from + (Float) animation.getAnimatedValue());
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finishGame(true);
                    }
                }, shortAnimationDelay);
            }
        });

        // start animation
        valueAnimator.start();
    }

    private void animateGameObjectsDisappear(final boolean isPlayerWin) {
        // initialize progress bar
        ProgressBar progressBackground = findViewById(R.id.progress_background_dinosaur);

        // initialize appear animation
        Animation disappearAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_scale_disappear);
        disappearAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!isPlayerWin) {
                    // finish game
                    finishGame(false);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // start animation
        targetView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_scale_disappear));
        progressBar.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_scale_disappear));
        progressBackground.startAnimation(disappearAnimation);
    }

    private void finishGame(boolean isPlayerWin) {
        // case player win
        if (isPlayerWin) {
            SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            final boolean isGameDifficultyHard = prefs.getBoolean(KEY_IS_GAME_DIFFICULTY_HARD, false);
            int gameRewardItemCode = new PrefsController(context).addRandomItemPrefs(isGameDifficultyHard ? REWARD_TYPE_GAME_ITEM_HARD : REWARD_TYPE_GAME_ITEM_EASY);
            DefaultDialogFragment defaultDialogFragment = new DefaultDialogFragment(DIALOG_TYPE_WIN_DINOSAUR);
            defaultDialogFragment.show(getSupportFragmentManager(), Converter.getItemName(context, gameRewardItemCode));
        }
        // case enemy win
        else {
            long totalDuration = 0;
            Scenario ranAwayScenario = new Scenario(context, R.array.scripts_dinosaur_ran_away_sunflower, R.array.scripts_dinosaur_ran_away_sunflower_translation);
            for (int i = 0; i <= ranAwayScenario.getSize(); i++) {
                // initialize script
                final CharSequence script = ranAwayScenario.removeFirstScript();
                final CharSequence translatedScript = ranAwayScenario.removeFirstTranslatedScript();
                final long duration = ranAwayScenario.removeFirstDuration();

                // read script
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int j = 1; j <= script.length(); j++)
                            setTextDelayed(sunflowerSpeakerTextView, script.subSequence(0, j), j * readCharDelay);
                        setTextDelayed(translationSpeakerTextView, translatedScript, readCharDelay);
                    }
                }, totalDuration);

                // calculate total duration
                totalDuration += duration;
            }

            // finish activity
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setResult(-1);
                    finish();
                }
            }, totalDuration + shortAnimationDelay);
        }
    }

    // others
    public void skipOpening() {
        // initialize is opening skipped
        isOpeningSkipped = true;

        // set speaker visibility
        dinoSpeakerTextView.setVisibility(View.INVISIBLE);
        sunflowerSpeakerTextView.setVisibility(View.INVISIBLE);
        translationSpeakerTextView.setVisibility(View.INVISIBLE);

        // set delay 0
        readCharDelay = 0;
        shortAnimationDelay = 0;
        defaultAnimationDelay = 0;
        longAnimationDelay = 0;
    }

    private void initializeDelays() {
        readCharDelay = DELAY_GAME_READ_CHAR;
        shortAnimationDelay = DELAY_GAME_ANIMATION_SHORT;
        defaultAnimationDelay = DELAY_GAME_ANIMATION_DEFAULT;
        longAnimationDelay = DELAY_GAME_ANIMATION_LONG;
    }

    private void setTextDelayed(final TextView speakerTextView, final CharSequence message, long delay) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (speakerTextView != null) {
                    if (isOpeningFinished || !isOpeningSkipped)
                        speakerTextView.setVisibility(View.VISIBLE);
                    speakerTextView.setText(message);
                }
            }
        }, delay);
    }

    private void setFaceDelayed(final int faceCode, long delay) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dinoFaceImageView != null)
                    dinoFaceImageView.setImageResource(getFaceResourceId(faceCode));
            }
        }, delay);
    }

    private long calculateDuration(float distance) {
        long duration;
        duration = Math.round((int) (100 * Math.sqrt(distance / 49)));
        duration = Math.round(context.getSharedPreferences(PREFS, MODE_PRIVATE).getFloat(KEY_ANIMATOR_DURATION_SCALE, 1) * duration);
        return duration;
    }

    private long calculateKittenJumpRatioDuration(float distanceX) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float kittenJumpDistance = prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE);
        final long kittenJumpDuration = calculateDuration(prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE));
        return (long) ((distanceX * kittenJumpDuration) / kittenJumpDistance);
    }

    private void cancelAnimators() {
        // cancel animator x
        cancelAnimatorX();

        // cancel animator y
        cancelAnimatorY();

        // cancel animator set
        cancelAnimatorSet();
    }

    private void cancelAnimatorX() {
        if (valueAnimatorX != null) {
            valueAnimatorX.cancel();
            valueAnimatorX.removeAllUpdateListeners();
            valueAnimatorX.removeAllListeners();
        }

        // cancel animator set
        cancelAnimatorSet();
    }

    private void cancelAnimatorY() {
        if (valueAnimatorY != null) {
            valueAnimatorY.cancel();
            valueAnimatorY.removeAllUpdateListeners();
            valueAnimatorY.removeAllListeners();
        }

        // cancel animator set
        cancelAnimatorSet();
    }

    private void cancelAnimatorSet() {
        if (animatorSet != null) {
            animatorSet.cancel();
            animatorSet.removeAllListeners();
        }
    }

    private void cancelAnimatorCloud() {
        if (valueAnimatorCloud != null) {
            valueAnimatorCloud.removeAllUpdateListeners();
            valueAnimatorCloud.removeAllListeners();
            valueAnimatorCloud.cancel();
        }
    }

    private void cancelAnimatorProgress() {
        if (valueAnimatorProgress != null) {
            valueAnimatorProgress.removeAllUpdateListeners();
            valueAnimatorProgress.removeAllListeners();
            valueAnimatorProgress.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game_in, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_skip_game) {
            if (isOpeningFinished || isOpeningSkipped)
                return true;

            DefaultDialogFragment defaultDialogFragment = new DefaultDialogFragment(DIALOG_TYPE_SKIP_OPENING);
            defaultDialogFragment.show(getSupportFragmentManager(), Converter.getCostumeName(context, COSTUME_CODE_DINOSAUR));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DefaultDialogFragment defaultDialogFragment = new DefaultDialogFragment(DIALOG_TYPE_FINISH_GAME);
        defaultDialogFragment.show(getSupportFragmentManager(), Integer.toString(DIALOG_TYPE_FINISH_GAME));
    }

    @Override
    protected void onDestroy() {
        cancelAnimators();
        cancelAnimatorProgress();
        cancelAnimatorCloud();

        super.onDestroy();
    }
}