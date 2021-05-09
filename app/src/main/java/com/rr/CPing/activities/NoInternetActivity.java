package com.rr.CPing.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rr.CPing.R;

public class NoInternetActivity extends AppCompatActivity {

    private TextView continueWithOldDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        continueWithOldDate = findViewById(R.id.continue_with);
        continueWithOldDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoInternetActivity.this, MainActivity.class));
                finish();
            }
        });

    }
}