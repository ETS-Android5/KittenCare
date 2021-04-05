package com.pleiades.pleione.kittencare.controller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.ui.activity.EscapeActivity;
import com.pleiades.pleione.kittencare.ui.activity.MainActivity;

import static android.content.Context.MODE_PRIVATE;
import static com.pleiades.pleione.kittencare.Config.KEY_NAME;
import static com.pleiades.pleione.kittencare.Config.NOTIFICATION_CHANNEL_ID;
import static com.pleiades.pleione.kittencare.Config.NOTIFICATION_CHANNEL_NAME;
import static com.pleiades.pleione.kittencare.Config.PREFS;

public class NotificationController {
    private final Context context;

    public NotificationController(Context context) {
        this.context = context;
    }

    public Notification initializeNotification() {
        // case above android O
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // create notification channel
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
        }

        // initialize notification click intent
        Intent clickIntent = new Intent(context, MainActivity.class);
        clickIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // prevent recreate instance(activity, fragment)
        PendingIntent clickPendingIntent = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT); // update only if already exist

        // initialize notification action intent
        Intent actionIntent = new Intent(context, EscapeActivity.class);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(context, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // initialize notification content title
        String kittenName = context.getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_NAME, context.getString(R.string.default_name));
        String contentTitle = String.format(context.getString(R.string.notification_contents_title), kittenName);

        // initialize notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder
                //.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentTitle(contentTitle) // title
//                .setSubText(contentTitle) // sub text
//                .setContentText(contentTitle) // text

                .setDefaults(Notification.DEFAULT_SOUND)
                .setVibrate(new long[]{0L}) // sound, vibrate, etc..

                .setAutoCancel(false) // touch remove

                .setOnlyAlertOnce(true) // prevent redundant
                .setOngoing(true) // persistence

                .setShowWhen(false) // push time

                .setSmallIcon(R.drawable.icon_home) // small icon
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher)) // large icon

                .addAction(0, context.getString(R.string.notification_escape), actionPendingIntent) // action intent

                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(clickPendingIntent);

        // build notification
        return builder.build();
    }
}
