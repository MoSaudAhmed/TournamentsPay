package com.mgdapps.play360.broadcast;

import android.app.Notification;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mgdapps.play360.R;
import com.mgdapps.play360.helper.Constants;

import java.util.HashSet;
import java.util.Set;

public class FirebaseNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Notification.Builder not = new Notification.Builder(getApplicationContext());
        not.setContentTitle(remoteMessage.getNotification().getTitle());
        not.setContentText(remoteMessage.getNotification().getBody());
        not.setSmallIcon(R.drawable.ic_launcher);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        not.setSound(sound);
        NotificationManagerCompat.from(getApplicationContext()).notify(0, not.build());

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constants.notifications, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int count = sharedPreferences.getInt(Constants.notifications, 0);
        editor.putInt(Constants.notifications, count + 1);
        Set<String> stringSet = sharedPreferences.getStringSet(Constants.notificationsList, new HashSet<String>());
        stringSet.add(remoteMessage.getNotification().getTitle() + ": " + remoteMessage.getNotification().getBody());
        editor.putStringSet(Constants.notificationsList, stringSet);
        editor.apply();

    }
}