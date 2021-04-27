package com.raghu.CPing.fragments;

import android.graphics.Color;
import android.os.Bundle;
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
import com.raghu.CPing.SharedPref.SharedPrefConfig;
import com.raghu.CPing.adapters.ContestDetailsRecyclerViewAdapter;
import com.raghu.CPing.classes.CodeChefUserDetails;
import com.raghu.CPing.classes.ContestDetails;
import com.raghu.CPing.database.JSONResponseDBHandler;

import java.util.ArrayList;

public class CodeChefFragment extends Fragment {

    private View groupFragmentView;

    private TextView currentRating, currentStars, maxRating;

    private TextView ongoing_nothing, today_nothing, future_nothing;

    private final ArrayList<ContestDetails> ongoingContestsArrayList = new ArrayList<>();
    private final ArrayList<ContestDetails> todayContestsArrayList = new ArrayList<>();
    private final ArrayList<ContestDetails> futureContestsArrayList = new ArrayList<>();

    private RecyclerView OngoingRV, TodayRV, FutureRV;

    public CodeChefFragment() {
        // Required empty public constructor
    }

    public static CodeChefFragment newInstance(String param1, String param2) {
        CodeChefFragment fragment = new CodeChefFragment();
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
        ArrayList<ContestDetails> contestDetailsArrayList = jsonResponseDBHandler.getPlatformDetails("CodeChef");

        //Contest Details Recycler View

        for (ContestDetails cd : contestDetailsArrayList) {
//            if (isGreaterThan10days(cd.getContestDuration())) continue;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        groupFragmentView = inflater.inflate(R.layout.fragment_code_chef, container, false);

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

        CodeChefUserDetails codeChefUserDetails = SharedPrefConfig.readInCodeChefPref(getContext());

        currentRating.setText(String.valueOf(codeChefUserDetails.getCurrentRating()));
        currentStars.setText(codeChefUserDetails.getCurrentStars());
        maxRating.setText(String.valueOf(codeChefUserDetails.getHighestRating()));

        GraphView graphView = groupFragmentView.findViewById(R.id.codeChef_graph_view);

        ArrayList<Integer> recentRatingsArrayList = codeChefUserDetails.getRecentContestRatings();

        DataPoint[] values = new DataPoint[recentRatingsArrayList.size()];

        int maxY = 0, minY = Integer.MAX_VALUE;
        for (int i = 0; i < recentRatingsArrayList.size(); ++i) {
            int temp = recentRatingsArrayList.get(i);
            maxY = Math.max(maxY, temp);
            minY = Math.min(minY, temp);
            values[i] = new DataPoint(i, temp);
        }

        LineGraphSeries<DataPoint> codeChefSeries = new LineGraphSeries<>(values);

        codeChefSeries.setColor(Color.rgb(255, 164, 161));
        codeChefSeries.setDrawDataPoints(true);

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxX(recentRatingsArrayList.size());

        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMaxY(maxY);
        graphView.getViewport().setMinY(minY);

        graphView.addSeries(codeChefSeries);

        return groupFragmentView;
    }

    private void findViewsByIds() {
        currentRating = groupFragmentView.findViewById(R.id.codeChef_current_rating);
        currentStars = groupFragmentView.findViewById(R.id.codeChef_current_stars);
        maxRating = groupFragmentView.findViewById(R.id.codeChef_max_rating);

        ongoing_nothing = groupFragmentView.findViewById(R.id.codeChef_ongoing_nothing);
        today_nothing = groupFragmentView.findViewById(R.id.codeChef_today_nothing);
        future_nothing = groupFragmentView.findViewById(R.id.codeChef_future_nothing);

        OngoingRV = groupFragmentView.findViewById(R.id.codeChef_ongoing_recycler_view);
        TodayRV = groupFragmentView.findViewById(R.id.codeChef_today_recycler_view);
        FutureRV = groupFragmentView.findViewById(R.id.codeChef_future_recycler_view);
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