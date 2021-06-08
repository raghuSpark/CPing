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
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.rr.CPing.Activities.AlarmRingingActivity;
import com.rr.CPing.Activities.SplashActivity;
import com.rr.CPing.Handlers.BottomSheetHandler;
import com.rr.CPing.Model.AlarmIdClass;
import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;

import java.util.ArrayList;

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

            ArrayList<AlarmIdClass> idClassArrayList = SharedPrefConfig.readInIdsOfReminderContests(context);

            final int index = getIndexFromList(idClassArrayList, contestName);
            AlarmIdClass alarmIdClass = idClassArrayList.get(index);

            idClassArrayList.remove(index);
            if (Math.abs(alarmIdClass.getStartTime() - System.currentTimeMillis()) / 60000 <= 5) {
                Toast.makeText(context, "This contest is going to start in less than 5 minutes!", Toast.LENGTH_SHORT).show();
            } else {
                long alarmSetTime = roundTheValue(System.currentTimeMillis());
                alarmIdClass.setAlarmSetTime(alarmSetTime);
                alarmIdClass.setSpinnerPosition(alarmIdClass.getSpinnerPosition() - 1);

                idClassArrayList.add(alarmIdClass);

                SharedPrefConfig.writeInIdsOfReminderContests(context, idClassArrayList);

                new BottomSheetHandler().setNotification(context, -5, contestName,
                        alarmIdClass.getStartTime(),
                        alarmSetTime, properStartTime);
            }

        } else {
            Intent alarmIntent = new Intent(context, AlarmRingingActivity.class);
            alarmIntent.putExtra("ContestName", contestName);
            alarmIntent.putExtra("ProperStartTime", properStartTime);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(alarmIntent);
        }
    }

    private long roundTheValue(long currentTimeMillis) {
        return (currentTimeMillis / 60000) * 60000;
    }

    private int getIndexFromList(ArrayList<AlarmIdClass> currentList, String contestName) {
        for (int i = 0; i < currentList.size(); ++i) {
            if (currentList.get(i).getContestNameAsID().equals(contestName)) return i;
        }
        return -1;
    }
}