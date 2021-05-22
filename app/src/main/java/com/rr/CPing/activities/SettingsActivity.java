package com.rr.CPing.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.adapters.PlatformAdapter;
import com.rr.CPing.model.AtCoderUserDetails;
import com.rr.CPing.model.CodeChefUserDetails;
import com.rr.CPing.model.CodeForcesUserDetails;
import com.rr.CPing.model.LeetCodeUserDetails;
import com.rr.CPing.model.PlatformListItem;
import com.rr.CPing.util.NetworkChangeListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    private PlatformAdapter platformAdapter;
    private Button settingsSaveButton;
    private EditText appUsernameEditText;
    private ProgressBar settingsProgressBar;

    private ArrayList<PlatformListItem> platformNamesList;
    private ArrayList<Pair<String, String>> newlyAddedPlatforms = new ArrayList<>();

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        newlyAddedPlatforms = new ArrayList<>();

        Toolbar dashBoardToolbar = findViewById(R.id.settings_page_toolbar);
        setSupportActionBar(dashBoardToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
        getSupportActionBar().setHomeButtonEnabled(true);

        settingsSaveButton = findViewById(R.id.settings_save_button);
        ListView platformsListView = findViewById(R.id.settings_platforms_list_view);
        appUsernameEditText = findViewById(R.id.editTextUserName);
        settingsProgressBar = findViewById(R.id.settings_page_progress_bar);

        settingsProgressBar.setVisibility(View.GONE);
        settingsSaveButton.setVisibility(View.VISIBLE);

        settingsSaveButton.setOnClickListener(v -> {
            if (appUsernameEditText.getText().toString().isEmpty()) {
                Toast.makeText(SettingsActivity.this, "How should I call you?", Toast.LENGTH_SHORT).show();
            } else if (SharedPrefConfig.readPlatformsCount(SettingsActivity.this) == 0) {
                Toast.makeText(SettingsActivity.this, "No Platform is selected!", Toast.LENGTH_SHORT).show();
            } else {
                settingsSaveButton.setVisibility(View.GONE);
                settingsProgressBar.setVisibility(View.VISIBLE);

                if (SharedPrefConfig.readIsFirstTime(SettingsActivity.this)) {
                    SharedPrefConfig.writeIsFirstTime(SettingsActivity.this, false);
                }
                if (!newlyAddedPlatforms.isEmpty()) {
                    for (int i = 0; i < newlyAddedPlatforms.size(); i++) {
                        String un = newlyAddedPlatforms.get(i).second;
                        Log.d(TAG, "onCreate: " + newlyAddedPlatforms.get(i).first + " , " + un);
                        switch (newlyAddedPlatforms.get(i).first) {
                            case "atcoder":
                                getAC(un);
                                break;
                            case "codechef":
                                getCC(un);
                                break;
                            case "codeforces":
                                getCF(un);
                                break;
                            case "leetcode":
                                getLC(un);
                                break;
                        }
                    }
                    new Handler().postDelayed(() -> {
                        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                        finish();
                    }, newlyAddedPlatforms.size() * 1300);
                } else {
                    new Handler().postDelayed(() -> {
                        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                        finish();
                    }, 500);
                }
            }
        });

        appUsernameEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) appUsernameEditText.clearFocus();
            return false;
        });

        appUsernameEditText.setText(SharedPrefConfig.readAppUserName(this));

        appUsernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                SharedPrefConfig.writeAppUserName(SettingsActivity.this, s.toString());
            }
        });

        if (SharedPrefConfig.readIsFirstTime(this)) {
            loadFirstTimeData();
        } else {
            loadData();
        }

        platformAdapter = new PlatformAdapter(this, platformNamesList);
        platformsListView.setAdapter(platformAdapter);

        platformsListView.setOnItemClickListener((parent, view, position, id) -> {
            if (platformNamesList.get(position).isUserNameAllowed()) {
                createPopupDialog(position);
            } else {
                platformAdapter.setSelectedIndex(position, "", false);
            }
        });
    }

    private void createPopupDialog(int position) {
        String platformName = getPlatformName(platformNamesList.get(position).getPlatformName());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.platforms_list_dialog, null);

        ImageView platformDialogImage = view.findViewById(R.id.platform_list_dialog_image);
        EditText platformDialogUserName = view.findViewById(R.id.platform_list_dialog_user_name);
        Button platformDialogSaveButton = view.findViewById(R.id.platform_list_dialog_save_button);
        Button platformDialogRemoveButton = view.findViewById(R.id.platform_list_dialog_remove_button);
        ProgressBar platformDialogProgressBar = view.findViewById(R.id.platform_list_dialog_progress_bar);

//        if (!platformNamesList.get(position).getUserName().equals("null")) {
//            platformDialogUserName.setText(platformNamesList.get(position).getUserName());
//        }
        boolean update;
        if (!platformNamesList.get(position).getUserName().isEmpty()) {
            platformDialogUserName.setText(platformNamesList.get(position).getUserName());
            update = true;
        } else update = false;

        platformDialogUserName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) platformDialogUserName.clearFocus();
            return false;
        });

        platformDialogImage.setImageResource(platformNamesList.get(position).getLogo2X());

        if (platformNamesList.get(position).isEnabled()) {
            platformDialogRemoveButton.setVisibility(View.VISIBLE);
        } else {
            platformDialogRemoveButton.setVisibility(View.GONE);
        }

        platformDialogRemoveButton.setOnClickListener(v -> {
            platformAdapter.setSelectedIndex(position, "", false);
            dialog.cancel();
        });

        platformDialogSaveButton.setOnClickListener(v -> {
            if (platformDialogUserName.getText().toString().isEmpty()) {
                platformAdapter.setSelectedIndex(position, "", false);
                Snackbar.make(v, "Invalid User Name!", Snackbar.LENGTH_SHORT).show();
            } else {
                platformDialogSaveButton.setVisibility(View.GONE);
                platformDialogRemoveButton.setVisibility(View.GONE);
                platformDialogProgressBar.setVisibility(View.VISIBLE);

                checkValidUsername(platformDialogProgressBar, platformDialogSaveButton, v,
                        platformName, platformDialogUserName.getText().toString().trim(),
                        position, update);
            }
        });
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private String getPlatformName(String platformName) {
        switch (platformName) {
            case "AtCoder":
                return "atcoder";
            case "CodeChef":
                return "codechef";
            case "CodeForces":
                return "codeforces";
            case "LeetCode":
                return "leetcode";
        }
        return null;
    }

    private void checkValidUsername(ProgressBar platformDialogProgressBar,
                                    Button platformDialogSaveButton, View v, String platform,
                                    String username, int position, boolean update) {
        String url = "https://competitive-coding-api.herokuapp.com/api/" + platform + "/" + username;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                if (response.getString("status").equals("Success")) {
                    for (int i = 0; i < newlyAddedPlatforms.size(); i++) {
                        if (newlyAddedPlatforms.get(i).first.equals(platform)) {
                            newlyAddedPlatforms.remove(i);
                            break;
                        }
                    }
                    newlyAddedPlatforms.add(new Pair<>(platform, username));
                    platformAdapter.setSelectedIndex(position, username, update);
                    dialog.dismiss();
                } else {
                    for (int i = 0; i < newlyAddedPlatforms.size(); i++) {
                        if (newlyAddedPlatforms.get(i).first.equals(platform)) {
                            newlyAddedPlatforms.remove(i);
                            break;
                        }
                    }
                    Snackbar.make(v, "Invalid User Name!", Snackbar.LENGTH_SHORT).show();
                    platformDialogProgressBar.setVisibility(View.GONE);
                    platformDialogSaveButton.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Snackbar.make(v, "Some error occurred! Retry again...", Snackbar.LENGTH_SHORT).show();
                platformDialogProgressBar.setVisibility(View.GONE);
                platformDialogSaveButton.setVisibility(View.VISIBLE);
            }
        }, error -> {
//            Toast.makeText(SettingsActivity.this, "ERROR : " + error.getMessage(), Toast.LENGTH_SHORT).show();
            Snackbar.make(v, "Some error occurred! Retry again...", Snackbar.LENGTH_SHORT).show();
            platformDialogProgressBar.setVisibility(View.GONE);
            platformDialogSaveButton.setVisibility(View.VISIBLE);
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void loadData() {
        platformNamesList = SharedPrefConfig.readPlatformsSelected(this);
    }

    private void loadFirstTimeData() {
        platformNamesList = new ArrayList<>();
        platformNamesList.add(new PlatformListItem("AtCoder", "", false, true, R.drawable.ic_at_coder_logo, R.drawable.ic_at_coder_logo));
        platformNamesList.add(new PlatformListItem("CodeChef", "", false, true, R.drawable.ic_codechef_logo, R.drawable.ic_codechef_logo_2x));
        platformNamesList.add(new PlatformListItem("CodeForces", "", false, true, R.drawable.ic_codeforces_logo, R.drawable.ic_codeforces_logo_2x));
        platformNamesList.add(new PlatformListItem("HackerEarth", "", false, false, R.drawable.ic_hacker_earth_logo, -1));
        platformNamesList.add(new PlatformListItem("HackerRank", "", false, false, R.drawable.ic_hackerrank_logo, -1));
        platformNamesList.add(new PlatformListItem("Kick Start", "", false, false, R.drawable.ic_kickstart_logo, -1));
        platformNamesList.add(new PlatformListItem("LeetCode", "", false, true, R.drawable.ic_leetcode_logo, R.drawable.ic_leetcode_logo_2x));
        platformNamesList.add(new PlatformListItem("TopCoder", "", false, false, R.drawable.ic_topcoder_logo, -1));

        saveData();
    }

    private void saveData() {
        SharedPrefConfig.writePlatformsSelected(this, platformNamesList);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        if (appUsernameEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "How should I call you?", Toast.LENGTH_SHORT).show();
        } else if (SharedPrefConfig.readPlatformsCount(this) == 0) {
            Toast.makeText(this, "No Platform is selected!", Toast.LENGTH_SHORT).show();
        } else {
            if (newlyAddedPlatforms.isEmpty()) {
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                finish();
                super.onBackPressed();
            } else Toast.makeText(this, "Settings not saved!", Toast.LENGTH_SHORT).show();
        }
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
                        response.getInt(
                                "rating"),
                        response.getInt("max rating"),
                        response.getString("rank"),
                        response.getString("max rank"),
                        recentRatingsArrayList);

                SharedPrefConfig.writeInCodeForcesPref(getApplicationContext(), item);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.d(TAG, "onErrorResponse: " + error.getMessage()));
        requestQueue.add(jsonObjectRequest);
    }

    private void getLC(String user_name) {
        String platform_name = "leetcode";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://competitive-coding-api.herokuapp.com/api/" + platform_name + "/" + user_name, null, response -> {
            try {
                LeetCodeUserDetails item = new LeetCodeUserDetails(user_name,
                        response.getString(
                                "ranking"),
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
        }, error -> Log.d(TAG, "onErrorResponse: " + error.getMessage()));
        requestQueue.add(jsonObjectRequest);
    }

    private void getAC(String user_name) {
        String platform_name = "atcoder";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://competitive-coding-api.herokuapp.com/api/" + platform_name + "/" + user_name, null, response -> {
            try {
                AtCoderUserDetails item = new AtCoderUserDetails(
                        user_name,
                        response.getInt("rating"),
                        response.getInt("highest"),
                        response.getInt("rank"),
                        response.getString("level"));

                SharedPrefConfig.writeInAtCoderPref(getApplicationContext(), item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.d(TAG, "onErrorResponse: " + error.getMessage()));
        requestQueue.add(jsonObjectRequest);
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