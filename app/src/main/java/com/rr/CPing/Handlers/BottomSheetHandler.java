package com.rr.CPing.Handlers;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rr.CPing.Adapters.AllParentRecyclerViewAdapter;
import com.rr.CPing.Adapters.ContestDetailsRecyclerViewAdapter;
import com.rr.CPing.Model.AlarmIdClass;
import com.rr.CPing.Model.ContestDetails;
import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.Utils.ReminderBroadCast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import static android.content.Context.ALARM_SERVICE;

public class BottomSheetHandler {

    Context context;
    AlertDialog dialog;
    ContestDetailsRecyclerViewAdapter contestDetailsRecyclerViewAdapter;
    AllParentRecyclerViewAdapter allParentRecyclerViewAdapter;

    public BottomSheetHandler() {
    }

    @SuppressLint({"QueryPermissionsNeeded", "SetTextI18n"})
    public void showBottomSheetDialog(AllParentRecyclerViewAdapter allParentRecyclerViewAdapter,
                                      ContestDetailsRecyclerViewAdapter contestDetailsRecyclerViewAdapter,
                                      Context context,
                                      ArrayList<ContestDetails> contestsArrayList,
                                      int position,
                                      LayoutInflater layoutInflater) {
        this.context = context;
        this.contestDetailsRecyclerViewAdapter = contestDetailsRecyclerViewAdapter;
        this.allParentRecyclerViewAdapter = allParentRecyclerViewAdapter;

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

        ArrayList<AlarmIdClass> currentList =
                SharedPrefConfig.readInIdsOfReminderContests(context);
        int idx = getIndexFromList(currentList,
                contestsArrayList.get(position).getContestName());
        if (!currentList.isEmpty() && idx != -1) {
            if (currentList.get(idx).isGoogleReminderSet()) {
                googleRemainder.setVisibility(View.GONE);
            } else googleRemainder.setVisibility(View.VISIBLE);
            if (currentList.get(idx).isInAppReminderSet()) {
                appRemainder.setText("Edit in-app reminder");
            } else {
                appRemainder.setText("Set in-app reminder");
            }
        }

        platformImage.setImageResource(getImageResource(contestsArrayList.get(position).getSite()));
        platformTitle.setText(contestsArrayList.get(position).getSite());
        contestTitle.setText(contestsArrayList.get(position).getContestName());

        String properStartTime = properFormat(dateTimeHandler.getCompleteDetails(start)).toString();

        startTime.setText(properStartTime);
        endTime.setText(properFormat(dateTimeHandler.getCompleteDetails(end)));

        visitWebsite.setOnClickListener(v -> {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(contestsArrayList.get(position).getContestUrl())));
            dialog.cancel();
        });

        appRemainder.setOnClickListener(v -> {
            if (getTimeFromNow(start) / 60000 <= 5) {
                Toast.makeText(context, "This contest is going to start in less than 5 minutes!", Toast.LENGTH_SHORT).show();
            } else {
                showAlarmSelectorDialog(contestsArrayList.get(position), start, layoutInflater, properStartTime);
            }
            dialog.cancel();
        });

        googleRemainder.setOnClickListener(v -> {
            if (getTimeFromNow(start) / 60000 <= 5) {
                Toast.makeText(context, "This contest is going to start in less than 5 minutes!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setData(CalendarContract.Events.CONTENT_URI);
                intent.putExtra(CalendarContract.Events.TITLE, contestsArrayList.get(position).getContestName());
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start.getTimeInMillis());
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end.getTimeInMillis());

                if (intent.resolveActivity(Objects.requireNonNull(context).getPackageManager()) != null) {

                    ArrayList<AlarmIdClass> idClassArrayList =
                            SharedPrefConfig.readInIdsOfReminderContests(context);

                    int index = getIndexFromList(idClassArrayList, contestsArrayList.get(position).getContestName());

                    if (idClassArrayList.isEmpty() || index == -1) {
                        idClassArrayList.add(new AlarmIdClass(contestsArrayList.get(position).getContestName(),
                                getTimeFromNow(start),
                                System.currentTimeMillis(), false, true));
                    } else {
                        idClassArrayList.get(index).setGoogleReminderSet(true);
                    }
                    SharedPrefConfig.writeInIdsOfReminderContests(context, idClassArrayList);

                    if (allParentRecyclerViewAdapter == null)
                        contestDetailsRecyclerViewAdapter.notifyDataSetChanged();
                    else allParentRecyclerViewAdapter.notifyDataSetChanged();

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
                ans.insert(0, DateTimeHandler.hr_24To12Format(hr_24Time));
                i += 5;
                spaceCount++;
            } else ans.append(completeDetails.charAt(i));
        }
        return ans;
    }

    @SuppressLint("SetTextI18n")
    public void showAlarmSelectorDialog(ContestDetails contestDetails,
                                        Calendar start, LayoutInflater layoutInflater, String properStartTime) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = layoutInflater.inflate(R.layout.alarm_selector_popup_dialog, null);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();

        Button saveButton = view.findViewById(R.id.saveReminder),
                discardButton = view.findViewById(R.id.discardReminder);

        ArrayList<String> beforeTimesArray = new ArrayList<>();

        // Gives time in milli-seconds
        long timeFromNow = getTimeFromNow(start);
        //Gives Time in minutes
        timeFromNow /= 60000;

        for (long i = 5; i < Math.min(timeFromNow, 35); i += 5) {
            beforeTimesArray.add(i + " minutes");
        }

        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, beforeTimesArray);
        adapter.setDropDownViewResource(R.layout.drop_down_item);
        spinner.setAdapter(adapter);

        ArrayList<AlarmIdClass> currentList = SharedPrefConfig.readInIdsOfReminderContests(context);
        int index = getIndexFromList(currentList, contestDetails.getContestName());
//        int deleteNotificationTime;

        if (!currentList.isEmpty() && index != -1) {
            if (currentList.get(index).isInAppReminderSet()) {
                discardButton.setText("Delete");
//                deleteNotificationTime = (int) ((currentList.get(index).getStartTime() - currentList.get(index).getAlarmSetTime()) / 1000);
                spinner.setSelection(0);
            } else {
                discardButton.setText("Cancel");
                currentList.get(index).setInAppReminderSet(true);
            }
        }

        saveButton.setOnClickListener(v -> {
            Toast.makeText(context, "Reminder set!", Toast.LENGTH_SHORT).show();
            dialog.cancel();

            long id = System.currentTimeMillis();
            boolean temp = false;
            if (discardButton.getText().equals("Delete")) {
                deleteNotification(
                        currentList.get(index).getAlarmSetTime(), contestDetails.getContestName());

                if (currentList.get(index).isGoogleReminderSet()) {
                    currentList.get(index).setInAppReminderSet(false);
                    temp = true;
                } else currentList.remove(index);
            }

            if (temp) {
                currentList.get(index).setAlarmSetTime(id);
                currentList.get(index).setInAppReminderSet(true);
            } else currentList.add(new AlarmIdClass(contestDetails.getContestName(),
                    getTimeFromNow(start),
                    id, true, false));

            SharedPrefConfig.writeInIdsOfReminderContests(context, currentList);

            if (allParentRecyclerViewAdapter == null)
                contestDetailsRecyclerViewAdapter.notifyDataSetChanged();
            else allParentRecyclerViewAdapter.notifyDataSetChanged();

            setNotification(context, getNum(spinner.getSelectedItem().toString()),
                    contestDetails.getContestName(), start,
                    id / 1000, false, properStartTime);
        });

        discardButton.setOnClickListener(v -> {
            dialog.cancel();
            if (discardButton.getText().equals("Delete")) {
                deleteNotification(currentList.get(index).getAlarmSetTime(),
                        contestDetails.getContestName());

                if (currentList.get(index).isGoogleReminderSet()) {
                    currentList.get(index).setInAppReminderSet(false);
                } else currentList.remove(index);

                SharedPrefConfig.writeInIdsOfReminderContests(context, currentList);

                if (allParentRecyclerViewAdapter == null)
                    contestDetailsRecyclerViewAdapter.notifyDataSetChanged();
                else allParentRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    private int getIndexFromList(ArrayList<AlarmIdClass> currentList, String contestName) {
        for (int i = 0; i < currentList.size(); ++i) {
            if (currentList.get(i).getContestNameAsID().equals(contestName)) return i;
        }
        return -1;
    }

    private long getTimeFromNow(Calendar startTime) {
        return startTime.getTimeInMillis() - System.currentTimeMillis();
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
                return R.drawable.ic_hackerearth_logo;
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

    public void setNotification(Context context, int time, String contestName, Calendar start,
                                long id, boolean isSnooze, String properStartTime) {
        Intent intent = new Intent(context, ReminderBroadCast.class);
        intent.putExtra("ContestName", contestName);
        intent.putExtra("ProperStartTime", properStartTime);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(context).getSystemService(ALARM_SERVICE);

        long t1 = isSnooze ? System.currentTimeMillis() : start.getTimeInMillis();
        long t2 = 60000 * time;

        Log.e("TAG t1-t2", t1 + " , " + (t1 - t2));
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pendingIntent);
    }

    private void deleteNotification(long id, String contestName) {
        Intent intent = new Intent(context, ReminderBroadCast.class);
        intent.putExtra("ContestName", contestName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(context).getSystemService(ALARM_SERVICE);
        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
    }
}
