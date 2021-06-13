package com.rr.CPing.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.rr.CPing.Adapters.TabsAccessorAdapter;
import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.Utils.NetworkChangeListener;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 2323;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private TabsAccessorAdapter dashBoardTabsAccessorAdapter;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppTheme();
        setContentView(R.layout.activity_main);

        AppearOnTopPermission();

        Toolbar dashBoardToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(dashBoardToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("@" + SharedPrefConfig.readAppUserName(this));
        dashBoardToolbar.setTitleTextColor(getResources().getColor(R.color.fontColor, null));

        ViewPager dashBoardViewPager = findViewById(R.id.main_tabs_pager);
        dashBoardTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager(), 1, this);
        dashBoardTabsAccessorAdapter.notifyDataSetChanged();
        dashBoardViewPager.setAdapter(dashBoardTabsAccessorAdapter);

        TabLayout dashBoardTabLayout = findViewById(R.id.main_tabs);
        dashBoardTabLayout.setupWithViewPager(dashBoardViewPager);

        createNotificationChannel();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "Permission is required to show full screen reminders.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.dash_board_menu) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        return true;
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        dashBoardTabsAccessorAdapter.notifyDataSetChanged();
        Objects.requireNonNull(getSupportActionBar()).setTitle("@" + SharedPrefConfig.readAppUserName(this));
        super.onStart();
    }

    @Override
    protected void onResume() {
        dashBoardTabsAccessorAdapter.notifyDataSetChanged();
        Objects.requireNonNull(getSupportActionBar()).setTitle("@" + SharedPrefConfig.readAppUserName(this));
        super.onResume();
    }

    @Override
    protected void onRestart() {
        dashBoardTabsAccessorAdapter.notifyDataSetChanged();
        super.onRestart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    private void AppearOnTopPermission() {
        // If the user already granted the permission to appear on top or the API is below Android 10 no need to ask for permission

        if (!SharedPrefConfig.readOverlayDoNotAskAgain(this) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.appear_on_top_permission_dialog, null);
            builder.setView(view);

            view.findViewById(R.id.overlay_allow_permission).setOnClickListener(v -> {
                RequestAppearOnTopPermission();
                dialog.cancel();
                AutoStartPermission();
            });

            view.findViewById(R.id.overlay_deny_permission).setOnClickListener(v -> {
                Toast.makeText(MainActivity.this, "Permission is required to show full screen reminders.", Toast.LENGTH_SHORT).show();
                dialog.cancel();
                AutoStartPermission();
            });

            view.findViewById(R.id.overlay_deny_and_do_not_ask_permission).setOnClickListener(v -> {
                Toast.makeText(MainActivity.this, "Permission is required to show full screen reminders.", Toast.LENGTH_SHORT).show();
                SharedPrefConfig.writeOverlayDoNotAskAgain(MainActivity.this, true);
                dialog.cancel();
                AutoStartPermission();
            });

            dialog = builder.create();
            dialog.getWindow().setWindowAnimations(R.style.PopupDialogAnimation);
            dialog.setCancelable(false);
            dialog.show();
        } else AutoStartPermission();
    }

    private void AutoStartPermission() {
        if (!SharedPrefConfig.readAutoStartDoNotAskAgain(this)) {
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer) || "oppo".equalsIgnoreCase(manufacturer) || "vivo".equalsIgnoreCase(manufacturer) || "Letv".equalsIgnoreCase(manufacturer) || "Honor".equalsIgnoreCase(manufacturer)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = getLayoutInflater().inflate(R.layout.auto_start_permission_popup_dialog, null);
                builder.setView(view);

                view.findViewById(R.id.allow_auto_start_permission).setOnClickListener(v -> {
                    SharedPrefConfig.writeAutoStartDoNotAskAgain(MainActivity.this, true);
                    RequestAutoStartPermission();
                    dialog.cancel();
                });

                view.findViewById(R.id.deny_auto_start_permission).setOnClickListener(v -> {
                    Toast.makeText(MainActivity.this, "Permission is required to keep your alarms alive after device restart!", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                });

                view.findViewById(R.id.auto_start_deny_and_do_not_ask_permission).setOnClickListener(v -> {
                    Toast.makeText(MainActivity.this, "Permission is required to keep your alarms alive after device restart!", Toast.LENGTH_SHORT).show();
                    SharedPrefConfig.writeAutoStartDoNotAskAgain(MainActivity.this, true);
                    dialog.cancel();
                });

                dialog = builder.create();
                dialog.getWindow().setWindowAnimations(R.style.PopupDialogAnimation);
                dialog.setCancelable(false);
                dialog.show();
            }
        }
    }

    private void RequestAutoStartPermission() {
        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }

            @SuppressLint("QueryPermissionsNeeded")
            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("exc", e.getMessage());
        }
    }

    private void RequestAppearOnTopPermission() {
        // Check if Android M or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + this.getPackageName()));
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("notify_contest", "Contest Reminder", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("This notification channel is used to notify user.");
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);

            NotificationManager notificationManager = Objects.requireNonNull(getSystemService(NotificationManager.class));
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}