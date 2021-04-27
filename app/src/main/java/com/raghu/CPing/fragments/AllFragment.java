package com.raghu.CPing.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.raghu.CPing.R;
import com.raghu.CPing.SharedPref.SharedPrefConfig;
import com.raghu.CPing.adapters.AllParentRecyclerViewAdapter;
import com.raghu.CPing.classes.AtCoderUserDetails;
import com.raghu.CPing.classes.CodeChefUserDetails;
import com.raghu.CPing.classes.CodeForcesUserDetails;
import com.raghu.CPing.classes.ContestDetails;
import com.raghu.CPing.classes.PlatformDetails;
import com.raghu.CPing.database.JSONResponseDBHandler;

import java.util.ArrayList;
import java.util.Arrays;

public class AllFragment extends Fragment {

    private View groupFragmentView;
    private GraphView graphView;

    private TextView ongoing_nothing, today_nothing, future_nothing;

    private LinearLayout codeForcesRatingChanges, codeChefRatingChanges, leetCodeRatingChanges, atCoderRatingChanges;

    private TextView codeForcesRating, codeForcesRank, codeChefRating, codeChefStars,
            leetCodeRating, leetCodeRank, atCoderRating, atCoderLevel;

    private TextView codeForcesGraphBelow, codeChefGraphBelow, leetCodeGraphBelow, atCoderGraphBelow;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        groupFragmentView = inflater.inflate(R.layout.fragment_all, container, false);

        findViewsByIds();

        if (ongoingPlatformsArrayList.isEmpty()) {
            ongoing_nothing.setVisibility(View.VISIBLE);
            OngoingRV.setVisibility(View.GONE);
        } else {
            ongoing_nothing.setVisibility(View.GONE);
            OngoingRV.setVisibility(View.VISIBLE);
        }

        if (todayPlatformsArrayList.isEmpty()) {
            today_nothing.setVisibility(View.VISIBLE);
            TodayRV.setVisibility(View.GONE);
        } else {
            today_nothing.setVisibility(View.GONE);
            TodayRV.setVisibility(View.VISIBLE);
        }

        if (futurePlatformsArrayList.isEmpty()) {
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

        // AtCoder Rating Cards

        AtCoderUserDetails atCoderUserDetails = SharedPrefConfig.readInAtCoderPref(getContext());
        atCoderRating.setText(String.valueOf(atCoderUserDetails.getCurrentRating()));
        atCoderLevel.setText(atCoderUserDetails.getCurrentLevel());

        // CodeForces Graph

        CodeForcesUserDetails codeForcesUserDetails = SharedPrefConfig.readInCodeForcesPref(getContext());
        codeForcesRating.setText(String.valueOf(codeForcesUserDetails.getCurrentRating()));
        codeForcesRank.setText(codeForcesUserDetails.getCurrentRank());

        ArrayList<String> codeForcesRecentRatingsArrayList = codeForcesUserDetails.getRecentContestRatings();

        DataPoint[] codeForcesValues = new DataPoint[codeForcesRecentRatingsArrayList.size()];
        int maxY = 0, minY = Integer.MAX_VALUE;
        for (int i = 0; i < codeForcesRecentRatingsArrayList.size(); i++) {
            int temp = Integer.parseInt(codeForcesRecentRatingsArrayList.get(i));
            maxY = Math.max(maxY, temp);
            minY = Math.min(minY, temp);
            codeForcesValues[i] = new DataPoint(i, temp);
        }

        LineGraphSeries<DataPoint> codeForcesSeries = new LineGraphSeries<>(codeForcesValues);

        codeForcesSeries.setColor(Color.rgb(72, 221, 205));
        codeForcesSeries.setDrawDataPoints(true);


        // CodeChef Graph

        CodeChefUserDetails codeChefUserDetails = SharedPrefConfig.readInCodeChefPref(getContext());
        codeChefRating.setText(String.valueOf(codeChefUserDetails.getCurrentRating()));
        codeChefStars.setText(codeChefUserDetails.getCurrentStars());

        ArrayList<Integer> codeChefRecentRatingsArrayList = codeChefUserDetails.getRecentContestRatings();
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

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxX(Math.max(codeChefRecentRatingsArrayList.size(), codeForcesRecentRatingsArrayList.size()));

        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMaxY(maxY);
        graphView.getViewport().setMinY(minY);

        graphView.addSeries(codeForcesSeries);
        graphView.addSeries(codeChefSeries);

        return groupFragmentView;
    }

    private void findViewsByIds() {
        // Rating Changes Cards

        codeForcesRatingChanges = groupFragmentView.findViewById(R.id.all_code_forces_rating_card);
        codeChefRatingChanges = groupFragmentView.findViewById(R.id.all_code_chef_rating_card);
        leetCodeRatingChanges = groupFragmentView.findViewById(R.id.all_leet_code_rating_card);
        atCoderRatingChanges = groupFragmentView.findViewById(R.id.all_at_coder_rating_card);

        codeForcesRating = groupFragmentView.findViewById(R.id.all_code_forces_current_rating);
        codeForcesRank = groupFragmentView.findViewById(R.id.all_code_forces_current_rank);
        codeChefRating = groupFragmentView.findViewById(R.id.all_code_chef_current_rating);
        codeChefStars = groupFragmentView.findViewById(R.id.all_code_chef_current_stars);
        leetCodeRating = groupFragmentView.findViewById(R.id.all_leet_code_current_rating);
        leetCodeRank = groupFragmentView.findViewById(R.id.all_leet_code_current_rank);
        atCoderRating = groupFragmentView.findViewById(R.id.all_at_coder_current_rating);
        atCoderLevel = groupFragmentView.findViewById(R.id.all_at_coder_current_level);

        codeForcesGraphBelow = groupFragmentView.findViewById(R.id.code_forces_graph_below);
        codeChefGraphBelow = groupFragmentView.findViewById(R.id.code_chef_graph_below);
        leetCodeGraphBelow = groupFragmentView.findViewById(R.id.leet_code_graph_below);
        atCoderGraphBelow = groupFragmentView.findViewById(R.id.at_coder_graph_below);

        // Contest Details

        ongoing_nothing = groupFragmentView.findViewById(R.id.all_ongoing_nothing);
        today_nothing = groupFragmentView.findViewById(R.id.all_today_nothing);
        future_nothing = groupFragmentView.findViewById(R.id.all_future_nothing);

        OngoingRV = groupFragmentView.findViewById(R.id.all_ongoing_contests_recycler_view);
        TodayRV = groupFragmentView.findViewById(R.id.all_today_contests_recycler_view);
        FutureRV = groupFragmentView.findViewById(R.id.all_future_contests_recycler_view);

        graphView = groupFragmentView.findViewById(R.id.all_graph_view);
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