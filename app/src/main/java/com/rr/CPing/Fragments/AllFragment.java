package com.rr.CPing.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.rr.CPing.Adapters.AllParentRecyclerViewAdapter;
import com.rr.CPing.Handlers.BottomSheetHandler;
import com.rr.CPing.Handlers.DateTimeHandler;
import com.rr.CPing.Handlers.SetRankColorHandler;
import com.rr.CPing.Model.AtCoderUserDetails;
import com.rr.CPing.Model.CodeChefUserDetails;
import com.rr.CPing.Model.CodeForcesUserDetails;
import com.rr.CPing.Model.ContestDetails;
import com.rr.CPing.Model.HiddenContestsClass;
import com.rr.CPing.Model.PlatformDetails;
import com.rr.CPing.Model.PlatformListItem;
import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.database.JSONResponseDBHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class AllFragment extends Fragment {

    private final ArrayList<PlatformDetails> ongoingPlatformsArrayList = new ArrayList<>();
    private final ArrayList<PlatformDetails> todayPlatformsArrayList = new ArrayList<>();
    private final ArrayList<PlatformDetails> futurePlatformsArrayList = new ArrayList<>();

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

    private SetRankColorHandler setRankColor;

    public AllFragment() {
        // Required empty public constructor
    }

//    @Override
//    public void onResume() {
//        ongoingRVA.notifyDataSetChanged();
//        todayRVA.notifyDataSetChanged();
//        futureRVA.notifyDataSetChanged();
//        super.onResume();
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JSONResponseDBHandler jsonResponseDBHandler = new JSONResponseDBHandler(getContext());

        platforms = new ArrayList<>();
        setRankColor = new SetRankColorHandler(getContext());

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
                Calendar calendar = Calendar.getInstance();
                new DateTimeHandler().setCalender(calendar, cd.getContestEndTime());
                if (chekInDeleteContests(cd.getContestName(), calendar.getTimeInMillis()))
                    continue;
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

    private boolean chekInDeleteContests(String contestName, long endTime) {
        ArrayList<HiddenContestsClass> hiddenContestsArrayList = SharedPrefConfig.readInHiddenContests(getContext());
        for (HiddenContestsClass hcc : hiddenContestsArrayList) {
            if (hcc.getContestName().equals(contestName) && hcc.getContestEndTime() == endTime)
                return true;
        }
        return false;
    }

    private boolean isGreaterThan10days(int contestDuration) {
        return ((contestDuration / 3600.0) / 24.0 > 10);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView = inflater.inflate(R.layout.fragment_all, container, false);

        findViewsByIds();

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

        int maxY = 0, minY = Integer.MAX_VALUE;
        boolean noGraph = true;

        // AtCoder Rating Cards and Graph
        ArrayList<Integer> atCoderRecentRatingsArrayList;

        if (platforms.contains("AtCoder")) {
            AtCoderUserDetails atCoderUserDetails = SharedPrefConfig.readInAtCoderPref(getContext());
            if (atCoderUserDetails == null) {
                atCoderRating.setText("-");
                atCoderLevel.setText("-");
                atCoderRecentRatingsArrayList = new ArrayList<>();
            } else {
                atCoderRating.setText(String.valueOf(atCoderUserDetails.getCurrentRating()));
                atCoderLevel.setText(atCoderUserDetails.getCurrentLevel());
                atCoderLevel.setTextColor(setRankColor.getAtCoderColor(atCoderUserDetails.getCurrentLevel()));
                atCoderRecentRatingsArrayList = atCoderUserDetails.getRecentContestRatings();
            }

            if (atCoderRecentRatingsArrayList.isEmpty()) {
                atCoderGraphBelow.setVisibility(View.GONE);
                atCoderRatingChanges.setVisibility(View.GONE);
            } else {
                noGraph = false;
                DataPoint[] atCoderValues = new DataPoint[atCoderRecentRatingsArrayList.size()];

                for (int i = 0; i < atCoderRecentRatingsArrayList.size(); i++) {
                    int temp = atCoderRecentRatingsArrayList.get(i);
                    maxY = Math.max(maxY, temp);
                    minY = Math.min(minY, temp);
                    atCoderValues[i] = new DataPoint(i, temp);
                }

                LineGraphSeries<DataPoint> atCoderSeries = new LineGraphSeries<>(atCoderValues);
                atCoderSeries.setColor(Color.rgb(179, 145, 255));
                atCoderSeries.setDrawDataPoints(true);
                atCoderSeries.setDataPointsRadius(7);

                graphView.addSeries(atCoderSeries);
            }
        } else {
            atCoderGraphBelow.setVisibility(View.GONE);
            atCoderRatingChanges.setVisibility(View.GONE);
        }

        // CodeForces Graph

        ArrayList<Integer> codeForcesRecentRatingsArrayList = new ArrayList<>();

        if (platforms.contains("CodeForces")) {
            CodeForcesUserDetails codeForcesUserDetails = SharedPrefConfig.readInCodeForcesPref(getContext());
            if (codeForcesUserDetails == null) {
                codeForcesRating.setText("-");
                codeForcesRank.setText("-");
                codeForcesRecentRatingsArrayList = new ArrayList<>();
            } else {
                codeForcesRating.setText(String.valueOf(codeForcesUserDetails.getCurrentRating()));
                codeForcesRank.setText(codeForcesUserDetails.getCurrentRank());
                codeForcesRank.setTextColor(setRankColor.getCodeforcesColor(codeForcesUserDetails.getCurrentRank()));
                codeForcesRecentRatingsArrayList = codeForcesUserDetails.getRecentContestRatings();
            }

            if (codeForcesRecentRatingsArrayList.isEmpty()) {
                codeForcesGraphBelow.setVisibility(View.GONE);
                codeForcesRatingChanges.setVisibility(View.GONE);
            } else {
                noGraph = false;
                DataPoint[] codeForcesValues = new DataPoint[codeForcesRecentRatingsArrayList.size()];

                for (int i = 0; i < codeForcesRecentRatingsArrayList.size(); i++) {
                    int temp = codeForcesRecentRatingsArrayList.get(i);
                    maxY = Math.max(maxY, temp);
                    minY = Math.min(minY, temp);
                    codeForcesValues[i] = new DataPoint(i, temp);
                }

                LineGraphSeries<DataPoint> codeForcesSeries = new LineGraphSeries<>(codeForcesValues);
                codeForcesSeries.setColor(Color.rgb(72, 221, 205));
                codeForcesSeries.setDrawDataPoints(true);
                codeForcesSeries.setDataPointsRadius(7);

                graphView.addSeries(codeForcesSeries);
            }
        } else {
            codeForcesGraphBelow.setVisibility(View.GONE);
            codeForcesRatingChanges.setVisibility(View.GONE);
        }

        // CodeChef Graph
        ArrayList<Integer> codeChefRecentRatingsArrayList = new ArrayList<>();

        if (platforms.contains("CodeChef")) {
            CodeChefUserDetails codeChefUserDetails = SharedPrefConfig.readInCodeChefPref(getContext());

            if (codeChefUserDetails == null) {
                codeChefRating.setText("-");
                codeChefStars.setText("-");
                codeChefRecentRatingsArrayList = new ArrayList<>();
            } else {
                codeChefRating.setText(String.valueOf(codeChefUserDetails.getCurrentRating()));
                codeChefStars.setText(codeChefUserDetails.getCurrentStars());
                codeChefStars.setTextColor(setRankColor.getCodeChefColor(codeChefUserDetails.getCurrentStars()));
                codeChefRecentRatingsArrayList = codeChefUserDetails.getRecentContestRatings();
            }

            if (codeChefRecentRatingsArrayList.isEmpty()) {
                codeChefGraphBelow.setVisibility(View.GONE);
                codeChefRatingChanges.setVisibility(View.GONE);
            } else {
                noGraph = false;
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
                codeChefSeries.setDataPointsRadius(7);

                graphView.addSeries(codeChefSeries);
            }
        } else {
            codeChefGraphBelow.setVisibility(View.GONE);
            codeChefRatingChanges.setVisibility(View.GONE);
        }

        if (noGraph || (!platforms.contains("CodeForces") && !platforms.contains("CodeChef") && !platforms.contains("AtCoder"))) {
            allGraphsCardView.setVisibility(View.GONE);
            allRatingsCardView.setVisibility(View.GONE);
            allRatingsChangeTextView.setVisibility(View.GONE);
        } else {
            graphView.getGridLabelRenderer().setGridColor(getResources().getColor(R.color.graphGridsColor));
            graphView.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(R.color.graphGridsColor));
            graphView.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(R.color.graphGridsColor));
            graphView.getViewport().setXAxisBoundsManual(true);
            graphView.getViewport().setMaxX(Math.max(codeChefRecentRatingsArrayList.size(), codeForcesRecentRatingsArrayList.size()));
            graphView.getViewport().setYAxisBoundsManual(true);
            graphView.getViewport().setMaxY(maxY);
            graphView.getViewport().setMinY(minY);
        }

        // On Item Click Listener (Reminders, Visiting Website)

        ongoingRVA.setOnItemClickListener((platFormName, position) -> new BottomSheetHandler().showBottomSheetDialog(ongoingRVA, null, getContext(),
                getPlatformDetails(ongoingPlatformsArrayList, platFormName), position,
                getLayoutInflater()));
        todayRVA.setOnItemClickListener((platFormName, position) -> new BottomSheetHandler().showBottomSheetDialog(todayRVA, null, getContext(),
                getPlatformDetails(todayPlatformsArrayList, platFormName), position,
                getLayoutInflater()));
        futureRVA.setOnItemClickListener((platFormName, position) -> new BottomSheetHandler().showBottomSheetDialog(futureRVA, null, getContext(),
                getPlatformDetails(futurePlatformsArrayList, platFormName), position,
                getLayoutInflater()));

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

    private void findViewsByIds() {
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
