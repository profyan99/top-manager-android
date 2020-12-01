package com.topmngr.game;

import android.annotation.TargetApi;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.topmngr.game.Utils.NotificationHandler;

/**
 * Created by PROFYAN on 17.04.2017.
 */
public class AdapterAndroid implements NotificationHandler {

    private Activity gameActivity;

    public AdapterAndroid(Activity gameActivity) {
        this.gameActivity = gameActivity;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void showNotification(String title, String text) {

        Intent notificationIntent = new Intent(gameActivity, AndroidLauncher.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(gameActivity, 0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder mBuilder = new Notification.Builder(gameActivity)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setTicker(title)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setContentText(text);



        int notificationId = 1;
        // Gets an instance of the NotificationManager service
        NotificationManager notificationManager =
                (NotificationManager) gameActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        notificationManager.notify(notificationId, mBuilder.build());
    }
}
