package com.rr.CPing.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.rr.CPing.Adapters.HiddenContestsRecyclerViewAdapter;
import com.rr.CPing.Adapters.PlatformAdapter;
import com.rr.CPing.Model.AtCoderUserDetails;
import com.rr.CPing.Model.CodeChefUserDetails;
import com.rr.CPing.Model.CodeForcesUserDetails;
import com.rr.CPing.Model.HiddenContestsClass;
import com.rr.CPing.Model.LeetCodeUserDetails;
import com.rr.CPing.Model.PlatformListItem;
import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.Utils.NetworkChangeListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class SettingsNextActivity extends AppCompatActivity {

    private static final String TAG = "SettingsNextActivity";
    private static final float ALPHA_FULL = 1.0f;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    // For making loading tasks in background
    private boolean saveButtonClicked = false;
    private int stillLoadingCount = 0;

    private ListView platformsListView;
    private PlatformAdapter platformAdapter;
    private TextView hiddenNothingText;
    private ImageView hiddenNothingImage;
    private ArrayList<PlatformListItem> platformNamesList;
    private AlertDialog dialog;
    private String title;

    private Button settingsSaveButton;
    private EditText searchBar;
    private ProgressBar settingsProgressBar;

    private RecyclerView hiddenContestsRV;
    private HiddenContestsRecyclerViewAdapter hiddenContestsRVA;
    private ArrayList<HiddenContestsClass> hiddenContestsArrayList;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppTheme();
        setContentView(R.layout.activity_settings_next);

        if (SharedPrefConfig.readIsFirstTime(this)) {
            loadFirstTimeData();
            SharedPrefConfig.writePlatformsCount(this, 0);
        } else loadData();

        findViewByIds();

        requestQueue = Volley.newRequestQueue(this);

        title = getIntent().getAction();

        Toolbar dashBoardToolbar = findViewById(R.id.settings_next_page_toolbar);
        setSupportActionBar(dashBoardToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
        dashBoardToolbar.setTitleTextColor(getResources().getColor(R.color.fontColor, null));

        dashBoardToolbar.setNavigationIcon(R.drawable.ic_back_button);
        dashBoardToolbar.setNavigationOnClickListener(v -> BackPressed());

        if (title.equalsIgnoreCase("Platforms")) {
            hiddenContestsRV.setVisibility(View.GONE);
            hiddenNothingText.setVisibility(View.GONE);
            hiddenNothingImage.setVisibility(View.GONE);
            searchBar.setVisibility(View.GONE);

            platformAdapter = new PlatformAdapter(this, platformNamesList);
            platformsListView.setAdapter(platformAdapter);
        } else {
            searchBar.setVisibility(View.VISIBLE);
            hiddenContestsRV.setVisibility(View.VISIBLE);
            settingsSaveButton.setVisibility(View.GONE);
            settingsProgressBar.setVisibility(View.GONE);
            platformsListView.setVisibility(View.GONE);

            hiddenContestsArrayList = SharedPrefConfig.readInHiddenContests(this);
            initialize();
            if (hiddenContestsArrayList.isEmpty()) {
                hiddenNothingText.setVisibility(View.VISIBLE);
                hiddenNothingImage.setVisibility(View.VISIBLE);
                searchBar.setVisibility(View.GONE);
            } else {
                searchBar.setVisibility(View.VISIBLE);
                hiddenNothingText.setVisibility(View.GONE);
                hiddenNothingImage.setVisibility(View.GONE);
            }
        }

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        searchBar.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                searchBar.setText(null);
                searchBar.clearFocus();
            }
        });

        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) searchBar.clearFocus();
            return false;
        });

        platformsListView.setOnItemClickListener((parent, view, position, id) -> {
            if (platformNamesList.get(position).isUserNameAllowed()) {
                createPopupDialog(position);
            } else {
                platformAdapter.setSelectedIndex(position, "", false);
            }
        });

        settingsSaveButton.setOnClickListener(v -> {
            if (SharedPrefConfig.readPlatformsCount(this) == 0) {
                Toast.makeText(this, "No Platform is selected!", Toast.LENGTH_SHORT).show();
            } else {
                saveButtonClicked = true;

                settingsSaveButton.setVisibility(View.GONE);
                settingsProgressBar.setVisibility(View.VISIBLE);

                if (SharedPrefConfig.readIsFirstTime(this)) {
                    SharedPrefConfig.writeIsFirstTime(this, false);
                }
                if (stillLoadingCount <= 0) {
                    goToSettingsActivity();
                }
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(hiddenContestsRV);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            HiddenContestsClass restoredContest = hiddenContestsArrayList.get(position);
            hiddenContestsArrayList.remove(position);
            hiddenContestsRVA.notifyItemRemoved(position);

            String searchText = searchBar.getText().toString();
            if (!searchText.isEmpty()) {
                filter(searchText);
            }

            Snackbar.make(hiddenContestsRV, restoredContest.getContestName(), Snackbar.LENGTH_LONG)
                    .setActionTextColor(getResources().getColor(R.color.appBlueColor, null))
                    .setAction("Undo", v -> {
                        hiddenContestsArrayList.add(position, restoredContest);
                        hiddenContestsRVA.notifyItemInserted(position);

                        String search_text = searchBar.getText().toString();
                        if (!search_text.isEmpty()) {
                            filter(search_text);
                        }

                        if (hiddenContestsArrayList.isEmpty()) {
                            hiddenNothingText.setVisibility(View.VISIBLE);
                            hiddenNothingImage.setVisibility(View.VISIBLE);
                            searchBar.setVisibility(View.GONE);
                        } else {
                            hiddenNothingImage.setVisibility(View.GONE);
                            hiddenNothingText.setVisibility(View.GONE);
                            searchBar.setVisibility(View.VISIBLE);
                        }
                    }).show();
            if (hiddenContestsArrayList.isEmpty()) {
                hiddenNothingText.setVisibility(View.VISIBLE);
                hiddenNothingImage.setVisibility(View.VISIBLE);
                searchBar.setVisibility(View.GONE);
            } else {
                hiddenNothingText.setVisibility(View.GONE);
                hiddenNothingImage.setVisibility(View.GONE);
            }
            SharedPrefConfig.writeInHiddenContests(SettingsNextActivity.this, hiddenContestsArrayList);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            View itemView = viewHolder.itemView;
            Paint paint = new Paint();
            paint.setARGB(100, 52, 168, 83);

            @SuppressLint("UseCompatLoadingForDrawables")
            Bitmap icon = drawableToBitmap(getResources().getDrawable(R.drawable.ic_restore, null));

            if (dX > 0) {
                // Draw Rect with varying right side, equal to displacement dX
                c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom(), paint);
                // Set the image icon for Right swipe
                assert icon != null;
                c.drawBitmap(icon, (float) itemView.getLeft() + convertDpToPx(), (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight()) / 2, paint);
            } else {
                // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
                c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), paint);
                //Set the image icon for Left swipe
                assert icon != null;
                c.drawBitmap(icon, (float) itemView.getRight() - convertDpToPx() - icon.getWidth(), (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight()) / 2, paint);
            }

            // Fade out the view as it is swiped out of the parent's bounds
            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (searchBar.getText().toString().isEmpty() && event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    view.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        BackPressed();
        super.onBackPressed();
    }

    @Override
    public void finish() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.finish();
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

    @SuppressLint("SetTextI18n")
    private void filter(String text) {
        ArrayList<HiddenContestsClass> filteredList = new ArrayList<>();
        if (hiddenContestsArrayList.size() == 0) return;
        for (HiddenContestsClass item : hiddenContestsArrayList) {
            if (item.getContestName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        Log.d(TAG, "filter: " + filteredList.size());
        if (filteredList.size() == 0) {
            hiddenNothingText.setVisibility(View.VISIBLE);
            hiddenNothingImage.setVisibility(View.VISIBLE);
            hiddenNothingText.setText("No results found...");
        } else {
            hiddenNothingText.setText("Nothing to show");
            hiddenNothingImage.setVisibility(View.GONE);
            hiddenNothingText.setVisibility(View.GONE);
        }
        hiddenContestsRVA.filteredList(filteredList);
    }

    private void createPopupDialog(int position) {
        String platformName = getPlatformName(platformNamesList.get(position).getPlatformName());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.platforms_list_dialog, null);

        ImageView platformDialogImage = view.findViewById(R.id.platform_list_dialog_image);
        AutoCompleteTextView platformDialogUserName = view.findViewById(R.id.platform_list_dialog_user_name);
        Button platformDialogSaveButton = view.findViewById(R.id.platform_list_dialog_save_button);
        Button platformDialogRemoveButton = view.findViewById(R.id.platform_list_dialog_remove_button);
        ProgressBar platformDialogProgressBar = view.findViewById(R.id.platform_list_dialog_progress_bar);

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
        dialog.getWindow().setWindowAnimations(R.style.PopupDialogAnimation);
        dialog.show();
    }

    private void loadData() {
        platformNamesList = SharedPrefConfig.readPlatformsSelected(this);
    }

    private void loadFirstTimeData() {
        platformNamesList = new ArrayList<>();
        platformNamesList.add(new PlatformListItem("AtCoder", "", false, true, R.drawable.ic_at_coder_logo, R.drawable.ic_at_coder_logo));
        platformNamesList.add(new PlatformListItem("CodeChef", "", false, true, R.drawable.ic_codechef_logo, R.drawable.ic_codechef_logo_2x));
        platformNamesList.add(new PlatformListItem("CodeForces", "", false, true, R.drawable.ic_codeforces_logo, R.drawable.ic_codeforces_logo_2x));
        platformNamesList.add(new PlatformListItem("HackerEarth", "", false, false, R.drawable.ic_hackerearth_logo, -1));
        platformNamesList.add(new PlatformListItem("HackerRank", "", false, false, R.drawable.ic_hackerrank_logo, -1));
        platformNamesList.add(new PlatformListItem("Kick Start", "", false, false, R.drawable.ic_kickstart_logo, -1));
        platformNamesList.add(new PlatformListItem("LeetCode", "", false, true, R.drawable.ic_leetcode_logo, R.drawable.ic_leetcode_logo_2x));
        platformNamesList.add(new PlatformListItem("TopCoder", "", false, false, R.drawable.ic_topcoder_logo, -1));

        saveData();
    }

    private void saveData() {
        SharedPrefConfig.writePlatformsSelected(this, platformNamesList);
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

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private int convertDpToPx() {
        return Math.round(16 * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void initialize() {
        hiddenContestsRV.setHasFixedSize(true);
        hiddenContestsRV.setLayoutManager(new LinearLayoutManager(this));
        hiddenContestsRVA = new HiddenContestsRecyclerViewAdapter(hiddenContestsArrayList);
        hiddenContestsRV.setAdapter(hiddenContestsRVA);
        hiddenContestsRVA.notifyDataSetChanged();
    }

    private void checkValidUsername(ProgressBar platformDialogProgressBar, Button platformDialogSaveButton, View v, String platform, String username, int position, boolean update) {
        String url = "https://cping-api.herokuapp.com/api/" + platform + "/" + username;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                if (response.getString("status").equals("Success")) {
                    platformAdapter.setSelectedIndex(position, username, update);
                    dialog.dismiss();
                    switch (platform) {
                        case "atcoder":
                            getAC(username);
                            break;
                        case "codechef":
                            getCC(username);
                            break;
                        case "codeforces":
                            getCF(username);
                            break;
                        case "leetcode":
                            getLC(username);
                            break;
                    }
                } else {
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
            Snackbar.make(v, "Some error occurred! Retry again...", Snackbar.LENGTH_SHORT).show();
            platformDialogProgressBar.setVisibility(View.GONE);
            platformDialogSaveButton.setVisibility(View.VISIBLE);
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void getAC(String user_name) {
        stillLoadingCount++;

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
                AtCoderUserDetails item = new AtCoderUserDetails(
                        user_name,
                        response.getInt("rating"),
                        response.getInt("highest"),
                        response.getInt("rank"),
                        response.getString("level"),
                        recentRatingsArrayList);

                SharedPrefConfig.writeInAtCoderPref(getApplicationContext(), item);

                stillLoadingCount--;
                if (stillLoadingCount <= 0 && saveButtonClicked) {
                    goToSettingsActivity();
                }
            } catch (JSONException e) {
                Log.d(TAG, "getAC: " + e.getMessage());
                e.printStackTrace();
                stillLoadingCount--;
                getAC(user_name);
            }
        }, error -> {
            Log.d(TAG, "onErrorResponse: " + error.getMessage());
            stillLoadingCount--;
            getAC(user_name);
        });
        if (stillLoadingCount <= 0 && saveButtonClicked) {
            goToSettingsActivity();
        }
        requestQueue.add(jsonObjectRequest);
    }

    private void getCC(String user_name) {
        stillLoadingCount++;

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

                stillLoadingCount--;
                if (stillLoadingCount <= 0 && saveButtonClicked) {
                    goToSettingsActivity();
                }
            } catch (JSONException e) {
                Log.d(TAG, "onResponse: " + e.getMessage());
                e.printStackTrace();
                stillLoadingCount--;
                getCC(user_name);
            }
        }, error -> {
            Log.d(TAG, "onErrorResponse: " + error.getMessage());
            stillLoadingCount--;
            getCC(user_name);
        });
        if (stillLoadingCount <= 0 && saveButtonClicked) {
            goToSettingsActivity();
        }
        requestQueue.add(jsonObjectRequest);
    }

    private void getCF(String user_name) {
        stillLoadingCount++;

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
                        response.getInt(
                                "rating"),
                        response.getInt("max rating"),
                        response.getString("rank"),
                        response.getString("max rank"),
                        recentRatingsArrayList);

                SharedPrefConfig.writeInCodeForcesPref(getApplicationContext(), item);
                stillLoadingCount--;
                if (stillLoadingCount <= 0 && saveButtonClicked) {
                    goToSettingsActivity();
                }
            } catch (JSONException e) {
                Log.d(TAG, "getCF: " + e.getMessage());
                e.printStackTrace();
                stillLoadingCount--;
                getCF(user_name);
            }
        }, error -> {
            stillLoadingCount--;
            Log.d(TAG, "onErrorResponse: " + error.getMessage());
            getCF(user_name);
        });
        if (stillLoadingCount <= 0 && saveButtonClicked) {
            goToSettingsActivity();
        }
        requestQueue.add(jsonObjectRequest);
    }

    private void getLC(String user_name) {
        stillLoadingCount++;

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

                stillLoadingCount--;
                if (stillLoadingCount <= 0 && saveButtonClicked) {
                    goToSettingsActivity();
                }
            } catch (JSONException e) {
                Log.d(TAG, "getLC: " + e.getMessage());
                e.printStackTrace();
                stillLoadingCount--;
                getLC(user_name);
            }
        }, error -> {
            stillLoadingCount--;
            Log.d(TAG, "onErrorResponse: " + error.getMessage());
            getLC(user_name);
        });
        if (stillLoadingCount <= 0 && saveButtonClicked) {
            goToSettingsActivity();
        }
        requestQueue.add(jsonObjectRequest);
    }

    private void goToSettingsActivity() {
        startActivity(new Intent(this, SettingsActivity.class));
        finish();
    }

    private void BackPressed() {
        if (title.equalsIgnoreCase("Platforms")) {
            if (SharedPrefConfig.readPlatformsCount(this) == 0) {
                Toast.makeText(this, "No Platform is selected!", Toast.LENGTH_SHORT).show();
            } else {
                saveButtonClicked = true;

                settingsSaveButton.setVisibility(View.GONE);
                settingsProgressBar.setVisibility(View.VISIBLE);

                if (SharedPrefConfig.readIsFirstTime(this)) {
                    SharedPrefConfig.writeIsFirstTime(this, false);
                }
                if (stillLoadingCount <= 0) {
                    goToSettingsActivity();
                }
            }
        } else goToSettingsActivity();
    }

    private void findViewByIds() {
        platformsListView = findViewById(R.id.settings_platforms_list_view);
        settingsSaveButton = findViewById(R.id.settings_save_button);
        settingsProgressBar = findViewById(R.id.settings_page_progress_bar);
        searchBar = findViewById(R.id.hidden_contests_search_bar);
        hiddenNothingText = findViewById(R.id.hidden_nothing_show_text);
        hiddenNothingImage = findViewById(R.id.hidden_nothing_show_img);
        hiddenContestsRV = findViewById(R.id.hidden_contests_recycler_view);
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