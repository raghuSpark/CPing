package com.rr.CPing.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.adapters.TabsAccessorAdapter;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public FrameLayout internetConnectionFrameLayout;
    private TabsAccessorAdapter dashBoardTabsAccessorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        internetConnectionFrameLayout = findViewById(R.id.internet_connection_frame);

        // TODO: A condition has to be checked.
        internetConnectionFrameLayout.setVisibility(View.GONE);

        Toolbar dashBoardToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(dashBoardToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Hello, "+ SharedPrefConfig.readAppUserName(this));

        ViewPager dashBoardViewPager = findViewById(R.id.main_tabs_pager);
        dashBoardTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager(), 1,this);
        dashBoardTabsAccessorAdapter.notifyDataSetChanged();
        dashBoardViewPager.setAdapter(dashBoardTabsAccessorAdapter);

        TabLayout dashBoardTabLayout = findViewById(R.id.main_tabs);
        dashBoardTabLayout.setupWithViewPager(dashBoardViewPager);
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
            startActivity(new Intent(MainActivity.this,SettingsActivity.class));
            finish();
        }
        return true;
    }

    @Override
    protected void onStart() {
        dashBoardTabsAccessorAdapter.notifyDataSetChanged();
        Objects.requireNonNull(getSupportActionBar()).setTitle("@"+ SharedPrefConfig.readAppUserName(this));
        super.onStart();
    }

    @Override
    protected void onResume() {
        dashBoardTabsAccessorAdapter.notifyDataSetChanged();
        Objects.requireNonNull(getSupportActionBar()).setTitle("@"+ SharedPrefConfig.readAppUserName(this));
        super.onResume();
    }

    @Override
    protected void onRestart() {
        dashBoardTabsAccessorAdapter.notifyDataSetChanged();
        super.onRestart();
    }
}