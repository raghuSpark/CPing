package com.rr.CPing.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify_contest")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(contestName)
                .setContentText("Contest is about to start")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setFullScreenIntent(PendingIntent.getActivity(context,0,new Intent(context, SplashActivity.class),PendingIntent.FLAG_UPDATE_CURRENT),true)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());

        try{
            Log.e("TAG", "onReceive: ASSAM");
            Intent alarmIntent = new Intent(context, AlarmRingingActivity.class);
            alarmIntent.putExtra("ContestName", contestName);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(alarmIntent);
            Log.e("TAG", "onReceive: ASSAM 2.0");
        }catch (Exception e){
            Log.e("TAG", e.getMessage());
        }
//        Intent alarmIntent = new Intent(context, AlarmRingingActivity.class);
//        alarmIntent.putExtra("ContestName", contestName);
//        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS);
//        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//        context.startActivity(alarmIntent);
    }
}