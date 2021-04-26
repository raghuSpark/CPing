package com.raghu.CPing.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.raghu.CPing.R;
import com.raghu.CPing.adapters.AllParentRecyclerViewAdapter;
import com.raghu.CPing.classes.ContestDetails;
import com.raghu.CPing.classes.PlatformDetails;
import com.raghu.CPing.database.JSONResponseDBHandler;

import java.util.ArrayList;
import java.util.Arrays;

public class AllFragment extends Fragment {

    private static final String TAG = "AllFragment";

    private final ArrayList<PlatformDetails> ongoingPlatformsArrayList = new ArrayList<>();
    private final ArrayList<PlatformDetails> todayPlatformsArrayList = new ArrayList<>();
    private final ArrayList<PlatformDetails> futurePlatformsArrayList = new ArrayList<>();

    private RecyclerView OngoingRV, TodayRV, FutureRV;

    public AllFragment() {
        // Required empty public constructor
    }

    public static AllFragment newInstance(String param1, String param2) {
        AllFragment fragment = new AllFragment();
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

        ArrayList<String> platforms = new ArrayList<>(
                Arrays.asList("AtCoder", "CodeChef", "CodeForces", "HackerEarth", "HackerRank", "Kick Start", "LeetCode", "TopCoder")
        );

        for (String platform : platforms) {
            ArrayList<ContestDetails> contestDetailsArrayList = jsonResponseDBHandler.getPlatformDetails(platform);
            ArrayList<ContestDetails> ongoingContestsArrayList = new ArrayList<>(),
                    todayContestsArrayList = new ArrayList<>(),
                    futureContestsArrayList = new ArrayList<>();

            for (ContestDetails cd : contestDetailsArrayList) {
                if (isGreaterThan10days(cd.getContestDuration())) continue;
                if (!cd.getIsToday().equals("No")) {
                    todayContestsArrayList.add(cd);
                } else if (cd.getContestStatus().equals("CODING")) {
                    ongoingContestsArrayList.add(cd);
                } else {
                    futureContestsArrayList.add(cd);
                }
            }
            if (!ongoingContestsArrayList.isEmpty()) ongoingPlatformsArrayList.add(new PlatformDetails(platform,ongoingContestsArrayList));
            if (!todayContestsArrayList.isEmpty()) todayPlatformsArrayList.add(new PlatformDetails(platform,todayContestsArrayList));
            if (!futureContestsArrayList.isEmpty()) futurePlatformsArrayList.add(new PlatformDetails(platform,futureContestsArrayList));
        }
    }

    private boolean isGreaterThan10days(int contestDuration) {
        return ((contestDuration / 3600.0) / 24.0 > 10);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View groupFragmentView = inflater.inflate(R.layout.fragment_all, container, false);

        TextView ongoing_nothing = groupFragmentView.findViewById(R.id.all_ongoing_nothing);
        TextView today_nothing = groupFragmentView.findViewById(R.id.all_today_nothing);
        TextView future_nothing = groupFragmentView.findViewById(R.id.all_future_nothing);

        OngoingRV = groupFragmentView.findViewById(R.id.all_ongoing_contests_recycler_view);
        TodayRV = groupFragmentView.findViewById(R.id.all_today_contests_recycler_view);
        FutureRV = groupFragmentView.findViewById(R.id.all_future_contests_recycler_view);

        if (ongoingPlatformsArrayList.isEmpty()) {
            ongoing_nothing.setVisibility(View.VISIBLE);
            OngoingRV.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "onCreateView: "+ongoingPlatformsArrayList.size());
            ongoing_nothing.setVisibility(View.GONE);
            OngoingRV.setVisibility(View.VISIBLE);
        }

        if (todayPlatformsArrayList.isEmpty()) {
            today_nothing.setVisibility(View.VISIBLE);
            TodayRV.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "onCreateView: "+todayPlatformsArrayList.size());
            today_nothing.setVisibility(View.GONE);
            TodayRV.setVisibility(View.VISIBLE);
        }

        if (futurePlatformsArrayList.isEmpty()) {
            future_nothing.setVisibility(View.VISIBLE);
            FutureRV.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "onCreateView: "+futurePlatformsArrayList.size());
            future_nothing.setVisibility(View.GONE);
            FutureRV.setVisibility(View.VISIBLE);
        }

        // 0 -> Ongoing
        initialize(0);
        // 1 -> Today
        initialize(1);
        // 2 -> Future
        initialize(2);

        GraphView graphView = groupFragmentView.findViewById(R.id.all_graph_view);
        LineGraphSeries<DataPoint> codeForcesSeries = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 363),
                new DataPoint(1, 615),
                new DataPoint(2, 781),
                new DataPoint(3, 825),
                new DataPoint(4, 824),
                new DataPoint(5, 988),
                new DataPoint(6, 973),
                new DataPoint(7, 866),
                new DataPoint(8, 1042)
        });
        codeForcesSeries.setColor(Color.rgb(72, 221, 205));
        codeForcesSeries.setDrawDataPoints(true);
        graphView.setTitleTextSize(18);
        graphView.addSeries(codeForcesSeries);

        LineGraphSeries<DataPoint> codeChefSeries = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 1500),
                new DataPoint(1, 1398),
                new DataPoint(2, 1503),
                new DataPoint(3, 1558),
                new DataPoint(4, 1571),
                new DataPoint(5, 1660),
                new DataPoint(6, 1570),
                new DataPoint(7, 1670),
                new DataPoint(8, 1696)
        });
        codeChefSeries.setColor(Color.rgb(255, 164, 161));
        codeChefSeries.setDrawDataPoints(true);
        graphView.setTitleTextSize(18);
        graphView.addSeries(codeChefSeries);

        return groupFragmentView;
    }

    private void initialize(int i) {
        if (i == 0) {
            OngoingRV.setHasFixedSize(true);
            OngoingRV.setLayoutManager(new LinearLayoutManager(getContext()));
            AllParentRecyclerViewAdapter ongoingRVA = new AllParentRecyclerViewAdapter(ongoingPlatformsArrayList);
            OngoingRV.setAdapter(ongoingRVA);
            ongoingRVA.notifyDataSetChanged();
        } else if (i == 1) {
            TodayRV.setHasFixedSize(true);
            TodayRV.setLayoutManager(new LinearLayoutManager(getContext()));
            AllParentRecyclerViewAdapter todayRVA = new AllParentRecyclerViewAdapter(todayPlatformsArrayList);
            TodayRV.setAdapter(todayRVA);
            todayRVA.notifyDataSetChanged();
        } else {
            FutureRV.setHasFixedSize(true);
            FutureRV.setLayoutManager(new LinearLayoutManager(getContext()));
            AllParentRecyclerViewAdapter futureRVA = new AllParentRecyclerViewAdapter(futurePlatformsArrayList);
            FutureRV.setAdapter(futureRVA);
            futureRVA.notifyDataSetChanged();
        }
    }
}