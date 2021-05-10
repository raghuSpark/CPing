package com.rr.CPing.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.adapters.ContestDetailsRecyclerViewAdapter;
import com.rr.CPing.classes.CodeForcesUserDetails;
import com.rr.CPing.classes.ContestDetails;
import com.rr.CPing.database.JSONResponseDBHandler;

import java.util.ArrayList;
import java.util.Objects;

public class CodeForcesFragment extends Fragment {

    private final ArrayList<ContestDetails> ongoingContestsArrayList = new ArrayList<>();
    private final ArrayList<ContestDetails> todayContestsArrayList = new ArrayList<>();
    private final ArrayList<ContestDetails> futureContestsArrayList = new ArrayList<>();
    private SwipeRefreshLayout codeForcesSwipeRefreshLayout;
    private View groupFragmentView;
    private TextView currentRating, currentRank, maxRating, maxRank;
    private TextView ongoing_nothing, today_nothing, future_nothing;
    private RecyclerView OngoingRV, TodayRV, FutureRV;
    private ContestDetailsRecyclerViewAdapter ongoingRVA, todayRVA, futureRVA;

    private AlertDialog dialog;

    public CodeForcesFragment() {
        // Required empty public constructor
    }

    public static CodeForcesFragment newInstance(String param1, String param2) {
        CodeForcesFragment fragment = new CodeForcesFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JSONResponseDBHandler jsonResponseDBHandler = new JSONResponseDBHandler(getContext());
        ArrayList<ContestDetails> contestDetailsArrayList = jsonResponseDBHandler.getPlatformDetails("CodeForces");

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        groupFragmentView = inflater.inflate(R.layout.fragment_code_forces, container, false);

        findViewsByIds();

        codeForcesSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                codeForcesSwipeRefreshLayout.setRefreshing(false);
            }
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

        currentRating.setText(String.valueOf(codeForcesUserDetails.getCurrentRating()));
        currentRank.setText(codeForcesUserDetails.getCurrentRank());
        maxRating.setText(String.valueOf(codeForcesUserDetails.getMaxRating()));
        maxRank.setText(codeForcesUserDetails.getMaxRank());

        setColors(codeForcesUserDetails.getCurrentRank(), codeForcesUserDetails.getMaxRank());

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

        ongoingRVA.setOnItemClickListener(new ContestDetailsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                createPopupDialog(ongoingContestsArrayList, position);
            }
        });

        todayRVA.setOnItemClickListener(new ContestDetailsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                createPopupDialog(todayContestsArrayList, position);
            }
        });

        futureRVA.setOnItemClickListener(new ContestDetailsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                createPopupDialog(futureContestsArrayList, position);
            }
        });

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
                appRemainder = view.findViewById(R.id.contest_remainder);
        ImageView platformImage = view.findViewById(R.id.platform_title_image);

        if (contestsArrayList.get(position).getContestStatus().equals("CODING")) {
            appRemainder.setVisibility(View.GONE);
        } else {
            appRemainder.setVisibility(View.VISIBLE);
        }

        platformImage.setImageResource(R.drawable.ic_codeforces_logo);
        platformTitle.setText(contestsArrayList.get(position).getSite());
        contestTitle.setText(contestsArrayList.get(position).getContestName());
        startTime.setText(contestsArrayList.get(position).getContestStartTime());
        endTime.setText(contestsArrayList.get(position).getContestEndTime());

        visitWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(contestsArrayList.get(position).getContestUrl())));
                dialog.cancel();
            }
        });

        appRemainder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: App Remainder functionality should be implemented
                Toast.makeText(getContext(), "To be implemented!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private void findViewsByIds() {
        codeForcesSwipeRefreshLayout = groupFragmentView.findViewById(R.id.codeForces_swipe_refresh);

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

    @SuppressLint("ResourceAsColor")
    private void setColors(String rank1, String rank2) {
        switch (rank1) {
            case "newbie":
                currentRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesNewbieColor));
                break;
            case "pupil":
                currentRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesPupilColor));
                break;
            case "specialist":
                currentRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesSpecialistColor));
                break;
            case "expert":
                currentRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesExpertColor));
                break;
            case "candidate master":
                currentRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesCandidateMasterColor));
                break;
            case "master":
            case "international master":
                currentRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesMasterColor));
                break;
            case "grandmaster":
            case "legendary grandmaster":
            case "international grandmaster":
                currentRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesGrandMasterColor));
                break;
        }
        switch (rank2) {
            case "newbie":
                maxRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesNewbieColor));
                break;
            case "pupil":
                maxRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesPupilColor));
                break;
            case "specialist":
                maxRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesSpecialistColor));
                break;
            case "expert":
                maxRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesExpertColor));
                break;
            case "candidate master":
                maxRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesCandidateMasterColor));
                break;
            case "master":
            case "international master":
                maxRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesMasterColor));
                break;
            case "grandmaster":
            case "legendary grandmaster":
            case "international grandmaster":
                maxRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesGrandMasterColor));
                break;
        }
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