package com.rr.CPing.fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.adapters.AllParentRecyclerViewAdapter;
import com.rr.CPing.database.JSONResponseDBHandler;
import com.rr.CPing.model.AtCoderUserDetails;
import com.rr.CPing.model.CodeChefUserDetails;
import com.rr.CPing.model.CodeForcesUserDetails;
import com.rr.CPing.model.ContestDetails;
import com.rr.CPing.model.DateTimeHandler;
import com.rr.CPing.model.PlatformDetails;
import com.rr.CPing.model.PlatformListItem;
import com.rr.CPing.model.SetRankColor;
import com.rr.CPing.util.ReminderBroadCast;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static android.content.Context.ALARM_SERVICE;

public class AllFragment extends Fragment {

    private final ArrayList<PlatformDetails> ongoingPlatformsArrayList = new ArrayList<>();
    private final ArrayList<PlatformDetails> todayPlatformsArrayList = new ArrayList<>();
    private final ArrayList<PlatformDetails> futurePlatformsArrayList = new ArrayList<>();

    private SwipeRefreshLayout allSwipeRefreshLayout;
    private View groupFragmentView;
    private GraphView graphView;
    private TextView ongoing_nothing, today_nothing, future_nothing, allRatingsChangeTextView;
    private LinearLayout codeForcesRatingChanges, codeChefRatingChanges, atCoderRatingChanges;
    private CardView allGraphsCardView, allRatingsCardView;
    private TextView codeForcesRating, codeForcesRank, codeChefRating, codeChefStars, atCoderRating, atCoderLevel;
    private TextView codeForcesGraphBelow, codeChefGraphBelow, atCoderGraphBelow;
    private ArrayList<String> platforms;
    private RecyclerView ongoingRV, todayRV, futureRV;
    private AllParentRecyclerViewAdapter ongoingRVA, todayRVA, futureRVA;

    private SetRankColor setRankColor;
    private DateTimeHandler dateTimeHandler;

    public AllFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JSONResponseDBHandler jsonResponseDBHandler = new JSONResponseDBHandler(getContext());

        platforms = new ArrayList<>();
        setRankColor = new SetRankColor(getContext());
        dateTimeHandler = new DateTimeHandler();

        ArrayList<PlatformListItem> platformListItemArrayList = SharedPrefConfig.readPlatformsSelected(getContext());

        for (int i = 0; i < platformListItemArrayList.size(); i++) {
            if (platformListItemArrayList.get(i).isEnabled()) {
                platforms.add(platformListItemArrayList.get(i).getPlatformName());
            }
        }

        Collections.sort(platforms);

        for (String platform : platforms) {
            ArrayList<ContestDetails> contestDetailsArrayList = jsonResponseDBHandler.getPlatformDetails(platform);

            ArrayList<ContestDetails> ongoingContestsArrayList = new ArrayList<>(),
                    todayContestsArrayList = new ArrayList<>(),
                    futureContestsArrayList = new ArrayList<>();

            for (ContestDetails cd : contestDetailsArrayList) {
                if (isGreaterThan10days(cd.getContestDuration()))
                    continue;
                if (!cd.getIsToday().equals("No")) {
                    todayContestsArrayList.add(cd);
                } else if (cd.getContestStatus().equals("CODING")) {
                    ongoingContestsArrayList.add(cd);
                } else {
                    futureContestsArrayList.add(cd);
                }
            }

            if (!ongoingContestsArrayList.isEmpty())
                ongoingPlatformsArrayList.add(new PlatformDetails(platform, ongoingContestsArrayList));
            if (!todayContestsArrayList.isEmpty())
                todayPlatformsArrayList.add(new PlatformDetails(platform, todayContestsArrayList));
            if (!futureContestsArrayList.isEmpty())
                futurePlatformsArrayList.add(new PlatformDetails(platform, futureContestsArrayList));
        }
    }

    private boolean isGreaterThan10days(int contestDuration) {
        return ((contestDuration / 3600.0) / 24.0 > 10);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView = inflater.inflate(R.layout.fragment_all, container, false);

        findViewsByIds();

        allSwipeRefreshLayout.setOnRefreshListener(() -> {
            // TODO: Data should be updated here
            allSwipeRefreshLayout.setRefreshing(false);
        });

        if (ongoingPlatformsArrayList.isEmpty()) {
            ongoing_nothing.setVisibility(View.VISIBLE);
            ongoingRV.setVisibility(View.GONE);
        } else {
            ongoing_nothing.setVisibility(View.GONE);
            ongoingRV.setVisibility(View.VISIBLE);
        }
        if (todayPlatformsArrayList.isEmpty()) {
            today_nothing.setVisibility(View.VISIBLE);
            todayRV.setVisibility(View.GONE);
        } else {
            today_nothing.setVisibility(View.GONE);
            todayRV.setVisibility(View.VISIBLE);
        }
        if (futurePlatformsArrayList.isEmpty()) {
            future_nothing.setVisibility(View.VISIBLE);
            futureRV.setVisibility(View.GONE);
        } else {
            future_nothing.setVisibility(View.GONE);
            futureRV.setVisibility(View.VISIBLE);
        }

        // 0 -> Ongoing
        initialize(0);
        // 1 -> Today
        initialize(1);
        // 2 -> Future
        initialize(2);

        // AtCoder Rating Cards
        if (platforms.contains("AtCoder")) {
            AtCoderUserDetails atCoderUserDetails = SharedPrefConfig.readInAtCoderPref(getContext());
            atCoderRating.setText(String.valueOf(atCoderUserDetails.getCurrentRating()));
            atCoderLevel.setText(atCoderUserDetails.getCurrentLevel());

            atCoderLevel.setTextColor(setRankColor.getAtCoderColor(atCoderUserDetails.getCurrentLevel()));
        } else {
            atCoderGraphBelow.setVisibility(View.GONE);
            atCoderRatingChanges.setVisibility(View.GONE);
        }

        // CodeForces Graph
        int maxY = 0, minY = Integer.MAX_VALUE;
        ArrayList<String> codeForcesRecentRatingsArrayList = new ArrayList<>();

        if (platforms.contains("CodeForces")) {
            CodeForcesUserDetails codeForcesUserDetails = SharedPrefConfig.readInCodeForcesPref(getContext());
            codeForcesRating.setText(String.valueOf(codeForcesUserDetails.getCurrentRating()));
            codeForcesRank.setText(codeForcesUserDetails.getCurrentRank());

            codeForcesRank.setTextColor(setRankColor.getCodeforcesColor(codeForcesUserDetails.getCurrentRank()));

            codeForcesRecentRatingsArrayList = codeForcesUserDetails.getRecentContestRatings();

            DataPoint[] codeForcesValues = new DataPoint[codeForcesRecentRatingsArrayList.size()];

            for (int i = 0; i < codeForcesRecentRatingsArrayList.size(); i++) {
                int temp = Integer.parseInt(codeForcesRecentRatingsArrayList.get(i));
                maxY = Math.max(maxY, temp);
                minY = Math.min(minY, temp);
                codeForcesValues[i] = new DataPoint(i, temp);
            }

            LineGraphSeries<DataPoint> codeForcesSeries = new LineGraphSeries<>(codeForcesValues);
            codeForcesSeries.setColor(Color.rgb(72, 221, 205));
            codeForcesSeries.setDrawDataPoints(true);

            graphView.addSeries(codeForcesSeries);
        } else {
            codeForcesGraphBelow.setVisibility(View.GONE);
            codeForcesRatingChanges.setVisibility(View.GONE);
        }

        // CodeChef Graph
        ArrayList<Integer> codeChefRecentRatingsArrayList = new ArrayList<>();

        if (platforms.contains("CodeChef")) {
            CodeChefUserDetails codeChefUserDetails = SharedPrefConfig.readInCodeChefPref(getContext());

            codeChefRating.setText(String.valueOf(codeChefUserDetails.getCurrentRating()));
            codeChefStars.setText(codeChefUserDetails.getCurrentStars());

            codeChefStars.setTextColor(setRankColor.getCodeChefColor(codeChefUserDetails.getCurrentStars()));

            codeChefRecentRatingsArrayList = codeChefUserDetails.getRecentContestRatings();
            DataPoint[] codeChefValues = new DataPoint[codeChefRecentRatingsArrayList.size()];

            for (int i = 0; i < codeChefRecentRatingsArrayList.size(); ++i) {
                int temp = codeChefRecentRatingsArrayList.get(i);
                maxY = Math.max(maxY, temp);
                minY = Math.min(minY, temp);
                codeChefValues[i] = new DataPoint(i, temp);
            }

            LineGraphSeries<DataPoint> codeChefSeries = new LineGraphSeries<>(codeChefValues);
            codeChefSeries.setColor(Color.rgb(255, 164, 161));
            codeChefSeries.setDrawDataPoints(true);

            graphView.addSeries(codeChefSeries);
        } else {
            codeChefGraphBelow.setVisibility(View.GONE);
            codeChefRatingChanges.setVisibility(View.GONE);
        }

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxX(Math.max(codeChefRecentRatingsArrayList.size(), codeForcesRecentRatingsArrayList.size()));
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMaxY(maxY);
        graphView.getViewport().setMinY(minY);

        if (!platforms.contains("CodeForces") && !platforms.contains("CodeChef")) {
            allGraphsCardView.setVisibility(View.GONE);
            if (!platforms.contains("AtCoder")) {
                allRatingsCardView.setVisibility(View.GONE);
                allRatingsChangeTextView.setVisibility(View.GONE);
            }
        }

        // On Item Click Listener (Reminders, Visiting Website)

        ongoingRVA.setOnItemClickListener((platFormName, position) -> showBottomSheetDialog(getPlatformDetails(ongoingPlatformsArrayList, platFormName), position));
        todayRVA.setOnItemClickListener((platFormName, position) -> showBottomSheetDialog(getPlatformDetails(todayPlatformsArrayList, platFormName), position));
        futureRVA.setOnItemClickListener((platFormName, position) -> showBottomSheetDialog(getPlatformDetails(futurePlatformsArrayList, platFormName), position));

        return groupFragmentView;
    }

    private ArrayList<ContestDetails> getPlatformDetails(ArrayList<PlatformDetails> PlatformsArrayList, String platFormName) {
        for (PlatformDetails platformDetails : PlatformsArrayList) {
            if (platformDetails.getPlatformName().equals(platFormName)) {
                return platformDetails.getPlatformContests();
            }
        }
        return new ArrayList<>();
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void showBottomSheetDialog(ArrayList<ContestDetails> contestsArrayList, int position) {
        final Dialog dialog = new Dialog(getContext());
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
        startTime.setText(dateTimeHandler.getCompleteDetails(start));
        endTime.setText(dateTimeHandler.getCompleteDetails(end));

        visitWebsite.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(contestsArrayList.get(position).getContestUrl())));
            dialog.cancel();
        });

        appRemainder.setOnClickListener(v -> {
            dialog.cancel();
            showAlarmSelectorDialog(contestsArrayList.get(position), start);
        });

        googleRemainder.setOnClickListener(v -> {
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

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomSheetAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showAlarmSelectorDialog(ContestDetails contestDetails, Calendar start){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alarm_selector_layout);

        Spinner spinner = dialog.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.times, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        dialog.show();

        dialog.findViewById(R.id.saveReminder).setOnClickListener(view -> {
            setNotification(getNum(spinner.getSelectedItem().toString()), contestDetails, start);
            dialog.cancel();
        });

        dialog.findViewById(R.id.discardReminder).setOnClickListener(view -> dialog.cancel());
    }

    private int getNum(String s){
        if(s.charAt(1)==' ') return Integer.parseInt(s.substring(0,1));
        return Integer.parseInt(s.substring(0,2));
    }

    private void setNotification(int time, ContestDetails contestDetails, Calendar start){
        Intent intent = new Intent(getContext(), ReminderBroadCast.class);
        intent.putExtra("ContestName", contestDetails.getContestName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), (int) System.currentTimeMillis(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(getActivity()).getSystemService(ALARM_SERVICE);
        long t1 = start.getTimeInMillis();
        long t2 = 60000 * time;
        Log.e("TAG", String.valueOf(t1-t2));
        alarmManager.set(AlarmManager.RTC_WAKEUP, t1-t2, pendingIntent);
        Toast.makeText(getContext(), "Reminder Set", Toast.LENGTH_SHORT).show();
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

    private void findViewsByIds() {

        allSwipeRefreshLayout = groupFragmentView.findViewById(R.id.all_swipe_refresh);

        allRatingsChangeTextView = groupFragmentView.findViewById(R.id.all_rating_changes_text_view);
        allGraphsCardView = groupFragmentView.findViewById(R.id.all_graphs_card_view);
        allRatingsCardView = groupFragmentView.findViewById(R.id.all_ratings_card_view);

        // Rating Changes Cards

        codeForcesRatingChanges = groupFragmentView.findViewById(R.id.all_code_forces_rating_card);
        codeChefRatingChanges = groupFragmentView.findViewById(R.id.all_code_chef_rating_card);
//        leetCodeRatingChanges = groupFragmentView.findViewById(R.id.all_leet_code_rating_card);
        atCoderRatingChanges = groupFragmentView.findViewById(R.id.all_at_coder_rating_card);

        codeForcesRating = groupFragmentView.findViewById(R.id.all_code_forces_current_rating);
        codeForcesRank = groupFragmentView.findViewById(R.id.all_code_forces_current_rank);
        codeChefRating = groupFragmentView.findViewById(R.id.all_code_chef_current_rating);
        codeChefStars = groupFragmentView.findViewById(R.id.all_code_chef_current_stars);
//        leetCodeRating = groupFragmentView.findViewById(R.id.all_leet_code_current_rating);
//        leetCodeRank = groupFragmentView.findViewById(R.id.all_leet_code_current_rank);
        atCoderRating = groupFragmentView.findViewById(R.id.all_at_coder_current_rating);
        atCoderLevel = groupFragmentView.findViewById(R.id.all_at_coder_current_level);

        codeForcesGraphBelow = groupFragmentView.findViewById(R.id.code_forces_graph_below);
        codeChefGraphBelow = groupFragmentView.findViewById(R.id.code_chef_graph_below);
//        leetCodeGraphBelow = groupFragmentView.findViewById(R.id.leet_code_graph_below);
        atCoderGraphBelow = groupFragmentView.findViewById(R.id.at_coder_graph_below);

        // Contest Details

        ongoing_nothing = groupFragmentView.findViewById(R.id.all_ongoing_nothing);
        today_nothing = groupFragmentView.findViewById(R.id.all_today_nothing);
        future_nothing = groupFragmentView.findViewById(R.id.all_future_nothing);

        ongoingRV = groupFragmentView.findViewById(R.id.all_ongoing_contests_recycler_view);
        todayRV = groupFragmentView.findViewById(R.id.all_today_contests_recycler_view);
        futureRV = groupFragmentView.findViewById(R.id.all_future_contests_recycler_view);

        graphView = groupFragmentView.findViewById(R.id.all_graph_view);
    }

    private void initialize(int i) {
        if (i == 0) {
            ongoingRV.setHasFixedSize(true);
            ongoingRV.setLayoutManager(new LinearLayoutManager(getContext()));
            ongoingRVA = new AllParentRecyclerViewAdapter(ongoingPlatformsArrayList);
            ongoingRV.setAdapter(ongoingRVA);
            ongoingRVA.notifyDataSetChanged();
        } else if (i == 1) {
            todayRV.setHasFixedSize(true);
            todayRV.setLayoutManager(new LinearLayoutManager(getContext()));
            todayRVA = new AllParentRecyclerViewAdapter(todayPlatformsArrayList);
            todayRV.setAdapter(todayRVA);
            todayRVA.notifyDataSetChanged();
        } else {
            futureRV.setHasFixedSize(true);
            futureRV.setLayoutManager(new LinearLayoutManager(getContext()));
            futureRVA = new AllParentRecyclerViewAdapter(futurePlatformsArrayList);
            futureRV.setAdapter(futureRVA);
            futureRVA.notifyDataSetChanged();
        }
    }
}