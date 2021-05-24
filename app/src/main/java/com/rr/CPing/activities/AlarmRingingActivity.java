package com.rr.CPing.activities;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.model.AlarmIdClass;
import com.rr.CPing.model.BottomSheetHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class AlarmRingingActivity extends AppCompatActivity {

    private Button dismissButton, snoozeButton;
    private TextView contestNameTextView,
            timeDescriptionTextView;

    private String contestName;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);

        findViewByIds();

        MediaPlayer player = MediaPlayer.create(this,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        player.start();

        contestName = getIntent().getStringExtra("ContestName");
        contestNameTextView.setText(contestName);

        ArrayList<AlarmIdClass> idClassArrayList =
                SharedPrefConfig.readInIdsOfReminderContests(this);
        int index = getIndexFromList(idClassArrayList, contestName);

        AlarmIdClass alarmIdClass = idClassArrayList.get(index);
        timeDescriptionTextView.setText("Starts in " + (System.currentTimeMillis() - idClassArrayList.get(index).getStartTime()) / 1000 + " minutes");

        idClassArrayList.remove(index);
        SharedPrefConfig.writeInIdsOfReminderContests(this, idClassArrayList);

        dismissButton.setOnClickListener(v -> {
            player.stop();
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
                new BottomSheetHandler().setNotification(5, contestName, Calendar.getInstance(),
                        System.currentTimeMillis(), true);
            }
            player.stop();
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

        new Handler().postDelayed(() -> {
            alarmIdClass.setAlarmSetTime(System.currentTimeMillis() / 1000);
            idClassArrayList.add(alarmIdClass);

            SharedPrefConfig.writeInIdsOfReminderContests(AlarmRingingActivity.this,
                    idClassArrayList);

            player.stop();
            Toast.makeText(AlarmRingingActivity.this, "Snoozed for 5 minutes!", Toast.LENGTH_SHORT).show();

            new BottomSheetHandler().setNotification(5, contestName, Calendar.getInstance(),
                    System.currentTimeMillis() / 1000, true);
            finish();
        }, 60000);
    }

//    private void setNotification() {
//        Intent intent = new Intent(this, ReminderBroadCast.class);
//        intent.putExtra("ContestName", contestName);
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
//                (int) System.currentTimeMillis() / 1000, intent, 0);
//        AlarmManager alarmManager =
//                (AlarmManager) this.getSystemService(ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000,
//                pendingIntent);
//    }

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