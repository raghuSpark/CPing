package com.rr.CPing.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.model.AlarmIdClass;
import com.rr.CPing.util.ReminderBroadCast;

import java.util.ArrayList;

public class AlarmRingingActivity extends AppCompatActivity {

    private ImageView dismissButton, snoozeButton;
    private TextView contestNameTextView,
            timeDescriptionTextView;

    private String contestName;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);

        findViewByIds();
        contestName = getIntent().getStringExtra("ContestName");

        contestNameTextView.setText(contestName);

        ArrayList<AlarmIdClass> idClassArrayList =
                SharedPrefConfig.readInIdsOfReminderContests(this);
        int index = getIndexFromList(idClassArrayList, contestName);

        idClassArrayList.remove(index);
        SharedPrefConfig.writeInIdsOfReminderContests(this, idClassArrayList);

        AlarmIdClass alarmIdClass = idClassArrayList.get(index);

        timeDescriptionTextView.setText("Starts in " + (System.currentTimeMillis() - idClassArrayList.get(index).getStartTime()) / 1000 + " minutes");
        dismissButton.setOnClickListener(v -> {
            finish();
        });

        snoozeButton.setOnClickListener(v -> {
            alarmIdClass.setAlarmSetTime(System.currentTimeMillis() / 1000);
            idClassArrayList.add(alarmIdClass);
            setNotification();
            finish();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            setVisible(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this, null);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void setNotification() {
        Intent intent = new Intent(this, ReminderBroadCast.class);
        intent.putExtra("ContestName", contestName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                (int) System.currentTimeMillis() / 1000, intent, 0);
        AlarmManager alarmManager =
                (AlarmManager) this.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000,
                pendingIntent);
        Toast.makeText(this, "Snoozed for 5 minutes!", Toast.LENGTH_SHORT).show();
    }

    private int getIndexFromList(ArrayList<AlarmIdClass> currentList, String contestName) {
        for (int i = 0; i < currentList.size(); ++i) {
            if (currentList.get(i).getContestNameAsID().equals(contestName)) return i;
        }
        return -1;
    }

    private void findViewByIds() {
        dismissButton = findViewById(R.id.alarm_dismiss_button);
        snoozeButton = findViewById(R.id.alarm_snooze_button);
        contestNameTextView = findViewById(R.id.alarm_contest_name_text_View);
        timeDescriptionTextView = findViewById(R.id.alarm_time_description_text_View);
    }

}