package com.rr.CPing.SharedPref;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rr.CPing.Model.AlarmIdClass;
import com.rr.CPing.Model.AtCoderUserDetails;
import com.rr.CPing.Model.CodeChefUserDetails;
import com.rr.CPing.Model.CodeForcesUserDetails;
import com.rr.CPing.Model.HiddenContestsClass;
import com.rr.CPing.Model.LeetCodeUserDetails;
import com.rr.CPing.Model.PlatformListItem;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPrefConfig {

    public static void writeBottomSheetOpen(Context context, boolean isOpen) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean("IS_OPEN", isOpen);
        editor.apply();
    }

    public static boolean readBottomSheetOpen(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        return pref.getBoolean("IS_OPEN", false);
    }

    public static void writeOverlayDoNotAskAgain(Context context, boolean canAsk) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean("DO_NOT_ASK_AGAIN", canAsk);
        editor.apply();
    }

    public static boolean readOverlayDoNotAskAgain(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        return pref.getBoolean("DO_NOT_ASK_AGAIN", false);
    }

    public static void writeAutoStartDoNotAskAgain(Context context, boolean canAsk) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean("DO_NOT_ASK_AGAIN_AUTO_START", canAsk);
        editor.apply();
    }

    public static boolean readAutoStartDoNotAskAgain(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        return pref.getBoolean("DO_NOT_ASK_AGAIN_AUTO_START", false);
    }

    public static void writeAppTheme(Context context, int theme) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt("APP_THEME", theme);
        editor.apply();
    }

    public static int readAppTheme(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        return pref.getInt("APP_THEME", -1);
    }

    public static void writeAppUserName(Context context, String appUserName) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("APP_USER_NAME", appUserName);
        editor.apply();
    }

    public static String readAppUserName(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        return pref.getString("APP_USER_NAME", "");
    }

    public static void writeIsFirstTime(Context context, boolean isFirstTime) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean("IS_FIRST_TIME", isFirstTime);
        editor.apply();
    }

    public static boolean readIsFirstTime(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        return pref.getBoolean("IS_FIRST_TIME", true);
    }

    public static void writePlatformsCount(Context context, int platformsCount) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt("PLATFORMS_COUNT", platformsCount);
        editor.apply();
    }

    public static int readPlatformsCount(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        return pref.getInt("PLATFORMS_COUNT", 0);
    }

    public static void writePlatformsSelected(Context context, ArrayList<PlatformListItem> platformDetailsArrayList) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(platformDetailsArrayList);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("PLATFORM_DETAILS", jsonString);
        editor.apply();
    }

    public static ArrayList<PlatformListItem> readPlatformsSelected(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString("PLATFORM_DETAILS", "");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<PlatformListItem>>() {
        }.getType();
        return gson.fromJson(jsonString, type);
    }

    public static void writeInCodeChefPref(Context context, CodeChefUserDetails codeChefUserDetails) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(codeChefUserDetails);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("CC_USER", jsonString);
        editor.apply();
    }

    public static CodeChefUserDetails readInCodeChefPref(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString("CC_USER", "");

        Gson gson = new Gson();
        Type type = new TypeToken<CodeChefUserDetails>() {
        }.getType();
        return gson.fromJson(jsonString, type);
    }

    public static void writeInCodeForcesPref(Context context, CodeForcesUserDetails codeForcesUserDetails) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(codeForcesUserDetails);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("CF_USER", jsonString);
        editor.apply();
    }

    public static CodeForcesUserDetails readInCodeForcesPref(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString("CF_USER", "");

        Gson gson = new Gson();
        Type type = new TypeToken<CodeForcesUserDetails>() {
        }.getType();
        return gson.fromJson(jsonString, type);
    }

    public static void writeInLeetCodePref(Context context, LeetCodeUserDetails leetCodeUserDetails) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(leetCodeUserDetails);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("LC_USER", jsonString);
        editor.apply();
    }

    public static LeetCodeUserDetails readInLeetCodePref(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString("LC_USER", "");

        Gson gson = new Gson();
        Type type = new TypeToken<LeetCodeUserDetails>() {
        }.getType();
        return gson.fromJson(jsonString, type);
    }

    public static void writeInAtCoderPref(Context context, AtCoderUserDetails atCoderUserDetails) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(atCoderUserDetails);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("AC_USER", jsonString);
        editor.apply();
    }

    public static AtCoderUserDetails readInAtCoderPref(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString("AC_USER", "");

        Gson gson = new Gson();
        Type type = new TypeToken<AtCoderUserDetails>() {
        }.getType();
        return gson.fromJson(jsonString, type);
    }

    @SuppressLint("MutatingSharedPrefs")
    public static void writeInIdsOfReminderContests(Context context, ArrayList<AlarmIdClass> contestsWithReminderAdded) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(contestsWithReminderAdded);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("CONTEST_REMINDERS_IDS", jsonString);
        editor.apply();
    }

    public static ArrayList<AlarmIdClass> readInIdsOfReminderContests(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString("CONTEST_REMINDERS_IDS", "");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<AlarmIdClass>>() {
        }.getType();
        if (gson.fromJson(jsonString, type) == null) return new ArrayList<>();
        return gson.fromJson(jsonString, type);
    }

    public static void writeInHiddenContests(Context context, ArrayList<HiddenContestsClass> hiddenContestClassArrayList) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(hiddenContestClassArrayList);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("DELETED_CONTESTS", jsonString);
        editor.apply();
    }

    public static ArrayList<HiddenContestsClass> readInHiddenContests(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString("DELETED_CONTESTS", "");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<HiddenContestsClass>>() {
        }.getType();
        if (gson.fromJson(jsonString, type) == null) return new ArrayList<>();
        return gson.fromJson(jsonString, type);
    }
}