package com.rr.CPing.util;

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
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.rr.CPing.R;
import com.rr.CPing.activities.TimePassActivity;
import com.rr.CPing.activities.AlarmRingingActivity;

public class ReminderBroadCast extends BroadcastReceiver {
    private static final String TAG = "ReminderBroadCast";

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onReceive(Context context, Intent intent) {
        String contestName = intent.getStringExtra("ContestName");

        boolean isAppearOnTopPermitted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                !Settings.canDrawOverlays(context)) {
            isAppearOnTopPermitted = false;
        }

        if (isAppearOnTopPermitted) {
            try {
                Intent alarmIntent = new Intent(context, AlarmRingingActivity.class);
                alarmIntent.putExtra("ContestName", contestName);
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(alarmIntent);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        } else {

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

            int notificationId = (int) System.currentTimeMillis()/1000;

            Intent snoozeIntent = new Intent(context, TimePassActivity.class);
            snoozeIntent.putExtra("action", "snooze");
            snoozeIntent.putExtra("id", notificationId);
            snoozeIntent.putExtra("contestName", contestName);
            snoozeIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent snoozePendingIntent = PendingIntent.getActivity(context, notificationId, snoozeIntent, PendingIntent.FLAG_ONE_SHOT);

            Intent dismissIntent = new Intent(context, TimePassActivity.class);
            dismissIntent.putExtra("action", "dismiss");
            dismissIntent.putExtra("id", notificationId);
            dismissIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent dismissPendingIntent = PendingIntent.getActivity(context, notificationId, dismissIntent, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify_contest");

            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(contestName)
                    .setContentText("Contest is going to start at: ")
                    .setSound(uri, AudioManager.STREAM_RING)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .addAction(R.drawable.ic_decrease_snooze_icon, "Snooze", snoozePendingIntent)
                    .addAction(R.drawable.ic_increase_snooze_icon, "Dismiss", dismissPendingIntent);

            manager.notify(notificationId, builder.build());

//            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify_contest")
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentTitle(contestName)
//                    .setContentText("Contest is about to start")
//                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                    .setCategory(NotificationCompat.CATEGORY_ALARM)
//                    .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, SplashActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
//                    .setAutoCancel(true)
//                    .setOnlyAlertOnce(true);
//
//            ArrayList<AlarmIdClass> idClassArrayList = SharedPrefConfig.readInIdsOfReminderContests(context);
//            int index = getIndexFromList(idClassArrayList, contestName);
//
//            AlarmIdClass alarmIdClass = idClassArrayList.get(index);
//
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify_contest")
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setCustomContentView(notificationCustomView)
//                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle());

//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
//
//            if (!alarmIdClass.isGoogleReminderSet()) idClassArrayList.remove(index);
//            else alarmIdClass.setInAppReminderSet(false);
//
//            SharedPrefConfig.writeInIdsOfReminderContests(context, idClassArrayList);

//            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//            Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
//            ringtone.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());
//            ringtone.setVolume(100);
//            ringtone.play();

//            new Handler().postDelayed(() -> {
//                if (Math.abs(alarmIdClass.getStartTime() - System.currentTimeMillis()) / 60000 <= 5) {
//                    Toast.makeText(context, "This contest is going to start in less than 5 minutes!",
//                            Toast.LENGTH_SHORT).show();
//                } else {
//                    alarmIdClass.setAlarmSetTime(System.currentTimeMillis() / 1000);
//
//                    int idx = getIndexFromList(idClassArrayList, contestName);
//                    if (idx == -1) idClassArrayList.add(alarmIdClass);
//                    else idClassArrayList.get(idx).setInAppReminderSet(true);
//
//                    SharedPrefConfig.writeInIdsOfReminderContests(context, idClassArrayList);
//
//                    Toast.makeText(context, "Snoozed for 5 minutes!", Toast.LENGTH_SHORT).show();
//
//                    new BottomSheetHandler().setNotification(context, -5, contestName, Calendar.getInstance(), System.currentTimeMillis() / 1000, true);
//                }
//                ringtone.stop();
//            }, 60000);
        }
    }
}