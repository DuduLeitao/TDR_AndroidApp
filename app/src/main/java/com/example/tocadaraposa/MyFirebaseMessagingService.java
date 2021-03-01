package com.example.tocadaraposa;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */

    SharedPreferences prefs_info;

    @Override
    public void onNewToken(String token){
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);


        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        // Guardamos en "infos" que ya hemos hecho el wellcome y cerramos activity
        prefs_info = getSharedPreferences("info", Context.MODE_PRIVATE);
        prefs_info.edit().putString("token", token).apply();

        sendRegistrationToServer(token);
    }

    public void sendRegistrationToServer(String token){
        fetchData process = new fetchData("{\"user\": \""+MainActivity.name+"\", \"type\": \"token\", \"value\": \""+token+"\"}","ServerRequest");
        process.execute();
    }
/*
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        showNotification(remoteMessage.getData().get("message"));
    }

    private void showNotification(String message) {
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("FCM test")
                .setContentText(message)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0,builder.build());

    }

*/

    private static final String TAG = "DBG";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null)
            return;

        // check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification body: " + remoteMessage.getNotification().getBody());
            createNotification(remoteMessage.getNotification());
        }

        // check if message contains a data payload
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "From: " + remoteMessage.getFrom());
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            createNotificationBORRAR(remoteMessage.getData().get("message"));
        }
    }

    private void createNotification (RemoteMessage.Notification notification) {
        Intent intent = new Intent(this , MainActivity.class );
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultIntent = PendingIntent.getActivity( this , 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder( this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel( true )
                .setContentIntent(resultIntent);
        //.setSound(notificationSoundURI)

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, mNotificationBuilder.build());
    }

    private void createNotificationBORRAR (String message) {
        Intent intent = new Intent(this , MainActivity.class );
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultIntent = PendingIntent.getActivity( this , 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder( this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Hardcoded title")
                .setContentText(message)
                .setAutoCancel( true )
                .setContentIntent(resultIntent);
        //.setSound(notificationSoundURI)

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, mNotificationBuilder.build());
    }

}
