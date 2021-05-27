package com.rr.CPing.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.rr.CPing.Handlers.BottomSheetHandler;
import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.activities.AlarmRingingActivity;
import com.rr.CPing.activities.SplashActivity;
import com.rr.CPing.model.AlarmIdClass;

import java.util.ArrayList;
import java.util.Calendar;

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
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify_contest")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(contestName)
                    .setContentText("Contest is about to start")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, SplashActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());

            ArrayList<AlarmIdClass> idClassArrayList = SharedPrefConfig.readInIdsOfReminderContests(context);
            int index = getIndexFromList(idClassArrayList, contestName);

            AlarmIdClass alarmIdClass = idClassArrayList.get(index);

            if (!alarmIdClass.isGoogleReminderSet()) idClassArrayList.remove(index);
            else alarmIdClass.setInAppReminderSet(false);

            SharedPrefConfig.writeInIdsOfReminderContests(context, idClassArrayList);

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Ringtone ringtone = RingtoneManager.getRingtone(context, notification);
            ringtone.setVolume(100);
            ringtone.play();

            new Handler().postDelayed(() -> {
                if (Math.abs(alarmIdClass.getStartTime() - System.currentTimeMillis()) / 60000 <= 5) {
                    Toast.makeText(context, "This contest is going to start in less than 5 minutes!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    alarmIdClass.setAlarmSetTime(System.currentTimeMillis() / 1000);

                    int idx = getIndexFromList(idClassArrayList, contestName);
                    if (idx == -1) idClassArrayList.add(alarmIdClass);
                    else idClassArrayList.get(idx).setInAppReminderSet(true);

                    SharedPrefConfig.writeInIdsOfReminderContests(context, idClassArrayList);

                    Toast.makeText(context, "Snoozed for 5 minutes!", Toast.LENGTH_SHORT).show();

                    new BottomSheetHandler().setNotification(context, 5, contestName, Calendar.getInstance(), System.currentTimeMillis() / 1000, true);
                }
                ringtone.stop();
            }, 60000);
        }
    }

    private int getIndexFromList(ArrayList<AlarmIdClass> currentList, String contestName) {
        for (int i = 0; i < currentList.size(); ++i) {
            if (currentList.get(i).getContestNameAsID().equals(contestName)) return i;
        }
        return -1;
    }
}