package com.rr.CPing.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.adapters.TabsAccessorAdapter;
import com.rr.CPing.util.NetworkChangeListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 2323;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private TabsAccessorAdapter dashBoardTabsAccessorAdapter;

//    private static void autoLaunchVivo(Context context) {
//        try {
//            Intent intent = new Intent();
//            intent.setComponent(new ComponentName("com.iqoo.secure",
//                    "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"));
//            context.startActivity(intent);
//        } catch (Exception e) {
//            try {
//                Intent intent = new Intent();
//                intent.setComponent(new ComponentName("com.vivo.permissionmanager",
//                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
//                context.startActivity(intent);
//            } catch (Exception ex) {
//                try {
//                    Intent intent = new Intent();
//                    intent.setClassName("com.iqoo.secure",
//                            "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager");
//                    context.startActivity(intent);
//                } catch (Exception exx) {
//                    ex.printStackTrace();
//                }
//            }
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//if the user already granted the permission or the API is below Android 10 no need to ask for permission

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                !Settings.canDrawOverlays(this)) {
            RequestPermission();
        }

//        if (Build.MANUFACTURER.equalsIgnoreCase("oppo")) {
//            initOPPO();
//        } else if (Build.MANUFACTURER.equalsIgnoreCase("vivo")) {
//            autoLaunchVivo(MainActivity.this);
//        } else if (Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) {
//            try {
//                Intent intent = new Intent();
//                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
//                startActivity(intent);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        Toolbar dashBoardToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(dashBoardToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Hello, " + SharedPrefConfig.readAppUserName(this));

        ViewPager dashBoardViewPager = findViewById(R.id.main_tabs_pager);
        dashBoardTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager(), 1, this);
        dashBoardTabsAccessorAdapter.notifyDataSetChanged();
        dashBoardViewPager.setAdapter(dashBoardTabsAccessorAdapter);

        TabLayout dashBoardTabLayout = findViewById(R.id.main_tabs);
        dashBoardTabLayout.setupWithViewPager(dashBoardViewPager);

        createNotificationChannel();
    }

    private void RequestPermission() {
        // Check if Android M or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + this.getPackageName()));
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
//                    PermissionDenied();
                    Toast.makeText(this, "Give permission to appear on top, to get full screen reminders...", Toast.LENGTH_SHORT).show();
                }
//                else {
//                    // Permission Granted-System will work
//                }

            }
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

//    private void initOPPO() {
//        try {
//            Intent i = new Intent(Intent.ACTION_MAIN);
//            i.setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.floatwindow.FloatWindowListActivity"));
//            startActivity(i);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.d("TAG", "error: " + e.getMessage());
//            try {
//                Intent intent = new Intent("action.coloros.safecenter.FloatWindowListActivity");
//                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.floatwindow.FloatWindowListActivity"));
//                startActivity(intent);
//            } catch (Exception ee) {
//                ee.printStackTrace();
//
//                try {
//                    Intent i = new Intent("com.coloros.safecenter");
//                    i.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity"));
//                    startActivity(i);
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                }
//            }
//        }
//    }
}