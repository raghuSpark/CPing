package com.rr.CPing.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.rr.CPing.Activities.SplashActivity;
import com.rr.CPing.Model.AlarmIdClass;
import com.rr.CPing.SharedPref.SharedPrefConfig;

import java.util.ArrayList;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            ArrayList<AlarmIdClass> alarmIdClassArrayList = new ArrayList<>();
            alarmIdClassArrayList.add(new AlarmIdClass("kdfnglslsk", System.currentTimeMillis(), 638461463, 1));
            Toast.makeText(context, "kdfnglslsk", Toast.LENGTH_LONG).show();
            SharedPrefConfig.writeInIdsOfReminderContests(context, alarmIdClassArrayList);
//            ArrayList<AlarmIdClass> currentList = SharedPrefConfig.readInIdsOfReminderContests(context), newList = new ArrayList<>();
//            for (int i = 0; i < currentList.size(); i++) {
//                AlarmIdClass alarmIdClass = currentList.get(i);
//                if (alarmIdClass.getStartTime() > System.currentTimeMillis() && alarmIdClass.getAlarmSetTime() > System.currentTimeMillis()) {
//                    long id = System.currentTimeMillis();
//                    new BottomSheetHandler().setNotification(context, (alarmIdClass.getSpinnerPosition() + 1) * 5, alarmIdClass.getContestNameAsID(), alarmIdClass.getStartTime(), id, "properStartTime");
//                    alarmIdClass.setAlarmSetTime(id);
//                    newList.add(alarmIdClass);
//                }
//                newList.add(new AlarmIdClass("aksdbfaj", i, i * i * i, 1));
//            }
//            SharedPrefConfig.writeInIdsOfReminderContests(context, newList);
        }
//        else {
//            ArrayList<AlarmIdClass> alarmIdClassArrayList = new ArrayList<>();
//            alarmIdClassArrayList.add(new AlarmIdClass("a;lsdkfm", System.currentTimeMillis(), 638461463, 1));
//            Toast.makeText(context, "a;lsdkfm", Toast.LENGTH_LONG).show();
//            SharedPrefConfig.writeInIdsOfReminderContests(context, alarmIdClassArrayList);
//        }
    }
}
