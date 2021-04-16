package com.pleiades.pleione.kittencare.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.pleiades.pleione.kittencare.KittenService;
import com.pleiades.pleione.kittencare.R;

import java.util.Random;

import static android.content.Context.MODE_PRIVATE;
import static com.pleiades.pleione.kittencare.Config.BUFF_CODE_FAIL;
import static com.pleiades.pleione.kittencare.Config.BUFF_CODE_ITEM;
import static com.pleiades.pleione.kittencare.Config.COORDINATE_X;
import static com.pleiades.pleione.kittencare.Config.COORDINATE_Y;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_DEFAULT;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_JUMP_ALTITUDE;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_JUMP_DISTANCE;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_SHOW_POSITION;
import static com.pleiades.pleione.kittencare.Config.DELAY_ANIMATION;
import static com.pleiades.pleione.kittencare.Config.DELAY_FACE_MAINTAIN;
import static com.pleiades.pleione.kittencare.Config.DIRECTION_TO_LEFT;
import static com.pleiades.pleione.kittencare.Config.DIRECTION_TO_RIGHT;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_ANGRY;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_1;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_2;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_3;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_DEFAULT;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_MEOW_1;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_MEOW_2;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SLEEP;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SMILE;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SPARKLE;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SURPRISED;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SWEAT_1;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SWEAT_2;
import static com.pleiades.pleione.kittencare.Config.KEY_ANIMATOR_DURATION_SCALE;
import static com.pleiades.pleione.kittencare.Config.KEY_BUFF;
import static com.pleiades.pleione.kittencare.Config.KEY_HAPPINESS;
import static com.pleiades.pleione.kittencare.Config.KEY_JUMP_ALTITUDE;
import static com.pleiades.pleione.kittencare.Config.KEY_JUMP_DISTANCE;
import static com.pleiades.pleione.kittencare.Config.KEY_SHOW_POSITION;
import static com.pleiades.pleione.kittencare.Config.KEY_ITEM_TOWER;
import static com.pleiades.pleione.kittencare.Config.PREFS;
import static com.pleiades.pleione.kittencare.Config.REWARD_TYPE_EXPLORE;
import static com.pleiades.pleione.kittencare.Config.SNIFF_ANGLE_LEFT;
import static com.pleiades.pleione.kittencare.Config.SNIFF_ANGLE_RIGHT;
import static com.pleiades.pleione.kittencare.Config.SNIFF_ANGLE_LEFT_SHALLOW;
import static com.pleiades.pleione.kittencare.Config.SNIFF_ANGLE_RIGHT_SHALLOW;
import static com.pleiades.pleione.kittencare.Converter.getCostumeResourceId;
import static com.pleiades.pleione.kittencare.Converter.getFaceResourceId;

public class AnimationController {
    private final Context context;

    private ValueAnimator valueAnimatorX, valueAnimatorY;
    private ObjectAnimator objectAnimator;
    private AnimatorSet animatorSet;

    public boolean isActionLocked, isAttractedLeft, isAttractedRight;
    public double attractedHeightRatio;

    public AnimationController(Context context) {
        this.context = context;
    }

    // public
    public void animateKittenShow() {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // initialize kitten position
        KittenService.kittenLayoutParams.y = context.getSharedPreferences(PREFS, MODE_PRIVATE).getInt(KEY_SHOW_POSITION, DEFAULT_SHOW_POSITION);
        KittenService.kittenWindowManager.updateViewLayout(KittenService.kittenLayout, KittenService.kittenLayoutParams);

        // animate kitten fall
        animateKittenFall();
    }

    public void animateKittenFall() {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        // initialize from, to
        float from = KittenService.kittenLayoutParams.y;
        float to = 0;

        // initialize duration
        float distance = Math.abs(to - from);
        long duration = calculateDurationGravity(distance, true);

        // initialize value animator
        valueAnimatorY = ValueAnimator.ofFloat(from, to);
        valueAnimatorY.setDuration(duration);
        valueAnimatorY.setInterpolator(new BounceInterpolator());
        valueAnimatorY.addUpdateListener(new KittenPositionAnimatorUpdateListener(COORDINATE_Y));
        valueAnimatorY.addListener(new ActionUnlockAnimatorEndListenerAdapter());

        // start kitten animation
        valueAnimatorY.start();
    }

    public void animateKittenRunAway() {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        // initialize from, to
        float from = KittenService.kittenLayoutParams.y;
        float to = 0;

        // initialize duration
        float distance = Math.abs(to - from);
        long duration = calculateDurationGravity(distance, false);

        // initialize value animator
        valueAnimatorY = ValueAnimator.ofFloat(from, to);
        valueAnimatorY.setDuration(duration);
        valueAnimatorY.setInterpolator(new AccelerateInterpolator());
        valueAnimatorY.addUpdateListener(new KittenPositionAnimatorUpdateListener(COORDINATE_Y));
        valueAnimatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // change kitten face
                changeKittenFace(FACE_CODE_SURPRISED, 0);

                // animate kitten jump up
                animateKittenLongJumpUp(decideDirection());
            }
        });

        // start kitten animation
        valueAnimatorY.start();
    }

    public void animateKittenAttractionLeft() {

        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        // initialize device controller
        final DeviceController deviceController = new DeviceController(context);

        // initialize from, to
        float from = KittenService.kittenLayoutParams.x;
        float to = deviceController.getWidthMax() - KittenService.kittenLayout.getWidth();

        // initialize duration
        float distance = Math.abs(to - from);
        long duration = calculateDurationGravity(distance, true);

        // initialize value animator
        valueAnimatorX = ValueAnimator.ofFloat(from, to);
        valueAnimatorX.setDuration(duration);
        valueAnimatorX.setInterpolator(new AccelerateInterpolator());
        valueAnimatorX.addUpdateListener(new KittenPositionAnimatorUpdateListener(COORDINATE_X));
        valueAnimatorX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // change kitten face
                changeKittenFace(FACE_CODE_SLEEP, 0);

                isAttractedLeft = true;
                attractedHeightRatio = (double) KittenService.kittenLayoutParams.y / (double) deviceController.getHeightMax();
            }
        });

        // start kitten animation
        valueAnimatorX.start();
    }

    public void animateKittenAttractionRight() {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        // initialize device controller
        final DeviceController deviceController = new DeviceController(context);

        // initialize from, to
        float from = KittenService.kittenLayoutParams.x;
        float to = 0;

        // initialize duration
        float distance = Math.abs(to - from);
        long duration = calculateDurationGravity(distance, true);

        // initialize value animator
        valueAnimatorX = ValueAnimator.ofFloat(from, to);
        valueAnimatorX.setDuration(duration);
        valueAnimatorX.setInterpolator(new AccelerateInterpolator());
        valueAnimatorX.addUpdateListener(new KittenPositionAnimatorUpdateListener(COORDINATE_X));
        valueAnimatorX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // change kitten face
                changeKittenFace(FACE_CODE_SLEEP, 0);

                isAttractedRight = true;
                attractedHeightRatio = (double) KittenService.kittenLayoutParams.y / (double) deviceController.getHeightMax();
            }
        });

        // start kitten animation
        valueAnimatorX.start();
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
        if (objectAnimator != null) {
            objectAnimator.removeAllUpdateListeners();
            objectAnimator.removeAllListeners();
            objectAnimator.cancel();
        }
    }

    public void changeKittenFace(final int faceCode, long delay) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (KittenService.kittenView == null)
                    return;

                // initialize image view
                ImageView faceImageView = KittenService.kittenView.findViewById(R.id.face_kitten);
                faceImageView.setImageResource(getFaceResourceId(faceCode));
            }
        }, delay);
    }

    public static void changeKittenCostume(final int costumeCode) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // case click unlocked costume when kitten is not shown
                if (KittenService.kittenView == null)
                    return;

                // initialize image views
                ImageView bodyImageView = KittenService.kittenView.findViewById(R.id.body_kitten);
                ImageView costumeImageView = KittenService.kittenView.findViewById(R.id.costume_kitten);

                if (costumeCode == COSTUME_CODE_DEFAULT) {
                    bodyImageView.setImageResource(R.drawable.image_body);
                    costumeImageView.setVisibility(View.INVISIBLE);
                } else {
                    bodyImageView.setImageResource(R.drawable.image_body_crop);
                    costumeImageView.setImageResource(getCostumeResourceId(costumeCode));
                    costumeImageView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public static long calculateDurationGravity(Context context, float distance, boolean bounce) {
        long duration;

        duration = Math.round((int) (100 * Math.sqrt(distance / 49)));
        duration = bounce ? (2 * duration) : duration;
        duration = Math.round(context.getSharedPreferences(PREFS, MODE_PRIVATE).getFloat(KEY_ANIMATOR_DURATION_SCALE, 1) * duration);

        return duration;
    }

    // private
    private void animateKittenLongJumpUp(final int direction) {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        float fromX = KittenService.kittenLayoutParams.x;
        float fromY = KittenService.kittenLayoutParams.y;
        float toX = (direction == DIRECTION_TO_LEFT) ? (fromX + prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE)) : (fromX - prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE));
        float toY = fromY + prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);

        // initialize duration
        float distanceY = Math.abs(toY - fromY);
        long duration = calculateDurationGravity(distanceY, false);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(fromX, toX);
        valueAnimatorY = ValueAnimator.ofFloat(fromY, toY);
        valueAnimatorX.addUpdateListener(new KittenPositionAnimatorUpdateListener(COORDINATE_X));
        valueAnimatorY.addUpdateListener(new KittenPositionAnimatorUpdateListener(COORDINATE_Y));

        // initialize animator set
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateKittenLongJumpDown(direction);
            }
        });

        // start kitten animation
        animatorSet.start();
    }

    private void animateKittenLongJumpDown(final int direction) {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        float fromX = KittenService.kittenLayoutParams.x;
        float fromY = KittenService.kittenLayoutParams.y;
        float toX = (direction == DIRECTION_TO_LEFT) ? (fromX + prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE)) : (fromX - prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE));
        float toY = 0;

        // initialize duration
        float distanceY = Math.abs(toY - fromY);
        long duration = calculateDurationGravity(distanceY, false);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(fromX, toX);
        valueAnimatorY = ValueAnimator.ofFloat(fromY, toY);
        valueAnimatorX.addUpdateListener(new KittenPositionAnimatorUpdateListener(COORDINATE_X));
        valueAnimatorY.addUpdateListener(new KittenPositionAnimatorUpdateListener(COORDINATE_Y));

        // initialize animator set
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new AccelerateInterpolator());
        if (isWallDetected(direction, toX)) {
            animatorSet.addListener(new ActionUnlockAnimatorEndListenerAdapter());
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    changeKittenFace(FACE_CODE_DEFAULT, 0);
                }
            });
        } else {
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    animateKittenLongJumpUp(direction);
                }
            });
        }

        // start kitten animation
        animatorSet.start();
    }

    private void animateKittenJumpUp() {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        float from = KittenService.kittenLayoutParams.y;
        float to = from + prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);

        // initialize duration
        float distance = Math.abs(to - from);
        long duration = calculateDurationGravity(distance, false);

        // initialize value animator
        valueAnimatorY = ValueAnimator.ofFloat(from, to);
        valueAnimatorY.setDuration(duration);
        valueAnimatorY.setInterpolator(new DecelerateInterpolator());
        valueAnimatorY.addUpdateListener(new KittenPositionAnimatorUpdateListener(COORDINATE_Y));
        valueAnimatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateKittenJumpDown();
            }
        });

        // start kitten animation
        valueAnimatorY.start();
    }

    private void animateKittenJumpDown() {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        // initialize from, to
        float from = KittenService.kittenLayoutParams.y;
        float to = 0;

        // initialize duration
        float distance = Math.abs(to - from);
        long duration = calculateDurationGravity(distance, false);

        // initialize value animator
        valueAnimatorY = ValueAnimator.ofFloat(from, to);
        valueAnimatorY.setDuration(duration);
        valueAnimatorY.setInterpolator(new AccelerateInterpolator());
        valueAnimatorY.addUpdateListener(new KittenPositionAnimatorUpdateListener(COORDINATE_Y));
        valueAnimatorY.addListener(new ActionUnlockAnimatorEndListenerAdapter());
        valueAnimatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                changeKittenFace(FACE_CODE_DEFAULT, DELAY_FACE_MAINTAIN);
            }
        });

        // start kitten animation
        valueAnimatorY.start();
    }

    // explore
    public void explore() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                // prevent action overlap
                if (isActionLocked)
                    return;
                else
                    isActionLocked = true;

                // change kitten face
                // changeKittenFace(FACE_DEFAULT, 0);

                // initialize random bound
                int bound = 200;
                if (prefs.getBoolean(KEY_ITEM_TOWER, false))
                    bound = (int) (bound / 1.1);
                if (prefs.getInt(KEY_BUFF, BUFF_CODE_FAIL) == BUFF_CODE_ITEM)
                    bound = (int) (bound / 1.1);

                // initialize happiness
                int happiness = prefs.getInt(KEY_HAPPINESS, 100);

                // initialize random value
                Random random = new Random();
                int randomValue = random.nextInt(bound);

                // case item found
                if (randomValue == 0) {
                    // apply happiness
                    editor.putInt(KEY_HAPPINESS, Math.min(100, happiness + 1));
                    editor.apply();

                    new PrefsController(context).addRandomItemPrefs(REWARD_TYPE_EXPLORE);
                    animateKittenSniffItem(decideDirection());
                }
                // case behavior
                else {
                    // initialize random value
                    randomValue = random.nextInt(15);

                    if (randomValue == 0)
                        animateKittenSlip(decideDirection());
                    else if (randomValue == 1)
                        animateKittenLongJumpUpRandom();
                    else if (randomValue == 2)
                        animateKittenFaceMeow();
                    else if (randomValue == 3) {
                        // angry
                        if (happiness < 50) {
                            // prevent wall bug
                            float from = KittenService.kittenLayoutParams.x;
                            float shiverDistance = (float) prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE) / 5;
                            if (!isWallDetected(DIRECTION_TO_LEFT, from + shiverDistance) && !isWallDetected(DIRECTION_TO_RIGHT, from - shiverDistance))
                                animateKittenShiverAngry(decideDirection(), 6);
                            else
                                animateKittenFaceBlink();
                        }
                        // smile
                        else
                            animateKittenSniffSmile(decideDirection(), true);
                    } else
                        animateKittenFaceBlink();
                }
            }
        });
    }

    private int decideDirection() {
        // initialize device controller
        final DeviceController deviceController = new DeviceController(context);

        if (KittenService.kittenLayoutParams.x > (deviceController.getWidthMax() / 2))
            return DIRECTION_TO_RIGHT;
        else
            return DIRECTION_TO_LEFT;
    }

    private long calculateDurationGravity(float distance, boolean bounce) {
        long duration;

        duration = Math.round((int) (100 * Math.sqrt(distance / 49)));
        duration = bounce ? (2 * duration) : duration;
        duration = Math.round(context.getSharedPreferences(PREFS, MODE_PRIVATE).getFloat(KEY_ANIMATOR_DURATION_SCALE, 1) * duration);

        return duration;
    }

    private long calculateDurationSniff() {
        return calculateDurationGravity(KittenService.kittenLayout.getHeight() * 4, false);
    }

    private boolean isWallDetected(int direction, float toX) {
        // initialize device controller
        final DeviceController deviceController = new DeviceController(context);

        if (direction == DIRECTION_TO_LEFT)
            return (toX + (float) KittenService.kittenLayout.getWidth() / 2) >= (deviceController.getWidthMax() - KittenService.kittenLayout.getWidth());
        else
            return (toX - (float) KittenService.kittenLayout.getWidth() / 2) <= 0;
    }

    private void animateKittenSniffItem(final int direction) {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        // initialize from, to
        float from = 0;
        float to = (direction == DIRECTION_TO_LEFT) ? SNIFF_ANGLE_LEFT : SNIFF_ANGLE_RIGHT;

        // initialize duration
        long duration = calculateDurationSniff();

        // initialize object animator
        objectAnimator = ObjectAnimator.ofFloat(KittenService.kittenLayout, "rotation", from, to);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int nextDirection = (direction == DIRECTION_TO_LEFT) ? DIRECTION_TO_RIGHT : DIRECTION_TO_LEFT;

                        // animate kitten sniff back item
                        animateKittenSniffBackItem(nextDirection);
                    }
                }, DELAY_ANIMATION);
            }
        });

        // start kitten animation
        objectAnimator.start();
    }

    private void animateKittenSniffBackItem(final int direction) {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        // initialize from, to
        float from = (direction == DIRECTION_TO_LEFT) ? SNIFF_ANGLE_RIGHT : SNIFF_ANGLE_LEFT;
        float to = 0;

        // initialize duration
        long duration = calculateDurationSniff();

        // initialize object animator
        objectAnimator = ObjectAnimator.ofFloat(KittenService.kittenLayout, "rotation", from, to);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // change kitten face
                        changeKittenFace(FACE_CODE_SPARKLE, 0);

                        // animate kitten jump up
                        animateKittenJumpUp();
                    }
                }, DELAY_ANIMATION);
            }
        });

        // start kitten animation
        objectAnimator.start();
    }

    private void animateKittenSniffSmile(final int direction, boolean first) {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        // initialize from, to
        float from = (first) ? 0 : ((direction == DIRECTION_TO_LEFT) ? SNIFF_ANGLE_RIGHT_SHALLOW : SNIFF_ANGLE_LEFT_SHALLOW);
        float to = (direction == DIRECTION_TO_LEFT) ? SNIFF_ANGLE_LEFT_SHALLOW : SNIFF_ANGLE_RIGHT_SHALLOW;

        // initialize duration
        long duration = first ? calculateDurationSniff() : (2 * calculateDurationSniff());

        // initialize object animator
        objectAnimator = ObjectAnimator.ofFloat(KittenService.kittenLayout, "rotation", from, to);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                int nextDirection = (direction == DIRECTION_TO_LEFT) ? DIRECTION_TO_RIGHT : DIRECTION_TO_LEFT;

                if (isWallDetected(nextDirection, KittenService.kittenLayoutParams.x))
                    animateKittenSniffBackSmile(nextDirection);
                else if (new Random().nextInt(3) == 0)
                    animateKittenSniffBackSmile(nextDirection);
                else
                    animateKittenSniffSmile(nextDirection, false);
            }
        });

        // start kitten animation
        objectAnimator.start();
    }

    private void animateKittenSniffBackSmile(final int direction) {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        // initialize from, to
        float from = (direction == DIRECTION_TO_LEFT) ? SNIFF_ANGLE_RIGHT_SHALLOW : SNIFF_ANGLE_LEFT_SHALLOW;
        float to = 0;

        // initialize duration
        long duration = calculateDurationSniff();

        // initialize object animator
        objectAnimator = ObjectAnimator.ofFloat(KittenService.kittenLayout, "rotation", from, to);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // cancel animator
                        cancelAnimator();

                        // animate kitten face smile
                        changeKittenFace(FACE_CODE_BLINK_1, 0);
                        changeKittenFace(FACE_CODE_BLINK_2, 40);
                        changeKittenFace(FACE_CODE_BLINK_3, 80);
                        changeKittenFace(FACE_CODE_SMILE, 120);
                        changeKittenFace(FACE_CODE_BLINK_3, 120 + DELAY_FACE_MAINTAIN);
                        changeKittenFace(FACE_CODE_BLINK_2, 160 + DELAY_FACE_MAINTAIN);
                        changeKittenFace(FACE_CODE_BLINK_1, 200 + DELAY_FACE_MAINTAIN);
                        changeKittenFace(FACE_CODE_DEFAULT, 240 + DELAY_FACE_MAINTAIN);

                        // initialize value animator for unlock
                        valueAnimatorX = ValueAnimator.ofFloat(KittenService.kittenLayoutParams.x, KittenService.kittenLayoutParams.x);
                        valueAnimatorX.setDuration(240 + DELAY_FACE_MAINTAIN);
                        valueAnimatorX.addListener(new ActionUnlockAnimatorEndListenerAdapter());
                        valueAnimatorX.start();
                    }
                }, DELAY_ANIMATION);
            }
        });

        // start kitten animation
        objectAnimator.start();
    }

    private void animateKittenSlip(final int direction) {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        // initialize from, to
        float from = 0;
        float to = (direction == DIRECTION_TO_LEFT) ? SNIFF_ANGLE_LEFT : SNIFF_ANGLE_RIGHT;

        // initialize duration
        long duration = calculateDurationSniff();

        // initialize object animator
        objectAnimator = ObjectAnimator.ofFloat(KittenService.kittenLayout, "rotation", from, to);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // animate kitten slip jump up
                        animateKittenLongJumpUpSlip(direction);
                    }
                }, DELAY_ANIMATION);
            }
        });

        // start kitten animation
        objectAnimator.start();
    }

    private void animateKittenLongJumpUpSlip(final int direction) {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        // change kitten face
        changeKittenFace(FACE_CODE_SWEAT_1, 0);

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        float fromX = KittenService.kittenLayoutParams.x;
        float fromY = KittenService.kittenLayoutParams.y;
        float toX = (direction == DIRECTION_TO_LEFT) ? (fromX + prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE)) : (fromX - prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE));
        float toY = fromY + prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);

        // initialize duration
        float distanceY = Math.abs(toY - fromY);
        long duration = calculateDurationGravity(distanceY, false);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(fromX, toX);
        valueAnimatorY = ValueAnimator.ofFloat(fromY, toY);
        valueAnimatorX.addUpdateListener(new KittenPositionAnimatorUpdateListener(COORDINATE_X));
        valueAnimatorY.addUpdateListener(new KittenPositionAnimatorUpdateListener(COORDINATE_Y));

        // initialize animator set
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateKittenLongJumpDownSlip(direction);
            }
        });

        // start kitten animation
        animatorSet.start();
    }

    private void animateKittenLongJumpDownSlip(final int direction) {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final float fromX = KittenService.kittenLayoutParams.x;
        final float fromY = KittenService.kittenLayoutParams.y;
        final float toX = (direction == DIRECTION_TO_LEFT) ? (fromX + prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE)) : (fromX - prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE));
        final float toY = 0;

        // initialize duration
        float distanceY = Math.abs(toY - fromY);
        long duration = calculateDurationGravity(distanceY, false);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(fromX, toX);
        valueAnimatorY = ValueAnimator.ofFloat(fromY, toY);
        valueAnimatorX.addUpdateListener(new KittenPositionAnimatorUpdateListener(COORDINATE_X));
        valueAnimatorY.addUpdateListener(new KittenPositionAnimatorUpdateListener(COORDINATE_Y));

        // initialize animator set
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new AccelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (new Random().nextInt(2) == 0 || isWallDetected(direction, toX)) {
                    int nextDirection = (direction == DIRECTION_TO_LEFT) ? DIRECTION_TO_RIGHT : DIRECTION_TO_LEFT;

                    // animate kitten slip back
                    animateKittenSlipBack(nextDirection);
                } else {
                    // animate kitten slip long jump up
                    animateKittenLongJumpUpSlip(direction);
                }
            }
        });

        // start kitten animation
        animatorSet.start();
    }

    private void animateKittenSlipBack(int direction) {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        // initialize from, to
        float from = (direction == DIRECTION_TO_LEFT) ? SNIFF_ANGLE_RIGHT : SNIFF_ANGLE_LEFT;
        float to = 0;

        // initialize duration
        long duration = calculateDurationSniff() / 2;

        // initialize object animator
        objectAnimator = ObjectAnimator.ofFloat(KittenService.kittenLayout, "rotation", from, to);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // cancel animator
                        cancelAnimator();

                        // animate kitten face sweat back
                        changeKittenFace(FACE_CODE_SWEAT_2, 0);
                        changeKittenFace(FACE_CODE_MEOW_1, 40);
                        changeKittenFace(FACE_CODE_MEOW_2, 80);
                        changeKittenFace(FACE_CODE_DEFAULT, 120);

                        // initialize value animator for unlock
                        valueAnimatorX = ValueAnimator.ofFloat(KittenService.kittenLayoutParams.x, KittenService.kittenLayoutParams.x);
                        valueAnimatorX.setDuration(120);
                        valueAnimatorX.addListener(new ActionUnlockAnimatorEndListenerAdapter());
                        valueAnimatorX.start();
                    }
                }, DELAY_ANIMATION);
            }
        });

        // start kitten animation
        objectAnimator.start();
    }

    private void animateKittenShiverAngry(final int direction, final int repeatCount) {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        // change kitten face
        changeKittenFace(FACE_CODE_ANGRY, 0);

        // initialize distance
        float shiverDistance = (float) context.getSharedPreferences(PREFS, MODE_PRIVATE).getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE) / 5;

        // initialize from, to
        float from = KittenService.kittenLayoutParams.x;
        float to = (direction == DIRECTION_TO_LEFT) ? (from + shiverDistance) : (from - shiverDistance);

        // initialize duration
        long duration = calculateDurationGravity(shiverDistance, true);

        // initialize value animator
        valueAnimatorX = ValueAnimator.ofFloat(from, to);
        valueAnimatorX.setDuration(duration);
        valueAnimatorX.setInterpolator(new DecelerateInterpolator());
        valueAnimatorX.addUpdateListener(new KittenPositionAnimatorUpdateListener(COORDINATE_X));
        valueAnimatorX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (repeatCount == 1) {
                    animateKittenJumpUp();
                } else {
                    if (direction == DIRECTION_TO_LEFT)
                        animateKittenShiverAngry(DIRECTION_TO_RIGHT, repeatCount - 1);
                    else
                        animateKittenShiverAngry(DIRECTION_TO_LEFT, repeatCount - 1);
                }
            }
        });

        // start kitten animation
        valueAnimatorX.start();
    }

    private void animateKittenLongJumpUpRandom() {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        // initialize from, to without X
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        float fromX = KittenService.kittenLayoutParams.x;
        float fromY = KittenService.kittenLayoutParams.y;
        float toY = fromY + prefs.getInt(KEY_JUMP_ALTITUDE, DEFAULT_JUMP_ALTITUDE);

        // initialize random direction
        float distanceX = prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE); // default toX - fromX, but toX is not initialized
        final int direction;
        if (isWallDetected(DIRECTION_TO_LEFT, fromX + distanceX))
            direction = DIRECTION_TO_RIGHT;
        else if (isWallDetected(DIRECTION_TO_RIGHT, fromX - distanceX))
            direction = DIRECTION_TO_LEFT;
        else
            direction = new Random().nextInt(2);

        // initialize to X
        float toX = (direction == DIRECTION_TO_LEFT) ? (fromX + distanceX) : (fromX - distanceX);

        // initialize duration
        float distanceY = Math.abs(toY - fromY);
        long duration = calculateDurationGravity(distanceY, false);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(fromX, toX);
        valueAnimatorY = ValueAnimator.ofFloat(fromY, toY);
        valueAnimatorX.addUpdateListener(new KittenPositionAnimatorUpdateListener(COORDINATE_X));
        valueAnimatorY.addUpdateListener(new KittenPositionAnimatorUpdateListener(COORDINATE_Y));

        // initialize animator set
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateKittenLongJumpDownRandom(direction);
            }
        });

        // start kitten animation
        animatorSet.start();
    }

    private void animateKittenLongJumpDownRandom(int direction) {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        // initialize from, to
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        float fromX = KittenService.kittenLayoutParams.x;
        float fromY = KittenService.kittenLayoutParams.y;
        float toX = (direction == DIRECTION_TO_LEFT) ? (fromX + prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE)) : (fromX - prefs.getInt(KEY_JUMP_DISTANCE, DEFAULT_JUMP_DISTANCE));
        float toY = 0;

        // initialize duration
        float distanceY = Math.abs(toY - fromY);
        long duration = calculateDurationGravity(distanceY, false);

        // initialize value animator x, y
        valueAnimatorX = ValueAnimator.ofFloat(fromX, toX);
        valueAnimatorY = ValueAnimator.ofFloat(fromY, toY);
        valueAnimatorX.addUpdateListener(new KittenPositionAnimatorUpdateListener(COORDINATE_X));
        valueAnimatorY.addUpdateListener(new KittenPositionAnimatorUpdateListener(COORDINATE_Y));

        // initialize animator set
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new AccelerateInterpolator());
        if (new Random().nextInt(2) == 0) {
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    animateKittenLongJumpUpRandom();
                }
            });
        } else
            animatorSet.addListener(new ActionUnlockAnimatorEndListenerAdapter());

        // start kitten animation
        animatorSet.start();
    }

    private void animateKittenFaceMeow() {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        // animate kitten face meow
        changeKittenFace(FACE_CODE_MEOW_2, 0);
        changeKittenFace(FACE_CODE_MEOW_1, 40);
        changeKittenFace(FACE_CODE_MEOW_2, 160); // + 120
        changeKittenFace(FACE_CODE_DEFAULT, 200);

        // initialize value animator for unlock
        valueAnimatorX = ValueAnimator.ofFloat(KittenService.kittenLayoutParams.x, KittenService.kittenLayoutParams.x);
        valueAnimatorX.setDuration(200);
        valueAnimatorX.addListener(new ActionUnlockAnimatorEndListenerAdapter());
        valueAnimatorX.start();
    }

    private void animateKittenFaceBlink() {
        // case kitten hide
        if (KittenService.kittenView == null)
            return;

        // cancel animator
        cancelAnimator();

        changeKittenFace(FACE_CODE_BLINK_1, 0);
        changeKittenFace(FACE_CODE_BLINK_2, 40);
        changeKittenFace(FACE_CODE_BLINK_3, 80);
        changeKittenFace(FACE_CODE_BLINK_2, 160); // + 80
        changeKittenFace(FACE_CODE_BLINK_1, 200);
        changeKittenFace(FACE_CODE_DEFAULT, 240);

        // initialize value animator for unlock
        valueAnimatorX = ValueAnimator.ofFloat(KittenService.kittenLayoutParams.x, KittenService.kittenLayoutParams.x);
        valueAnimatorX.setDuration(240);
        valueAnimatorX.addListener(new ActionUnlockAnimatorEndListenerAdapter());
        valueAnimatorX.start();
    }

    private class ActionUnlockAnimatorEndListenerAdapter extends AnimatorListenerAdapter {
        @Override
        public void onAnimationEnd(Animator animation) {
            isActionLocked = false;
        }
    }

    private static class KittenPositionAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        private final int coordinate;

        KittenPositionAnimatorUpdateListener(int coordinate) {
            this.coordinate = coordinate;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            // case kitten hide
            if (KittenService.kittenView == null)
                return;

            if (coordinate == COORDINATE_X)
                KittenService.kittenLayoutParams.x = Math.round((Float) valueAnimator.getAnimatedValue());
            else
                KittenService.kittenLayoutParams.y = Math.round((Float) valueAnimator.getAnimatedValue());
            KittenService.kittenWindowManager.updateViewLayout(KittenService.kittenLayout, KittenService.kittenLayoutParams);
        }
    }
}