package com.rr.CPing.activities;

import android.app.KeyguardManager;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rr.CPing.R;

public class AlarmRingingActivity extends AppCompatActivity {

    private ImageView dismissButton, snoozeButton, snoozeTimeIncreaseButton,
            snoozeTimeDecreaseButton;
    private TextView snoozeTimeTextView, platformNameTextView, contestNameTextView,
            timeDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);

        findViewByIds();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
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

    private void findViewByIds() {
        dismissButton = findViewById(R.id.alarm_dismiss_button);
        snoozeButton = findViewById(R.id.alarm_snooze_button);
        snoozeTimeIncreaseButton = findViewById(R.id.alarm_snooze_time_increase_button);
        snoozeTimeDecreaseButton = findViewById(R.id.alarm_snooze_time_decrease_button);
        snoozeTimeTextView = findViewById(R.id.alarm_snooze_time_text_view);
        contestNameTextView = findViewById(R.id.alarm_contest_name_text_View);
        platformNameTextView = findViewById(R.id.alarm_platform_name_text_View);
        timeDescriptionTextView = findViewById(R.id.alarm_time_description_text_View);
    }

}