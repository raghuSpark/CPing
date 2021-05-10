package com.rr.CPing.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.rr.CPing.R;
import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.adapters.AllParentRecyclerViewAdapter;
import com.rr.CPing.adapters.ContestDetailsRecyclerViewAdapter;
import com.rr.CPing.classes.AtCoderUserDetails;
import com.rr.CPing.classes.CodeChefUserDetails;
import com.rr.CPing.classes.CodeForcesUserDetails;
import com.rr.CPing.classes.ContestDetails;
import com.rr.CPing.classes.PlatformDetails;
import com.rr.CPing.classes.PlatformListItem;
import com.rr.CPing.database.JSONResponseDBHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class AllFragment extends Fragment {

    private final ArrayList<PlatformDetails> ongoingPlatformsArrayList = new ArrayList<>();
    private final ArrayList<PlatformDetails> todayPlatformsArrayList = new ArrayList<>();
    private final ArrayList<PlatformDetails> futurePlatformsArrayList = new ArrayList<>();

    private SwipeRefreshLayout allSwipeRefreshLayout;
    private View groupFragmentView;
    private GraphView graphView;
    private TextView ongoing_nothing, today_nothing, future_nothing, allRatingsChangeTextView;
    private LinearLayout codeForcesRatingChanges, codeChefRatingChanges, leetCodeRatingChanges, atCoderRatingChanges;
    private CardView allGraphsCardView, allRatingsCardView;
    private TextView codeForcesRating, codeForcesRank, codeChefRating, codeChefStars,
            leetCodeRating, leetCodeRank, atCoderRating, atCoderLevel;
    private TextView codeForcesGraphBelow, codeChefGraphBelow, leetCodeGraphBelow, atCoderGraphBelow;
    private ArrayList<String> platforms;

    private RecyclerView OngoingRV, TodayRV, FutureRV;
    private AllParentRecyclerViewAdapter ongoingRVA, todayRVA, futureRVA;
    private AlertDialog dialog;

    public AllFragment() {
        // Required empty public constructor
    }

    public static AllFragment newInstance(String param1, String param2) {
        AllFragment fragment = new AllFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JSONResponseDBHandler jsonResponseDBHandler = new JSONResponseDBHandler(getContext());

        platforms = new ArrayList<>();

        ArrayList<PlatformListItem> platformListItemArrayList = SharedPrefConfig.readPlatformsSelected(getContext());

        for (int i = 0; i < platformListItemArrayList.size(); i++) {
            if (platformListItemArrayList.get(i).isEnabled()) {
                String platform = platformListItemArrayList.get(i).getPlatformName();
                platforms.add(platform);
            }
        }

        Collections.sort(platforms);

        for (String platform : platforms) {
            ArrayList<ContestDetails> contestDetailsArrayList = jsonResponseDBHandler.getPlatformDetails(platform);

            ArrayList<ContestDetails> ongoingContestsArrayList = new ArrayList<>(),
                    todayContestsArrayList = new ArrayList<>(),
                    futureContestsArrayList = new ArrayList<>();

            for (ContestDetails cd : contestDetailsArrayList) {
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

    private boolean isGreaterThan10days(int contestDuration) {
        return ((contestDuration / 3600.0) / 24.0 > 10);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        groupFragmentView = inflater.inflate(R.layout.fragment_all, container, false);

        findViewsByIds();

        allSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO: Data should be updated here
                allSwipeRefreshLayout.setRefreshing(false);
            }
        });

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
        if (platforms.contains("AtCoder")) {
            AtCoderUserDetails atCoderUserDetails = SharedPrefConfig.readInAtCoderPref(getContext());
            atCoderRating.setText(String.valueOf(atCoderUserDetails.getCurrentRating()));
            atCoderLevel.setText(atCoderUserDetails.getCurrentLevel());

            setAtCoderColors(atCoderUserDetails.getCurrentLevel());
        } else {
            atCoderGraphBelow.setVisibility(View.GONE);
            atCoderRatingChanges.setVisibility(View.GONE);
        }

        // CodeForces Graph
        int maxY = 0, minY = Integer.MAX_VALUE;
        ArrayList<String> codeForcesRecentRatingsArrayList = new ArrayList<>();

        if (platforms.contains("CodeForces")) {
            CodeForcesUserDetails codeForcesUserDetails = SharedPrefConfig.readInCodeForcesPref(getContext());
            codeForcesRating.setText(String.valueOf(codeForcesUserDetails.getCurrentRating()));
            codeForcesRank.setText(codeForcesUserDetails.getCurrentRank());

            setCodeForcesColors(codeForcesUserDetails.getCurrentRank());

            codeForcesRecentRatingsArrayList = codeForcesUserDetails.getRecentContestRatings();

            DataPoint[] codeForcesValues = new DataPoint[codeForcesRecentRatingsArrayList.size()];

            for (int i = 0; i < codeForcesRecentRatingsArrayList.size(); i++) {
                int temp = Integer.parseInt(codeForcesRecentRatingsArrayList.get(i));
                maxY = Math.max(maxY, temp);
                minY = Math.min(minY, temp);
                codeForcesValues[i] = new DataPoint(i, temp);
            }

            LineGraphSeries<DataPoint> codeForcesSeries = new LineGraphSeries<>(codeForcesValues);

            codeForcesSeries.setColor(Color.rgb(72, 221, 205));
            codeForcesSeries.setDrawDataPoints(true);

            graphView.addSeries(codeForcesSeries);
        } else {
            codeForcesGraphBelow.setVisibility(View.GONE);
            codeForcesRatingChanges.setVisibility(View.GONE);
        }

        // CodeChef Graph
        ArrayList<Integer> codeChefRecentRatingsArrayList = new ArrayList<>();

        if (platforms.contains("CodeChef")) {
            CodeChefUserDetails codeChefUserDetails = SharedPrefConfig.readInCodeChefPref(getContext());

            codeChefRating.setText(String.valueOf(codeChefUserDetails.getCurrentRating()));
            codeChefStars.setText(codeChefUserDetails.getCurrentStars());

            setCodeChefColors(codeChefUserDetails.getCurrentStars());

            codeChefRecentRatingsArrayList = codeChefUserDetails.getRecentContestRatings();
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

            graphView.addSeries(codeChefSeries);
        } else {
            codeChefGraphBelow.setVisibility(View.GONE);
            codeChefRatingChanges.setVisibility(View.GONE);
        }

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxX(Math.max(codeChefRecentRatingsArrayList.size(), codeForcesRecentRatingsArrayList.size()));
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMaxY(maxY);
        graphView.getViewport().setMinY(minY);

        if (!platforms.contains("CodeForces") && !platforms.contains("CodeChef")) {
            allGraphsCardView.setVisibility(View.GONE);
            if (!platforms.contains("AtCoder")) {
                allRatingsCardView.setVisibility(View.GONE);
                allRatingsChangeTextView.setVisibility(View.GONE);
            }
        }

        // On Item Click Listener (Reminders, Visiting Website)

        ongoingRVA.setOnItemClickListener(new ContestDetailsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String platFormName, int position) {
                createPopupDialog(getPlatformDetails(ongoingPlatformsArrayList, platFormName), position);
            }
        });

        todayRVA.setOnItemClickListener(new ContestDetailsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String platFormName, int position) {
                createPopupDialog(getPlatformDetails(todayPlatformsArrayList, platFormName), position);
            }
        });

        futureRVA.setOnItemClickListener(new ContestDetailsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String platFormName, int position) {
                createPopupDialog(getPlatformDetails(futurePlatformsArrayList, platFormName), position);
            }
        });

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

        platformImage.setImageResource(getImageResource(contestsArrayList.get(position).getSite()));
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

    private int getImageResource(String site) {
        switch (site) {
            case "AtCoder":
                return R.drawable.ic_at_coder_logo;
            case "CodeChef":
                return R.drawable.ic_codechef_logo;
            case "CodeForces":
                return R.drawable.ic_codeforces_logo;
            case "HackerEarth":
                return R.drawable.ic_hacker_earth_logo;
            case "HackerRank":
                return R.drawable.ic_hackerrank_logo;
            case "Kick Start":
                return R.drawable.ic_kickstart_logo;
            case "LeetCode":
                return R.drawable.ic_leetcode_logo;
            case "TopCoder":
                return R.drawable.ic_topcoder_logo;
        }
        return 0;
    }

    private void setCodeChefColors(String stars) {
        switch (stars) {
            case "1★":
                codeChefStars.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.oneStar));
                break;
            case "2★":
                codeChefStars.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.twoStar));
                break;
            case "3★":
                codeChefStars.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.threeStar));
                break;
            case "4★":
                codeChefStars.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.fourStar));
                break;
            case "5★":
                codeChefStars.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.fiveStar));
                break;
            case "6★":
                codeChefStars.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.sixStar));
                break;
            case "7★":
                codeChefStars.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.sevenStar));
                break;
        }
    }

    private void setCodeForcesColors(String rank) {
        switch (rank) {
            case "newbie":
                codeForcesRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesNewbieColor));
                break;
            case "pupil":
                codeForcesRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesPupilColor));
                break;
            case "specialist":
                codeForcesRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesSpecialistColor));
                break;
            case "expert":
                codeForcesRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesExpertColor));
                break;
            case "candidate master":
                codeForcesRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesCandidateMasterColor));
                break;
            case "master":
            case "international master":
                codeForcesRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesMasterColor));
                break;
            case "grandmaster":
            case "legendary grandmaster":
            case "international grandmaster":
                codeForcesRank.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.codeForcesGrandMasterColor));
                break;
        }
    }

    private void setAtCoderColors(String level) {
        switch (level) {
            case "5 Dan":
                atCoderLevel.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.atCoderFiveDan));
                break;
            case "6 Dan":
                atCoderLevel.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.atCoderSixDan));
                break;
            case "7 Dan":
                atCoderLevel.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.atCoderSevenDan));
                break;
            case "8 Dan":
                atCoderLevel.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.atCoderEightDan));
                break;
            case "9 Dan":
                atCoderLevel.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.atCoderNineDan));
                break;
            case "10 Dan":
                atCoderLevel.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.atCoderTenDan));
                break;
            case "Legend":
                atCoderLevel.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.atCoderLegend));
                break;
            case "King":
                atCoderLevel.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.atCoderKing));
                break;
        }
    }

    private void findViewsByIds() {

        allSwipeRefreshLayout = groupFragmentView.findViewById(R.id.all_swipe_refresh);

        allRatingsChangeTextView = groupFragmentView.findViewById(R.id.all_rating_changes_text_view);
        allGraphsCardView = groupFragmentView.findViewById(R.id.all_graphs_card_view);
        allRatingsCardView = groupFragmentView.findViewById(R.id.all_ratings_card_view);

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
            ongoingRVA = new AllParentRecyclerViewAdapter(ongoingPlatformsArrayList);
            OngoingRV.setAdapter(ongoingRVA);
            ongoingRVA.notifyDataSetChanged();
        } else if (i == 1) {
            TodayRV.setHasFixedSize(true);
            TodayRV.setLayoutManager(new LinearLayoutManager(getContext()));
            todayRVA = new AllParentRecyclerViewAdapter(todayPlatformsArrayList);
            TodayRV.setAdapter(todayRVA);
            todayRVA.notifyDataSetChanged();
        } else {
            FutureRV.setHasFixedSize(true);
            FutureRV.setLayoutManager(new LinearLayoutManager(getContext()));
            futureRVA = new AllParentRecyclerViewAdapter(futurePlatformsArrayList);
            FutureRV.setAdapter(futureRVA);
            futureRVA.notifyDataSetChanged();
        }
    }

}