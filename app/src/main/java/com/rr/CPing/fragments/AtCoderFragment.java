package com.rr.CPing.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.adapters.ContestDetailsRecyclerViewAdapter;
import com.rr.CPing.classes.AtCoderUserDetails;
import com.rr.CPing.classes.ContestDetails;
import com.rr.CPing.database.JSONResponseDBHandler;

import java.util.ArrayList;
import java.util.Objects;

public class AtCoderFragment extends Fragment {

    private final ArrayList<ContestDetails> ongoingContestsArrayList = new ArrayList<>();
    private final ArrayList<ContestDetails> todayContestsArrayList = new ArrayList<>();
    private final ArrayList<ContestDetails> futureContestsArrayList = new ArrayList<>();
    private SwipeRefreshLayout atCoderSwipeRefreshLayout;
    private View groupFragmentView;
    private TextView currentRating, highestRating, currentLevel, currentRank;
    private TextView ongoing_nothing, today_nothing, future_nothing;
    private RecyclerView OngoingRV, TodayRV, FutureRV;
    private ContestDetailsRecyclerViewAdapter ongoingRVA, todayRVA, futureRVA;

    private AlertDialog dialog;

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

        atCoderSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                atCoderSwipeRefreshLayout.setRefreshing(false);
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

//         Set Ratings

        AtCoderUserDetails atCoderUserDetails = SharedPrefConfig.readInAtCoderPref(getContext());

        currentRating.setText(String.valueOf(atCoderUserDetails.getCurrentRating()));
        highestRating.setText(String.valueOf(atCoderUserDetails.getHighestRating()));
        currentRank.setText(String.valueOf(atCoderUserDetails.getCurrentRank()));
        currentLevel.setText(atCoderUserDetails.getCurrentLevel());

        setColors(atCoderUserDetails.getCurrentLevel());

        // On Item Click Listener (Reminders, Visiting Website)

        ongoingRVA.setOnItemClickListener(new ContestDetailsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String platFormName, int position) {
                createPopupDialog(ongoingContestsArrayList, position);
            }
        });

        todayRVA.setOnItemClickListener(new ContestDetailsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String platFormName, int position) {
                createPopupDialog(todayContestsArrayList, position);
            }
        });

        futureRVA.setOnItemClickListener(new ContestDetailsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String platFormName, int position) {
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

        platformImage.setImageResource(R.drawable.ic_at_coder_logo);
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

    @SuppressLint("ResourceAsColor")
    private void setColors(String level) {
        switch (level) {
            case "5 Dan":
                currentLevel.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.atCoderFiveDan));
                break;
            case "6 Dan":
                currentLevel.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.atCoderSixDan));
                break;
            case "7 Dan":
                currentLevel.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.atCoderSevenDan));
                break;
            case "8 Dan":
                currentLevel.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.atCoderEightDan));
                break;
            case "9 Dan":
                currentLevel.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.atCoderNineDan));
                break;
            case "10 Dan":
                currentLevel.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.atCoderTenDan));
                break;
            case "Legend":
                currentLevel.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.atCoderLegend));
                break;
            case "King":
                currentLevel.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.atCoderKing));
                break;
        }
    }

    private void findViewsByIds() {

        atCoderSwipeRefreshLayout = groupFragmentView.findViewById(R.id.atCoder_swipe_refresh);

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