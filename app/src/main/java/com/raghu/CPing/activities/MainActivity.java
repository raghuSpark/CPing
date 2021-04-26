package com.raghu.CPing.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.raghu.CPing.R;
import com.raghu.CPing.database.JSONResponseDBHandler;
import com.raghu.CPing.util.TabsAccessorAdapter;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Toolbar dashBoardToolbar;
    private ViewPager dashBoardViewPager;
    private TabLayout dashBoardTabLayout;
    private TabsAccessorAdapter dashBoardTabsAccessorAdapter;

    private JSONResponseDBHandler jsonResponseDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dashBoardToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(dashBoardToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Hello, Raghu");

        dashBoardViewPager = findViewById(R.id.main_tabs_pager);
        dashBoardTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager(),1);
        dashBoardViewPager.setAdapter(dashBoardTabsAccessorAdapter);

        dashBoardTabLayout = findViewById(R.id.main_tabs);
        dashBoardTabLayout.setupWithViewPager(dashBoardViewPager);

        jsonResponseDBHandler = new JSONResponseDBHandler(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.dash_board_menu) {
            Toast.makeText(this, "To be implemented!", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}