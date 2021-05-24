package com.rr.CPing.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.rr.CPing.R;
import com.rr.CPing.activities.AlarmRingingActivity;
import com.rr.CPing.activities.SplashActivity;

public class ReminderBroadCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String contestName = intent.getStringExtra("ContestName");

        boolean isAppearOnTopPermitted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                !Settings.canDrawOverlays(context)) {
            isAppearOnTopPermitted = false;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify_contest")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(contestName)
                .setContentText("Contest is about to start")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setFullScreenIntent(PendingIntent.getActivity(context, 0, new Intent(context, SplashActivity.class), PendingIntent.FLAG_ONE_SHOT), true)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true);

//        if (!isAppearOnTopPermitted) {
//            builder.addAction(R.drawable.ic_baseline_cancel_24,"Snooze",null);
//            builder.addAction(R.drawable.ic_baseline_cancel_24,"Dismiss",null);
//        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());

        if (isAppearOnTopPermitted) {
            try {
                Intent alarmIntent = new Intent(context, AlarmRingingActivity.class);
                alarmIntent.putExtra("ContestName", contestName);
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(alarmIntent);
            } catch (Exception e) {
                Log.e("TAG", e.getMessage());
            }
        }
//        else {
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//            Ringtone ringtone = RingtoneManager.getRingtone(context, notification);
//            ringtone.play();
//            new CountDownTimer(10000, 1000) {
//
//                @Override
//                public void onTick(long millisUntilFinished) {
//
//                }
//                @Override
//                public void onFinish() {
//                    ringtone.stop();
//                }
//            };
    }
}