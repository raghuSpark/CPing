package com.rr.CPing.Utils;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.rr.CPing.Handlers.BottomSheetHandler;
import com.rr.CPing.Model.AlarmIdClass;
import com.rr.CPing.SharedPref.SharedPrefConfig;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.NOTIFICATION_SERVICE;

public class BackgroundProcess extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        MyProperties.getInstance().ringtone.stop();

        String action = intent.getStringExtra("action");
        if (action.equals("dismiss")) MyProperties.getInstance().isDismissed = true;

        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(intent.getIntExtra("id", 0));

        String contestName = intent.getStringExtra("contestName");

        ArrayList<AlarmIdClass> idClassArrayList = SharedPrefConfig.readInIdsOfReminderContests(context);
        int index = getIndexFromList(idClassArrayList, contestName);
        AlarmIdClass alarmIdClass = idClassArrayList.get(index);

        if (!alarmIdClass.isGoogleReminderSet()) idClassArrayList.remove(index);
        else idClassArrayList.get(index).setInAppReminderSet(false);

        SharedPrefConfig.writeInIdsOfReminderContests(context, idClassArrayList);

        if (action.equals("snooze")) {

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

                new BottomSheetHandler().setNotification(context, -5, contestName, Calendar.getInstance(), System.currentTimeMillis() / 1000, true, intent.getStringExtra("ProperStartTime"));
            }
        }
    }

    private int getIndexFromList(ArrayList<AlarmIdClass> currentList, String contestName) {
        for (int i = 0; i < currentList.size(); ++i) {
            if (currentList.get(i).getContestNameAsID().equals(contestName)) return i;
        }
        return -1;
    }
}
