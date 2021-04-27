package com.raghu.CPing.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.raghu.CPing.R;
import com.raghu.CPing.SharedPref.SharedPrefConfig;
import com.raghu.CPing.adapters.ContestDetailsRecyclerViewAdapter;
import com.raghu.CPing.classes.AtCoderUserDetails;
import com.raghu.CPing.classes.ContestDetails;
import com.raghu.CPing.database.JSONResponseDBHandler;

import java.util.ArrayList;

public class AtCoderFragment extends Fragment {

    private View groupFragmentView;
    private TextView currentRating, highestRating, currentLevel, currentRank;

    private TextView ongoing_nothing, today_nothing, future_nothing;

    private final ArrayList<ContestDetails> ongoingContestsArrayList = new ArrayList<>();
    private final ArrayList<ContestDetails> todayContestsArrayList = new ArrayList<>();
    private final ArrayList<ContestDetails> futureContestsArrayList = new ArrayList<>();

    private RecyclerView OngoingRV, TodayRV, FutureRV;

    public AtCoderFragment() {
        // Required empty public constructor
    }

    public static AtCoderFragment newInstance(String param1, String param2) {
        AtCoderFragment fragment = new AtCoderFragment();
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
        ArrayList<ContestDetails> contestDetailsArrayList = jsonResponseDBHandler.getPlatformDetails("AtCoder");

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
        // Inflate the layout for this fragment
        groupFragmentView = inflater.inflate(R.layout.fragment_at_coder, container, false);

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

//         Set Ratings

        AtCoderUserDetails atCoderUserDetails = SharedPrefConfig.readInAtCoderPref(getContext());

        currentRating.setText(String.valueOf(atCoderUserDetails.getCurrentRating()));
        highestRating.setText(String.valueOf(atCoderUserDetails.getHighestRating()));
        currentRank.setText(String.valueOf(atCoderUserDetails.getCurrentRank()));
        currentLevel.setText(atCoderUserDetails.getCurrentLevel());

//        setColors(atCoderUserDetails.getCurrentLevel());

        return groupFragmentView;
    }

    @SuppressLint("ResourceAsColor")
    private void setColors(String level) {
//        switch (level) {
//            case "5 Dan":
//                currentLevel.setTextColor(R.color.atCoderFiveDan);
//                break;
//            case "6 Dan":
//                Log.d("TAG", "setColors: ENTERED");
//                currentLevel.setTextColor(R.color.atCoderSixDan);
//                break;
//            case "7 Dan":
//                currentLevel.setTextColor(R.color.atCoderSevenDan);
//                break;
//            case "8 Dan":
//                currentLevel.setTextColor(R.color.atCoderEightDan);
//                break;
//            case "9 Dan":
//                currentLevel.setTextColor(R.color.atCoderNineDan);
//                break;
//            case "10 Dan":
//                currentLevel.setTextColor(R.color.atCoderTenDan);
//                break;
//            case "Legend":
//                currentLevel.setTextColor(R.color.atCoderLegend);
//                break;
//            case "King":
//                currentLevel.setTextColor(R.color.atCoderKing);
//                break;
//        }
    }

    private void findViewsByIds() {
        currentRating = groupFragmentView.findViewById(R.id.atCoder_current_rating);
        highestRating = groupFragmentView.findViewById(R.id.atCoder_max_rating);
        currentLevel = groupFragmentView.findViewById(R.id.atCoder_current_level);
        currentRank = groupFragmentView.findViewById(R.id.atCoder_current_rank);

        ongoing_nothing = groupFragmentView.findViewById(R.id.atCoder_ongoing_nothing);
        today_nothing = groupFragmentView.findViewById(R.id.atCoder_today_nothing);
        future_nothing = groupFragmentView.findViewById(R.id.atCoder_future_nothing);

        OngoingRV = groupFragmentView.findViewById(R.id.atCoder_ongoing_recycler_view);
        TodayRV = groupFragmentView.findViewById(R.id.atCoder_today_recycler_view);
        FutureRV = groupFragmentView.findViewById(R.id.atCoder_future_recyclerView);
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