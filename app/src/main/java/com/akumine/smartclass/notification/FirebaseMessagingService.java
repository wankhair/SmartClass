package com.akumine.smartclass.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.akumine.smartclass.R;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final long[] DEFAULT_VIBRATE = {0, 250, 250, 250};

    private final static String SMARTCLASS_NOTIFICATION_SERVICE_CHANNEL_ID = "SMARTCLASS_NOTIFICATION_SERVICE_CHANNEL_ID";
    private final static int SMARTCLASS_NOTIFICATION_SERVICE_NOTIFICATION_ID = 4000;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String clickAction = remoteMessage.getNotification().getClickAction();
        String locationId = remoteMessage.getData().get("location_id");

        Intent resultIntent = new Intent(clickAction);
        resultIntent.putExtra("assign_id", locationId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String notificationTitle = remoteMessage.getNotification().getTitle();
        String notificationMessage = remoteMessage.getNotification().getBody();
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    SMARTCLASS_NOTIFICATION_SERVICE_CHANNEL_ID,
                    "SmartClass",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        //build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                SMARTCLASS_NOTIFICATION_SERVICE_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_foreground))
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setVibrate(DEFAULT_VIBRATE)
                .setSound(uri)
                .setContentIntent(pendingIntent);
        notificationManager.notify(SMARTCLASS_NOTIFICATION_SERVICE_NOTIFICATION_ID, builder.build());

//        NotificationCompat.Builder builder;
//        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        builder = new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.mipmap.ic_launcher_foreground)
//                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_foreground))
//                        .setContentTitle(notificationTitle)
//                        .setContentText(notificationMessage)
//                        .setVibrate(DEFAULT_VIBRATE)
//                        .setSound(uri);
    }
}
