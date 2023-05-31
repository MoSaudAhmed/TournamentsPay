package com.mgdapps.play360.broadcast;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.mgdapps.play360.R;
import com.mgdapps.play360.activities.SplashScreenActivity;
import com.mgdapps.play360.helper.Constants;
import com.mgdapps.play360.helper.Preferences;

import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class MyAlarmReceiver extends BroadcastReceiver {

    Preferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.notifications, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int count = sharedPreferences.getInt(Constants.notifications, 0);
        editor.putInt(Constants.notifications, count + 1);
        if (intent.getStringExtra(Constants.tournamentTitle) != null) {
            Set<String> stringSet = sharedPreferences.getStringSet(Constants.notificationsList, new HashSet<String>());
            stringSet.add(intent.getStringExtra(Constants.tournamentTitle) + " is about to start");
            editor.putStringSet(Constants.notificationsList, stringSet);
        }
        editor.apply();
        Intent newBroadcasr = new Intent("com.notifications.receiver");
        context.sendBroadcast(newBroadcasr);

        Log.e("AlarmReceiver", "Received");
        preferences = new Preferences();
        preferences.loadPreferences(context);

        Intent launchIntent = new Intent(context, SplashScreenActivity.class);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        launchIntent.putExtra(Constants.MatchId, intent.getStringExtra(Constants.MatchId));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, launchIntent,
                PendingIntent.FLAG_ONE_SHOT);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "Channel_Id");

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setLargeIcon(bm);

        notificationBuilder.setContentTitle("Match is about to start");
        notificationBuilder.setContentText("Your match is starting in 5 mins, Please check RoomId and Password");

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("Match is about to start");
        bigTextStyle.bigText("Your match is starting in 5 mins, Please check RoomId and Password");

        notificationBuilder.setStyle(bigTextStyle);
        notificationBuilder.setAutoCancel(true);

        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setNumber(2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
            notificationBuilder.setColor(context.getResources().getColor(R.color.lytBlue));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel("Channel_Id", "Channel_Name", NotificationManager.IMPORTANCE_HIGH);
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                if (preferences.isSound()) {
                    Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getApplicationContext().getPackageName() + "/" + R.raw.ringtone);
                    notificationChannel.setSound(alarmSound, audioAttributes);
                }
                if (preferences.isVibration()) {
                    notificationChannel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
                }
                notificationManager.createNotificationChannel(notificationChannel);
            }

        }
        notificationManager.notify(0, notificationBuilder.build());
    }
}