package com.raghu.CPing.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.raghu.CPing.fragments.AllFragment;
import com.raghu.CPing.fragments.AtCoderFragment;
import com.raghu.CPing.fragments.CodeChefFragment;
import com.raghu.CPing.fragments.CodeForcesFragment;
import com.raghu.CPing.fragments.HackerEarthFragment;
import com.raghu.CPing.fragments.HackerRankFragment;
import com.raghu.CPing.fragments.KickStartFragment;
import com.raghu.CPing.fragments.LeetCodeFragment;
import com.raghu.CPing.fragments.TopCoderFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    private final ArrayList<CharSequence> pageTitlesArrayList = new ArrayList<>(
            Arrays.asList("All", "AtCoder", "CodeChef", "CodeForces", "HackerEarth", "HackerRank", "KickStart", "LeetCode", "TopCoder")
    );

    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>(
            Arrays.asList(new AllFragment(), new AtCoderFragment(), new CodeChefFragment(), new CodeForcesFragment(),
                    new HackerEarthFragment(), new HackerRankFragment(), new KickStartFragment(), new LeetCodeFragment(),
                    new TopCoderFragment())
    );

    public TabsAccessorAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        int cnt = 0;
        return 9;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitlesArrayList.get(position);
    }
}
