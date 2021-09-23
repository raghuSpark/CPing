package com.rr.CPing.Activities;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rr.CPing.Model.AlarmIdClass;
import com.rr.CPing.Model.AtCoderUserDetails;
import com.rr.CPing.Model.CodeChefUserDetails;
import com.rr.CPing.Model.CodeForcesUserDetails;
import com.rr.CPing.Model.ContestDetails;
import com.rr.CPing.Model.HiddenContestsClass;
import com.rr.CPing.Model.LeetCodeUserDetails;
import com.rr.CPing.Model.PlatformListItem;
import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.Utils.NetworkChangeListener;
import com.rr.CPing.database.JSONResponseDBHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    int count = 0;
    private JSONResponseDBHandler jsonResponseDBHandler;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAppTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        requestQueue = Volley.newRequestQueue(this);

        NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        manager.cancelAll();

        ImageView logoBellImage = findViewById(R.id.logo_bell);
        logoBellImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_logo));

        // Removing finished contests from stored id's
        ArrayList<AlarmIdClass> currentAlarmIdsList = SharedPrefConfig.readInIdsOfReminderContests(this),
                newAlarmIdsList = new ArrayList<>();
        for (AlarmIdClass alarmIdClass : currentAlarmIdsList) {
            if (alarmIdClass.getAlarmSetTime() > System.currentTimeMillis()) {
                newAlarmIdsList.add(alarmIdClass);
            }
        }
        SharedPrefConfig.writeInIdsOfReminderContests(this, newAlarmIdsList);

        // Removing finished contests from the list of deleted contests
        ArrayList<HiddenContestsClass> hiddenContestsArrayList = SharedPrefConfig.readInHiddenContests(this),
                newHiddenContestsArrayList = new ArrayList<>();
        for (HiddenContestsClass hiddenContest : hiddenContestsArrayList) {
            if (hiddenContest.getContestEndTime() > System.currentTimeMillis()) {
                newHiddenContestsArrayList.add(hiddenContest);
            }
        }
        SharedPrefConfig.writeInHiddenContests(this, newHiddenContestsArrayList);

        jsonResponseDBHandler = new JSONResponseDBHandler(this);
        jsonResponseDBHandler.deleteAll();
        count = 1;
        if (SharedPrefConfig.readIsFirstTime(this) || SharedPrefConfig.readPlatformsCount(this) < 1) {
            getContestDetailsFromAPI(true);
            if (count <= 0) goToSettingsNextActivity();
        } else {
            getContestDetailsFromAPI(false);

            ArrayList<PlatformListItem> platformListItemArrayList = SharedPrefConfig.readPlatformsSelected(this);

            for (PlatformListItem platformListItem : platformListItemArrayList) {
                if (platformListItem.isEnabled())
                    if (platformListItem.getPlatformName().equals("AtCoder") || platformListItem.getPlatformName().equals("LeetCode") || platformListItem.getPlatformName().equals("CodeChef") || platformListItem.getPlatformName().equals("CodeForces"))
                        count++;
            }

            for (PlatformListItem platformListItem : platformListItemArrayList) {
                if (platformListItem.isEnabled())
                    switch (platformListItem.getPlatformName()) {
                        case "AtCoder":
                            getAC(platformListItem.getUserName());
                            break;
                        case "CodeChef":
                            getCC(platformListItem.getUserName());
                            break;
                        case "CodeForces":
                            getCF(platformListItem.getUserName());
                            break;
                        case "LeetCode":
                            getLC(platformListItem.getUserName());
                            break;
                    }
            }
            if (count <= 0) {
                if (SharedPrefConfig.readAppUserName(this).isEmpty()) {
                    Toast.makeText(this, "How should we call you?", Toast.LENGTH_SHORT).show();
                    goToSettingsActivity();
                } else {
                    goToMainActivity();
                }
            }
        }
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

    private void goToSettingsNextActivity() {
        Intent intent = new Intent(SplashActivity.this, SettingsNextActivity.class);
        intent.setAction("Platforms");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void goToSettingsActivity() {
        startActivity(new Intent(SplashActivity.this, SettingsActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void goToMainActivity() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void getAC(String user_name) {
        String platform_name = "atcoder";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://cping-api.herokuapp.com/api/" + platform_name + "/" + user_name, null, response -> {
            try {
                ArrayList<Integer> recentRatingsArrayList = new ArrayList<>();
                JSONArray jsonArray = response.getJSONArray("contest_ratings");
                int n = jsonArray.length();
                for (int i = 0; i < n; ++i) {
                    recentRatingsArrayList.add(jsonArray.getInt(i));
                }
                AtCoderUserDetails item;
                item = new AtCoderUserDetails(
                        user_name,
                        response.getInt("rating"),
                        response.getInt("highest"),
                        response.getInt("rank"),
                        response.getString("level"),
                        recentRatingsArrayList);
                count--;
                if (count <= 0) {
                    if (SharedPrefConfig.readAppUserName(this).isEmpty()) {
                        Toast.makeText(this, "How should we call you?", Toast.LENGTH_SHORT).show();
                        goToSettingsActivity();
                    } else {
                        goToMainActivity();
                    }
                }
                SharedPrefConfig.writeInAtCoderPref(getApplicationContext(), item);
            } catch (JSONException e) {
                e.printStackTrace();
                count--;
                if (count <= 0) {
                    if (SharedPrefConfig.readAppUserName(this).isEmpty()) {
                        Toast.makeText(this, "How should we call you?", Toast.LENGTH_SHORT).show();
                        goToSettingsActivity();
                    } else {
                        goToMainActivity();
                    }
                }
            }
        }, error -> {
            Log.e(TAG, "onErrorResponse: " + error.getMessage());
            getAC(user_name);
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void getCC(String user_name) {
        String platform_name = "codechef";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://cping-api.herokuapp.com/api/" + platform_name + "/" + user_name, null, response -> {
            try {
                ArrayList<Integer> recentRatingsArrayList = new ArrayList<>();
                JSONArray jsonArray = response.getJSONArray("contest_ratings");
                int n = jsonArray.length();
                for (int i = 0; i < n; ++i) {
                    recentRatingsArrayList.add(jsonArray.getInt(i));
                }
                CodeChefUserDetails item = new CodeChefUserDetails(
                        user_name,
                        response.getInt("rating"),
                        response.getInt("highest_rating"),
                        response.getString("stars"),
                        recentRatingsArrayList);

                SharedPrefConfig.writeInCodeChefPref(getApplicationContext(), item);
                count--;
                if (count <= 0) {
                    if (SharedPrefConfig.readAppUserName(this).isEmpty()) {
                        Toast.makeText(this, "How should we call you?", Toast.LENGTH_SHORT).show();
                        goToSettingsActivity();
                    } else {
                        goToMainActivity();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                count--;
                if (count <= 0) {
                    if (SharedPrefConfig.readAppUserName(this).isEmpty()) {
                        Toast.makeText(this, "How should we call you?", Toast.LENGTH_SHORT).show();
                        goToSettingsActivity();
                    } else {
                        goToMainActivity();
                    }
                }
            }
        }, error -> {
            Log.e(TAG, "getCC: " + error.getMessage());
            getCC(user_name);
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void getCF(String user_name) {
        String platform_name = "codeforces";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://cping-api.herokuapp.com/api/" + platform_name + "/" + user_name, null, response -> {
            try {
                ArrayList<Integer> recentRatingsArrayList = new ArrayList<>();
                JSONArray jsonArray = response.getJSONArray("contests");
                int n = jsonArray.length();
                for (int i = 0; i < n; i++) {
                    recentRatingsArrayList.add(jsonArray.getInt(i));
                }
                Collections.reverse(recentRatingsArrayList);
                CodeForcesUserDetails item = new CodeForcesUserDetails(user_name,
                        response.getInt("rating"),
                        response.getInt("max rating"),
                        response.getString("rank"),
                        response.getString("max rank"),
                        recentRatingsArrayList);

                SharedPrefConfig.writeInCodeForcesPref(getApplicationContext(), item);
                count--;
                if (count == 0) {
                    if (SharedPrefConfig.readAppUserName(this).isEmpty()) {
                        Toast.makeText(this, "How should we call you?", Toast.LENGTH_SHORT).show();
                        goToSettingsActivity();
                    } else {
                        goToMainActivity();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                count--;
                if (count <= 0) {
                    if (SharedPrefConfig.readAppUserName(this).isEmpty()) {
                        Toast.makeText(this, "How should we call you?", Toast.LENGTH_SHORT).show();
                        goToSettingsActivity();
                    } else {
                        goToMainActivity();
                    }
                }
            }
        }, error -> {
            Log.d(TAG, "getCF: " + error.getMessage());
            getCF(user_name);
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void getLC(String user_name) {
        String platform_name = "leetcode";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://cping-api.herokuapp.com/api/" + platform_name + "/" + user_name, null, response -> {
            try {
                LeetCodeUserDetails item = new LeetCodeUserDetails(user_name,
                        response.getString("total_problems_solved"),
                        response.getString("acceptance_rate"),
                        response.getString("easy_questions_solved"),
                        response.getString("total_easy_questions"),
                        response.getString("medium_questions_solved"),
                        response.getString("total_medium_questions"),
                        response.getString("hard_questions_solved"),
                        response.getString("total_hard_questions"));

                SharedPrefConfig.writeInLeetCodePref(getApplicationContext(), item);
                count--;
                if (count == 0) {
                    if (SharedPrefConfig.readAppUserName(this).isEmpty()) {
                        Toast.makeText(this, "How should we call you?", Toast.LENGTH_SHORT).show();
                        goToSettingsActivity();
                    } else {
                        goToMainActivity();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                count--;
                if (count <= 0) {
                    if (SharedPrefConfig.readAppUserName(this).isEmpty()) {
                        Toast.makeText(this, "How should we call you?", Toast.LENGTH_SHORT).show();
                        goToSettingsActivity();
                    } else {
                        goToMainActivity();
                    }
                }
            }
        }, error -> {
            Log.d(TAG, "getLC: " + error.getMessage());
            getLC(user_name);
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void getContestDetailsFromAPI(boolean isFirstTime) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                "https://kontests.net/api/v1/all", null, response -> {
            try {
                for (int i = 0; i < response.length(); ++i) {
                    JSONObject obj = response.getJSONObject(i);
                    ContestDetails item = new ContestDetails(obj.getString("site"),
                            obj.getString("name"),
                            obj.getString("url"),
                            obj.getInt("duration"),
                            obj.getString("start_time"),
                            obj.getString("end_time"),
                            obj.getString("in_24_hours"),
                            obj.getString("status")
                    );
                    jsonResponseDBHandler.addItem(item);
                }
                count--;
                if (count <= 0) {
                    if (isFirstTime) goToSettingsNextActivity();
                    else {
                        if (SharedPrefConfig.readAppUserName(this).isEmpty()) {
                            Toast.makeText(this, "How should we call you?", Toast.LENGTH_SHORT).show();
                            goToSettingsActivity();
                        } else {
                            goToMainActivity();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                getContestDetailsFromAPI(isFirstTime);
            }
        }, error -> {
            Toast.makeText(SplashActivity.this, "Something went wrong! Check your network...",
                    Toast.LENGTH_SHORT).show();
            getContestDetailsFromAPI(isFirstTime);
        });
        requestQueue.add(jsonArrayRequest);
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
}