package com.rr.CPing.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.rr.CPing.SharedPref.SharedPrefConfig;
import com.rr.CPing.Fragments.AllFragment;
import com.rr.CPing.Fragments.AtCoderFragment;
import com.rr.CPing.Fragments.CodeChefFragment;
import com.rr.CPing.Fragments.CodeForcesFragment;
import com.rr.CPing.Fragments.HackerEarthFragment;
import com.rr.CPing.Fragments.HackerRankFragment;
import com.rr.CPing.Fragments.KickStartFragment;
import com.rr.CPing.Fragments.LeetCodeFragment;
import com.rr.CPing.Fragments.TopCoderFragment;
import com.rr.CPing.Model.PlatformListItem;

import java.util.ArrayList;
import java.util.Collections;

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    Context context;

    private ArrayList<String> pageTitlesArrayList = new ArrayList<>();
    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();

    public TabsAccessorAdapter(@NonNull FragmentManager fm, int behavior, Context context) {
        super(fm, behavior);
        this.context = context;

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
        int count = SharedPrefConfig.readPlatformsCount(context);
        if (count > 1) count++;
        return count;
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
