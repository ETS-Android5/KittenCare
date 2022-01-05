package com.pleiades.pleione.kittencare.ui.activity.game;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.TypedValue;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.pleiades.pleione.kittencare.Converter;
import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.controller.DeviceController;
import com.pleiades.pleione.kittencare.controller.PrefsController;
import com.pleiades.pleione.kittencare.object.Scenario;
import com.pleiades.pleione.kittencare.ui.fragment.dialog.DefaultDialogFragment;

import java.util.Random;

import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_PAJAMAS;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_JUMP_ALTITUDE;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_JUMP_DISTANCE;
import static com.pleiades.pleione.kittencare.Config.DELAY_GAME_ANIMATION_DEFAULT;
import static com.pleiades.pleione.kittencare.Config.DELAY_GAME_ANIMATION_LONG;
import static com.pleiades.pleione.kittencare.Config.DELAY_GAME_ANIMATION_SHORT;
import static com.pleiades.pleione.kittencare.Config.DELAY_GAME_READ_CHAR;
import static com.pleiades.pleione.kittencare.Config.DELAY_GAME_READ_CHAR_SLOW;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_FINISH_GAME;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_SKIP_OPENING;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_START_PAJAMAS;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_WIN_PAJAMAS;
import static com.pleiades.pleione.kittencare.Config.DIRECTION_LEFT_TO_CENTER;
import static com.pleiades.pleione.kittencare.Config.DIRECTION_LEFT_TO_RIGHT;
import static com.pleiades.pleione.kittencare.Config.DIRECTION_RIGHT_TO_CENTER;
import static com.pleiades.pleione.kittencare.Config.DIRECTION_RIGHT_TO_LEFT;
import static com.pleiades.pleione.kittencare.Config.DIRECTION_TO_LEFT;
import static com.pleiades.pleione.kittencare.Config.DIRECTION_TO_RIGHT;
import static com.pleiades.pleione.kittencare.Config.KEY_ANIMATOR_DURATION_SCALE;
import static com.pleiades.pleione.kittencare.Config.KEY_IS_GAME_DIFFICULTY_HARD;
import static com.pleiades.pleione.kittencare.Config.KEY_JUMP_ALTITUDE;
import static com.pleiades.pleione.kittencare.Config.KEY_JUMP_DISTANCE;
import static com.pleiades.pleione.kittencare.Config.PREFS;
import static com.pleiades.pleione.kittencare.Config.REWARD_TYPE_GAME_ITEM_EASY;
import static com.pleiades.pleione.kittencare.Config.REWARD_TYPE_GAME_ITEM_HARD;
import static com.pleiades.pleione.kittencare.controller.AnimationController.calculateDurationGravity;

public class PajamasActivity extends AppCompatActivity {
    private Context context;

    // translation view
    private TextView translationSpeakerTextView;

    // floor view
    private TextView floorSpeakerTextView;

    // bed view
    private ImageView bedImageView;
    private TextView bedSpeakerTextView;

    // left view
    private View leftKittenView;
    private TextView leftSpeakerTextView;

    // right view
    private View rightKittenView;
    private ImageView rightFaceImageView;
    private TextView rightSpeakerTextView;

    // game object view
    private ProgressBar playerProgressBar, enemyProgressBar;
    private ImageView playerTtakjiImageView, enemyTtakjiImageView;
    private TextView playerHPTextView, enemyHPTextView;
    private TextView enemySpeakerTextView;

    // animator
    private ValueAnimator valueAnimatorX, valueAnimatorY, valueAnimatorProgress;
    private AnimatorSet animatorSet;

    public boolean isOpeningSkipped;
    private boolean isOpeningFinished;
    private boolean isPlayerButtonLocked, isEnemyButtonLocked;
    private int playerHP = 50;
    private int enemyHP = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_pajamas);

        // initialize context
        context = PajamasActivity.this;

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // set navigation color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
            getWindow().setNavigationBarColor(ContextCompat.getColor(context, R.color.color_navigation_background));

        // initialize appbar
        View appbar = findViewById(R.id.appbar_pajamas);
        Toolbar toolbar = appbar.findViewById(R.id.toolbar_sub);
        setSupportActionBar(toolbar);

        // initialize action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // initialize translation views
        translationSpeakerTextView = findViewById(R.id.speaker_translation_pajamas);

        // initialize floor views
        floorSpeakerTextView = findViewById(R.id.speaker_floor_pajamas);

        // initialize bed views
        bedImageView = findViewById(R.id.bed_pajamas);
        bedSpeakerTextView = findViewById(R.id.speaker_bed_pajamas);

        // initialize left kitten views
        leftKittenView = findViewById(R.id.kitten_left_pajamas);
        ImageView leftBodyImageView = leftKittenView.findViewById(R.id.body_kitten);
        leftBodyImageView.setImageResource(R.drawable.image_body_crop);
        ImageView leftFaceImageView = leftKittenView.findViewById(R.id.face_kitten);
        leftFaceImageView.setImageResource(R.drawable.image_face_angry);
        ImageView leftCostumeImageView = leftKittenView.findViewById(R.id.costume_kitten);
        leftCostumeImageView.setImageResource(R.drawable.image_costume_pajamas);
        leftSpeakerTextView = findViewById(R.id.speaker_left_pajamas);

        // initialize right kitten views
        rightKittenView = findViewById(R.id.kitten_right_pajamas);
        ImageView rightBodyImageView = rightKittenView.findViewById(R.id.body_kitten);
        rightBodyImageView.setImageResource(R.drawable.image_body_crop);
        rightFaceImageView = rightKittenView.findViewById(R.id.face_kitten);
        ImageView rightCostumeImageView = rightKittenView.findViewById(R.id.costume_kitten);
        rightCostumeImageView.setImageResource(R.drawable.image_costume_sweater);
        rightSpeakerTextView = findViewById(R.id.speaker_right_pajamas);

        // initialize game object views
        playerProgressBar = findViewById(R.id.progress_player_pajamas);
        enemyProgressBar = findViewById(R.id.progress_enemy_pajamas);
        playerTtakjiImageView = findViewById(R.id.ttakji_player_pajamas);
        enemyTtakjiImageView = findViewById(R.id.ttakji_enemy_pajamas);
        playerHPTextView = findViewById(R.id.hp_player_pajamas);
        enemyHPTextView = findViewById(R.id.hp_enemy_pajamas);
        enemySpeakerTextView = findViewById(R.id.speaker_enemy_pajamas);

        // start story
        startStory();
    }

    // start story
    private void startStory() {
        readSleepScript(DELAY_GAME_ANIMATION_SHORT);

        // read sleep again
        readSleepScript(DELAY_GAME_ANIMATION_SHORT + DELAY_GAME_ANIMATION_LONG);

        // animate bed left
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::animateBedLeft, DELAY_GAME_ANIMATION_SHORT + (2 * DELAY_GAME_ANIMATION_LONG));
    }

    private void readSleepScript(long delay) {
        final CharSequence script = getString(R.string.pajamas_script_sleep);

        // read script char sequence
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            for (int i = 1; i <= script.length(); i++)
                setTextDelayed(bedSpeakerTextView, script.subSequence(0, i), i * DELAY_GAME_READ_CHAR_SLOW);
        }, delay);
    }

    private void animateBedLeft() {
        // cancel animator
        cancelAnimator();

        // case opening finished
        if (isOpeningFinished)
            return;

        // initialize device controller
        final DeviceController deviceController = new DeviceController(context);

        // initialize distance, duration
        final float distance = (float) deviceController.getWidthMax() / 2;
        final float animatorDurationScale = getSharedPreferences(PREFS, MODE_PRIVATE).getFloat(KEY_ANIMATOR_DURATION_SCALE, 1);
        final long duration = 5 * (long) (animatorDurationScale * distance);

        // initialize from, to
        final float from = bedImageView.getX();
        final float to = from - distance;

        // initialize bed speaker from
        final float bedSpeakerFrom = bedSpeakerTextView.getX();

        // initialize value animator
        valueAnimatorX = ValueAnimator.ofFloat(from, to);

        // set value animator attributes
        valueAnimatorX.setDuration(duration);
        valueAnimatorX.setInterpolator(new AccelerateInterpolator());
        valueAnimatorX.addUpdateListener(animation -> {
            float movedDistance = from - (Float) animation.getAnimatedValue();

            bedImageView.setX((Float) animation.getAnimatedValue());
            bedSpeakerTextView.setX(bedSpeakerFrom - movedDistance);
        });
        valueAnimatorX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateFourLeft();
            }
        });

        // start animation
        valueAnimatorX.start();
    }

    private void animateFourLeft() {
        // cancel animator
        cancelAnimator();

        // case opening finished
        if (isOpeningFinished)
            return;

        // initialize distance, duration
        final float distance = (float) bedImageView.getWidth() / 2 + bedSpeakerTextView.getWidth();
        final float animatorDurationScale = getSharedPreferences(PREFS, MODE_PRIVATE).getFloat(KEY_ANIMATOR_DURATION_SCALE, 1);
        final long duration = 5 * (long) (animatorDurationScale * distance);

        // initialize from, to
        final float from = bedImageView.getX();
        final float to = from - distance;

        // initialize other from
        final float floorSpeakerFrom = floorSpeakerTextView.getX();
        final float bedSpeakerFrom = bedSpeakerTextView.getX();
        final float rightKittenFrom = rightKittenView.getX();
        final float rightSpeakerFrom = rightSpeakerTextView.getX();

        // initialize value animator
        valueAnimatorX = ValueAnimator.ofFloat(from, to);

        // set value animator attributes
        valueAnimatorX.setDuration(duration);
        valueAnimatorX.setInterpolator(new DecelerateInterpolator());
        valueAnimatorX.addUpdateListener(animation -> {
            float movedDistance = from - (Float) animation.getAnimatedValue();

            bedImageView.setX((Float) animation.getAnimatedValue());
            floorSpeakerTextView.setX(floorSpeakerFrom - movedDistance);
            bedSpeakerTextView.setX(bedSpeakerFrom - movedDistance);
            rightKittenView.setX(rightKittenFrom - movedDistance);
            rightSpeakerTextView.setX(rightSpeakerFrom - movedDistance);
        });
        valueAnimatorX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> animateRightKittenJumpUp(3), DELAY_GAME_ANIMATION_DEFAULT);
            }
        });

        // start animation
        valueAnimatorX.start();
    }

    private void animateRightKittenJumpUp(final int repeatCount) {
        // cancel animator
        cancelAnimator();

        // case opening finished
        if (isOpeningFinished)
            return;

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float from = rightKittenView.getY();
        final float to = from - prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);

        // initialize duration
        float distance = Math.abs(to - from);
        long duration = (4 - repeatCount) * calculateDurationGravity(context, distance, false);

        // initialize value animator
        valueAnimatorY = ValueAnimator.ofFloat(from, to);
        valueAnimatorY.setDuration(duration);
        valueAnimatorY.setInterpolator(new DecelerateInterpolator());
        valueAnimatorY.addUpdateListener(animation -> rightKittenView.setY((Float) animation.getAnimatedValue()));
        valueAnimatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateRightKittenJumpDown(from, repeatCount);
            }
        });

        // start animation
        valueAnimatorY.start();
    }

    private void animateRightKittenJumpDown(float to, final int repeatCount) {
        // cancel animator
        cancelAnimator();

        // case opening finished
        if (isOpeningFinished)
            return;

        // initialize from, to
        float from = rightKittenView.getY();

        // initialize duration
        float distance = Math.abs(to - from);
        long duration = calculateDurationGravity(context, distance, false);

        // initialize value animator
        valueAnimatorY = ValueAnimator.ofFloat(from, to);
        valueAnimatorY.setDuration(duration);
        valueAnimatorY.setInterpolator(new AccelerateInterpolator());
        valueAnimatorY.addUpdateListener(animation -> rightKittenView.setY((Float) animation.getAnimatedValue()));
        valueAnimatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Resources resources = context.getResources();
                switch (repeatCount) {
                    case 3:
                        floorSpeakerTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.text_size_default));
                        break;
                    case 2:
                        floorSpeakerTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.text_size_very_large));
                        break;
                    case 1:
                        floorSpeakerTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.text_size_huge));
                        break;
                }
                floorSpeakerTextView.setVisibility(View.VISIBLE);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    floorSpeakerTextView.setVisibility(View.INVISIBLE);

                    if (repeatCount == 1) {
                        rightFaceImageView.setImageResource(R.drawable.image_face_sparkle);
                        animateRightKittenJumpUp();
                    } else
                        animateRightKittenJumpUp(repeatCount - 1);
                }, (repeatCount == 1) ? 2 * DELAY_GAME_ANIMATION_DEFAULT : DELAY_GAME_ANIMATION_DEFAULT);
            }
        });

        // start animation
        valueAnimatorY.start();
    }

    private void animateRightKittenJumpUp() {
        // cancel animator
        cancelAnimator();

        // case opening finished
        if (isOpeningFinished)
            return;

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float from = rightKittenView.getY();
        final float to = from - prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);

        // initialize duration
        float distance = Math.abs(to - from);
        long duration = calculateDurationGravity(context, distance, false);

        // initialize value animator
        valueAnimatorY = ValueAnimator.ofFloat(from, to);
        valueAnimatorY.setDuration(duration);
        valueAnimatorY.setInterpolator(new DecelerateInterpolator());
        valueAnimatorY.addUpdateListener(animation -> rightKittenView.setY((Float) animation.getAnimatedValue()));
        valueAnimatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateRightKittenJumpDown(from);
            }
        });

        // start animation
        valueAnimatorY.start();
    }

    private void animateRightKittenJumpDown(float to) {
        // cancel animator
        cancelAnimator();

        // case opening finished
        if (isOpeningFinished)
            return;

        // initialize from, to
        float from = rightKittenView.getY();

        // initialize duration
        float distance = Math.abs(to - from);
        long duration = calculateDurationGravity(context, distance, false);

        // initialize value animator
        valueAnimatorY = ValueAnimator.ofFloat(from, to);
        valueAnimatorY.setDuration(duration);
        valueAnimatorY.setInterpolator(new AccelerateInterpolator());
        valueAnimatorY.addUpdateListener(animation -> rightKittenView.setY((Float) animation.getAnimatedValue()));
        valueAnimatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> animateLeftKittenLongJumpUp(), 2 * DELAY_GAME_ANIMATION_DEFAULT);
            }
        });

        // start animation
        valueAnimatorY.start();
    }

    private void animateLeftKittenLongJumpUp() {
        // cancel animator
        cancelAnimator();

        // case opening finished
        if (isOpeningFinished)
            return;

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float fromX = leftKittenView.getX();
        final float fromY = leftKittenView.getY();
        final float toX = fromX + prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE);
        final float toY = fromY - prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);

        // initialize other from
        final float leftSpeakerFromX = leftSpeakerTextView.getX();

        // initialize duration
        float distanceY = Math.abs(toY - fromY);
        long duration = calculateDurationGravity(context, distanceY, false);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(fromX, toX);
        valueAnimatorY = ValueAnimator.ofFloat(fromY, toY);
        valueAnimatorX.addUpdateListener(animation -> {
            float movedDistanceX = (Float) animation.getAnimatedValue() - fromX;

            leftKittenView.setX((Float) animation.getAnimatedValue());
            leftSpeakerTextView.setX(leftSpeakerFromX + movedDistanceX);
        });
        valueAnimatorY.addUpdateListener(animation -> leftKittenView.setY((Float) animation.getAnimatedValue()));

        // initialize animator set
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateLeftKittenLongJumpDown(fromY);
            }
        });

        // start kitten animation
        animatorSet.start();
    }

    private void animateLeftKittenLongJumpDown(float toY) {
        // cancel animator
        cancelAnimator();

        // case opening finished
        if (isOpeningFinished)
            return;

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float fromX = leftKittenView.getX();
        final float fromY = leftKittenView.getY();
        final float toX = fromX + prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE);

        // initialize other from
        final float leftSpeakerFromX = leftSpeakerTextView.getX();

        // initialize duration
        float distanceY = Math.abs(toY - fromY);
        long duration = calculateDurationGravity(context, distanceY, false);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(fromX, toX);
        valueAnimatorY = ValueAnimator.ofFloat(fromY, toY);
        valueAnimatorX.addUpdateListener(animation -> {
            float movedDistanceX = (Float) animation.getAnimatedValue() - fromX;

            leftKittenView.setX((Float) animation.getAnimatedValue());
            leftSpeakerTextView.setX(leftSpeakerFromX + movedDistanceX);
        });
        valueAnimatorY.addUpdateListener(animation -> leftKittenView.setY((Float) animation.getAnimatedValue()));

        // initialize animator set
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rightFaceImageView.setImageResource(R.drawable.image_face_default);
                readFightScripts();
            }
        });

        // start kitten animation
        animatorSet.start();
    }

    private void readFightScripts() {
        // case opening finished
        if (isOpeningFinished)
            return;

        Scenario fightScenarioLeft = new Scenario(context, R.array.scripts_pajamas_fight_left, R.array.scripts_pajamas_fight_translation_left);
        Scenario fightScenarioRight = new Scenario(context, R.array.scripts_pajamas_fight_right, R.array.scripts_pajamas_fight_translation_right);
        int leftSize = fightScenarioLeft.getSize();
        int rightSize = fightScenarioRight.getSize();
        boolean isTurnLeft = true;
        long totalDuration = DELAY_GAME_ANIMATION_DEFAULT;

        for (int i = 0; i < leftSize + rightSize; i++) {
            CharSequence script = isTurnLeft ? fightScenarioLeft.removeFirstScript() : fightScenarioRight.removeFirstScript();
            CharSequence translatedScript = isTurnLeft ? fightScenarioLeft.removeFirstTranslatedScript() : fightScenarioRight.removeFirstTranslatedScript();
            long duration = isTurnLeft ? fightScenarioLeft.removeFirstDuration() : fightScenarioRight.removeFirstDuration();

            // read fight script char sequence
            readFightScript(isTurnLeft, script, translatedScript, totalDuration);

            // change face before last script
            if (i == (leftSize + rightSize - 1)) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> rightFaceImageView.setImageResource(R.drawable.image_face_angry), totalDuration);
            }

            // calculate total duration
            totalDuration += duration;

            // alternate layout after last script
            if (i == (leftSize + rightSize - 1)) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(this::alternateLayout, totalDuration + DELAY_GAME_ANIMATION_SHORT);
            }

            // change direction
            isTurnLeft = !isTurnLeft;
        }
    }

    private void readFightScript(final boolean isLeft, final CharSequence script, final CharSequence translatedScript, long delay) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            // case opening finished
            if (isOpeningFinished)
                return;

            leftSpeakerTextView.setText("");
            rightSpeakerTextView.setText("");

            if (isLeft) {
                rightSpeakerTextView.setVisibility(View.INVISIBLE);

                // read script
                for (int i = 1; i <= script.length(); i++)
                    setTextDelayed(leftSpeakerTextView, script.subSequence(0, i), i * DELAY_GAME_READ_CHAR);
            } else {
                leftSpeakerTextView.setVisibility(View.INVISIBLE);

                // read script
                for (int i = 1; i <= script.length(); i++)
                    setTextDelayed(rightSpeakerTextView, script.subSequence(0, i), i * DELAY_GAME_READ_CHAR);
            }

            // read translated script
            setTextDelayed(translationSpeakerTextView, translatedScript, DELAY_GAME_READ_CHAR);
        }, delay);
    }

    public void alternateLayout() {
        // case method is called from story timeline after skip
        if (isOpeningFinished)
            return;
        else
            isOpeningFinished = true;

        // set floor round
        ((ImageView) findViewById(R.id.floor_pajamas)).setImageResource(R.drawable.drawable_game_floor_round);

        // initialize origin constraint layout
        ConstraintLayout originLayout = findViewById(R.id.layout_pajamas);

        // initialize alternative constraint set
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(context, R.layout.activity_game_pajamas_alternative);

        // initialize duration
        long duration = calculateDurationGravity(context, originLayout.getHeight(), false);

        // initialize transition
        Transition transition = new ChangeBounds();
        transition.setInterpolator(new DecelerateInterpolator());
        transition.setDuration(duration);
        TransitionManager.beginDelayedTransition(originLayout, transition);

        // apply layout
        constraintSet.applyTo(originLayout);

        // animate game objects appear
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::animateGameObjectsAppear, duration);
    }

    private void animateGameObjectsAppear() {
        playerProgressBar.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_scale_appear));
        enemyProgressBar.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_scale_appear));
        playerTtakjiImageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_scale_appear));

        Animation appearAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_scale_appear);
        appearAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animateGameTextViewsAppear();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        enemyTtakjiImageView.startAnimation(appearAnimation);
    }

    private void animateGameTextViewsAppear() {
        // cancel animator
        cancelAnimator();

        // initialize distance, duration
        final float distance = playerHPTextView.getHeight();
        final long duration = 200;

        // initialize from
        final float playerHPFromY = playerHPTextView.getY();
        final float enemyHPFromY = enemyHPTextView.getY();

        // initialize value animator
        valueAnimatorY = ValueAnimator.ofFloat(0, distance);

        // set value animator attributes
        valueAnimatorY.setDuration(duration);
        valueAnimatorY.setInterpolator(new LinearInterpolator());
        valueAnimatorY.addUpdateListener(animation -> {
            float animatedValue = (Float) animation.getAnimatedValue();
            playerHPTextView.setY(playerHPFromY - animatedValue);
            enemyHPTextView.setY(enemyHPFromY + animatedValue);
        });
        valueAnimatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                playerHPTextView.setText(String.format(getString(R.string.pajamas_hp), playerHP));
                enemyHPTextView.setText(String.format(getString(R.string.pajamas_hp), enemyHP));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isOpeningSkipped) {
                    startGame();
                } else {
                    DefaultDialogFragment defaultDialogFragment = new DefaultDialogFragment(DIALOG_TYPE_START_PAJAMAS);
                    defaultDialogFragment.show(getSupportFragmentManager(), Integer.toString(DIALOG_TYPE_START_PAJAMAS));
                }
            }
        });

        // start animation
        valueAnimatorY.start();
    }

    // start game
    @SuppressLint("ClickableViewAccessibility")
    public void startGame() {
        // initialize button touch listener
        Button button = findViewById(R.id.button_pajamas);
        button.setOnTouchListener((v, event) -> {
            // case button is locked
            if (isPlayerButtonLocked)
                return false;

            // motion event
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // play ripple effect
                    v.setPressed(true);

                    // play click sound
                    v.playSoundEffect(android.view.SoundEffectConstants.CLICK);

                    // animate player progress bar
                    animatePlayerProgressBar();

                    return true;
                case MotionEvent.ACTION_UP:
                    // prevent first turn lock error (action down is not called)
                    if (valueAnimatorProgress == null)
                        return false;

                    // stop ripple effect
                    v.setPressed(false);

                    // lock button
                    isPlayerButtonLocked = true;

                    // cancel animator
                    cancelProgressAnimator();

                    // calculate damage
                    int progress = (10 * playerProgressBar.getProgress()) / playerProgressBar.getWidth();

                    // animate enemy ttakji shiver
                    animateEnemyTtakjiShiver(DIRECTION_TO_LEFT, progress);

                    return true;
            }
            return false;
        });
    }

    private void animatePlayerProgressBar() {
        // cancel animator
        cancelProgressAnimator();

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        final boolean isGameDifficultyHard = prefs.getBoolean(KEY_IS_GAME_DIFFICULTY_HARD, false);
        final int maxProgress = playerProgressBar.getWidth();
        final float distance = (float) maxProgress;
        final long duration = isGameDifficultyHard ? 300 : 375; // * 1.25

        // initialize value animator
        valueAnimatorProgress = ValueAnimator.ofFloat(0, distance);

        // set value animator attributes
        valueAnimatorProgress.setDuration(duration);
        valueAnimatorProgress.setInterpolator(new LinearInterpolator());
        valueAnimatorProgress.addUpdateListener(animation -> {
            if (isPlayerButtonLocked)
                return;

            playerProgressBar.setMax(maxProgress);
            float animatedValue = (Float) animation.getAnimatedValue(); // convert Float to float
            int progress = (int) animatedValue; // convert float to int
            playerProgressBar.setProgress(progress);
        });
        valueAnimatorProgress.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isPlayerButtonLocked)
                    return;

                // recursive call
                animatePlayerProgressBar();
            }
        });

        // start animation
        valueAnimatorProgress.start();
    }

    private void animateEnemyTtakjiShiver(final int direction, final int repeatCount) {
        // initialize shiver animation
        Animation shiverAnimation;
        if (direction == DIRECTION_TO_LEFT)
            shiverAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_shiver_to_left);
        else if (direction == DIRECTION_LEFT_TO_RIGHT)
            shiverAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_shiver_left_to_right);
        else if (direction == DIRECTION_RIGHT_TO_LEFT)
            shiverAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_shiver_right_to_left);
        else if (direction == DIRECTION_LEFT_TO_CENTER)
            shiverAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_shiver_left_to_center);
        else if (direction == DIRECTION_RIGHT_TO_CENTER)
            shiverAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_shiver_right_to_center);
        else
            return;

        // set animation listener
        shiverAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (enemyHP == 0) {
                    finishGame();
                    return;
                }

                if (repeatCount == -1) {
                    readEnemyScript();
                } else {
                    // damage to enemy (progress + 1)
                    enemyHP = enemyHP - 1;
                    enemyHPTextView.setText(String.format(getString(R.string.pajamas_hp), Math.max(enemyHP, 0)));

                    if (enemyHP == 0 || repeatCount == 0) {
                        if (direction == DIRECTION_LEFT_TO_RIGHT)
                            animateEnemyTtakjiShiver(DIRECTION_RIGHT_TO_CENTER, repeatCount - 1);
                        else
                            animateEnemyTtakjiShiver(DIRECTION_LEFT_TO_CENTER, repeatCount - 1);
                    } else {
                        if (direction == DIRECTION_LEFT_TO_RIGHT)
                            animateEnemyTtakjiShiver(DIRECTION_RIGHT_TO_LEFT, repeatCount - 1);
                        else
                            animateEnemyTtakjiShiver(DIRECTION_LEFT_TO_RIGHT, repeatCount - 1);
                    }
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // start animation
        enemyTtakjiImageView.startAnimation(shiverAnimation);
    }

    private void readEnemyScript() {
        // initialize hp gap
        int hpGap = playerHP - enemyHP;

        // initialize script index
        final int scriptIndex;
        if (hpGap <= -10)
            scriptIndex = 0;
        else if (hpGap <= -5)
            scriptIndex = 1;
        else if (hpGap <= 10)
            scriptIndex = 2;
        else if (hpGap <= 20)
            scriptIndex = 3;
        else
            scriptIndex = 4;

        // initialize enemy scenario
        final Scenario enemyScenario = new Scenario(context, R.array.scripts_pajamas_enemy, R.array.scripts_pajamas_enemy_translation);

        // initialize script, translated script
        final String script = enemyScenario.getScript(scriptIndex);
        final String translatedScript = enemyScenario.getTranslatedScript(scriptIndex);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            // read script
            for (int i = 1; i <= script.length(); i++)
                setTextDelayed(enemySpeakerTextView, script.subSequence(0, i), i * DELAY_GAME_READ_CHAR);

            // read translated script
            setTextDelayed(translationSpeakerTextView, translatedScript, DELAY_GAME_READ_CHAR);

            // change turn to enemy
            Handler handler1 = new Handler(Looper.getMainLooper());
            handler1.postDelayed(this::playEnemyTurn, enemyScenario.getDuration(scriptIndex));
        }, DELAY_GAME_ANIMATION_SHORT);
    }

    private void playEnemyTurn() {
        // set speaker visibility
        enemySpeakerTextView.setVisibility(View.INVISIBLE);
        translationSpeakerTextView.setVisibility(View.INVISIBLE);

        // initialize random values
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        final boolean isGameDifficultyHard = prefs.getBoolean(KEY_IS_GAME_DIFFICULTY_HARD, false);
        Random random = new Random();
        final int randomDamage = isGameDifficultyHard ? random.nextInt(5) + 5 : random.nextInt(10);
        final int randomIterator = random.nextInt(5);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            // unlock enemy button
            isEnemyButtonLocked = false;

            // animate enemy progress bar
            animateEnemyProgressBar(randomIterator, randomDamage);
        }, DELAY_GAME_ANIMATION_DEFAULT);
    }

    private void animateEnemyProgressBar(final int repeatCount, final int damage) {
        // cancel animator
        cancelProgressAnimator();

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        final boolean isGameDifficultyHard = prefs.getBoolean(KEY_IS_GAME_DIFFICULTY_HARD, false);
        final int maxProgress = enemyProgressBar.getWidth();
        final float distance = (float) maxProgress;
        final long duration = isGameDifficultyHard ? 300 : 375; // * 1.25

        // initialize value animator
        valueAnimatorProgress = ValueAnimator.ofFloat(0, distance);

        // set value animator attributes
        valueAnimatorProgress.setDuration(duration);
        valueAnimatorProgress.setInterpolator(new LinearInterpolator());
        valueAnimatorProgress.addUpdateListener(animation -> {
            if (isEnemyButtonLocked)
                return;

            enemyProgressBar.setMax(maxProgress);
            float animatedValue = (Float) animation.getAnimatedValue(); // convert Float to float
            int progress = (int) animatedValue; // convert float to int
            enemyProgressBar.setProgress(progress);

            int progressDamage = (10 * enemyProgressBar.getProgress()) / enemyProgressBar.getWidth();
            if (repeatCount == 0 && damage == progressDamage) {
                // lock enemy button
                isEnemyButtonLocked = true;

                // cancel animator
                cancelProgressAnimator();

                // animate player ttakji shiver
                animatePlayerTtakjiShiver(DIRECTION_TO_RIGHT, damage);
            }
        });
        valueAnimatorProgress.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isEnemyButtonLocked)
                    return;

                // recursive call
                animateEnemyProgressBar(repeatCount - 1, damage);
            }
        });

        // start animation
        valueAnimatorProgress.start();
    }

    private void animatePlayerTtakjiShiver(final int direction, final int repeatCount) {
        // initialize shiver animation
        Animation shiverAnimation;
        if (direction == DIRECTION_TO_RIGHT)
            shiverAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_shiver_to_right);
        else if (direction == DIRECTION_RIGHT_TO_LEFT)
            shiverAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_shiver_right_to_left);
        else if (direction == DIRECTION_LEFT_TO_RIGHT)
            shiverAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_shiver_left_to_right);
        else if (direction == DIRECTION_RIGHT_TO_CENTER)
            shiverAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_shiver_right_to_center);
        else if (direction == DIRECTION_LEFT_TO_CENTER)
            shiverAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_shiver_left_to_center);
        else
            return;

        // set animation listener
        shiverAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (playerHP == 0) {
                    finishGame();
                    return;
                }

                if (repeatCount == -1) {
                    // unlock player button
                    isPlayerButtonLocked = false;
                } else {
                    // damage to player
                    playerHP = playerHP - 1;
                    playerHPTextView.setText(String.format(getString(R.string.pajamas_hp), Math.max(playerHP, 0)));

                    if (playerHP == 0 || repeatCount == 0) {
                        if (direction == DIRECTION_RIGHT_TO_LEFT)
                            animatePlayerTtakjiShiver(DIRECTION_LEFT_TO_CENTER, repeatCount - 1);
                        else
                            animatePlayerTtakjiShiver(DIRECTION_RIGHT_TO_CENTER, repeatCount - 1);
                    } else {
                        if (direction == DIRECTION_RIGHT_TO_LEFT)
                            animatePlayerTtakjiShiver(DIRECTION_LEFT_TO_RIGHT, repeatCount - 1);
                        else
                            animatePlayerTtakjiShiver(DIRECTION_RIGHT_TO_LEFT, repeatCount - 1);
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // start animation
        playerTtakjiImageView.startAnimation(shiverAnimation);
    }

    private void finishGame() {
        // initialize total duration
        long totalDuration = DELAY_GAME_ANIMATION_DEFAULT;
        final Scenario enemyScenario;

        // initialize enemy scenario
        if (enemyHP == 0) {
            enemyScenario = new Scenario(context, R.array.scripts_pajamas_player_win, R.array.scripts_pajamas_player_win_translation);
        } else if (playerHP == 0) {
            enemyScenario = new Scenario(context, R.array.scripts_pajamas_enemy_win, R.array.scripts_pajamas_enemy_win_translation);
        } else {
            return;
        }

        // read scripts
        for (int i = 0; i < enemyScenario.getSize(); i++) {
            final CharSequence script = enemyScenario.getScript(i);
            final CharSequence translatedScript = enemyScenario.getTranslatedScript(i);
            final long duration = enemyScenario.getDuration(i);
            final long slowDuration = enemyScenario.getSlowDuration(i);
            final boolean isSlow = (enemyHP == 0 && i == 0);
            final long delay = isSlow ? DELAY_GAME_READ_CHAR_SLOW : DELAY_GAME_READ_CHAR;

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                // read script
                for (int j = 1; j <= script.length(); j++)
                    setTextDelayed(enemySpeakerTextView, script.subSequence(0, j), j * delay);

                // read translated script
                setTextDelayed(translationSpeakerTextView, translatedScript, delay);
            }, totalDuration);

            // calculate total duration
            totalDuration += (i == 0 ? slowDuration : duration);
        }

        // result
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            // case player win
            if (enemyHP == 0) {
                SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
                final boolean isGameDifficultyHard = prefs.getBoolean(KEY_IS_GAME_DIFFICULTY_HARD, false);
                int gameRewardItemCode = new PrefsController(context).addRandomItemPrefs(isGameDifficultyHard ? REWARD_TYPE_GAME_ITEM_HARD : REWARD_TYPE_GAME_ITEM_EASY);
                DefaultDialogFragment defaultDialogFragment = new DefaultDialogFragment(DIALOG_TYPE_WIN_PAJAMAS);
                defaultDialogFragment.show(getSupportFragmentManager(), Converter.getItemName(context, gameRewardItemCode));
            }
            // case enemy win
            else {
                setResult(-1);
                finish();
            }
        }, totalDuration + DELAY_GAME_ANIMATION_SHORT);
    }

    // others
    private void cancelProgressAnimator() {
        if (valueAnimatorProgress != null) {
            valueAnimatorProgress.removeAllUpdateListeners();
            valueAnimatorProgress.removeAllListeners();
            valueAnimatorProgress.cancel();
        }
    }

    private void setTextDelayed(final TextView speakerTextView, final CharSequence message, long delay) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (speakerTextView != null) {
                speakerTextView.setVisibility(View.VISIBLE);
                speakerTextView.setText(message);
            }
        }, delay);
    }

    public void cancelAnimator() {
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
            if (isOpeningFinished)
                return true;

            DefaultDialogFragment defaultDialogFragment = new DefaultDialogFragment(DIALOG_TYPE_SKIP_OPENING);
            defaultDialogFragment.show(getSupportFragmentManager(), Converter.getCostumeName(context, COSTUME_CODE_PAJAMAS));
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
        cancelProgressAnimator();

        super.onDestroy();
    }
}