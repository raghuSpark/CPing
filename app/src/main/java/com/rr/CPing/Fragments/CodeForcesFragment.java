package com.rr.CPing.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.rr.CPing.Adapters.ContestDetailsRecyclerViewAdapter;
import com.rr.CPing.Handlers.BottomSheetHandler;
import com.rr.CPing.Handlers.DateTimeHandler;
import com.rr.CPing.Handlers.SetRankColorHandler;
import com.rr.CPing.Model.CodeForcesUserDetails;
import com.rr.CPing.Model.ContestDetails;
import com.rr.CPing.Model.HiddenContestsClass;
import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.database.JSONResponseDBHandler;

import java.util.ArrayList;
import java.util.Calendar;

public class CodeForcesFragment extends Fragment {

    private final ArrayList<ContestDetails> ongoingContestsArrayList = new ArrayList<>();
    private final ArrayList<ContestDetails> todayContestsArrayList = new ArrayList<>();
    private final ArrayList<ContestDetails> futureContestsArrayList = new ArrayList<>();

    private View groupFragmentView;
    private TextView codeForcesUserName, currentRating, currentRank, maxRating, maxRank;
    private TextView ongoing_nothing, today_nothing, future_nothing;
    private RecyclerView OngoingRV, TodayRV, FutureRV;
    private ContestDetailsRecyclerViewAdapter ongoingRVA, todayRVA, futureRVA;

    private CardView codeForcesGraphCard;

    private SetRankColorHandler setRankColor;

    public CodeForcesFragment() {
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
        ArrayList<ContestDetails> contestDetailsArrayList = jsonResponseDBHandler.getPlatformDetails("CodeForces");
        setRankColor = new SetRankColorHandler(getContext());

        for (ContestDetails cd : contestDetailsArrayList) {
            Calendar calendar = Calendar.getInstance();
            new DateTimeHandler().setCalender(calendar, cd.getContestEndTime());
            if (chekInDeleteContests(cd.getContestName(), calendar.getTimeInMillis()))
                continue;
            if (!cd.getIsToday().equals("No")) {
                todayContestsArrayList.add(cd);
            } else if (cd.getContestStatus().equals("CODING")) {
                ongoingContestsArrayList.add(cd);
            } else {
                futureContestsArrayList.add(cd);
            }
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        ArrayList<Integer> recentRatingsArrayList = new ArrayList<>();

        if (codeForcesUserDetails == null) {
            codeForcesUserName.setText("-");
            currentRating.setText("-");
            currentRank.setText("-");
            maxRating.setText("-");
            maxRank.setText("-");
        } else {
            codeForcesUserName.setText(String.format("@%s", codeForcesUserDetails.getUserName()));
            currentRating.setText(String.valueOf(codeForcesUserDetails.getCurrentRating()));
            currentRank.setText(codeForcesUserDetails.getCurrentRank());
            maxRating.setText(String.valueOf(codeForcesUserDetails.getMaxRating()));
            maxRank.setText(codeForcesUserDetails.getMaxRank());

            currentRank.setTextColor(setRankColor.getCodeforcesColor(codeForcesUserDetails.getCurrentRank()));
            maxRank.setTextColor(setRankColor.getCodeforcesColor(codeForcesUserDetails.getMaxRank()));
            recentRatingsArrayList = codeForcesUserDetails.getRecentContestRatings();
        }

        GraphView graphView = groupFragmentView.findViewById(R.id.codeForces_graph_view);

        if (recentRatingsArrayList.size() == 0)
            codeForcesGraphCard.setVisibility(View.GONE);
        else {
            DataPoint[] values = new DataPoint[recentRatingsArrayList.size()];

            int maxY = 0, minY = Integer.MAX_VALUE;
            for (int i = 0; i < recentRatingsArrayList.size(); i++) {
                int temp = recentRatingsArrayList.get(i);
                maxY = Math.max(maxY, temp);
                minY = Math.min(minY, temp);
                values[i] = new DataPoint(i, temp);
            }

            LineGraphSeries<DataPoint> codeForcesSeries = new LineGraphSeries<>(values);
            codeForcesSeries.setColor(Color.rgb(72, 221, 205));
            codeForcesSeries.setDrawDataPoints(true);
            codeForcesSeries.setDataPointsRadius(7);

            graphView.getGridLabelRenderer().setGridColor(getResources().getColor(R.color.graphGridsColor, null));
            graphView.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(R.color.graphGridsColor, null));
            graphView.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(R.color.graphGridsColor, null));
            graphView.getViewport().setXAxisBoundsManual(true);
            graphView.getViewport().setMaxX(recentRatingsArrayList.size());
            graphView.getViewport().setYAxisBoundsManual(true);
            graphView.getViewport().setMaxY(maxY);
            graphView.getViewport().setMinY(minY);

            graphView.addSeries(codeForcesSeries);
        }

        // On Item Click Listener (Reminders, Visiting Website)

        ongoingRVA.setOnItemClickListener((platFormName, position) -> new BottomSheetHandler().showBottomSheetDialog(null, ongoingRVA, getContext(), ongoingContestsArrayList, position, getLayoutInflater()));
        todayRVA.setOnItemClickListener((platFormName, position) -> new BottomSheetHandler().showBottomSheetDialog(null, todayRVA, getContext(), todayContestsArrayList, position, getLayoutInflater()));
        futureRVA.setOnItemClickListener((platFormName, position) -> new BottomSheetHandler().showBottomSheetDialog(null, futureRVA, getContext(), futureContestsArrayList, position, getLayoutInflater()));

        return groupFragmentView;
    }

    private void findViewsByIds() {
        codeForcesGraphCard = groupFragmentView.findViewById(R.id.codeForces_graph_card_view);

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