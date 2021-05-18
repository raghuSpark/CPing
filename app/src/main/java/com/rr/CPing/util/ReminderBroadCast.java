package com.rr.CPing.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.rr.CPing.R;

public class ReminderBroadCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String contestName = intent.getStringExtra("ContestName");
        Log.e("TAG", contestName);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify_contest")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(contestName)
                .setContentText("Contest is about to start")
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
