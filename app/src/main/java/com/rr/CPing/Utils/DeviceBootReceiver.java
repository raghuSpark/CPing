package com.rr.CPing.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rr.CPing.Model.AlarmIdClass;
import com.rr.CPing.SharedPref.SharedPrefConfig;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.ALARM_SERVICE;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")) {
            ArrayList<AlarmIdClass> currentList = SharedPrefConfig.readInIdsOfReminderContests(context),
                    newList = new ArrayList<>();
            for (AlarmIdClass alarmIdClass : currentList) {
                if (alarmIdClass.getAlarmSetTime() > System.currentTimeMillis()) {
                    newList.add(alarmIdClass);
                    setNotification(context, (alarmIdClass.getSpinnerPosition() + 1) * 5, alarmIdClass.getContestNameAsID(), alarmIdClass.getStartTime(), alarmIdClass.getAlarmSetTime(), alarmIdClass.getProperStartTime());
                }
            }
            SharedPrefConfig.writeInIdsOfReminderContests(context, newList);
        }
    }

    private void setNotification(Context context, int time, String contestName, long startTimeInMillis, long id, String properStartTime) {
        Intent intent = new Intent(context, ReminderBroadCast.class);
        intent.putExtra("ContestName", contestName);
        intent.putExtra("ProperStartTime", properStartTime);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(context).getSystemService(ALARM_SERVICE);

        long t2 = 60000 * time;

//        Log.e("TAG t1-t2", startTimeInMillis + " , " + time + " , " + (startTimeInMillis - t2));
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, (startTimeInMillis - t2), pendingIntent);
    }
}
