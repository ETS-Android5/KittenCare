package com.pleiades.pleione.kittencare.ui.activity.game;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.pleiades.pleione.kittencare.Converter;
import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.controller.PrefsController;
import com.pleiades.pleione.kittencare.object.Scenario;
import com.pleiades.pleione.kittencare.ui.fragment.dialog.DefaultDialogFragment;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_PLEIONE;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_JUMP_ALTITUDE;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_JUMP_DISTANCE;
import static com.pleiades.pleione.kittencare.Config.DELAY_GAME_ANIMATION_DEFAULT;
import static com.pleiades.pleione.kittencare.Config.DELAY_GAME_ANIMATION_LONG;
import static com.pleiades.pleione.kittencare.Config.DELAY_GAME_ANIMATION_SHORT;
import static com.pleiades.pleione.kittencare.Config.DELAY_GAME_READ_CHAR;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_FINISH_GAME;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_SKIP_OPENING;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_START_PLEIADES;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_WIN_PLEIADES;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_1;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_2;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_3;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_DEFAULT;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_MEOW_1;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_MEOW_2;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SLEEP;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SPARKLE;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SWEAT_2;
import static com.pleiades.pleione.kittencare.Config.KEY_ANIMATOR_DURATION_SCALE;
import static com.pleiades.pleione.kittencare.Config.KEY_IS_GAME_DIFFICULTY_HARD;
import static com.pleiades.pleione.kittencare.Config.KEY_JUMP_ALTITUDE;
import static com.pleiades.pleione.kittencare.Config.KEY_JUMP_DISTANCE;
import static com.pleiades.pleione.kittencare.Config.PREFS;
import static com.pleiades.pleione.kittencare.Config.REWARD_TYPE_GAME_ITEM_EASY;
import static com.pleiades.pleione.kittencare.Config.REWARD_TYPE_GAME_ITEM_HARD;
import static com.pleiades.pleione.kittencare.Converter.getFaceResourceId;

public class PleiadesActivity extends AppCompatActivity {
    private Context context;

    // translation view
    private TextView translationSpeakerTextView;

    // alcyone views
    private View alcyoneView;
    private ImageView alcyoneBodyImageView, alcyoneCostumeImageView, alcyoneFaceImageView;
    private TextView alcyoneSpeakerTextView;

    // pleione views
    private View pleioneView;
    private ImageView pleioneFaceImageView;
    private TextView pleioneSpeakerTextView;

    // other views
    private ImageView giftImageView, refrigeratorImageView;
    private ImageView bedLowerImageView, bedUpperImageView, bedPillarLeftImageView, bedPillarRightImageView;

    // animator
    private ValueAnimator valueAnimatorX, valueAnimatorY, valueAnimatorGameX, valueAnimatorGameY;
    private AnimatorSet animatorSet;

    private boolean isOpeningFinished, isOpeningSkipped, isButtonTouched, isAlcyoneLocked, isAlcyoneStand, isPleioneCaught;
    private long readCharDelay, shortAnimationDelay, defaultAnimationDelay, longAnimationDelay;

    private Timer timer;
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_pleiades);

        // initialize context
        context = PleiadesActivity.this;

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // set navigation color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
            getWindow().setNavigationBarColor(ContextCompat.getColor(context, R.color.color_navigation_background));

        // initialize appbar
        View appbar = findViewById(R.id.appbar_pleiades);
        Toolbar toolbar = appbar.findViewById(R.id.toolbar_sub);
        setSupportActionBar(toolbar);

        // initialize action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // initialize translation view
        translationSpeakerTextView = findViewById(R.id.speaker_translation_pleiades);

        // initialize alcyone kitten views
        alcyoneView = findViewById(R.id.alcyone_pleiades);
        alcyoneBodyImageView = alcyoneView.findViewById(R.id.body_kitten);
        alcyoneBodyImageView.setImageResource(R.drawable.image_body_crop);
        alcyoneCostumeImageView = alcyoneView.findViewById(R.id.costume_kitten);
        alcyoneCostumeImageView.setImageResource(R.drawable.image_costume_alcyone);
        alcyoneFaceImageView = alcyoneView.findViewById(R.id.face_kitten);
        alcyoneSpeakerTextView = findViewById(R.id.speaker_alcyone_pleiades);

        // initialize pleione kitten views
        pleioneView = findViewById(R.id.pleione_pleiades);
        ((ImageView) pleioneView.findViewById(R.id.body_kitten)).setImageResource(R.drawable.image_body_crop);
        ((ImageView) pleioneView.findViewById(R.id.costume_kitten)).setImageResource(R.drawable.image_costume_pleione);
        pleioneFaceImageView = pleioneView.findViewById(R.id.face_kitten);
        pleioneSpeakerTextView = findViewById(R.id.speaker_pleione_pleiades);

        // initialize other views
        giftImageView = findViewById(R.id.gift_pleiades);
        refrigeratorImageView = findViewById(R.id.refrigerator_pleiades);
        bedLowerImageView = findViewById(R.id.bed_lower_pleiades);
        bedUpperImageView = findViewById(R.id.bed_upper_pleiades);
        bedPillarLeftImageView = findViewById(R.id.bed_pillar_left_pleiades);
        bedPillarRightImageView = findViewById(R.id.bed_pillar_right_pleiades);

        // start story
        startStory();
    }

    // start story
    private void startStory() {
        // initialize delays
        initializeDelays();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // read alcyone conversation script
                readAlcyoneConversationScript();
            }
        }, shortAnimationDelay);
    }

    private void readAlcyoneConversationScript() {
        // initialize scenario
        Scenario alcyoneScenario = new Scenario(context, R.array.scripts_pleiades_conversation_alcyone, R.array.scripts_pleiades_conversation_alcyone_translation);
        CharSequence script = alcyoneScenario.removeFirstScript();
        CharSequence translatedScript = alcyoneScenario.removeFirstTranslatedScript();
        long duration = alcyoneScenario.removeFirstDuration();

        // read script
        for (int i = 1; i <= script.length(); i++)
            setTextDelayed(alcyoneSpeakerTextView, script.subSequence(0, i), i * readCharDelay);
        setTextDelayed(translationSpeakerTextView, translatedScript, readCharDelay);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                readPleioneConversationScript();
            }
        }, isOpeningSkipped ? 0 : duration);
    }

    private void readPleioneConversationScript() {
        // set alcyone speaker visibility
        alcyoneSpeakerTextView.setVisibility(View.INVISIBLE);

        // initialize scenario
        Scenario pleioneScenario = new Scenario(context, R.array.scripts_pleiades_conversation_pleione, R.array.scripts_pleiades_conversation_pleione_translation);
        CharSequence script = pleioneScenario.removeFirstScript();
        CharSequence translatedScript = pleioneScenario.removeFirstTranslatedScript();
        long duration = pleioneScenario.removeFirstDuration();

        // read script
        for (int i = 1; i <= script.length(); i++)
            setTextDelayed(pleioneSpeakerTextView, script.subSequence(0, i), i * readCharDelay);
        setTextDelayed(translationSpeakerTextView, translatedScript, readCharDelay);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // animate alcyone jump up
                animateAlcyoneJumpUp();
            }
        }, isOpeningSkipped ? 0 : duration);
    }

    private void animateAlcyoneJumpUp() {
        // set speaker visibility
        pleioneSpeakerTextView.setVisibility(View.INVISIBLE);
        translationSpeakerTextView.setVisibility(View.INVISIBLE);

        // cancel animator
        cancelAnimator();

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float from = alcyoneView.getY();
        final float to = from - prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);

        // initialize duration
        float distance = Math.abs(to - from);
        long duration = calculateDuration(distance);

        // initialize value animator
        valueAnimatorY = ValueAnimator.ofFloat(from, to);
        valueAnimatorY.setDuration(duration);
        valueAnimatorY.setInterpolator(new DecelerateInterpolator());
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                alcyoneView.setY((Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // set color filter visibility
                findViewById(R.id.color_filter_pleiades).setVisibility(View.VISIBLE);

                // animate alcyone jump down
                animateAlcyoneJumpDown(from);
            }
        });

        // start animation
        valueAnimatorY.start();
    }

    private void animateAlcyoneJumpDown(float to) {
        // cancel animator
        cancelAnimator();

        // initialize from, to
        float from = alcyoneView.getY();

        // initialize duration
        float distance = Math.abs(to - from);
        long duration = calculateDuration(distance);

        // initialize value animator
        valueAnimatorY = ValueAnimator.ofFloat(from, to);
        valueAnimatorY.setDuration(duration);
        valueAnimatorY.setInterpolator(new AccelerateInterpolator());
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                alcyoneView.setY((Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // animate kittens rotate
                animateKittensRotate();
            }
        });

        // start animation
        valueAnimatorY.start();
    }

    private void animateKittensRotate() {
        // initialize rotate alcyone animation
        Animation rotateAlcyoneAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_rotate_kitten_right);
        rotateAlcyoneAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // change face sleep
                setFaceDelayed(true, FACE_CODE_BLINK_1, 0);
                setFaceDelayed(true, FACE_CODE_BLINK_2, 40);
                setFaceDelayed(true, FACE_CODE_BLINK_3, 80);
                setFaceDelayed(true, FACE_CODE_SLEEP, 120);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // initialize rotate pleione animation
        Animation rotatePleioneAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_rotate_kitten_right);
        rotatePleioneAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // change face sleep
                setFaceDelayed(false, FACE_CODE_BLINK_1, 0);
                setFaceDelayed(false, FACE_CODE_BLINK_2, 40);
                setFaceDelayed(false, FACE_CODE_BLINK_3, 80);
                setFaceDelayed(false, FACE_CODE_SLEEP, 120);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // read silent script
                readSilentScripts();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // start rotate animation
        alcyoneView.startAnimation(rotateAlcyoneAnimation);
        pleioneView.startAnimation(rotatePleioneAnimation);
    }

    private void readSilentScripts() {
        setTextDelayed(translationSpeakerTextView, "..", longAnimationDelay);
        setTextDelayed(translationSpeakerTextView, ".. ..", 2 * longAnimationDelay);
        setTextDelayed(translationSpeakerTextView, ".. .. ..", 3 * longAnimationDelay);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // animate pleione rotate back
                animatePleioneRotateBack();
            }
        }, 4 * longAnimationDelay);
    }

    private void animatePleioneRotateBack() {
        // initialize rotate pleione animation
        Animation rotatePleioneAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_rotate_kitten_right_back);
        rotatePleioneAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // read hungry script
                        readHungryScript();
                    }
                }, longAnimationDelay);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // start rotate animation
        pleioneView.startAnimation(rotatePleioneAnimation);
    }

    private void readHungryScript() {
        // change face default
        setFaceDelayed(false, FACE_CODE_BLINK_3, 0);
        setFaceDelayed(false, FACE_CODE_BLINK_2, 40);
        setFaceDelayed(false, FACE_CODE_BLINK_1, 80);
        setFaceDelayed(false, FACE_CODE_DEFAULT, 120);

        // initialize hungry script
        CharSequence script = getString(R.string.pleiades_script_hungry);

        // read hungry script
        translationSpeakerTextView.setText(script);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // animate pleione rotate
                animatePleioneRotateAgain();
            }
        }, readCharDelay * script.length() + longAnimationDelay);
    }

    private void animatePleioneRotateAgain() {
        // initialize but script
        final CharSequence script = getString(R.string.pleiades_script_but);

        // read but script
        translationSpeakerTextView.setText(script);

        // initialize rotate pleione animation
        Animation rotatePleioneAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_rotate_kitten_right);
        rotatePleioneAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // change face sleep
                setFaceDelayed(false, FACE_CODE_BLINK_1, 0);
                setFaceDelayed(false, FACE_CODE_BLINK_2, 40);
                setFaceDelayed(false, FACE_CODE_BLINK_3, 80);
                setFaceDelayed(false, FACE_CODE_SLEEP, 120);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // animate pleione rotate back again
                        animatePleioneRotateBackAgain();
                    }
                }, readCharDelay * script.length() + longAnimationDelay);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        pleioneView.startAnimation(rotatePleioneAnimation);
    }

    private void animatePleioneRotateBackAgain() {
        // initialize rotate pleione animation
        Animation rotatePleioneAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_rotate_kitten_right_back);
        rotatePleioneAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // animate pleione long jump up bed
                        animatePleioneLongJumpUpBed();
                    }
                }, longAnimationDelay);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // start rotate animation
        pleioneView.startAnimation(rotatePleioneAnimation);
    }

    private void animatePleioneLongJumpUpBed() {
        // cancel animator
        cancelAnimator();

        // change face sparkle
        pleioneFaceImageView.setImageResource(getFaceResourceId(FACE_CODE_SPARKLE));

        // initialize yureka script
        CharSequence script = getString(R.string.pleiades_script_yureka);

        // read hungry script
        translationSpeakerTextView.setText(script);

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float fromX = pleioneView.getX();
        final float fromY = pleioneView.getY();
        final float toX = fromX - ((float) prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE) / 2);
        final float toY = fromY - ((float) prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE) / 2);

        // initialize duration
        float distanceY = Math.abs(toY - fromY);
        long duration = calculateDuration(distanceY);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(fromX, toX);
        valueAnimatorY = ValueAnimator.ofFloat(fromY, toY);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setX((Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setY((Float) animation.getAnimatedValue());
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
                animatePleioneLongJumpDownBed();
            }
        });

        // start animation
        animatorSet.start();
    }

    private void animatePleioneLongJumpDownBed() {
        // cancel animator
        cancelAnimator();

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float fromX = pleioneView.getX();
        final float fromY = pleioneView.getY();
        final float toX = fromX - ((float) prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE) / 2);
        final float toY = findViewById(R.id.floor_pleiades).getY() - pleioneView.getHeight();

        // initialize duration
        float distanceY = Math.abs(toY - fromY);
        long duration = calculateDuration(distanceY);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(fromX, toX);
        valueAnimatorY = ValueAnimator.ofFloat(fromY, toY);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setX((Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setY((Float) animation.getAnimatedValue());
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
                animatePleioneLongJumpUpToGift();
            }
        });

        // start animation
        animatorSet.start();
    }

    private void animatePleioneLongJumpUpToGift() {
        // cancel animator
        cancelAnimator();

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float fromX = pleioneView.getX();
        final float fromY = pleioneView.getY();
        final float toX = fromX - ((float) prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE) / 2);
        final float toY = fromY - ((float) prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE) / 2);

        // initialize duration
        float distanceY = Math.abs(toY - fromY);
        long duration = calculateDuration(distanceY);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(fromX, toX);
        valueAnimatorY = ValueAnimator.ofFloat(fromY, toY);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setX((Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setY((Float) animation.getAnimatedValue());
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
                animatePleioneLongJumpDownToGift(fromY);
            }
        });

        // start animation
        animatorSet.start();
    }

    private void animatePleioneLongJumpDownToGift(final float toY) {
        // cancel animator
        cancelAnimator();

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float fromX = pleioneView.getX();
        final float fromY = pleioneView.getY();
        final float toX = fromX - ((float) prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE) / 2);

        // initialize duration
        float distanceY = Math.abs(toY - fromY);
        long duration = calculateDuration(distanceY);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(fromX, toX);
        valueAnimatorY = ValueAnimator.ofFloat(fromY, toY);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setX((Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setY((Float) animation.getAnimatedValue());
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
                // case arrival
                if (pleioneView.getX() <= giftImageView.getX()) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // animate gift long jump up
                            animateGiftLongJumpUp();
                        }
                    }, longAnimationDelay);
                } else
                    animatePleioneLongJumpUpToGift();
            }
        });

        // start animation
        animatorSet.start();
    }

    private void animateGiftLongJumpUp() {
        // cancel animator
        cancelAnimator();

        // initialize tranlation speaker text view
        translationSpeakerTextView.setText("");

        // initialize pleione
        pleioneView.setX(giftImageView.getX());
        pleioneFaceImageView.setImageResource(getFaceResourceId(FACE_CODE_DEFAULT));

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float fromX = giftImageView.getX();
        final float fromY = giftImageView.getY();
        final float toX = fromX + ((float) prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE) / 2);
        final float toY = fromY - ((float) prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE) / 2);

        // initialize duration
        float distanceY = Math.abs(toY - fromY);
        long duration = calculateDuration(distanceY);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(fromX, toX);
        valueAnimatorY = ValueAnimator.ofFloat(fromY, toY);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setX((Float) animation.getAnimatedValue());
                giftImageView.setX((Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setY((Float) animation.getAnimatedValue());
                giftImageView.setY((Float) animation.getAnimatedValue());
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
                animateGiftLongJumpDown(fromY);
            }
        });

        // start animation
        animatorSet.start();
    }

    private void animateGiftLongJumpDown(final float toY) {
        // cancel animator
        cancelAnimator();

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float fromX = giftImageView.getX();
        final float fromY = giftImageView.getY();
        final float toX = fromX + ((float) prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE) / 2);

        // initialize duration
        float distanceY = Math.abs(toY - fromY);
        long duration = calculateDuration(distanceY);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(fromX, toX);
        valueAnimatorY = ValueAnimator.ofFloat(fromY, toY);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setX((Float) animation.getAnimatedValue());
                giftImageView.setX((Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setY((Float) animation.getAnimatedValue());
                giftImageView.setY((Float) animation.getAnimatedValue());
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
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // read hoo script
                        readHooScript();
                    }
                }, defaultAnimationDelay);
            }
        });

        // start animation
        animatorSet.start();
    }

    private void readHooScript() {
        // change face meow
        setFaceDelayed(false, FACE_CODE_MEOW_2, 0);
        setFaceDelayed(false, FACE_CODE_MEOW_1, 40);

        // initialize hoo script
        CharSequence script = getString(R.string.pleiades_script_hoo);

        // read hoo script
        translationSpeakerTextView.setText(script);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // animate gift hide
                animatePleioneHide();
            }
        }, readCharDelay * script.length() + longAnimationDelay);
    }

    private void animatePleioneHide() {
        // cancel animator
        cancelAnimator();

        // initialize from, to
        final float from = pleioneView.getY();
        final float to = from + ((float) pleioneView.getHeight() / 2);

        // initialize duration
        float distance = Math.abs(to - from);
        long duration = calculateDuration(distance);

        // initialize value animator
        valueAnimatorY = ValueAnimator.ofFloat(from, to);
        valueAnimatorY.setDuration(duration);
        valueAnimatorY.setInterpolator(new AccelerateInterpolator());
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setY((Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // read dispatch script
                readDispatchScript();
            }
        });

        // start animation
        valueAnimatorY.start();
    }

    private void readDispatchScript() {
        // change face default
        pleioneFaceImageView.setImageResource(getFaceResourceId(FACE_CODE_DEFAULT));

        // initialize dispatch script
        CharSequence script = getString(R.string.pleiades_script_dispatch);

        // read dispatch script
        translationSpeakerTextView.setText(script);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isOpeningSkipped)
                    startGame();
                else {
                    DefaultDialogFragment defaultDialogFragment = new DefaultDialogFragment(DIALOG_TYPE_START_PLEIADES);
                    defaultDialogFragment.show(getSupportFragmentManager(), Integer.toString(DIALOG_TYPE_START_PLEIADES));
                }
            }
        }, readCharDelay * script.length());
    }

    // start game
    @SuppressLint("ClickableViewAccessibility")
    public void startGame() {
        // initialize is opening finished
        isOpeningFinished = true;

        // initialize delays
        initializeDelays();

        // reset translation text view
        translationSpeakerTextView.setText("");

        // change face sweat
        pleioneFaceImageView.setImageResource(getFaceResourceId(FACE_CODE_SWEAT_2));

        // initialize timer task
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (isAlcyoneLocked)
                    return;
                if (!isButtonTouched)
                    return;

                // animate alcyone rotate back
                animateAlcyoneRotateBack();
            }
        };

        // initialize timer
        timer = new Timer();
        timer.schedule(timerTask, 4 * longAnimationDelay, 4 * longAnimationDelay);

        // initialize button touch listener
        Button button = findViewById(R.id.button_pleiades);
        button.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // motion event
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // play ripple effect
                        v.setPressed(true);

                        // play click sound
                        v.playSoundEffect(android.view.SoundEffectConstants.CLICK);

                        // set is button touched
                        isButtonTouched = true;

                        // animate head up
                        animateHeadUp();

                        return true;
                    case MotionEvent.ACTION_UP:
                        // stop ripple effect
                        v.setPressed(false);

                        // set is button touched
                        isButtonTouched = false;

                        // animate heap down
                        animateHeadDown();

                        return true;
                }
                return false;
            }
        });
    }

    private void animateHeadUp() {
        // cancel animator
        cancelAnimator();

        // initialize from, to
        final float from = pleioneView.getY();
        final float to = giftImageView.getY();

        // initialize duration
        float distance = Math.abs(to - from);
        long duration = calculateDuration(distance);

        // initialize value animator
        valueAnimatorGameY = ValueAnimator.ofFloat(from, to);
        valueAnimatorGameY.setDuration(duration);
        valueAnimatorGameY.setInterpolator(new DecelerateInterpolator());
        valueAnimatorGameY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setY((Float) animation.getAnimatedValue());

                // catch pleione
                if (isAlcyoneStand)
                    catchPleione();
            }
        });
        valueAnimatorGameY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isButtonTouched) {
                    if (valueAnimatorGameX == null)
                        animateGiftRight();
                    else
                        valueAnimatorGameX.resume();
                } else
                    animateHeadDown();
            }
        });

        // start animation
        if (isButtonTouched)
            valueAnimatorGameY.start();
    }

    private void animateHeadDown() {
        // cancel animator
        cancelAnimator();

        // initialize from, to
        final float from = pleioneView.getY();
        final float to = giftImageView.getY() + ((float) pleioneView.getHeight() / 2);

        // initialize duration
        float distance = Math.abs(to - from);
        long duration = calculateDuration(distance);

        // initialize value animator
        valueAnimatorGameY = ValueAnimator.ofFloat(from, to);
        valueAnimatorGameY.setDuration(duration);
        valueAnimatorGameY.setInterpolator(new AccelerateInterpolator());
        valueAnimatorGameY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setY((Float) animation.getAnimatedValue());

                // catch pleione
                if (isAlcyoneStand)
                    catchPleione();
            }
        });
        valueAnimatorGameY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {
                // pause gift right
                if (valueAnimatorGameX != null)
                    valueAnimatorGameX.pause();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isButtonTouched)
                    animateHeadUp();
            }
        });

        // start animation
        if (!isButtonTouched)
            valueAnimatorGameY.start();
    }

    private void animateGiftRight() {
        // initialize from, to
        final float from = giftImageView.getX();
        final float to = findViewById(R.id.layout_pleiades).getWidth() - ((float) giftImageView.getWidth() / 2);

        // initialize duration
        long duration = 10 * longAnimationDelay;

        // initialize value animator
        valueAnimatorGameX = ValueAnimator.ofFloat(from, to);
        valueAnimatorGameX.setDuration(duration);
        valueAnimatorGameX.setInterpolator(new LinearInterpolator());
        valueAnimatorGameX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                giftImageView.setX((Float) animation.getAnimatedValue());
                pleioneView.setX((Float) animation.getAnimatedValue());

                // catch pleione
                if (isAlcyoneStand)
                    catchPleione();
            }
        });
        valueAnimatorGameX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // animate all left
                if (!isPleioneCaught)
                    animateAllLeft();
            }
        });

        // start animation
        valueAnimatorGameX.start();
    }

    private void animateAlcyoneRotateBack() {
        // initialize is game difficulty hard
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        final boolean isGameDifficultyHard = prefs.getBoolean(KEY_IS_GAME_DIFFICULTY_HARD, false);

        // initialize rotate animation
        Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_rotate_kitten_right_back);
        rotateAnimation.setDuration(isGameDifficultyHard ? (int) (2 * rotateAnimation.getDuration()) : (int) (2.5 * rotateAnimation.getDuration()));
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // lock alcyone
                isAlcyoneLocked = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // set is alcyone stand true
                isAlcyoneStand = true;

                // initialize rotate delay
                int randomValue = new Random().nextInt(3) + 1; // 1 ~ 3
                long delay = randomValue * defaultAnimationDelay; // 1 ~ 3 seconds

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isPleioneCaught)
                            return;

                        // animate alcyone rotate again
                        animateAlcyoneRotateAgain();
                    }
                }, delay);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // start rotate animation
        alcyoneView.startAnimation(rotateAnimation);
    }

    private void animateAlcyoneRotateAgain() {
        // initialize is game difficulty hard
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        final boolean isGameDifficultyHard = prefs.getBoolean(KEY_IS_GAME_DIFFICULTY_HARD, false);

        // initialize rotate animation
        Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_rotate_kitten_right);
        rotateAnimation.setDuration(isGameDifficultyHard ? (int) (1.5 * rotateAnimation.getDuration()) : (2 * rotateAnimation.getDuration()));
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // set is alcyone stand false
                isAlcyoneStand = false;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // unlock alcyone
                isAlcyoneLocked = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // start rotate animation
        alcyoneView.startAnimation(rotateAnimation);
    }

    private void catchPleione() {
        if (!isPleioneCaught) {
            // set is pleione caught
            isPleioneCaught = true;

            // cancel game
            cancelGame();

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // read catch script
                    readCatchScript();
                }
            }, longAnimationDelay);
        }
    }

    private void readCatchScript() {
        // initialize scenario
        Scenario scenario = new Scenario(context, R.array.scripts_pleiades_catch, R.array.scripts_pleiades_catch_translation);
        CharSequence script = scenario.removeFirstScript();
        CharSequence translatedScript = scenario.removeFirstTranslatedScript();
        long duration = scenario.removeFirstDuration();

        // read script
        for (int i = 1; i <= script.length(); i++)
            setTextDelayedVisible(alcyoneSpeakerTextView, script.subSequence(0, i), i * readCharDelay);
        setTextDelayedVisible(translationSpeakerTextView, translatedScript, readCharDelay);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // finish game
                finishGame(false);
            }
        }, duration + shortAnimationDelay);
    }

    private void animateAllLeft() {
        // cancel game
        cancelGame();

        // cancel animator
        cancelAnimator();

        // initialize distance
        float distance = bedPillarRightImageView.getX() + bedPillarRightImageView.getWidth();
        long duration = calculateDuration(distance);

        // initialize refrigerator
        float refrigeratorX = refrigeratorImageView.getX();
        refrigeratorImageView.setX(refrigeratorX + distance);
        refrigeratorImageView.setVisibility(View.VISIBLE);

        // initialize alcyone
        alcyoneBodyImageView.setVisibility(View.INVISIBLE);
        alcyoneCostumeImageView.setVisibility(View.INVISIBLE);
        alcyoneFaceImageView.setVisibility(View.INVISIBLE);

        // initialize from
        final float pleioneFrom = pleioneView.getX();
        final float giftFrom = giftImageView.getX();
        final float refrigeratorFrom = refrigeratorImageView.getX();
        final float bedLowerFrom = bedLowerImageView.getX();
        final float bedUpperFrom = bedUpperImageView.getX();
        final float bedPillarLeftFrom = bedPillarLeftImageView.getX();
        final float bedPillarRightFrom = bedPillarRightImageView.getX();

        // initialize value animator
        valueAnimatorX = ValueAnimator.ofFloat(0, distance);
        valueAnimatorX.setDuration(duration);
        valueAnimatorX.setInterpolator(new DecelerateInterpolator());
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (Float) animation.getAnimatedValue();

                pleioneView.setX(pleioneFrom - animatedValue);
                giftImageView.setX(giftFrom - animatedValue);
                refrigeratorImageView.setX(refrigeratorFrom - animatedValue);
                bedLowerImageView.setX(bedLowerFrom - animatedValue);
                bedUpperImageView.setX(bedUpperFrom - animatedValue);
                bedPillarLeftImageView.setX(bedPillarLeftFrom - animatedValue);
                bedPillarRightImageView.setX(bedPillarRightFrom - animatedValue);
            }
        });
        valueAnimatorX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // animate gift long jump up to refrigerator
                        animateGiftLongJumpUpToRefrigerator();
                    }
                }, shortAnimationDelay);
            }
        });

        // start animation
        valueAnimatorX.start();
    }

    private void animateGiftLongJumpUpToRefrigerator() {
        // cancel animator
        cancelAnimator();

        // initialize to x max
        float toXMax = refrigeratorImageView.getX() - giftImageView.getWidth();

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float fromX = giftImageView.getX();
        final float fromY = giftImageView.getY();
        final float toX = Math.min(fromX + ((float) prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE) / 2), toXMax);
        final float toY = fromY - ((float) prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE) / 2);

        // initialize duration
        float distanceY = Math.abs(toY - fromY);
        long duration = calculateDuration(distanceY);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(fromX, toX);
        valueAnimatorY = ValueAnimator.ofFloat(fromY, toY);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setX((Float) animation.getAnimatedValue());
                giftImageView.setX((Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setY((Float) animation.getAnimatedValue());
                giftImageView.setY((Float) animation.getAnimatedValue());
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
                // animate gift long jump down to refrigerator
                animateGiftLongJumpDownToRefrigerator(fromY);
            }
        });

        // start animation
        animatorSet.start();
    }

    private void animateGiftLongJumpDownToRefrigerator(final float toY) {
        // cancel animator
        cancelAnimator();

        // initialize toXMax
        final float toXMax = refrigeratorImageView.getX() - giftImageView.getWidth();

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float fromX = giftImageView.getX();
        final float fromY = giftImageView.getY();
        final float toX = Math.min(fromX + ((float) prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE) / 2), toXMax);

        // initialize duration
        float distanceY = Math.abs(toY - fromY);
        long duration = calculateDuration(distanceY);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(fromX, toX);
        valueAnimatorY = ValueAnimator.ofFloat(fromY, toY);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setX((Float) animation.getAnimatedValue());
                giftImageView.setX((Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setY((Float) animation.getAnimatedValue());
                giftImageView.setY((Float) animation.getAnimatedValue());
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
                // case arrival
                if (toX == toXMax) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // cnahge pleione face
                            pleioneFaceImageView.setImageResource(R.drawable.image_face_sparkle);

                            // animate pleione jump up
                            animatePleioneJumpUp();
                        }
                    }, defaultAnimationDelay);
                } else
                    animateGiftLongJumpUpToRefrigerator();
            }
        });

        // start animation
        animatorSet.start();
    }

    private void animatePleioneJumpUp() {
        // cancel animator
        cancelAnimator();

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float from = pleioneView.getY();
        final float to = from - prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);

        // initialize duration
        float distance = Math.abs(to - from);
        long duration = calculateDuration(distance);

        // initialize value animator
        valueAnimatorY = ValueAnimator.ofFloat(from, to);
        valueAnimatorY.setDuration(duration);
        valueAnimatorY.setInterpolator(new DecelerateInterpolator());
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setY((Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // animate pleione jump down
                animatePleioneJumpDown(from);
            }
        });

        // start animation
        valueAnimatorY.start();
    }

    private void animatePleioneJumpDown(float to) {
        // cancel animator
        cancelAnimator();

        // initialize from, to
        float from = pleioneView.getY();

        // initialize duration
        float distance = Math.abs(to - from);
        long duration = calculateDuration(distance);

        // initialize value animator
        valueAnimatorY = ValueAnimator.ofFloat(from, to);
        valueAnimatorY.setDuration(duration);
        valueAnimatorY.setInterpolator(new AccelerateInterpolator());
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pleioneView.setY((Float) animation.getAnimatedValue());
            }
        });
        valueAnimatorY.addListener(new AnimatorListenerAdapter() {
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
        valueAnimatorY.start();
    }

    private void finishGame(boolean isPlayerWin) {
        // case player win
        if (isPlayerWin) {
            SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            final boolean isGameDifficultyHard = prefs.getBoolean(KEY_IS_GAME_DIFFICULTY_HARD, false);
            int gameRewardItemCode = new PrefsController(context).addRandomItemPrefs(isGameDifficultyHard ? REWARD_TYPE_GAME_ITEM_HARD : REWARD_TYPE_GAME_ITEM_EASY);
            DefaultDialogFragment defaultDialogFragment = new DefaultDialogFragment(DIALOG_TYPE_WIN_PLEIADES);
            defaultDialogFragment.show(getSupportFragmentManager(), Converter.getItemName(context, gameRewardItemCode));
        }
        // case enemy win
        else {
            setResult(-1);
            finish();
        }
    }

    // others
    public void skipOpening() {
        isOpeningSkipped = true;

        // set speaker visibility
        alcyoneSpeakerTextView.setVisibility(View.INVISIBLE);
        pleioneSpeakerTextView.setVisibility(View.INVISIBLE);
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
                    if (!isOpeningSkipped)
                        speakerTextView.setVisibility(View.VISIBLE);
                    speakerTextView.setText(message);
                }
            }
        }, delay);
    }

    private void setTextDelayedVisible(final TextView speakerTextView, final CharSequence message, long delay) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (speakerTextView != null) {
                    speakerTextView.setVisibility(View.VISIBLE);
                    speakerTextView.setText(message);
                }
            }
        }, delay);
    }

    private void setFaceDelayed(final boolean isAlcyone, final int faceCode, long delay) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAlcyone) {
                    if (alcyoneFaceImageView != null)
                        alcyoneFaceImageView.setImageResource(getFaceResourceId(faceCode));
                } else {
                    if (pleioneFaceImageView != null)
                        pleioneFaceImageView.setImageResource(getFaceResourceId(faceCode));
                }
            }
        }, delay);
    }

    private long calculateDuration(float distance) {
        long duration;
        duration = Math.round((int) (100 * Math.sqrt(distance / 49)));
        duration = Math.round(context.getSharedPreferences(PREFS, MODE_PRIVATE).getFloat(KEY_ANIMATOR_DURATION_SCALE, 1) * duration);
        return duration;
    }

    private void cancelAnimator() {
        if (valueAnimatorX != null) {
            valueAnimatorX.removeAllUpdateListeners();
            valueAnimatorX.removeAllListeners();
            valueAnimatorX.cancel();
        }
        if (valueAnimatorY != null) {
            valueAnimatorY.removeAllUpdateListeners();
            valueAnimatorY.removeAllListeners();
            valueAnimatorY.cancel();
        }
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            animatorSet.cancel();
        }
    }

    private void cancelGame() {
        // remove touch listener
        findViewById(R.id.button_pleiades).setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        if (valueAnimatorGameX != null) {
            valueAnimatorGameX.pause();
            valueAnimatorGameX.removeAllUpdateListeners();
            valueAnimatorGameX.removeAllListeners();
            valueAnimatorGameX.cancel();
        }

        if (valueAnimatorGameY != null) {
            valueAnimatorGameY.pause();
            valueAnimatorGameY.removeAllUpdateListeners();
            valueAnimatorGameY.removeAllListeners();
            valueAnimatorGameY.cancel();
        }

        // cancel timer task
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        // cancel timer
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
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
            defaultDialogFragment.show(getSupportFragmentManager(), Converter.getCostumeName(context, COSTUME_CODE_PLEIONE));
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
        cancelAnimator();
        cancelGame();

        super.onDestroy();
    }
}