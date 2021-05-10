package com.rr.CPing.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.classes.AtCoderUserDetails;
import com.rr.CPing.classes.CodeChefUserDetails;
import com.rr.CPing.classes.CodeForcesUserDetails;
import com.rr.CPing.classes.ContestDetails;
import com.rr.CPing.classes.LeetCodeUserDetails;
import com.rr.CPing.classes.PlatformListItem;
import com.rr.CPing.database.JSONResponseDBHandler;
import com.rr.CPing.util.CheckInternet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class SplashActivity extends AppCompatActivity {

    private JSONResponseDBHandler jsonResponseDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        jsonResponseDBHandler = new JSONResponseDBHandler(this);

        if (!CheckInternet.isConnectedToInternet(this)) {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, NoInternetActivity.class));
                finish();
            }, 2000);
        } else if (SharedPrefConfig.readIsFirstTime(this) || SharedPrefConfig.readPlatformsCount(this) < 1) {
            getContestDetailsFromAPI();

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, SettingsActivity.class));
                finish();
            }, 3000);
        } else {
            jsonResponseDBHandler.deleteAll();

            getContestDetailsFromAPI();

//            ArrayList<Boolean> enabledPlatforms = new ArrayList<>(4);

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

//            getAC();
//            getCC();
//            getCF();
//            getLC();

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }, 7000);
        }
    }

    private void getCC(String user_name) {
        Log.d("TAG", "getCC: ");
        String platform_name = "codechef";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://competitive-coding-api.herokuapp.com/api/" + platform_name + "/" + user_name, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayList<Integer> recentRatingsArrayList = new ArrayList<>();
                    JSONArray jsonArray = response.getJSONArray("contest_ratings");
                    int n = jsonArray.length();
                    for (int i = 0; i < n; ++i) {
                        recentRatingsArrayList.add(jsonArray.getJSONObject(i).getInt("rating"));
                    }
                    CodeChefUserDetails item = new CodeChefUserDetails(response.getInt("rating"),
                            response.getInt("highest_rating"),
                            response.getString("stars"),
                            recentRatingsArrayList);

                    SharedPrefConfig.writeInCodeChefPref(getApplicationContext(), item);

                } catch (JSONException e) {
                    Log.d("TAG", "onResponse: ERROR");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void getCF(String user_name) {
        Log.d("TAG", "getCF: ");
        String platform_name = "codeforces";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://competitive-coding-api.herokuapp.com/api/" + platform_name + "/" + user_name, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayList<String> recentRatingsArrayList = new ArrayList<>();
                    JSONArray jsonArray = response.getJSONArray("contests");
                    int n = jsonArray.length();
                    for (int i = 0; i < n; i++) {
                        recentRatingsArrayList.add(jsonArray.getJSONObject(i).getString("New Rating"));
                    }
                    Collections.reverse(recentRatingsArrayList);
                    CodeForcesUserDetails item = new CodeForcesUserDetails(response.getInt("rating"),
                            response.getInt("max rating"),
                            response.getString("rank"),
                            response.getString("max rank"),
                            recentRatingsArrayList);

                    SharedPrefConfig.writeInCodeForcesPref(getApplicationContext(), item);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void getLC(String user_name) {
        Log.d("TAG", "getLC: ");
        String platform_name = "leetcode";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://competitive-coding-api.herokuapp.com/api/" + platform_name + "/" + user_name, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    LeetCodeUserDetails item = new LeetCodeUserDetails(response.getString("ranking"),
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void getAC(String user_name) {
        Log.d("TAG", "getAC: ");
        String platform_name = "atcoder";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://competitive-coding-api.herokuapp.com/api/" + platform_name + "/" + user_name, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    AtCoderUserDetails item = new AtCoderUserDetails(response.getInt("rating"),
                            response.getInt("highest"),
                            response.getInt("rank"),
                            response.getString("level"));

                    SharedPrefConfig.writeInAtCoderPref(getApplicationContext(), item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
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
}