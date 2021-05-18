package com.rr.CPing.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.adapters.ContestDetailsRecyclerViewAdapter;
import com.rr.CPing.model.ContestDetails;
import com.rr.CPing.model.LeetCodeUserDetails;
import com.rr.CPing.database.JSONResponseDBHandler;

import java.util.ArrayList;

public class LeetCodeFragment extends Fragment {

    private final ArrayList<ContestDetails> ongoingContestsArrayList = new ArrayList<>();
    private final ArrayList<ContestDetails> todayContestsArrayList = new ArrayList<>();
    private final ArrayList<ContestDetails> futureContestsArrayList = new ArrayList<>();
    private SwipeRefreshLayout leetCodeSwipeRefreshLayout;
    private View groupFragmentView;
    private TextView ongoing_nothing, today_nothing, future_nothing;
    private SeekBar hardSeekBar, mediumSeekBar, easySeekBar;
    private TextView leetCodeUserName, acceptanceRate, totalProblemsSolved, leetCodeMedium, leetCodeEasy, leetCodeHard;
    private RecyclerView OngoingRV, TodayRV, FutureRV;
    private ContestDetailsRecyclerViewAdapter ongoingRVA, todayRVA, futureRVA;
    private AlertDialog dialog;

    public LeetCodeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JSONResponseDBHandler jsonResponseDBHandler = new JSONResponseDBHandler(getContext());
        ArrayList<ContestDetails> contestDetailsArrayList = jsonResponseDBHandler.getPlatformDetails("LeetCode");

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

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        groupFragmentView = inflater.inflate(R.layout.fragment_leet_code, container, false);

        findViewsByIds();

        leetCodeSwipeRefreshLayout.setOnRefreshListener(() -> {
            // TODO: To be implemented
            leetCodeSwipeRefreshLayout.setRefreshing(false);
        });

        hardSeekBar.setOnTouchListener((v, event) -> false);
        mediumSeekBar.setOnTouchListener((v, event) -> false);
        easySeekBar.setOnTouchListener((v, event) -> false);

        hardSeekBar.setOnLongClickListener(v -> false);
        mediumSeekBar.setOnLongClickListener(v -> false);
        easySeekBar.setOnLongClickListener(v -> false);

        final int[] originalProgress = {hardSeekBar.getProgress(), mediumSeekBar.getProgress(), easySeekBar.getProgress()};

        hardSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    hardSeekBar.setProgress(originalProgress[0]);
                } else originalProgress[0] = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mediumSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediumSeekBar.setProgress(originalProgress[1]);
                } else originalProgress[1] = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        easySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    easySeekBar.setProgress(originalProgress[2]);
                } else originalProgress[2] = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Contest Details RecyclerViews

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

        // Set Solved Problems Details

        LeetCodeUserDetails leetCodeUserDetails = SharedPrefConfig.readInLeetCodePref(getContext());

        hardSeekBar.setMax(Integer.parseInt(leetCodeUserDetails.getTotalHard()));
        hardSeekBar.setProgress(Integer.parseInt(leetCodeUserDetails.getHardSolved()));
        mediumSeekBar.setMax(Integer.parseInt(leetCodeUserDetails.getTotalMedium()));
        mediumSeekBar.setProgress(Integer.parseInt(leetCodeUserDetails.getMediumSolved()));
        easySeekBar.setMax(Integer.parseInt(leetCodeUserDetails.getTotalEasy()));
        easySeekBar.setProgress(Integer.parseInt(leetCodeUserDetails.getEasySolved()));

        leetCodeUserName.setText("@" + leetCodeUserDetails.getUserName());
        leetCodeHard.setText(leetCodeUserDetails.getHardSolved() + "/" + leetCodeUserDetails.getTotalHard());
        leetCodeMedium.setText(leetCodeUserDetails.getMediumSolved() + "/" + leetCodeUserDetails.getTotalMedium());
        leetCodeEasy.setText(leetCodeUserDetails.getEasySolved() + "/" + leetCodeUserDetails.getTotalEasy());

        totalProblemsSolved.setText(leetCodeUserDetails.getTotalProblemsSolved());
        acceptanceRate.setText(leetCodeUserDetails.getAcceptance_rate());

        // On Item Click Listener (Reminders, Visiting Website)

        ongoingRVA.setOnItemClickListener((platFormName, position) -> createPopupDialog(ongoingContestsArrayList, position));
        todayRVA.setOnItemClickListener((platFormName, position) -> createPopupDialog(todayContestsArrayList, position));
        futureRVA.setOnItemClickListener((platFormName, position) -> createPopupDialog(futureContestsArrayList, position));

        return groupFragmentView;
    }

    private void createPopupDialog(ArrayList<ContestDetails> contestsArrayList, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.contest_popup_dialog, null);

        TextView platformTitle = view.findViewById(R.id.platform_title),
                contestTitle = view.findViewById(R.id.contest_title),
                startTime = view.findViewById(R.id.start_time),
                endTime = view.findViewById(R.id.end_time),
                visitWebsite = view.findViewById(R.id.visit_website),
                appRemainder = view.findViewById(R.id.in_app_remainder);
        ImageView platformImage = view.findViewById(R.id.platform_title_image);

        if (contestsArrayList.get(position).getContestStatus().equals("CODING")) {
            appRemainder.setVisibility(View.GONE);
        } else {
            appRemainder.setVisibility(View.VISIBLE);
        }

        platformImage.setImageResource(R.drawable.ic_leetcode_logo);
        platformTitle.setText(contestsArrayList.get(position).getSite());
        contestTitle.setText(contestsArrayList.get(position).getContestName());
        startTime.setText(contestsArrayList.get(position).getContestStartTime());
        endTime.setText(contestsArrayList.get(position).getContestEndTime());

        visitWebsite.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(contestsArrayList.get(position).getContestUrl())));
            dialog.cancel();
        });

        appRemainder.setOnClickListener(v -> {
            // TODO: App Remainder functionality should be implemented
            Toast.makeText(getContext(), "To be implemented!", Toast.LENGTH_SHORT).show();
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private void findViewsByIds() {
        leetCodeSwipeRefreshLayout = groupFragmentView.findViewById(R.id.leet_code_swipe_refresh);

        leetCodeUserName = groupFragmentView.findViewById(R.id.leet_code_user_name);
        acceptanceRate = groupFragmentView.findViewById(R.id.leetCode_acceptance_rate);
        totalProblemsSolved = groupFragmentView.findViewById(R.id.leetCode_total_solved_problems);

        leetCodeHard = groupFragmentView.findViewById(R.id.leetCode_hard_solved);
        leetCodeMedium = groupFragmentView.findViewById(R.id.leetCode_medium_solved);
        leetCodeEasy = groupFragmentView.findViewById(R.id.leetCode_easy_solved);

        hardSeekBar = groupFragmentView.findViewById(R.id.leetCode_hard_seek_bar);
        mediumSeekBar = groupFragmentView.findViewById(R.id.leetCode_medium_seek_bar);
        easySeekBar = groupFragmentView.findViewById(R.id.leetCode_easy_seek_bar);

        ongoing_nothing = groupFragmentView.findViewById(R.id.leetCode_ongoing_nothing);
        today_nothing = groupFragmentView.findViewById(R.id.leetCode_today_nothing);
        future_nothing = groupFragmentView.findViewById(R.id.leetCode_future_nothing);

        OngoingRV = groupFragmentView.findViewById(R.id.leetCode_ongoing_recycler_view);
        TodayRV = groupFragmentView.findViewById(R.id.leetCode_today_recycler_view);
        FutureRV = groupFragmentView.findViewById(R.id.leetCode_future_recycler_view);
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