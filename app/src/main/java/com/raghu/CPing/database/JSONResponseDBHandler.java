package com.raghu.CPing.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.raghu.CPing.util.ContestDetails;

import java.util.ArrayList;

public class JSONResponseDBHandler extends SQLiteOpenHelper {

    Context context;

    private static final String DATABASE_NAME = "jsonResponseDB.db";
    private static final int DATABASE_VERSION = 1;

    private static final String createTableQuery = "create table jsonResponse (site STRING" + ",contestName STRING" + ",contestUrl STRING" + ",contestDuration NUMBER" + ",contestStartDate STRING" + ",contestEndDate STRING" + ",isToday STRING" + ",contestStatus STRING)";

    public JSONResponseDBHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addItem(ContestDetails contestDetails) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("site", contestDetails.getSite());
        contentValues.put("contestName", contestDetails.getContestName());
        contentValues.put("contestUrl", contestDetails.getContestUrl());
        contentValues.put("contestDuration", contestDetails.getContestDuration());
        contentValues.put("contestStartDate", contestDetails.getContestStartTime());
        contentValues.put("contestEndDate", contestDetails.getContestEndTime());
        contentValues.put("isToday", contestDetails.getIsToday());
        contentValues.put("contestStatus", contestDetails.getContestStatus());

        if (sqLiteDatabase.insert("jsonResponse", null, contentValues) != -1) {
            sqLiteDatabase.close();
        } else {
            Toast.makeText(context, "Error occurred in inserting data into database!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAll() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from " + "jsonResponse");
    }

    public ArrayList<ContestDetails> getCodeForcesDetails() {
        ArrayList<ContestDetails> codeForcesContestsList = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery("select * from jsonResponse", null);

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String site = cursor.getString(0);
                if (site.equals("CodeForces")) {
                    String contestName, contestUrl, contestStartDate, contestEndDate, isToday, contestStatus;
                    int contestDuration;
                    contestName = cursor.getString(1);
                    contestUrl = cursor.getString(2);
                    contestDuration = cursor.getInt(3);
                    contestStartDate = cursor.getString(4);
                    contestEndDate = cursor.getString(5);
                    isToday = cursor.getString(6);
                    contestStatus = cursor.getString(7);
                    codeForcesContestsList.add(new ContestDetails(site, contestName, contestUrl, contestDuration, contestStartDate, contestEndDate, isToday, contestStatus));
                }
            }
        } else {
            Toast.makeText(context, "Nothing to show in codeForces!", Toast.LENGTH_SHORT).show();
        }

        sqLiteDatabase.close();
        return codeForcesContestsList;
    }

    public ArrayList<ContestDetails> getCodeChefDetails() {
        ArrayList<ContestDetails> codeChefContestsList = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery("select * from jsonResponse", null);

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String site = cursor.getString(0);
                if (site.equals("CodeChef")) {
                    String contestName, contestUrl, contestStartDate, contestEndDate, isToday, contestStatus;
                    int contestDuration;
                    contestName = cursor.getString(1);
                    contestUrl = cursor.getString(2);
                    contestDuration = cursor.getInt(3);
                    contestStartDate = cursor.getString(4);
                    contestEndDate = cursor.getString(5);
                    isToday = cursor.getString(6);
                    contestStatus = cursor.getString(7);
                    codeChefContestsList.add(new ContestDetails(site, contestName, contestUrl, contestDuration, contestStartDate, contestEndDate, isToday, contestStatus));
                }
            }
        } else {
            Toast.makeText(context, "Nothing to show in codechef!", Toast.LENGTH_SHORT).show();
        }

        sqLiteDatabase.close();
        return codeChefContestsList;
    }

    public ArrayList<ContestDetails> getLeetCodeDetails() {
        ArrayList<ContestDetails> leetCodeContestsList = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery("select * from jsonResponse", null);

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String site = cursor.getString(0);
                if (site.equals("LeetCode")) {
                    String contestName, contestUrl, contestStartDate, contestEndDate, isToday, contestStatus;
                    int contestDuration;
                    contestName = cursor.getString(1);
                    contestUrl = cursor.getString(2);
                    contestDuration = cursor.getInt(3);
                    contestStartDate = cursor.getString(4);
                    contestEndDate = cursor.getString(5);
                    isToday = cursor.getString(6);
                    contestStatus = cursor.getString(7);
                    leetCodeContestsList.add(new ContestDetails(site, contestName, contestUrl, contestDuration, contestStartDate, contestEndDate, isToday, contestStatus));
                }
            }
        } else {
            Toast.makeText(context, "Nothing to show in leetcode!", Toast.LENGTH_SHORT).show();
        }

        sqLiteDatabase.close();
        return leetCodeContestsList;
    }

    public ArrayList<ContestDetails> getTopCoderDetails() {
        ArrayList<ContestDetails> topCoderContestDetails = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery("select * from jsonResponse", null);

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String site = cursor.getString(0);
                if (site.equals("TopCoder")) {
                    String contestName, contestUrl, contestStartDate, contestEndDate, isToday, contestStatus;
                    int contestDuration;
                    contestName = cursor.getString(1);
                    contestUrl = cursor.getString(2);
                    contestDuration = cursor.getInt(3);
                    contestStartDate = cursor.getString(4);
                    contestEndDate = cursor.getString(5);
                    isToday = cursor.getString(6);
                    contestStatus = cursor.getString(7);
                    topCoderContestDetails.add(new ContestDetails(site, contestName, contestUrl, contestDuration, contestStartDate, contestEndDate, isToday, contestStatus));
                }
            }
        } else {
            Toast.makeText(context, "Nothing to show in topCoder!", Toast.LENGTH_SHORT).show();
        }

        sqLiteDatabase.close();
        return topCoderContestDetails;
    }

    public ArrayList<ContestDetails> getHackerRankDetails() {
        ArrayList<ContestDetails> hackerRankContestDetails = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery("select * from jsonResponse", null);

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String site = cursor.getString(0);
                if (site.equals("HackerRank")) {
                    String contestName, contestUrl, contestStartDate, contestEndDate, isToday, contestStatus;
                    int contestDuration;
                    contestName = cursor.getString(1);
                    contestUrl = cursor.getString(2);
                    contestDuration = cursor.getInt(3);
                    contestStartDate = cursor.getString(4);
                    contestEndDate = cursor.getString(5);
                    isToday = cursor.getString(6);
                    contestStatus = cursor.getString(7);
                    hackerRankContestDetails.add(new ContestDetails(site, contestName, contestUrl, contestDuration, contestStartDate, contestEndDate, isToday, contestStatus));
                }
            }
        } else {
            Toast.makeText(context, "Nothing to show in AtCoder!", Toast.LENGTH_SHORT).show();
        }

        sqLiteDatabase.close();
        return hackerRankContestDetails;
    }

    public ArrayList<ContestDetails> getHackerEarthDetails() {
        ArrayList<ContestDetails> hackerEarthContestDetails = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery("select * from jsonResponse", null);

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String site = cursor.getString(0);
                if (site.equals("HackerEarth")) {
                    String contestName, contestUrl, contestStartDate, contestEndDate, isToday, contestStatus;
                    int contestDuration;
                    contestName = cursor.getString(1);
                    contestUrl = cursor.getString(2);
                    contestDuration = cursor.getInt(3);
                    contestStartDate = cursor.getString(4);
                    contestEndDate = cursor.getString(5);
                    isToday = cursor.getString(6);
                    contestStatus = cursor.getString(7);
                    hackerEarthContestDetails.add(new ContestDetails(site, contestName, contestUrl, contestDuration, contestStartDate, contestEndDate, isToday, contestStatus));
                }
            }
        } else {
            Toast.makeText(context, "Nothing to show in HackerEarth!", Toast.LENGTH_SHORT).show();
        }

        sqLiteDatabase.close();
        return hackerEarthContestDetails;
    }

    public ArrayList<ContestDetails> getAtCoderDetails() {
        ArrayList<ContestDetails> atCoderContestDetails = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery("select * from jsonResponse", null);

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String site = cursor.getString(0);
                if (site.equals("AtCoder")) {
                    String contestName, contestUrl, contestStartDate, contestEndDate, isToday, contestStatus;
                    int contestDuration;
                    contestName = cursor.getString(1);
                    contestUrl = cursor.getString(2);
                    contestDuration = cursor.getInt(3);
                    contestStartDate = cursor.getString(4);
                    contestEndDate = cursor.getString(5);
                    isToday = cursor.getString(6);
                    contestStatus = cursor.getString(7);
                    atCoderContestDetails.add(new ContestDetails(site, contestName, contestUrl, contestDuration, contestStartDate, contestEndDate, isToday, contestStatus));
                }
            }
        } else {
            Toast.makeText(context, "Nothing to show in AtCoder!", Toast.LENGTH_SHORT).show();
        }

        sqLiteDatabase.close();
        return atCoderContestDetails;
    }

    public ArrayList<ContestDetails> getKickStartDetails() {
        ArrayList<ContestDetails> kickStartContestDetails = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery("select * from jsonResponse", null);

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String site = cursor.getString(0);
                if (site.equals("Kick Start")) {
                    String contestName, contestUrl, contestStartDate, contestEndDate, isToday, contestStatus;
                    int contestDuration;
                    contestName = cursor.getString(1);
                    contestUrl = cursor.getString(2);
                    contestDuration = cursor.getInt(3);
                    contestStartDate = cursor.getString(4);
                    contestEndDate = cursor.getString(5);
                    isToday = cursor.getString(6);
                    contestStatus = cursor.getString(7);
                    kickStartContestDetails.add(new ContestDetails(site, contestName, contestUrl, contestDuration, contestStartDate, contestEndDate, isToday, contestStatus));
                }
            }
        } else {
            Toast.makeText(context, "Nothing to show in kickStart!", Toast.LENGTH_SHORT).show();
        }

        sqLiteDatabase.close();
        return kickStartContestDetails;
    }
}
