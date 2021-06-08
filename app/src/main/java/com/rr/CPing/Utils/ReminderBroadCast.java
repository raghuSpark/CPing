package com.rr.CPing.Utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import com.rr.CPing.Activities.AlarmRingingActivity;
import com.rr.CPing.Activities.SplashActivity;
import com.rr.CPing.R;

public class ReminderBroadCast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String contestName = intent.getStringExtra("ContestName");
        String properStartTime = intent.getStringExtra("ProperStartTime");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(context)) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            int notificationId = (int) System.currentTimeMillis();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify_contest");
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(contestName)
                    .setContentText("Starts at: " + properStartTime)
                    .setSound(uri, AudioManager.STREAM_NOTIFICATION)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(PendingIntent.getActivity(context, notificationId, new Intent(context, SplashActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                    .setAutoCancel(true);

            manager.notify(notificationId, builder.build());

        } else {
            Intent alarmIntent = new Intent(context, AlarmRingingActivity.class);
            alarmIntent.putExtra("ContestName", contestName);
            alarmIntent.putExtra("ProperStartTime", properStartTime);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(alarmIntent);
        }
    }
}