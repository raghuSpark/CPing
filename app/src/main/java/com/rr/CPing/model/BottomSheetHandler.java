package com.rr.CPing.model;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.util.ReminderBroadCast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.ALARM_SERVICE;

public class BottomSheetHandler {

    Context context;
    AlertDialog dialog;

    public BottomSheetHandler() {
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void showBottomSheetDialog(Context context,
                                      ArrayList<ContestDetails> contestsArrayList,
                                      int position,
                                      LayoutInflater layoutInflater) {
        this.context = context;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        TextView platformTitle = dialog.findViewById(R.id.bottom_sheet_platform_title),
                contestTitle = dialog.findViewById(R.id.bottom_sheet_contest_title),
                startTime = dialog.findViewById(R.id.bottom_sheet_start_time),
                endTime = dialog.findViewById(R.id.bottom_sheet_end_time),
                visitWebsite = dialog.findViewById(R.id.bottom_sheet_visit_website),
                appRemainder = dialog.findViewById(R.id.bottom_sheet_in_app_remainder),
                googleRemainder = dialog.findViewById(R.id.bottom_sheet_google_remainder);
        ImageView platformImage = dialog.findViewById(R.id.bottom_sheet_platform_title_image);

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        DateTimeHandler dateTimeHandler = new DateTimeHandler();
        dateTimeHandler.setCalender(start, contestsArrayList.get(position).getContestStartTime());
        dateTimeHandler.setCalender(end, contestsArrayList.get(position).getContestEndTime());

        if (contestsArrayList.get(position).getContestStatus().equals("CODING")) {
            appRemainder.setVisibility(View.GONE);
            googleRemainder.setVisibility(View.GONE);
        } else {
            appRemainder.setVisibility(View.VISIBLE);
            googleRemainder.setVisibility(View.VISIBLE);
        }

        platformImage.setImageResource(getImageResource(contestsArrayList.get(position).getSite()));
        platformTitle.setText(contestsArrayList.get(position).getSite());
        contestTitle.setText(contestsArrayList.get(position).getContestName());
        startTime.setText(properFormat(dateTimeHandler.getCompleteDetails(start)));
        endTime.setText(properFormat(dateTimeHandler.getCompleteDetails(end)));

        visitWebsite.setOnClickListener(v -> {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(contestsArrayList.get(position).getContestUrl())));
            dialog.cancel();
        });

        appRemainder.setOnClickListener(v -> {
            if (getTimeFromNow(contestsArrayList.get(position).getContestStartTime()) / 60000 <= 5) {
                Toast.makeText(context, "This contest is going to start in less than 5 minutes!", Toast.LENGTH_SHORT).show();
            } else {
                showAlarmSelectorDialog(contestsArrayList.get(position), start, layoutInflater);
            }
            dialog.cancel();
        });

        googleRemainder.setOnClickListener(v -> {
            if (getTimeFromNow(contestsArrayList.get(position).getContestStartTime()) / 60000 <= 5) {
                Toast.makeText(context, "This contest is going to start in less than 5 minutes!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setData(CalendarContract.Events.CONTENT_URI);
                intent.putExtra(CalendarContract.Events.TITLE, contestsArrayList.get(position).getContestName());
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start.getTimeInMillis());
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end.getTimeInMillis());

                if (intent.resolveActivity(Objects.requireNonNull(context).getPackageManager()) != null) {
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "No application found supporting this feature!",
                            Toast.LENGTH_SHORT).show();
                }
            }
            dialog.cancel();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomSheetAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private StringBuilder properFormat(String completeDetails) {
        int spaceCount = 0;
        StringBuilder ans = new StringBuilder();
        for (int i = 0; i < completeDetails.length(); i++) {
            if (completeDetails.charAt(i) == ' ') {
                spaceCount++;
            }
            if (spaceCount == 3) {
                ans.append(',');
                ans.insert(0, ", ");
                StringBuilder hr_24Time = new StringBuilder();
                for (int j = i + 1; j <= i + 5; j++) {
                    hr_24Time.append(completeDetails.charAt(j));
                }
                ans.insert(0, hr_24To12Format(hr_24Time));
                i += 5;
                spaceCount++;
            } else ans.append(completeDetails.charAt(i));
        }
        return ans;
    }

    public StringBuilder hr_24To12Format(StringBuilder hr_24Time) {
        int h1 = (int) hr_24Time.charAt(0) - '0';
        int h2 = (int) hr_24Time.charAt(1) - '0';

        int hh = h1 * 10 + h2;
        String Meridian = (hh < 12) ? "AM" : "PM";

        hh %= 12;
        StringBuilder hr_12Time;
        if (hh == 0) {
            hr_12Time = new StringBuilder("12");
        } else {
            hr_12Time = new StringBuilder(Integer.toString(hh));
        }
        for (int i = 2; i < 5; i++) hr_12Time.append(hr_24Time.charAt(i));
        hr_12Time.append(" ").append(Meridian);
        return hr_12Time;
    }

    private void showAlarmSelectorDialog(ContestDetails contestDetails,
                                         Calendar start, LayoutInflater layoutInflater) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = layoutInflater.inflate(R.layout.alarm_selector_layout, null);

        ArrayList<String> beforeTimesArray = new ArrayList<>();

        // Gives time in milli-seconds
        long timeFromNow = getTimeFromNow(contestDetails.getContestStartTime());
        //Gives Time in minutes
        timeFromNow /= 60000;

        for (long i = 5; i < Math.min(timeFromNow, 35); i += 5) {
            beforeTimesArray.add(i + " minutes");
        }

        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, beforeTimesArray);
        adapter.setDropDownViewResource(R.layout.drop_down_item);
        spinner.setAdapter(adapter);

        view.findViewById(R.id.saveReminder).setOnClickListener(v -> {
            Toast.makeText(context, "Reminder set!", Toast.LENGTH_SHORT).show();

            ArrayList<String> currentList = SharedPrefConfig.readInIdsOfReminderContests(context);
            if (currentList.size() == 0 || !currentList.contains(contestDetails.getContestName())) {
                currentList.add(contestDetails.getContestName());
                SharedPrefConfig.writeInIdsOfReminderContests(context, currentList);
            }

            setNotification(getNum(spinner.getSelectedItem().toString()), contestDetails, start);
            dialog.cancel();
        });

        view.findViewById(R.id.discardReminder).setOnClickListener(v -> dialog.cancel());

        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private long getTimeFromNow(String startTime) {
//        2021-05-22T12:00:00.000Z
        String currentTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.getDefault()).format(new Date());

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            return Math.abs(Objects.requireNonNull(simpleDateFormat.parse(startTime)).getTime() - Objects.requireNonNull(simpleDateFormat.parse(currentTime)).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getImageResource(String site) {
        switch (site) {
            case "AtCoder":
                return R.drawable.ic_at_coder_logo;
            case "CodeChef":
                return R.drawable.ic_codechef_logo;
            case "CodeForces":
                return R.drawable.ic_codeforces_logo;
            case "HackerEarth":
                return R.drawable.ic_hacker_earth_logo;
            case "HackerRank":
                return R.drawable.ic_hackerrank_logo;
            case "Kick Start":
                return R.drawable.ic_kickstart_logo;
            case "LeetCode":
                return R.drawable.ic_leetcode_logo;
            case "TopCoder":
                return R.drawable.ic_topcoder_logo;
        }
        return 0;
    }

    private int getNum(String s) {
        if (s.charAt(1) == ' ') return Integer.parseInt(s.substring(0, 1));
        return Integer.parseInt(s.substring(0, 2));
    }

    private void setNotification(int time, ContestDetails contestDetails, Calendar start) {
        Intent intent = new Intent(context, ReminderBroadCast.class);
        intent.putExtra("ContestName", contestDetails.getContestName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(context).getSystemService(ALARM_SERVICE);
        long t1 = start.getTimeInMillis();
        long t2 = 60000 * time;
        Log.e("TAG", String.valueOf(t1 - t2));
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pendingIntent);
        Toast.makeText(context, "Reminder Set", Toast.LENGTH_SHORT).show();
    }
}
