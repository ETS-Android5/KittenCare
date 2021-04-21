package com.pleiades.pleione.kittencare.ui.fragment.tutorial.happiness;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.controller.AnimationController;

import static android.content.Context.MODE_PRIVATE;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_JUMP_ALTITUDE;
import static com.pleiades.pleione.kittencare.Config.DELAY_ANIMATION;
import static com.pleiades.pleione.kittencare.Config.KEY_JUMP_ALTITUDE;
import static com.pleiades.pleione.kittencare.Config.PREFS;
import static com.pleiades.pleione.kittencare.Config.SNIFF_ANGLE_LEFT;
import static com.pleiades.pleione.kittencare.Config.SNIFF_ANGLE_RIGHT;

public class DailyTutorialFragment extends Fragment {
    private Context context;

    private View leftKittenView, rightKittenView;
    private ImageView leftFaceImageView, rightFaceImageView;
    private ValueAnimator valueAnimator;
    private ObjectAnimator leftObjectAnimator, rightObjectAnimator;
    private AnimatorSet animatorSet;
    private float originPositionY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tutorial_daily, container, false);

        // initialize left kitten view
        leftKittenView = rootView.findViewById(R.id.kitten_left_tutorial_daily);
        leftKittenView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        originPositionY = leftKittenView.getY();

                        leftKittenView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
        leftFaceImageView = leftKittenView.findViewById(R.id.face_kitten);
        ((ImageView) leftKittenView.findViewById(R.id.body_kitten)).setImageResource(R.drawable.image_body_crop);
        ((ImageView) leftKittenView.findViewById(R.id.costume_kitten)).setImageResource(R.drawable.image_costume_alcyone);

        // initialize right kitten view
        rightKittenView = rootView.findViewById(R.id.kitten_right_tutorial_daily);
        rightFaceImageView = rightKittenView.findViewById(R.id.face_kitten);
        ((ImageView) rightKittenView.findViewById(R.id.body_kitten)).setImageResource(R.drawable.image_body_crop);
        ((ImageView) rightKittenView.findViewById(R.id.costume_kitten)).setImageResource(R.drawable.image_costume_pleione);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // animate both kittens
        animateKittensSniff();
    }

    @Override
    public void onPause() {
        super.onPause();

        cancelAnimator();

        // reset both kittens
        leftFaceImageView.setImageResource(R.drawable.image_face_default);
        leftKittenView.setRotation(0);
        leftKittenView.setY(originPositionY);
        rightFaceImageView.setImageResource(R.drawable.image_face_default);
        rightKittenView.setRotation(0);
        rightKittenView.setY(originPositionY);
    }

    private void animateKittensSniff() {
        // cancel animator
        cancelAnimator();

        // initialize duration
        long duration = calculateDurationSniff();

        // initialize object animator
        leftObjectAnimator = ObjectAnimator.ofFloat(leftKittenView, "rotation", 0, SNIFF_ANGLE_RIGHT);
        rightObjectAnimator = ObjectAnimator.ofFloat(rightKittenView, "rotation", 0, SNIFF_ANGLE_LEFT);

        // initialize animator set
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(leftObjectAnimator, rightObjectAnimator);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new AccelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animateKittensSniffBack();
                    }
                }, DELAY_ANIMATION);
            }
        });

        // start animation
        animatorSet.start();
    }

    private void animateKittensSniffBack() {
        // cancel animator
        cancelAnimator();

        // initialize duration
        long duration = calculateDurationSniff();

        leftObjectAnimator = ObjectAnimator.ofFloat(leftKittenView, "rotation", SNIFF_ANGLE_RIGHT, 0);
        rightObjectAnimator = ObjectAnimator.ofFloat(rightKittenView, "rotation", SNIFF_ANGLE_LEFT, 0);

        // initialize animator set
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(leftObjectAnimator, rightObjectAnimator);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        leftFaceImageView.setImageResource(R.drawable.image_face_sparkle);
                        rightFaceImageView.setImageResource(R.drawable.image_face_sparkle);

                        animateKittensJumpUp();
                    }
                }, DELAY_ANIMATION);
            }
        });

        // start animation
        animatorSet.start();

    }

    private void animateKittensJumpUp() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);

        // cancel animator
        cancelAnimator();

        // initialize distance, duration
        float distance = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        long duration = AnimationController.calculateDurationGravity(context, distance, false);

        // initialize from, to
        float from = originPositionY;
        float to = from - distance;

        // initialize value animator
        valueAnimator = ValueAnimator.ofFloat(from, to);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                leftKittenView.setY((Float) animation.getAnimatedValue());
                rightKittenView.setY((Float) animation.getAnimatedValue());
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateKittenJumpDown();
            }
        });

        // start animation
        valueAnimator.start();
    }

    private void animateKittenJumpDown() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);

        // cancel animator
        cancelAnimator();

        // initialize distance, duration
        float distance = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        long duration = AnimationController.calculateDurationGravity(context, distance, false);

        // initialize from, to
        float from = leftKittenView.getY();
        float to = originPositionY;

        // initialize value animator
        valueAnimator = ValueAnimator.ofFloat(from, to);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                leftKittenView.setY((Float) animation.getAnimatedValue());
                rightKittenView.setY((Float) animation.getAnimatedValue());
            }
        });

        // start animation
        valueAnimator.start();
    }

    private void cancelAnimator() {
        if (valueAnimator != null) {
            valueAnimator.removeAllUpdateListeners();
            valueAnimator.removeAllListeners();
            valueAnimator.cancel();
        }
        if (leftObjectAnimator != null) {
            leftObjectAnimator.removeAllUpdateListeners();
            leftObjectAnimator.removeAllListeners();
            leftObjectAnimator.cancel();
        }
        if (rightObjectAnimator != null) {
            rightObjectAnimator.removeAllUpdateListeners();
            rightObjectAnimator.removeAllListeners();
            rightObjectAnimator.cancel();
        }
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            animatorSet.cancel();
        }
    }

    private long calculateDurationSniff() {
        return AnimationController.calculateDurationGravity(context, leftKittenView.getHeight() * 4, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
