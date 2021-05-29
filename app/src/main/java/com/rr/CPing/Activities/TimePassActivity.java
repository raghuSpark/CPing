package com.rr.CPing.Activities;

import android.app.NotificationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rr.CPing.Handlers.BottomSheetHandler;
import com.rr.CPing.Model.AlarmIdClass;
import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.Utils.MyProperties;

import java.util.ArrayList;
import java.util.Calendar;

public class TimePassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_pass);

        MyProperties.getInstance().ringtone.stop();

        String action = getIntent().getStringExtra("action");

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(getIntent().getIntExtra("id", 0));

        String contestName = getIntent().getStringExtra("contestName");

        ArrayList<AlarmIdClass> idClassArrayList = SharedPrefConfig.readInIdsOfReminderContests(this);
        int index = getIndexFromList(idClassArrayList, getIntent().getStringExtra("contestName"));
        AlarmIdClass alarmIdClass = idClassArrayList.get(index);

        if (!alarmIdClass.isGoogleReminderSet()) idClassArrayList.remove(index);
        else idClassArrayList.get(index).setInAppReminderSet(false);

        SharedPrefConfig.writeInIdsOfReminderContests(this, idClassArrayList);

        if (action.equals("snooze")) {

            if (Math.abs(alarmIdClass.getStartTime() - System.currentTimeMillis()) / 60000 <= 5) {
                Toast.makeText(this, "This contest is going to start in less than 5 minutes!",
                        Toast.LENGTH_SHORT).show();
            } else {
                alarmIdClass.setAlarmSetTime(System.currentTimeMillis() / 1000);

                int idx = getIndexFromList(idClassArrayList, contestName);
                if (idx == -1) idClassArrayList.add(alarmIdClass);
                else idClassArrayList.get(idx).setInAppReminderSet(true);

                SharedPrefConfig.writeInIdsOfReminderContests(this, idClassArrayList);

                Toast.makeText(this, "Snoozed for 5 minutes!", Toast.LENGTH_SHORT).show();

                new BottomSheetHandler().setNotification(this, -5, contestName, Calendar.getInstance(), System.currentTimeMillis() / 1000, true, getIntent().getStringExtra("ProperStartTime"));
            }
        }
        finish();
    }

    private int getIndexFromList(ArrayList<AlarmIdClass> currentList, String contestName) {
        for (int i = 0; i < currentList.size(); ++i) {
            if (currentList.get(i).getContestNameAsID().equals(contestName)) return i;
        }
        return -1;
    }
}