package com.pleiades.pleione.kittencare;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.pleiades.pleione.kittencare.controller.AnimationController;
import com.pleiades.pleione.kittencare.controller.DeviceController;
import com.pleiades.pleione.kittencare.controller.NotificationController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_DEFAULT;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_MAGNET_PERCENTAGE_HEIGHT;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_MAGNET_PERCENTAGE_WIDTH;
import static com.pleiades.pleione.kittencare.Config.DELAY_UNLOCK_SCREEN;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_DEFAULT;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SURPRISED;
import static com.pleiades.pleione.kittencare.Config.KEY_IS_RECOGNIZING_KEYBOARD;
import static com.pleiades.pleione.kittencare.Config.KEY_LAST_HIDE_DATE_STRING;
import static com.pleiades.pleione.kittencare.Config.KEY_MAGNET_PERCENTAGE_HEIGHT;
import static com.pleiades.pleione.kittencare.Config.KEY_MAGNET_PERCENTAGE_WIDTH;
import static com.pleiades.pleione.kittencare.Config.KEY_WEARING_COSTUME;
import static com.pleiades.pleione.kittencare.Config.NOTIFICATION_ID_FOREGROUND;
import static com.pleiades.pleione.kittencare.Config.PERIOD_EXPLORE;
import static com.pleiades.pleione.kittencare.Config.PREFS;

public class KittenService extends Service {
    private Context context;
    private Timer timer;
    private TimerTask timerTask;
    private boolean systemNavigationHide;
    private AnimationController animationController;

    @SuppressLint("StaticFieldLeak")
    public static View kittenView;

    public static CoordinatorLayout kittenLayout;
    public static WindowManager kittenWindowManager;
    public static WindowManager.LayoutParams kittenLayoutParams;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // remove last hide date string
        editor.remove(KEY_LAST_HIDE_DATE_STRING);
        editor.apply();

        // initialize context
        context = KittenService.this;

        // initialize kitten controller
        animationController = new AnimationController(context);

        // start foreground with notification
        startForeground(NOTIFICATION_ID_FOREGROUND, new NotificationController(context).initializeNotification());

        // initialize kitten
        initializeKitten();
    }

    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    private void initializeKitten() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        // case already initialized
        if (kittenWindowManager != null)
            return;

        // initialize kitten layout params
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            kittenLayoutParams = new WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT);

            if (prefs.getBoolean(KEY_IS_RECOGNIZING_KEYBOARD, false))
                kittenLayoutParams.flags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        } else {
            kittenLayoutParams = new WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT);
        }
        kittenLayoutParams.gravity = Gravity.END | Gravity.BOTTOM;

        // initialize kitten view
        kittenView = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.include_kitten, null);

        // initialize kitten layout
        kittenLayout = kittenView.findViewById(R.id.layout_kitten);
        kittenLayout.setOnTouchListener(new KittenOnTouchListener());

        // set on system ui visibility change listener
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // TODO implement
            // deprecation
            kittenLayout.setOnSystemUiVisibilityChangeListener(visibility -> systemNavigationHide = (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0);
        } else {
            kittenLayout.setOnSystemUiVisibilityChangeListener(visibility -> systemNavigationHide = (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0);
        }

        // initialize kitten window manager
        kittenWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // add kitten view to window with layout params
        kittenWindowManager.addView(kittenView, kittenLayoutParams);

        // initialize kitten costume
        AnimationController.changeKittenCostume(prefs.getInt(KEY_WEARING_COSTUME, COSTUME_CODE_DEFAULT));

        // show kitten
        animationController.animateKittenShow();

        // initialize timer task
        timerTask = new TimerTask() {
            @Override
            public void run() {
                animationController.explore();
            }
        };

        // initialize timer
        timer = new Timer();
        timer.schedule(timerTask, PERIOD_EXPLORE, PERIOD_EXPLORE);
    }

    private void destroyKitten() {
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

        // remove view
        if (kittenWindowManager != null) {
            if (kittenView != null) {
                kittenWindowManager.removeView(kittenView);
                kittenView = null;
            }
            kittenWindowManager = null;
        }

        // reset kitten layout
        if (kittenLayout != null) {
            kittenLayout = null;
        }

        // reset layout params
        if (kittenLayoutParams != null) {
            kittenLayoutParams = null;
        }
    }

    @Override
    public void onDestroy() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // apply last hide date string
        editor.putString(KEY_LAST_HIDE_DATE_STRING, new SimpleDateFormat("yy/MM/dd HH:mm:ss", Locale.US).format(new Date()));
        editor.apply();

        // destroy kitten
        destroyKitten();

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // case screen off
        registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                destroyKitten();
            }
        }, new IntentFilter(Intent.ACTION_SCREEN_OFF));

        // case screen on
        registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> initializeKitten(), DELAY_UNLOCK_SCREEN);
            }
        }, new IntentFilter(Intent.ACTION_USER_PRESENT));

        return START_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (kittenWindowManager != null) {
            DeviceController deviceController = new DeviceController(context);
            if (animationController.isAttractedLeft || animationController.isAttractedRight) {
                kittenLayoutParams.y = (int) (animationController.attractedHeightRatio * deviceController.getHeightMax());
                if (animationController.isAttractedLeft)
                    kittenLayoutParams.x = deviceController.getWidthMax() - kittenLayout.getWidth();

                // update layout position
                kittenWindowManager.updateViewLayout(kittenView, kittenLayoutParams);
            } else {
                if (kittenLayoutParams.x > (deviceController.getWidthMax() - kittenLayout.getWidth())) {
                    // initialize position x
                    kittenLayoutParams.x = deviceController.getWidthMax() - kittenLayout.getWidth();
                    kittenWindowManager.updateViewLayout(kittenView, kittenLayoutParams);
                }
            }
        }
    }

    private class KittenOnTouchListener implements View.OnTouchListener {
        private int gap = 0;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            DeviceController deviceController = new DeviceController(context);
            int viewWidth = view.getWidth();
            int viewHeight = view.getHeight();
            int deviceWidth = deviceController.getWidthMax();
            int deviceHeight = deviceController.getHeightMax();
            int orientation = getResources().getConfiguration().orientation;

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // getRawX, get RawY are based on TOP START
                    // params.x, params.y are based on BOTTOM END
                    animationController.cancelAnimator();
                    animationController.isActionLocked = true;
                    animationController.isAttractedLeft = false;
                    animationController.isAttractedRight = false;
                    animationController.changeKittenFace(FACE_CODE_SURPRISED, 0);

                    // reset kitten rotation
                    kittenLayout.setRotation(0);

                    // recognize keyboard (touch height)
                    int touchHeight = (int) (deviceHeight - motionEvent.getRawY());
                    gap = Math.abs(kittenLayoutParams.y - touchHeight);

                    // recognize keyboard (relative touch height)
                    int relativeTouchHeight;
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) relativeTouchHeight = (int) ((viewHeight - motionEvent.getY()) / 2);
                    else relativeTouchHeight = (int) ((viewHeight - motionEvent.getX()) / 2);
                    if (gap != 0)
                        gap = gap - relativeTouchHeight;

                    return true;

                case MotionEvent.ACTION_MOVE:
                    // default x, y initialize
                    kittenLayoutParams.x = (int) (deviceWidth - motionEvent.getRawX());
                    kittenLayoutParams.y = (int) (deviceHeight - motionEvent.getRawY());

                    // screen rotate state
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        kittenLayoutParams.x = kittenLayoutParams.x - viewWidth / 2;
                        if (systemNavigationHide)
                            kittenLayoutParams.y = kittenLayoutParams.y + viewHeight / 2;
                    } else {
                        kittenLayoutParams.y = kittenLayoutParams.y - viewWidth / 2;
                        if (systemNavigationHide)
                            kittenLayoutParams.x = kittenLayoutParams.x + viewHeight / 2;
                    }

                    // keyboard gap
                    kittenLayoutParams.y = kittenLayoutParams.y - gap;

                    // set x, y position
                    kittenWindowManager.updateViewLayout(kittenView, kittenLayoutParams);

                    return true;

                case MotionEvent.ACTION_UP:
                    float MAGNET_WIDTH_PERCENTAGE = prefs.getFloat(KEY_MAGNET_PERCENTAGE_WIDTH, DEFAULT_MAGNET_PERCENTAGE_WIDTH);
                    float MAGNET_HEIGHT_PERCENTAGE = prefs.getFloat(KEY_MAGNET_PERCENTAGE_HEIGHT, DEFAULT_MAGNET_PERCENTAGE_HEIGHT);

                    animationController.changeKittenFace(FACE_CODE_DEFAULT, 0);

                    // magnet range
                    boolean magnetLeft, magnetRight, magnetY;
                    magnetLeft = magnetRight = magnetY = false;
                    if (motionEvent.getRawY() / (deviceHeight - gap) < MAGNET_HEIGHT_PERCENTAGE)
                        magnetY = true;
                    if (motionEvent.getRawX() / deviceWidth > MAGNET_WIDTH_PERCENTAGE)
                        magnetRight = true;
                    if ((motionEvent.getRawX() - viewWidth) / deviceWidth < (1 - MAGNET_WIDTH_PERCENTAGE))
                        magnetLeft = true;

                    // perform
                    if (magnetRight && magnetY)
                        animationController.animateKittenAttractionRight();
                    else if (magnetLeft && magnetY)
                        animationController.animateKittenAttractionLeft();
                    else if ((deviceHeight - gap - motionEvent.getRawY()) > (deviceHeight / 5.0))
                        animationController.animateKittenFall();
                    else
                        animationController.animateKittenRunAway();

                    return false;
            }
            return false;
        }
    }
}
