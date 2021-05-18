package com.rr.CPing.fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.rr.CPing.R;
import com.rr.CPing.model.SetRankColor;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.adapters.ContestDetailsRecyclerViewAdapter;
import com.rr.CPing.model.CodeForcesUserDetails;
import com.rr.CPing.model.ContestDetails;
import com.rr.CPing.database.JSONResponseDBHandler;
import com.rr.CPing.util.ReminderBroadCast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static android.content.Context.ALARM_SERVICE;

public class CodeForcesFragment extends Fragment {

    private final ArrayList<ContestDetails> ongoingContestsArrayList = new ArrayList<>();
    private final ArrayList<ContestDetails> todayContestsArrayList = new ArrayList<>();
    private final ArrayList<ContestDetails> futureContestsArrayList = new ArrayList<>();
    private SwipeRefreshLayout codeForcesSwipeRefreshLayout;
    private View groupFragmentView;
    private TextView codeForcesUserName, currentRating, currentRank, maxRating, maxRank;
    private TextView ongoing_nothing, today_nothing, future_nothing;
    private RecyclerView OngoingRV, TodayRV, FutureRV;
    private ContestDetailsRecyclerViewAdapter ongoingRVA, todayRVA, futureRVA;
    private AlertDialog dialog;
    private SetRankColor setRankColor;

    public CodeForcesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JSONResponseDBHandler jsonResponseDBHandler = new JSONResponseDBHandler(getContext());
        ArrayList<ContestDetails> contestDetailsArrayList = jsonResponseDBHandler.getPlatformDetails("CodeForces");
        setRankColor = new SetRankColor(getContext());

        for (ContestDetails cd : contestDetailsArrayList) {
            if (!cd.getIsToday().equals("No")) {
                todayContestsArrayList.add(cd);
            } else if (cd.getContestStatus().equals("CODING")) {
                ongoingContestsArrayList.add(cd);
            } else {
                futureContestsArrayList.add(cd);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView = inflater.inflate(R.layout.fragment_code_forces, container, false);

        findViewsByIds();

        codeForcesSwipeRefreshLayout.setOnRefreshListener(() -> {
            // TODO: To be implemented
            codeForcesSwipeRefreshLayout.setRefreshing(false);
        });

        if (ongoingContestsArrayList.isEmpty()) {
            ongoing_nothing.setVisibility(View.VISIBLE);
            OngoingRV.setVisibility(View.GONE);
        } else {
            ongoing_nothing.setVisibility(View.GONE);
            OngoingRV.setVisibility(View.VISIBLE);
        }

        if (todayContestsArrayList.isEmpty()) {
            today_nothing.setVisibility(View.VISIBLE);
            TodayRV.setVisibility(View.GONE);
        } else {
            today_nothing.setVisibility(View.GONE);
            TodayRV.setVisibility(View.VISIBLE);
        }

        if (futureContestsArrayList.isEmpty()) {
            future_nothing.setVisibility(View.VISIBLE);
            FutureRV.setVisibility(View.GONE);
        } else {
            future_nothing.setVisibility(View.GONE);
            FutureRV.setVisibility(View.VISIBLE);
        }

        // 0 -> Ongoing
        initialize(0);
        // 1 -> Today
        initialize(1);
        // 2 -> Future
        initialize(2);

        // Ratings and graph

        CodeForcesUserDetails codeForcesUserDetails = SharedPrefConfig.readInCodeForcesPref(getContext());

        codeForcesUserName.setText(String.format("@%s", codeForcesUserDetails.getUserName()));
        currentRating.setText(String.valueOf(codeForcesUserDetails.getCurrentRating()));
        currentRank.setText(codeForcesUserDetails.getCurrentRank());
        maxRating.setText(String.valueOf(codeForcesUserDetails.getMaxRating()));
        maxRank.setText(codeForcesUserDetails.getMaxRank());

//        setColors(codeForcesUserDetails.getCurrentRank(), codeForcesUserDetails.getMaxRank());
        currentRank.setTextColor(setRankColor.getCodeforcesColor(codeForcesUserDetails.getCurrentRank()));
        maxRank.setTextColor(setRankColor.getCodeforcesColor(codeForcesUserDetails.getMaxRank()));

        GraphView graphView = groupFragmentView.findViewById(R.id.codeForces_graph_view);

        ArrayList<String> recentRatingsArrayList = codeForcesUserDetails.getRecentContestRatings();

        DataPoint[] values = new DataPoint[recentRatingsArrayList.size()];
        int maxY = 0, minY = Integer.MAX_VALUE;
        for (int i = 0; i < recentRatingsArrayList.size(); i++) {
            int temp = Integer.parseInt(recentRatingsArrayList.get(i));
            maxY = Math.max(maxY, temp);
            minY = Math.min(minY, temp);
            values[i] = new DataPoint(i, temp);
        }

        LineGraphSeries<DataPoint> codeForcesSeries = new LineGraphSeries<>(values);
        codeForcesSeries.setColor(Color.rgb(72, 221, 205));
        codeForcesSeries.setDrawDataPoints(true);

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxX(recentRatingsArrayList.size());
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMaxY(maxY);
        graphView.getViewport().setMinY(minY);

        graphView.addSeries(codeForcesSeries);

        // On Item Click Listener (Reminders, Visiting Website)

        ongoingRVA.setOnItemClickListener((platFormName, position) -> createPopupDialog(ongoingContestsArrayList, position));
        todayRVA.setOnItemClickListener((platFormName, position) -> createPopupDialog(todayContestsArrayList, position));
        futureRVA.setOnItemClickListener((platFormName, position) -> createPopupDialog(futureContestsArrayList, position));

        return groupFragmentView;
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void createPopupDialog(ArrayList<ContestDetails> contestsArrayList, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.contest_popup_dialog, null);

        TextView platformTitle = view.findViewById(R.id.platform_title),
                contestTitle = view.findViewById(R.id.contest_title),
                startTime = view.findViewById(R.id.start_time),
                endTime = view.findViewById(R.id.end_time),
                visitWebsite = view.findViewById(R.id.visit_website),
                appRemainder = view.findViewById(R.id.in_app_remainder),
                googleRemainder = view.findViewById(R.id.google_remainder);
        ImageView platformImage = view.findViewById(R.id.platform_title_image);

        if (contestsArrayList.get(position).getContestStatus().equals("CODING")) {
            appRemainder.setVisibility(View.GONE);
            googleRemainder.setVisibility(View.GONE);
        } else {
            appRemainder.setVisibility(View.VISIBLE);
            googleRemainder.setVisibility(View.VISIBLE);
        }

        platformImage.setImageResource(R.drawable.ic_codeforces_logo);
        platformTitle.setText(contestsArrayList.get(position).getSite());
        contestTitle.setText(contestsArrayList.get(position).getContestName());
        startTime.setText(contestsArrayList.get(position).getContestStartTime());
        endTime.setText(contestsArrayList.get(position).getContestEndTime());

        visitWebsite.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(contestsArrayList.get(position).getContestUrl())));
            dialog.cancel();
        });

        appRemainder.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Reminder Set", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), ReminderBroadCast.class);
            intent.putExtra("ContestName", contestsArrayList.get(position).getContestName());
            Log.e("TAG", contestsArrayList.get(position).getContestName());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), (int) System.currentTimeMillis(), intent, 0);
            AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(getActivity()).getSystemService(ALARM_SERVICE);
            long t1 = System.currentTimeMillis();
            long t2 = 1000 * 10;
            alarmManager.set(AlarmManager.RTC_WAKEUP, t1 + t2, pendingIntent);
            dialog.cancel();
//            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            i.addCategory(Intent.CATEGORY_DEFAULT);
//            i.setData(Uri.parse("package:" + getActivity().getPackageName()));
//            startActivity(i);
        });

        googleRemainder.setOnClickListener(v -> {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            Date startDate = convertISO8601ToDate(contestsArrayList.get(position).getContestStartTime());
            Date endDate = convertISO8601ToDate(contestsArrayList.get(position).getContestEndTime());
            if (startDate != null) start.setTime(startDate);
            if (endDate != null) end.setTime(endDate);

            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setData(CalendarContract.Events.CONTENT_URI);
            intent.putExtra(CalendarContract.Events.TITLE, contestsArrayList.get(position).getContestName());
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start.getTimeInMillis());
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end.getTimeInMillis());

            if (intent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "No application found supporting this feature!", Toast.LENGTH_SHORT).show();
            }
            dialog.cancel();
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private Date convertISO8601ToDate(String dateString) {
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void findViewsByIds() {
        codeForcesSwipeRefreshLayout = groupFragmentView.findViewById(R.id.codeForces_swipe_refresh);

        codeForcesUserName = groupFragmentView.findViewById(R.id.codeForces_user_name);
        currentRating = groupFragmentView.findViewById(R.id.codeForces_current_rating);
        currentRank = groupFragmentView.findViewById(R.id.codeForces_current_rank);
        maxRating = groupFragmentView.findViewById(R.id.codeForces_max_rating);
        maxRank = groupFragmentView.findViewById(R.id.codeForces_max_rank);

        ongoing_nothing = groupFragmentView.findViewById(R.id.codeForces_ongoing_nothing);
        today_nothing = groupFragmentView.findViewById(R.id.codeForces_today_nothing);
        future_nothing = groupFragmentView.findViewById(R.id.codeForces_future_nothing);

        OngoingRV = groupFragmentView.findViewById(R.id.codeForces_ongoing_recycler_view);
        TodayRV = groupFragmentView.findViewById(R.id.codeForces_today_recycler_view);
        FutureRV = groupFragmentView.findViewById(R.id.codeForces_future_recyclerView);
    }

    private void initialize(int i) {
        if (i == 0) {
            OngoingRV.setHasFixedSize(true);
            OngoingRV.setLayoutManager(new LinearLayoutManager(getContext()));
            ongoingRVA = new ContestDetailsRecyclerViewAdapter(ongoingContestsArrayList);
            OngoingRV.setAdapter(ongoingRVA);
            ongoingRVA.notifyDataSetChanged();
        } else if (i == 1) {
            TodayRV.setHasFixedSize(true);
            TodayRV.setLayoutManager(new LinearLayoutManager(getContext()));
            todayRVA = new ContestDetailsRecyclerViewAdapter(todayContestsArrayList);
            TodayRV.setAdapter(todayRVA);
            todayRVA.notifyDataSetChanged();
        } else {
            FutureRV.setHasFixedSize(true);
            FutureRV.setLayoutManager(new LinearLayoutManager(getContext()));
            futureRVA = new ContestDetailsRecyclerViewAdapter(futureContestsArrayList);
            FutureRV.setAdapter(futureRVA);
            futureRVA.notifyDataSetChanged();
        }
    }
}