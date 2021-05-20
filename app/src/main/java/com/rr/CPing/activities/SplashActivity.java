package com.rr.CPing.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.database.JSONResponseDBHandler;
import com.rr.CPing.model.AtCoderUserDetails;
import com.rr.CPing.model.CodeChefUserDetails;
import com.rr.CPing.model.CodeForcesUserDetails;
import com.rr.CPing.model.ContestDetails;
import com.rr.CPing.model.LeetCodeUserDetails;
import com.rr.CPing.model.PlatformListItem;
import com.rr.CPing.util.NetworkChangeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private JSONResponseDBHandler jsonResponseDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logoBellImage = findViewById(R.id.logo_bell);
        logoBellImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));

        jsonResponseDBHandler = new JSONResponseDBHandler(this);

//        if (CheckInternet.isConnectedToInternet(this)) {
//            Handler handler = new Handler();
//            handler.postDelayed(() -> {
//                startActivity(new Intent(SplashActivity.this, NoInternetActivity.class));
//                finish();
//            }, 2000);
//        } else
        if (SharedPrefConfig.readIsFirstTime(this) || SharedPrefConfig.readPlatformsCount(this) < 1) {
            getContestDetailsFromAPI();

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, SettingsActivity.class));
                finish();
            }, 3000);
        } else {
            jsonResponseDBHandler.deleteAll();

            getContestDetailsFromAPI();

            ArrayList<PlatformListItem> platformListItemArrayList = SharedPrefConfig.readPlatformsSelected(this);

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

            new Handler().postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }, 7000);
        }
    }

    private void getAC(String user_name) {
        String platform_name = "atcoder";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://competitive-coding-api.herokuapp.com/api/" + platform_name + "/" + user_name, null, response -> {
            try {
                AtCoderUserDetails item;
                if (!response.getString("level").equals("NA")) {
                    item = new AtCoderUserDetails(
                            user_name,
                            response.getInt("rating"),
                            response.getInt("highest"),
                            response.getInt("rank"),
                            response.getString("level"));
                } else {
                    item = new AtCoderUserDetails(user_name,
                            response.getInt("rating"),
                            -1,
                            -1,
                            "NA");
                }
                SharedPrefConfig.writeInAtCoderPref(getApplicationContext(), item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.d(TAG, "onErrorResponse: " + error.getMessage()));
        requestQueue.add(jsonObjectRequest);
    }

    private void getCC(String user_name) {
        String platform_name = "codechef";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://competitive-coding-api.herokuapp.com/api/" + platform_name + "/" + user_name, null, response -> {
            try {
                ArrayList<Integer> recentRatingsArrayList = new ArrayList<>();
                JSONArray jsonArray = response.getJSONArray("contest_ratings");
                int n = jsonArray.length();
                for (int i = 0; i < n; ++i) {
                    recentRatingsArrayList.add(jsonArray.getJSONObject(i).getInt("rating"));
                }
                CodeChefUserDetails item = new CodeChefUserDetails(
                        user_name,
                        response.getInt("rating"),
                        response.getInt("highest_rating"),
                        response.getString("stars"),
                        recentRatingsArrayList);

                SharedPrefConfig.writeInCodeChefPref(getApplicationContext(), item);

            } catch (JSONException e) {
                Log.d(TAG, "onResponse: " + e.getMessage());
                e.printStackTrace();
            }
        }, error -> Log.d(TAG, "onErrorResponse: " + error.getMessage()));
        requestQueue.add(jsonObjectRequest);
    }

    private void getCF(String user_name) {
        String platform_name = "codeforces";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://competitive-coding-api.herokuapp.com/api/" + platform_name + "/" + user_name, null, response -> {
            try {
                ArrayList<String> recentRatingsArrayList = new ArrayList<>();
                JSONArray jsonArray = response.getJSONArray("contests");
                int n = jsonArray.length();
                for (int i = 0; i < n; i++) {
                    recentRatingsArrayList.add(jsonArray.getJSONObject(i).getString("New Rating"));
                }
                Collections.reverse(recentRatingsArrayList);
                CodeForcesUserDetails item = new CodeForcesUserDetails(user_name,
                        response.getInt("rating"),
                        response.getInt("max rating"),
                        response.getString("rank"),
                        response.getString("max rank"),
                        recentRatingsArrayList);

                SharedPrefConfig.writeInCodeForcesPref(getApplicationContext(), item);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.d(TAG, "getCF: " + error.getMessage()));
        requestQueue.add(jsonObjectRequest);
    }

    private void getLC(String user_name) {
        String platform_name = "leetcode";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://competitive-coding-api.herokuapp.com/api/" + platform_name + "/" + user_name, null, response -> {
            try {
                LeetCodeUserDetails item = new LeetCodeUserDetails(user_name,
                        response.getString("ranking"),
                        response.getString("total_problems_solved"),
                        response.getString("acceptance_rate"),
                        response.getString("easy_questions_solved"),
                        response.getString("total_easy_questions"),
                        response.getString("medium_questions_solved"),
                        response.getString("total_medium_questions"),
                        response.getString("hard_questions_solved"),
                        response.getString("total_hard_questions"));

                SharedPrefConfig.writeInLeetCodePref(getApplicationContext(), item);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.d("TAG", "getLC: " + error.getMessage()));
        requestQueue.add(jsonObjectRequest);
    }

    private void getContestDetailsFromAPI() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(SplashActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show());
        requestQueue.add(jsonArrayRequest);
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
}