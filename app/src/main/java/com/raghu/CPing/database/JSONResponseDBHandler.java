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
        contentValues.put("site",contestDetails.getSite());
        contentValues.put("contestName",contestDetails.getContestName());
        contentValues.put("contestUrl",contestDetails.getContestUrl());
        contentValues.put("contestDuration",contestDetails.getContestDuration());
        contentValues.put("contestStartDate",contestDetails.getContestStartTime());
        contentValues.put("contestEndDate",contestDetails.getContestEndTime());
        contentValues.put("isToday",contestDetails.getIsToday());
        contentValues.put("contestStatus",contestDetails.getContestStatus());

        if(sqLiteDatabase.insert("jsonResponse",null,contentValues)!=-1) {
            sqLiteDatabase.close();
        } else {
            Toast.makeText(context, "Error occurred in inserting data into database!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAll() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from "+ "jsonResponse");
    }

    public ArrayList<ContestDetails> getCodeforcesDetails() {
        ArrayList<ContestDetails> codeforcesContestsList = new ArrayList<>();

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
                    codeforcesContestsList.add(new ContestDetails(site, contestName, contestUrl, contestDuration, contestStartDate, contestEndDate, isToday, contestStatus));
                }
            }
        } else {
            Toast.makeText(context, "Nothing to show in codeforces!", Toast.LENGTH_SHORT).show();
        }

        sqLiteDatabase.close();
        return codeforcesContestsList;
    }

    public ArrayList<ContestDetails> getCodechefDetails() {
        ArrayList<ContestDetails> codechefContestsList = new ArrayList<>();

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
                    codechefContestsList.add(new ContestDetails(site, contestName, contestUrl, contestDuration, contestStartDate, contestEndDate, isToday, contestStatus));
                }
            }
        } else {
            Toast.makeText(context, "Nothing to show in codechef!", Toast.LENGTH_SHORT).show();
        }

        sqLiteDatabase.close();
        return codechefContestsList;
    }

    public ArrayList<ContestDetails> getLeetcodeDetails() {
        ArrayList<ContestDetails> leetcodeContestsList = new ArrayList<>();

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
                    leetcodeContestsList.add(new ContestDetails(site, contestName, contestUrl, contestDuration, contestStartDate, contestEndDate, isToday, contestStatus));
                }
            }
        } else {
            Toast.makeText(context, "Nothing to show in leetcode!", Toast.LENGTH_SHORT).show();
        }

        sqLiteDatabase.close();
        return leetcodeContestsList;
    }
}
