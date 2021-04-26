package com.raghu.CPing.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.raghu.CPing.R;
import com.raghu.CPing.database.JSONResponseDBHandler;
import com.raghu.CPing.util.ContestDetails;
import com.raghu.CPing.util.ContestDetailsRecyclerViewAdapter;

import java.util.ArrayList;

public class HackerEarthFragment extends Fragment {

    private View groupFragmentView;

    private JSONResponseDBHandler jsonResponseDBHandler;

    private ArrayList<ContestDetails> contestDetailsArrayList = new ArrayList<>(),
            ongoingContestsArrayList = new ArrayList<>(),
            todayContestsArrayList = new ArrayList<>(),
            futureContestsArrayList = new ArrayList<>();

    private TextView ongoing_nothing, today_nothing, future_nothing;

    private RecyclerView OngoingRV, TodayRV, FutureRV;
    private ContestDetailsRecyclerViewAdapter OngoingRVA, TodayRVA, FutureRVA;

    public HackerEarthFragment() {
        // Required empty public constructor
    }

    public static HackerEarthFragment newInstance(String param1, String param2) {
        HackerEarthFragment fragment = new HackerEarthFragment();
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
        contestDetailsArrayList = jsonResponseDBHandler.getHackerEarthDetails();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        groupFragmentView = inflater.inflate(R.layout.fragment_hacker_earth, container, false);

        ongoing_nothing = groupFragmentView.findViewById(R.id.hackerEarth_ongoing_nothing);
        today_nothing = groupFragmentView.findViewById(R.id.hackerEarth_today_nothing);
        future_nothing = groupFragmentView.findViewById(R.id.hackerEarth_future_nothing);

        OngoingRV = groupFragmentView.findViewById(R.id.hackerEarth_ongoing_recycler_view);
        TodayRV = groupFragmentView.findViewById(R.id.hackerEarth_today_recycler_view);
        FutureRV = groupFragmentView.findViewById(R.id.hackerEarth_future_recycler_view);

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
        // 2 -> Later
        initialize(2);

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