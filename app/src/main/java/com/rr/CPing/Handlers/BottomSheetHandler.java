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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rr.CPing.Adapters.AllParentRecyclerViewAdapter;
import com.rr.CPing.Adapters.ContestDetailsRecyclerViewAdapter;
import com.rr.CPing.Model.AlarmIdClass;
import com.rr.CPing.Model.ContestDetails;
import com.rr.CPing.Model.HiddenContestsClass;
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
        ImageButton contestShareButton = dialog.findViewById(R.id.bottom_sheet_share_contest),
                contestDeleteButton = dialog.findViewById(R.id.bottom_sheet_delete_contest);

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

        ArrayList<AlarmIdClass> currentList = SharedPrefConfig.readInIdsOfReminderContests(context);
        int idx = getIndexFromList(currentList, contestsArrayList.get(position).getContestName());
        if (!currentList.isEmpty() && idx != -1)
            appRemainder.setText("Edit in-app reminder");
        else appRemainder.setText("Set in-app reminder");

        platformImage.setImageResource(getImageResource(contestsArrayList.get(position).getSite()));
        platformTitle.setText(contestsArrayList.get(position).getSite());
        contestTitle.setText(contestsArrayList.get(position).getContestName());

        String properStartTime = properFormat(dateTimeHandler.getCompleteDetails(start)).toString();
        String properEndTime = properFormat(dateTimeHandler.getCompleteDetails(end)).toString();

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
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "No application found supporting this feature!",
                            Toast.LENGTH_SHORT).show();
                }
            }
            dialog.cancel();
        });

        contestShareButton.setOnClickListener(v -> {
            ContestDetails contestDetails = contestsArrayList.get(position);

            String message = "Hey Coder! Get ready for: \n\n" + contestDetails.getContestName().toUpperCase() + "\n\nStart : " + properStartTime + "\nEnd  : " + properEndTime + "\n\n" + contestDetails.getContestUrl();

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);

            shareIntent.setType("text/*");

            context.startActivity(Intent.createChooser(shareIntent, contestDetails.getContestName()));
            dialog.cancel();
        });

        contestDeleteButton.setOnClickListener(v -> {
            ContestDetails contestDetails = contestsArrayList.get(position);

            ArrayList<HiddenContestsClass> hiddenContestsArrayList = SharedPrefConfig.readInHiddenContests(context);
            hiddenContestsArrayList.add(new HiddenContestsClass(contestDetails.getContestName(), contestDetails.getSite(), end.getTimeInMillis()));
            SharedPrefConfig.writeInHiddenContests(context, hiddenContestsArrayList);

            contestsArrayList.remove(position);

            if (!currentList.isEmpty() && idx != -1) {
                deleteNotification(currentList.get(idx).getAlarmSetTime(), contestDetails.getContestName(), properStartTime);
                currentList.remove(idx);
                SharedPrefConfig.writeInIdsOfReminderContests(context, currentList);
            }

            // Delete reminder if set
            if (!currentList.isEmpty() && idx != -1) {
                deleteNotification(currentList.get(idx).getAlarmSetTime(), contestDetails.getContestName(), properStartTime);
                currentList.remove(idx);

                SharedPrefConfig.writeInIdsOfReminderContests(context, currentList);
            }

            if (allParentRecyclerViewAdapter == null)
                contestDetailsRecyclerViewAdapter.notifyDataSetChanged();
            else allParentRecyclerViewAdapter.notifyDataSetChanged();

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
        dialog.getWindow().setWindowAnimations(R.style.PopupDialogAnimation);
        dialog.show();

        Button saveButton = view.findViewById(R.id.saveReminder),
                discardButton = view.findViewById(R.id.discardReminder);

        ArrayList<String> beforeTimesArray = new ArrayList<>();

        // Gives time in milli-seconds
        long timeFromNow = getTimeFromNow(start);
        //Gives Time in minutes
        timeFromNow /= 60000;

        for (long i = 5; i < Math.min(timeFromNow, 65); i += 5) {
            if (i == 30) beforeTimesArray.add("½ hr");
            else if (i == 60) beforeTimesArray.add("1 hr");
            else beforeTimesArray.add(i + " minutes");
        }

        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, beforeTimesArray);
        adapter.setDropDownViewResource(R.layout.drop_down_item);
        spinner.setAdapter(adapter);

        ArrayList<AlarmIdClass> currentList = SharedPrefConfig.readInIdsOfReminderContests(context);
        int index = getIndexFromList(currentList, contestDetails.getContestName());

        if (!currentList.isEmpty() && index != -1) {
            discardButton.setText("Delete");
            if (currentList.get(index).getSpinnerPosition() >= adapter.getCount())
                spinner.setSelection(adapter.getCount() - 1);
            else spinner.setSelection(currentList.get(index).getSpinnerPosition());
        } else discardButton.setText("Cancel");

        saveButton.setOnClickListener(v -> {
            Toast.makeText(context, "Reminder set!", Toast.LENGTH_SHORT).show();
            dialog.cancel();

            long id = start.getTimeInMillis() - getNum(spinner.getSelectedItem().toString()) * 60000;
            if (discardButton.getText().equals("Delete")) {
                deleteNotification(currentList.get(index).getAlarmSetTime(), contestDetails.getContestName(), properStartTime);
                currentList.remove(index);
            }

            currentList.add(new AlarmIdClass(contestDetails.getContestName(),
                    start.getTimeInMillis(), id, getPosition(spinner.getSelectedItem().toString()), properStartTime));

            SharedPrefConfig.writeInIdsOfReminderContests(context, currentList);

            if (allParentRecyclerViewAdapter == null)
                contestDetailsRecyclerViewAdapter.notifyDataSetChanged();
            else allParentRecyclerViewAdapter.notifyDataSetChanged();

            setNotification(context, getNum(spinner.getSelectedItem().toString()),
                    contestDetails.getContestName(), start.getTimeInMillis(),
                    id, properStartTime);
        });

        discardButton.setOnClickListener(v -> {
            dialog.cancel();
            if (discardButton.getText().equals("Delete")) {
                deleteNotification(currentList.get(index).getAlarmSetTime(), contestDetails.getContestName(), properStartTime);
                currentList.remove(index);

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
        String[] arrayList = s.split(" ");
        if (arrayList[0].equals("½")) return 30;
        if (arrayList[0].equals("1")) return 60;
        return Integer.parseInt(arrayList[0]);
    }

    private int getPosition(String s) {
        String[] arrayList = s.split(" ");
        if (arrayList[0].equals("½")) return 5;
        if (arrayList[0].equals("1")) return 11;
        return Integer.parseInt(arrayList[0]) / 5 - 1;
    }

    public void setNotification(Context context, int time, String contestName, long startTimeInMillis, long id, String properStartTime) {
        Intent intent = new Intent(context, ReminderBroadCast.class);
        intent.putExtra("ContestName", contestName);
        intent.putExtra("ProperStartTime", properStartTime);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(context).getSystemService(ALARM_SERVICE);

        long t2 = 60000 * time;

//        Log.e("TAG t1-t2", startTimeInMillis + " , " + time + " , " + (startTimeInMillis - t2));
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (startTimeInMillis - t2), pendingIntent);
    }

    private void deleteNotification(long id, String contestName, String properStartTime) {
        Intent intent = new Intent(context, ReminderBroadCast.class);
        intent.putExtra("ContestName", contestName);
        intent.putExtra("ProperStartTime", properStartTime);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(context).getSystemService(ALARM_SERVICE);
        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
    }
}
