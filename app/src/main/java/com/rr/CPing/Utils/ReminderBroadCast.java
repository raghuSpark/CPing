package com.rr.CPing.Utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.rr.CPing.Activities.AlarmRingingActivity;
import com.rr.CPing.R;

public class ReminderBroadCast extends BroadcastReceiver {
    private static final String TAG = "ReminderBroadCast";

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onReceive(Context context, Intent intent) {
        String contestName = intent.getStringExtra("ContestName");
        String properStartTime = intent.getStringExtra("ProperStartTime");

        boolean isAppearOnTopPermitted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                !Settings.canDrawOverlays(context)) {
            isAppearOnTopPermitted = false;
        }

        if (isAppearOnTopPermitted) {
            try {
                Intent alarmIntent = new Intent(context, AlarmRingingActivity.class);
                alarmIntent.putExtra("ContestName", contestName);
                alarmIntent.putExtra("ProperStartTime", properStartTime);
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(alarmIntent);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

            MyProperties.getInstance().ringtone = RingtoneManager.getRingtone(context, uri);
            MyProperties.getInstance().ringtone.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());
            MyProperties.getInstance().ringtone.play();

            int notificationId = (int) System.currentTimeMillis() / 1000;

            Intent snoozeIntent = new Intent(context, BackgroundProcess.class);
            snoozeIntent.putExtra("action", "snooze");
            snoozeIntent.putExtra("id", notificationId);
            snoozeIntent.putExtra("ProperStartTime", properStartTime);
            snoozeIntent.putExtra("contestName", contestName);
            snoozeIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, notificationId, snoozeIntent, PendingIntent.FLAG_ONE_SHOT);

            Intent dismissIntent = new Intent(context, BackgroundProcess.class);
            dismissIntent.putExtra("action", "dismiss");
            dismissIntent.putExtra("id", notificationId);
            dismissIntent.putExtra("contestName", contestName);
            dismissIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, notificationId + 1, dismissIntent, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify_contest");

            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(contestName)
                    .setContentText("Contest is going to start at: " + properStartTime)
                    .setSound(uri, AudioManager.STREAM_NOTIFICATION)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .addAction(R.drawable.ic_decrease_snooze_icon, "Snooze", snoozePendingIntent)
                    .addAction(R.drawable.ic_increase_snooze_icon, "Dismiss", dismissPendingIntent)
                    .setAutoCancel(true)
                    .setOngoing(true);

            manager.notify(notificationId, builder.build());
        }
    }
}