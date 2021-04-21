package com.pleiades.pleione.kittencare.ui.fragment.tutorial.happiness;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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

import java.util.Random;

import static android.content.Context.MODE_PRIVATE;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_JUMP_ALTITUDE;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_JUMP_DISTANCE;
import static com.pleiades.pleione.kittencare.Config.DELAY_ANIMATION;
import static com.pleiades.pleione.kittencare.Config.DIRECTION_TO_LEFT;
import static com.pleiades.pleione.kittencare.Config.DIRECTION_TO_RIGHT;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_1;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_2;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_3;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SMILE;
import static com.pleiades.pleione.kittencare.Config.KEY_JUMP_ALTITUDE;
import static com.pleiades.pleione.kittencare.Config.KEY_JUMP_DISTANCE;
import static com.pleiades.pleione.kittencare.Config.PREFS;
import static com.pleiades.pleione.kittencare.Config.SNIFF_ANGLE_LEFT_SHALLOW;
import static com.pleiades.pleione.kittencare.Config.SNIFF_ANGLE_RIGHT_SHALLOW;
import static com.pleiades.pleione.kittencare.Converter.getFaceResourceId;

public class FaceTutorialFragment extends Fragment {
    private Context context;

    private View leftKittenView, rightKittenView;
    private ImageView leftFaceImageView, rightFaceImageView;
    private ValueAnimator leftValueAnimator;
    private ObjectAnimator rightObjectAnimator;
    private float leftOriginPositionX, leftOriginPositionY;

    private float shiverDistance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tutorial_face, container, false);

        // initialize left kitten view
        leftKittenView = rootView.findViewById(R.id.kitten_left_tutorial_face);
        leftKittenView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        leftOriginPositionX = leftKittenView.getX();
                        leftOriginPositionY = leftKittenView.getY();
                    }
                });
        leftFaceImageView = leftKittenView.findViewById(R.id.face_kitten);
        ((ImageView) leftKittenView.findViewById(R.id.body_kitten)).setImageResource(R.drawable.image_body_crop);
        ((ImageView) leftKittenView.findViewById(R.id.costume_kitten)).setImageResource(R.drawable.image_costume_alcyone);

        // initialize right kitten view
        rightKittenView = rootView.findViewById(R.id.kitten_right_tutorial_face);
        rightFaceImageView = rightKittenView.findViewById(R.id.face_kitten);
        ((ImageView) rightKittenView.findViewById(R.id.body_kitten)).setImageResource(R.drawable.image_body_crop);
        ((ImageView) rightKittenView.findViewById(R.id.costume_kitten)).setImageResource(R.drawable.image_costume_pleione);

        // initialize shiver distance
        shiverDistance = (float) context.getSharedPreferences(PREFS, MODE_PRIVATE).getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE) / 5;

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // animate left kitten
        animateKittenShiverAngry(DIRECTION_TO_LEFT, 6);

        // animate right kitten
        animateKittenSniffSmile(DIRECTION_TO_RIGHT, true);
    }

    @Override
    public void onPause() {
        super.onPause();

        cancelAnimator(DIRECTION_TO_LEFT);
        cancelAnimator(DIRECTION_TO_RIGHT);

        // reset left
        leftFaceImageView.setImageResource(R.drawable.image_face_default);
        leftKittenView.setX(leftOriginPositionX);
        leftKittenView.setY(leftOriginPositionY);

        // reset right
        rightFaceImageView.setImageResource(R.drawable.image_face_default);
        rightKittenView.setRotation(0);
    }

    // left
    private void animateKittenShiverAngry(final int direction, final int repeatCount) {
        // cancel animator
        cancelAnimator(DIRECTION_TO_LEFT);

        // change kitten face
        leftFaceImageView.setImageResource(R.drawable.image_face_angry);

        // initialize from, to
        float from = leftKittenView.getX();
        float to = direction == DIRECTION_TO_LEFT ? from - shiverDistance : from + shiverDistance;

        // initialize duration
        long duration = AnimationController.calculateDurationGravity(context, shiverDistance, true);

        // initialize value animator
        leftValueAnimator = ValueAnimator.ofFloat(from, to);
        leftValueAnimator.setDuration(duration);
        leftValueAnimator.setInterpolator(new DecelerateInterpolator());
        leftValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // set kitten x
                leftKittenView.setX((Float) animation.getAnimatedValue());
            }
        });
        leftValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (repeatCount == 1)
                    animateKittenJumpUp();
                else
                    animateKittenShiverAngry(direction == DIRECTION_TO_LEFT ? DIRECTION_TO_RIGHT : DIRECTION_TO_LEFT, repeatCount - 1);
            }
        });

        // start animation
        leftValueAnimator.start();
    }

    private void animateKittenJumpUp() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);

        // cancel animator
        cancelAnimator(DIRECTION_TO_LEFT);

        // initialize distance, duration
        float distance = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        long duration = AnimationController.calculateDurationGravity(context, distance, false);

        // initialize from, to
        float from = leftKittenView.getY();
        float to = from - distance;

        // initialize value animator
        leftValueAnimator = ValueAnimator.ofFloat(from, to);
        leftValueAnimator.setDuration(duration);
        leftValueAnimator.setInterpolator(new DecelerateInterpolator());
        leftValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // set kitten y
                leftKittenView.setY((Float) animation.getAnimatedValue());
            }
        });
        leftValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateKittenJumpDown();
            }
        });

        // start animation
        leftValueAnimator.start();
    }

    private void animateKittenJumpDown() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);

        // cancel animator
        cancelAnimator(DIRECTION_TO_LEFT);

        // initialize distance, duration
        float distance = prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);
        long duration = AnimationController.calculateDurationGravity(context, distance, false);

        // initialize from, to
        float from = leftKittenView.getY();
        float to = from + distance;

        // initialize value animator
        leftValueAnimator = ValueAnimator.ofFloat(from, to);
        leftValueAnimator.setDuration(duration);
        leftValueAnimator.setInterpolator(new AccelerateInterpolator());
        leftValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // set kitten y
                leftKittenView.setY((Float) animation.getAnimatedValue());
            }
        });
//        leftValueAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                Handler handler = new Handler(Looper.getMainLooper());
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (leftFaceImageView != null)
//                            leftFaceImageView.setImageResource(Converter.getFaceResourceId(FACE_CODE_DEFAULT));
//                    }
//                }, DELAY_FACE_MAINTAIN);
//            }
//        });

        // start animation
        leftValueAnimator.start();
    }

    // right
    private void animateKittenSniffSmile(final int direction, boolean first) {
        // cancel animator
        cancelAnimator(DIRECTION_TO_RIGHT);

        // initialize from, to
        float from = first ? 0 : (direction == DIRECTION_TO_LEFT ? SNIFF_ANGLE_RIGHT_SHALLOW : SNIFF_ANGLE_LEFT_SHALLOW);
        float to = direction == DIRECTION_TO_LEFT ? SNIFF_ANGLE_LEFT_SHALLOW : SNIFF_ANGLE_RIGHT_SHALLOW;

        // initialize duration
        long duration = first ? calculateDurationSniff() : 2 * calculateDurationSniff();

        // initialize value animator
        rightObjectAnimator = ObjectAnimator.ofFloat(rightKittenView, "rotation", from, to);
        rightObjectAnimator.setDuration(duration);
        rightObjectAnimator.setInterpolator(new AccelerateInterpolator());
        rightObjectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                int nextDirection = direction == DIRECTION_TO_LEFT ? DIRECTION_TO_RIGHT : DIRECTION_TO_LEFT;
                if (new Random().nextInt(3) == 0)
                    animateKittenSniffBackSmile(nextDirection);
                else
                    animateKittenSniffSmile(nextDirection, false);
            }
        });

        // start animation
        rightObjectAnimator.start();
    }

    private void animateKittenSniffBackSmile(final int direction) {
        // cancel animator
        cancelAnimator(DIRECTION_TO_RIGHT);

        // initialize from, to
        float from = direction == DIRECTION_TO_LEFT ? SNIFF_ANGLE_RIGHT_SHALLOW : SNIFF_ANGLE_LEFT_SHALLOW;
        float to = 0;

        // initialize duration
        long duration = calculateDurationSniff();

        // initialize object animator
        rightObjectAnimator = ObjectAnimator.ofFloat(rightKittenView, "rotation", from, to);
        rightObjectAnimator.setDuration(duration);
        rightObjectAnimator.setInterpolator(new DecelerateInterpolator());
        rightObjectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                changeKittenFace(FACE_CODE_BLINK_1, DELAY_ANIMATION);
                changeKittenFace(FACE_CODE_BLINK_2, DELAY_ANIMATION + 40);
                changeKittenFace(FACE_CODE_BLINK_3, DELAY_ANIMATION + 80);
                changeKittenFace(FACE_CODE_SMILE, DELAY_ANIMATION + 120);
//                changeKittenFace(FACE_CODE_BLINK_3, DELAY_ANIMATION + 120 + DELAY_FACE_MAINTAIN);
//                changeKittenFace(FACE_CODE_BLINK_2, DELAY_ANIMATION + 160 + DELAY_FACE_MAINTAIN);
//                changeKittenFace(FACE_CODE_BLINK_1, DELAY_ANIMATION + 200 + DELAY_FACE_MAINTAIN);
            }
        });

        // start animation
        rightObjectAnimator.start();
    }

    private void cancelAnimator(int direction) {
        if (direction == DIRECTION_TO_LEFT && leftValueAnimator != null) {
            leftValueAnimator.removeAllUpdateListeners();
            leftValueAnimator.removeAllListeners();
            leftValueAnimator.cancel();
        }
        if (direction == DIRECTION_TO_RIGHT && rightObjectAnimator != null) {
            rightObjectAnimator.removeAllUpdateListeners();
            rightObjectAnimator.removeAllListeners();
            rightObjectAnimator.cancel();
        }
    }

    public void changeKittenFace(final int faceCode, long delay) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (rightFaceImageView != null)
                    rightFaceImageView.setImageResource(getFaceResourceId(faceCode));
            }
        }, delay);
    }

    private long calculateDurationSniff() {
        return AnimationController.calculateDurationGravity(context, rightKittenView.getHeight() * 4, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
