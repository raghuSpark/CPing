package com.raghu.CPing.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.raghu.CPing.R;
import com.raghu.CPing.database.JSONResponseDBHandler;
import com.raghu.CPing.util.ContestDetails;

import org.json.JSONArray;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    private JSONResponseDBHandler jsonResponseDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        jsonResponseDBHandler = new JSONResponseDBHandler(this);

        getDetailsFromAPI();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        },2000);
    }

    public void getDetailsFromAPI() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                "https://kontests.net/api/v1/all", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
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
//                        Log.d("Json array response", obj.getString("name"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SplashActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }
}