package com.raghu.CPing.fragments;

import android.annotation.SuppressLint;
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
import com.raghu.CPing.database.JSONResponseDBHandler;
import com.raghu.CPing.util.ContestDetails;
import com.raghu.CPing.util.ContestDetailsRecyclerViewAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CodeChefFragment extends Fragment {

    private View groupFragmentView;
    private GraphView graphView;

    private JSONResponseDBHandler jsonResponseDBHandler;

    private ArrayList<ContestDetails> contestDetailsArrayList = new ArrayList<>(),
            ongoingContestsArrayList = new ArrayList<>(),
            todayContestsArrayList = new ArrayList<>(),
            futureContestsArrayList = new ArrayList<>();

    private TextView ongoing_nothing, today_nothing, future_nothing;

    private RecyclerView OngoingRV, TodayRV, FutureRV;
    private ContestDetailsRecyclerViewAdapter OngoingRVA, TodayRVA, FutureRVA;

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

        jsonResponseDBHandler = new JSONResponseDBHandler(getContext());
        contestDetailsArrayList = jsonResponseDBHandler.getCodeChefDetails();

        //Contest Details Recycler View

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
    }

    private boolean isGreaterThan10days(int contestDuration) {
        return ((contestDuration / 3600.0) / 24.0 > 10);
    }

    @SuppressLint("SimpleDateFormat")
    private boolean isWithinAWeek(String contestStartTime) throws ParseException {
//        2021-04-25T10:00:00.000Z
        contestStartTime = contestStartTime.substring(0, 10) + contestStartTime.substring(11, 18);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String currentDate = simpleDateFormat.format(new Date());

        Date today = simpleDateFormat.parse(currentDate),
                start = simpleDateFormat.parse(contestStartTime);

        assert start != null;
        assert today != null;
        return (TimeUnit.MILLISECONDS.toDays(start.getTime() - today.getTime()) <= 7);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView = inflater.inflate(R.layout.fragment_code_chef, container, false);

        ongoing_nothing = groupFragmentView.findViewById(R.id.codeChef_ongoing_nothing);
        today_nothing = groupFragmentView.findViewById(R.id.codeChef_today_nothing);
        future_nothing = groupFragmentView.findViewById(R.id.codeChef_future_nothing);

        OngoingRV = groupFragmentView.findViewById(R.id.codeChef_ongoing_recycler_view);
        TodayRV = groupFragmentView.findViewById(R.id.codeChef_today_recycler_view);
        FutureRV = groupFragmentView.findViewById(R.id.codeChef_future_recycler_view);

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

        graphView = groupFragmentView.findViewById(R.id.codeChef_graph_view);
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
            OngoingRVA = new ContestDetailsRecyclerViewAdapter(ongoingContestsArrayList);
            OngoingRV.setAdapter(OngoingRVA);
            OngoingRVA.notifyDataSetChanged();
        } else if (i == 1) {
            TodayRV.setHasFixedSize(true);
            TodayRV.setLayoutManager(new LinearLayoutManager(getContext()));
            TodayRVA = new ContestDetailsRecyclerViewAdapter(todayContestsArrayList);
            TodayRV.setAdapter(TodayRVA);
            TodayRVA.notifyDataSetChanged();
        } else {
            FutureRV.setHasFixedSize(true);
            FutureRV.setLayoutManager(new LinearLayoutManager(getContext()));
            FutureRVA = new ContestDetailsRecyclerViewAdapter(futureContestsArrayList);
            FutureRV.setAdapter(FutureRVA);
            FutureRVA.notifyDataSetChanged();
        }
    }
}