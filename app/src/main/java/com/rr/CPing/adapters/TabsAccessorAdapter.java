package com.rr.CPing.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.classes.PlatformListItem;
import com.rr.CPing.fragments.AllFragment;
import com.rr.CPing.fragments.AtCoderFragment;
import com.rr.CPing.fragments.CodeChefFragment;
import com.rr.CPing.fragments.CodeForcesFragment;
import com.rr.CPing.fragments.HackerEarthFragment;
import com.rr.CPing.fragments.HackerRankFragment;
import com.rr.CPing.fragments.KickStartFragment;
import com.rr.CPing.fragments.LeetCodeFragment;
import com.rr.CPing.fragments.TopCoderFragment;

import java.util.ArrayList;
import java.util.Collections;

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    Context context;

//    private final ArrayList<CharSequence> pageTitlesArrayList = new ArrayList<>(
//            Arrays.asList("All", "AtCoder", "CodeChef", "CodeForces", "HackerEarth", "HackerRank", "KickStart", "LeetCode", "TopCoder")
//    );
//
//    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>(
//            Arrays.asList(new AllFragment(), new AtCoderFragment(), new CodeChefFragment(), new CodeForcesFragment(),
//                    new HackerEarthFragment(), new HackerRankFragment(), new KickStartFragment(), new LeetCodeFragment(),
//                    new TopCoderFragment())
//    );

    private ArrayList<String> pageTitlesArrayList = new ArrayList<>();

    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();

    public TabsAccessorAdapter(@NonNull FragmentManager fm, int behavior, Context context) {
        super(fm, behavior);

        this.context = context;

        ArrayList<PlatformListItem> platformListItemArrayList = SharedPrefConfig.readPlatformsSelected(context);

        notifyDataSetChanged();
    }

    private Fragment getFragment(String platform) {
        switch (platform) {
            case "AtCoder":
                return new AtCoderFragment();
            case "CodeChef":
                return new CodeChefFragment();
            case "CodeForces":
                return new CodeForcesFragment();
            case "HackerEarth":
                return new HackerEarthFragment();
            case "HackerRank":
                return new HackerRankFragment();
            case "Kick Start":
                return new KickStartFragment();
            case "LeetCode":
                return new LeetCodeFragment();
            case "TopCoder":
                return new TopCoderFragment();
        }
        return null;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        int cnt = SharedPrefConfig.readPlatformsCount(context);
        if (cnt > 1) cnt++;
        return cnt;
//        return 9;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitlesArrayList.get(position);
    }

    @Override
    public void notifyDataSetChanged() {

        pageTitlesArrayList = new ArrayList<>();
        fragmentArrayList = new ArrayList<>();

        ArrayList<PlatformListItem> platformListItemArrayList = SharedPrefConfig.readPlatformsSelected(context);

        for (int i = 0; i < platformListItemArrayList.size(); i++) {
            if (platformListItemArrayList.get(i).isEnabled()) {
                String platform = platformListItemArrayList.get(i).getPlatformName();
                pageTitlesArrayList.add(platform);
//                fragmentArrayList.add(getFragment(platform));
            }
        }

        // sorting array list
        Collections.sort(pageTitlesArrayList);

        for (String platform : pageTitlesArrayList) {
            fragmentArrayList.add(getFragment(platform));
        }

        if (pageTitlesArrayList.size() > 1) {
            fragmentArrayList.add(0, new AllFragment());
            pageTitlesArrayList.add(0, "All");
        }

        super.notifyDataSetChanged();
    }
}
