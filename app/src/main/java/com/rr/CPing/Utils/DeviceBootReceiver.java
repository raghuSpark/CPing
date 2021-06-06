package com.rr.CPing.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rr.CPing.Handlers.BottomSheetHandler;
import com.rr.CPing.Model.AlarmIdClass;
import com.rr.CPing.SharedPref.SharedPrefConfig;

import java.util.ArrayList;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")) {
            ArrayList<AlarmIdClass> currentList = SharedPrefConfig.readInIdsOfReminderContests(context),
                    newList = new ArrayList<>();
            for (AlarmIdClass alarmIdClass : currentList) {
                if (alarmIdClass.getStartTime() > System.currentTimeMillis() && alarmIdClass.getAlarmSetTime() > System.currentTimeMillis()) {
                    newList.add(alarmIdClass);
                    new BottomSheetHandler().setNotification(context, (alarmIdClass.getSpinnerPosition() + 1) * 5, alarmIdClass.getContestNameAsID(), alarmIdClass.getStartTime(), alarmIdClass.getAlarmSetTime(), alarmIdClass.getProperStartTime());
                }
            }
            SharedPrefConfig.writeInIdsOfReminderContests(context, newList);
        }
    }
}
