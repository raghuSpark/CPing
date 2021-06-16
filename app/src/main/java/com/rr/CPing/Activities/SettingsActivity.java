package com.rr.CPing.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.Utils.NetworkChangeListener;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    private TextView platFormsListTextView, hiddenContestsTextView;
    private RadioGroup themeRadioGroup;
    private EditText appUsernameEditText;

    @SuppressLint({"ResourceType", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppTheme();
        setContentView(R.layout.activity_settings);

        Toolbar dashBoardToolbar = findViewById(R.id.settings_page_toolbar);
        setSupportActionBar(dashBoardToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
        dashBoardToolbar.setTitleTextColor(getResources().getColor(R.color.fontColor, null));

        dashBoardToolbar.setNavigationIcon(R.drawable.ic_back_button);
        dashBoardToolbar.setNavigationOnClickListener(v -> BackPressed());

        findViewByIds();

        switch (SharedPrefConfig.readAppTheme(this)) {
            case -1:
                // System Default
                themeRadioGroup.check(R.id.system_default_theme_radio_button);
                break;
            case 0:
                // Light Theme
                themeRadioGroup.check(R.id.light_theme_radio_button);
                break;
            case 1:
                // Dark Theme
                themeRadioGroup.check(R.id.dark_theme_radio_button);
                break;
        }

        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.system_default_theme_radio_button:
                    // System Default
                    SharedPrefConfig.writeAppTheme(SettingsActivity.this, -1);
                    break;
                case R.id.light_theme_radio_button:
                    // Light Theme
                    SharedPrefConfig.writeAppTheme(SettingsActivity.this, 0);
                    break;
                case R.id.dark_theme_radio_button:
                    // Dark Theme
                    SharedPrefConfig.writeAppTheme(SettingsActivity.this, 1);
                    break;
            }
            setAppTheme();
        });

        appUsernameEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) appUsernameEditText.clearFocus();
            return false;
        });

        appUsernameEditText.setText(SharedPrefConfig.readAppUserName(this));

        appUsernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                SharedPrefConfig.writeAppUserName(SettingsActivity.this, s.toString());
            }
        });

        platFormsListTextView.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsNextActivity.class);
            intent.setAction("Platforms");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });

        hiddenContestsTextView.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsNextActivity.class);
            intent.setAction("Hidden Contests");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    view.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        BackPressed();
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    private void BackPressed() {
        if (appUsernameEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "How should I call you?", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }
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

    private void findViewByIds() {
        platFormsListTextView = findViewById(R.id.platforms_list_text_view);
        hiddenContestsTextView = findViewById(R.id.hidden_contests_text_view);
        themeRadioGroup = findViewById(R.id.theme_radio_group);
        appUsernameEditText = findViewById(R.id.editTextUserName);
    }
}