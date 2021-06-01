package com.rr.CPing.Activities;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.rr.CPing.Handlers.BottomSheetHandler;
import com.rr.CPing.Model.AlarmIdClass;
import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;

import java.util.ArrayList;

public class AlarmRingingActivity extends AppCompatActivity {

    private Button dismissButton, snoozeButton;
    private TextView contestNameTextView,
            timeDescriptionTextView;

    private String contestName, properStartTime;
    private long alarmSetTime;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppTheme();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                (int) WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
        setContentView(R.layout.activity_alarm_ringing);

        ImageView alarmBell = findViewById(R.id.alarmBell);
        alarmBell.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_alarm));

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

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
        if (ringtone == null || uri == null) {
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            ringtone = RingtoneManager.getRingtone(this, uri);
        }
        ringtone.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());
        ringtone.setVolume(100);
        ringtone.setLooping(true);
        ringtone.play();

        contestName = getIntent().getStringExtra("ContestName");
        contestNameTextView.setText(contestName);

        properStartTime = getIntent().getStringExtra("ProperStartTime");

        alarmSetTime = getIntent().getLongExtra("alarmSetTime", 0);

        ArrayList<AlarmIdClass> idClassArrayList = SharedPrefConfig.readInIdsOfReminderContests(this);
        final int[] index = {getIndexFromList(idClassArrayList, contestName)};

        AlarmIdClass alarmIdClass = idClassArrayList.get(index[0]);
        timeDescriptionTextView.setText("Starts at: " + properStartTime);

        if (!alarmIdClass.isGoogleReminderSet()) idClassArrayList.remove(index[0]);
        else idClassArrayList.get(index[0]).setInAppReminderSet(false);

        SharedPrefConfig.writeInIdsOfReminderContests(this, idClassArrayList);

        Ringtone finalRingtone1 = ringtone;
        CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (Math.abs(alarmIdClass.getStartTime() - System.currentTimeMillis()) / 60000 <= 5) {
                    Toast.makeText(AlarmRingingActivity.this, "This contest is going to start in " +
                            "less than 5 minutes!", Toast.LENGTH_SHORT).show();
                } else {
                    alarmIdClass.setAlarmSetTime(System.currentTimeMillis() / 1000);

                    index[0] = getIndexFromList(idClassArrayList, contestName);
                    if (index[0] == -1) idClassArrayList.add(alarmIdClass);
                    else idClassArrayList.get(index[0]).setInAppReminderSet(true);

                    SharedPrefConfig.writeInIdsOfReminderContests(AlarmRingingActivity.this,
                            idClassArrayList);

                    Toast.makeText(AlarmRingingActivity.this, "Snoozed for 5 minutes!", Toast.LENGTH_SHORT).show();

                    new BottomSheetHandler().setNotification(AlarmRingingActivity.this, -5,
                            contestName, alarmSetTime,
                            System.currentTimeMillis() / 1000, properStartTime);
                }
                finalRingtone1.stop();
                finish();
            }
        }.start();

        Ringtone finalRingtone = ringtone;
        dismissButton.setOnClickListener(v -> {
            countDownTimer.cancel();
            finalRingtone.stop();
            finish();
        });

        Ringtone finalRingtone2 = ringtone;
        snoozeButton.setOnClickListener(v -> {
            if (Math.abs(alarmIdClass.getStartTime() - System.currentTimeMillis()) / 60000 <= 5) {
                Toast.makeText(this, "This contest is going to start in less than 5 minutes!", Toast.LENGTH_SHORT).show();
            } else {
                alarmIdClass.setAlarmSetTime(System.currentTimeMillis() / 1000);

                index[0] = getIndexFromList(idClassArrayList, contestName);
                if (index[0] == -1) idClassArrayList.add(alarmIdClass);
                else idClassArrayList.get(index[0]).setInAppReminderSet(true);

                SharedPrefConfig.writeInIdsOfReminderContests(this, idClassArrayList);

                Toast.makeText(this, "Snoozed for 5 minutes!", Toast.LENGTH_SHORT).show();

                new BottomSheetHandler().setNotification(AlarmRingingActivity.this, -5, contestName,
                        alarmSetTime, System.currentTimeMillis() / 1000, properStartTime);
            }
            countDownTimer.cancel();
            finalRingtone2.stop();
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