package com.raghu.CPing.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raghu.CPing.classes.PlatformListItem;
import com.raghu.CPing.R;
import com.raghu.CPing.adapters.PlatformAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private PlatformAdapter adapter;
    private ListView mListView;
    private SharedPreferences sharedPreferences;
    private ArrayList<PlatformListItem> platformNamesList;
    private EditText appUsernameEditText;
    private Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mListView = findViewById(R.id.listview);
        appUsernameEditText = findViewById(R.id.editTextUserName);
        saveBtn = findViewById(R.id.saveBtn);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appUsernameEditText.setText(sharedPreferences.getString("appUsername", ""));
        saveBtn.setEnabled(sharedPreferences.getInt("count", 0) != 0);

        appUsernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                sharedPreferences.edit().putString("appUsername", s.toString()).apply();
            }
        });

        if(sharedPreferences.getBoolean("isFirstTime",true)){
            saveBtn.setVisibility(View.VISIBLE);
            sharedPreferences.edit().putBoolean("isFirstTime", false).apply();
            loadFirstTimeData();
        }else{
            loadData();
        }

        adapter = new PlatformAdapter(this, platformNamesList);
        mListView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(platformNamesList.get(position).isUsernameAllowed()){
                    builder.setTitle(platformNamesList.get(position).getPlatformName()+" Username");
                    View viewInflated = getLayoutInflater().inflate(R.layout.username_dialog, findViewById(android.R.id.content), false);
                    final EditText input = viewInflated.findViewById(R.id.edit_username);
                    if(platformNamesList.get(position).isEnabled()) input.setText(platformNamesList.get(position).getUsername());
                    builder.setView(viewInflated);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            String usernameInputString = input.getText().toString();
                            if(usernameInputString.contains(" "))
                                Toast.makeText(SettingsActivity.this, "Input contains white spaces", Toast.LENGTH_SHORT).show();
                            else{
                                if(usernameInputString.isEmpty())
                                    adapter.setSelectedIndex(position, "", saveBtn);
                                else
                                    checkValidUsername(platformNamesList.get(position).getPlatformName().toLowerCase(), usernameInputString, position);
                            }
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }else{
                    adapter.setSelectedIndex(position, "", saveBtn);
                }
//                saveBtn.setEnabled(sharedPreferences.getInt("count", 0) != 0);
            }
        });
    }

    private void checkValidUsername(String platform, String username, int position) {
        String url = "https://competitive-coding-api.herokuapp.com/api/"+platform+"/"+username;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.e("response", response.getString("status"));
                    if(response.getString("status").equals("Success")){
                        adapter.setSelectedIndex(position, username, saveBtn);
//                        saveBtn.setEnabled(sharedPreferences.getInt("count", 0) != 0);
                    }else
                        Toast.makeText(SettingsActivity.this, "Invalid Username", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("response", Objects.requireNonNull(error.getMessage()));
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void loadData(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString("platformNames", null);
        Type type = new TypeToken<ArrayList<PlatformListItem>>() {}.getType();
        platformNamesList = gson.fromJson(json, type);
    }

    private void loadFirstTimeData(){
        platformNamesList = new ArrayList<>();
        platformNamesList.add(new PlatformListItem("CODEFORCES", "", false, true));
        platformNamesList.add(new PlatformListItem("CODECHEF", "", false, true));
        platformNamesList.add(new PlatformListItem("LEETCODE", "", false, true));
        platformNamesList.add(new PlatformListItem("ATCODER", "", false, true));
        platformNamesList.add(new PlatformListItem("TOPCODER", "", false, false));
        platformNamesList.add(new PlatformListItem("KICKSTART", "", false, false));
        platformNamesList.add(new PlatformListItem("HACKERRANK", "", false, false));
        platformNamesList.add(new PlatformListItem("HACKEREARTH", "", false, false));
        saveData();
    }

    private void saveData() {
        Gson gson = new Gson();
        String json = gson.toJson(platformNamesList);
        sharedPreferences.edit().putString("platformNames", json).apply();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}