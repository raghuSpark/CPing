package com.raghu.CPing.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.raghu.CPing.R;
import com.raghu.CPing.SharedPref.SharedPrefConfig;
import com.raghu.CPing.adapters.ContestDetailsRecyclerViewAdapter;
import com.raghu.CPing.classes.CodeForcesUserDetails;
import com.raghu.CPing.classes.ContestDetails;
import com.raghu.CPing.database.JSONResponseDBHandler;

import java.util.ArrayList;

public class CodeForcesFragment extends Fragment {

    private View groupFragmentView;

    private TextView currentRating, currentRank, maxRating, maxRank;

    private TextView ongoing_nothing, today_nothing, future_nothing;

    private final ArrayList<ContestDetails> ongoingContestsArrayList = new ArrayList<>();
    private final ArrayList<ContestDetails> todayContestsArrayList = new ArrayList<>();
    private final ArrayList<ContestDetails> futureContestsArrayList = new ArrayList<>();

    private RecyclerView OngoingRV, TodayRV, FutureRV;

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

//        setColors(codeForcesUserDetails.getCurrentRank(), codeForcesUserDetails.getMaxRank());

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

        return groupFragmentView;
    }

    private void findViewsByIds() {
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
                currentRank.setTextColor(R.color.codeForcesNewbieColor);
                break;
            case "pupil":
                currentRank.setTextColor(R.color.codeForcesPupilColor);
                break;
            case "specialist":
                currentRank.setTextColor(R.color.codeForcesSpecialistColor);
                break;
            case "expert":
                currentRank.setTextColor(R.color.codeForcesExpertColor);
                break;
            case "candidate master":
                currentRank.setTextColor(R.color.codeForcesCandidateMasterColor);
                break;
            case "master":
            case "international master":
                currentRank.setTextColor(R.color.codeForcesMasterColor);
                break;
            case "grandmaster":
            case "legendary grandmaster":
            case "international grandmaster":
                currentRank.setTextColor(R.color.codeForcesGrandMasterColor);
                break;
        }
        switch (rank2) {
            case "newbie":
                currentRank.setTextColor(R.color.codeForcesNewbieColor);
                break;
            case "pupil":
                maxRank.setTextColor(R.color.codeForcesPupilColor);
                break;
            case "specialist":
                maxRank.setTextColor(R.color.codeForcesSpecialistColor);
                break;
            case "expert":
                maxRank.setTextColor(R.color.codeForcesExpertColor);
                break;
            case "candidate master":
                maxRank.setTextColor(R.color.codeForcesCandidateMasterColor);
                break;
            case "master":
            case "international master":
                maxRank.setTextColor(R.color.codeForcesMasterColor);
                break;
            case "grandmaster":
            case "legendary grandmaster":
            case "international grandmaster":
                maxRank.setTextColor(R.color.codeForcesGrandMasterColor);
                break;
        }
    }

    private void initialize(int i) {
        if (i == 0) {
            OngoingRV.setHasFixedSize(true);
            OngoingRV.setLayoutManager(new LinearLayoutManager(getContext()));
            ContestDetailsRecyclerViewAdapter ongoingRVA = new ContestDetailsRecyclerViewAdapter(ongoingContestsArrayList);
            OngoingRV.setAdapter(ongoingRVA);
            ongoingRVA.notifyDataSetChanged();
        } else if (i == 1) {
            TodayRV.setHasFixedSize(true);
            TodayRV.setLayoutManager(new LinearLayoutManager(getContext()));
            ContestDetailsRecyclerViewAdapter todayRVA = new ContestDetailsRecyclerViewAdapter(todayContestsArrayList);
            TodayRV.setAdapter(todayRVA);
            todayRVA.notifyDataSetChanged();
        } else {
            FutureRV.setHasFixedSize(true);
            FutureRV.setLayoutManager(new LinearLayoutManager(getContext()));
            ContestDetailsRecyclerViewAdapter futureRVA = new ContestDetailsRecyclerViewAdapter(futureContestsArrayList);
            FutureRV.setAdapter(futureRVA);
            futureRVA.notifyDataSetChanged();
        }
    }
}