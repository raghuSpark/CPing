package com.rr.CPing.activities;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.model.AlarmIdClass;
import com.rr.CPing.Handlers.BottomSheetHandler;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmRingingActivity extends AppCompatActivity {

    private Button dismissButton, snoozeButton;
    private TextView contestNameTextView,
            timeDescriptionTextView;

    private String contestName;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppTheme();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_alarm_ringing);

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

        findViewByIds();

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
        ringtone.setVolume(100);
        ringtone.play();

        contestName = getIntent().getStringExtra("ContestName");
        contestNameTextView.setText(contestName);

        ArrayList<AlarmIdClass> idClassArrayList =
                SharedPrefConfig.readInIdsOfReminderContests(this);
        int index = getIndexFromList(idClassArrayList, contestName);

        AlarmIdClass alarmIdClass = idClassArrayList.get(index);
        timeDescriptionTextView.setText("Starts in " + (System.currentTimeMillis() - idClassArrayList.get(index).getStartTime()) / 1000 + " minutes");

        if (!idClassArrayList.get(index).isGoogleReminderSet()) idClassArrayList.remove(index);
        else idClassArrayList.get(index).setInAppReminderSet(false);

        SharedPrefConfig.writeInIdsOfReminderContests(this, idClassArrayList);

        new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (Math.abs(alarmIdClass.getStartTime() - System.currentTimeMillis()) / 60000 <= 5) {
                    Toast.makeText(AlarmRingingActivity.this, "This contest is going to start in " +
                                    "less than 5 minutes!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    alarmIdClass.setAlarmSetTime(System.currentTimeMillis() / 1000);
                    idClassArrayList.add(alarmIdClass);

                    SharedPrefConfig.writeInIdsOfReminderContests(AlarmRingingActivity.this,
                            idClassArrayList);

                    Toast.makeText(AlarmRingingActivity.this, "Snoozed for 5 minutes!", Toast.LENGTH_SHORT).show();

                    new BottomSheetHandler().setNotification(AlarmRingingActivity.this, 5,
                            contestName, Calendar.getInstance(),
                            System.currentTimeMillis() / 1000, true);
                }
                ringtone.stop();
                finish();
            }
        };

        dismissButton.setOnClickListener(v -> {
            ringtone.stop();
            finish();
        });

        snoozeButton.setOnClickListener(v -> {
            if (Math.abs(alarmIdClass.getStartTime() - System.currentTimeMillis()) / 60000 <= 5) {
                Toast.makeText(this, "This contest is going to start in less than 5 minutes!",
                        Toast.LENGTH_SHORT).show();
            } else {
                alarmIdClass.setAlarmSetTime(System.currentTimeMillis() / 1000);
                idClassArrayList.add(alarmIdClass);

                SharedPrefConfig.writeInIdsOfReminderContests(this, idClassArrayList);
                Toast.makeText(this, "Snoozed for 5 minutes!", Toast.LENGTH_SHORT).show();
                new BottomSheetHandler().setNotification(AlarmRingingActivity.this, 5, contestName,
                        Calendar.getInstance(),
                        System.currentTimeMillis() / 1000, true);
            }
            ringtone.stop();
            finish();
        });
    }

    private void setAppTheme() {
        switch (SharedPrefConfig.readAppTheme(this)) {
            case -1:
                // System Default
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case 0:
                // Light Theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 1:
                // Dark Theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
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