package com.rr.CPing.Activities;

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

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
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

        ArrayList<AlarmIdClass> currentList = SharedPrefConfig.readInIdsOfReminderContests(this);
        int i;
        boolean isPresent = false;
        for (i = 0; i < currentList.size(); i++) {
            if (currentList.get(i).getStartTime() >= System.currentTimeMillis() + 300000) {
                isPresent = true;
                break;
            }
        }
        if (isPresent && !currentList.isEmpty()) currentList.remove(i);
        SharedPrefConfig.writeInIdsOfReminderContests(this, currentList);

        jsonResponseDBHandler = new JSONResponseDBHandler(this);
        count = 1;
        if (SharedPrefConfig.readIsFirstTime(this) || SharedPrefConfig.readPlatformsCount(this) < 1) {
            getContestDetailsFromAPI(true);
            if (count <= 0) goToSettingsActivity();
        } else {
            jsonResponseDBHandler.deleteAll();

            getContestDetailsFromAPI(false);

            ArrayList<PlatformListItem> platformListItemArrayList = SharedPrefConfig.readPlatformsSelected(this);

            for (PlatformListItem platformListItem : platformListItemArrayList) {
                switch (platformListItem.getPlatformName()) {
                    case "AtCoder":
                    case "LeetCode":
                    case "CodeChef":
                    case "CodeForces":
                        count++;
                        break;
                }
            }

            for (PlatformListItem platformListItem : platformListItemArrayList) {
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
                goToMainActivity();
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

    private void goToSettingsActivity() {
        startActivity(new Intent(SplashActivity.this, SettingsActivity.class));
        finish();
    }

    private void goToMainActivity() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
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
                    goToMainActivity();
                }
                SharedPrefConfig.writeInAtCoderPref(getApplicationContext(), item);
            } catch (JSONException e) {
                e.printStackTrace();
                count--;
                if (count <= 0) {
                    goToSettingsActivity();
                }
            }
        }, error -> {
            Log.d(TAG, "onErrorResponse: " + error.getMessage());
            count--;
            if (count <= 0) {
                goToSettingsActivity();
            }
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
                    goToMainActivity();
                }
            } catch (JSONException e) {
                Log.d(TAG, "onResponse: " + e.getMessage());
                e.printStackTrace();
                count--;
                if (count <= 0) {
                    goToSettingsActivity();
                }
            }
        }, error -> {
            Log.d(TAG, "onErrorResponse: " + error.getMessage());
            count--;
            if (count <= 0) {
                goToSettingsActivity();
            }
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
                    goToMainActivity();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                count--;
                if (count <= 0) {
                    goToSettingsActivity();
                }
            }
        }, error -> {
            Log.d(TAG, "getCF: " + error.getMessage());
            count--;
            if (count <= 0) {
                goToSettingsActivity();
            }
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
                    goToMainActivity();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                count--;
                if (count <= 0) {
                    goToSettingsActivity();
                }
            }
        }, error -> {
            Log.d(TAG, "getLC: " + error.getMessage());
            count--;
            if (count <= 0) {
                goToSettingsActivity();
            }
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
                    if (isFirstTime) goToSettingsActivity();
                    else goToMainActivity();
                }
            } catch (Exception e) {
                e.printStackTrace();
                count--;
                if (count <= 0) {
                    goToSettingsActivity();
                }
            }
        }, error -> {
            Toast.makeText(SplashActivity.this, "Something went wrong!",
                    Toast.LENGTH_SHORT).show();
            if (count <= 0) {
                goToSettingsActivity();
            }
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