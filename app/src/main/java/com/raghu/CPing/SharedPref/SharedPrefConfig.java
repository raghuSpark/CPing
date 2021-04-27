package com.raghu.CPing.SharedPref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raghu.CPing.classes.AtCoderUserDetails;
import com.raghu.CPing.classes.CodeChefUserDetails;
import com.raghu.CPing.classes.CodeForcesUserDetails;
import com.raghu.CPing.classes.LeetCodeUserDetails;

import java.lang.reflect.Type;

public class SharedPrefConfig {
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
}
