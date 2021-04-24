package com.raghu.CPing.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.raghu.CPing.fragments.AllFragment;
import com.raghu.CPing.fragments.CodeChefFragment;
import com.raghu.CPing.fragments.CodeForcesFragment;
import com.raghu.CPing.fragments.KickStartFragment;
import com.raghu.CPing.fragments.LeetCodeFragment;
import com.raghu.CPing.fragments.TopCoderFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    private final ArrayList<CharSequence> pageTitlesArrayList = new ArrayList<>(
            Arrays.asList("All", "Codeforces", "Codechef", "Leetcode", "Topcoder", "KickStart")
    );

    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>(
            Arrays.asList(new AllFragment(), new CodeForcesFragment(), new CodeChefFragment(), new LeetCodeFragment(), new TopCoderFragment(), new KickStartFragment())
    );

    public TabsAccessorAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        return fragmentArrayList.get(position);

//        switch (position) {
//            case 0:
//                return new AllFragment();
//            case 1:
//                return new CodeForcesFragment();
//            case 2:
//                return new CodeChefFragment();
//            case 3:
//                return new LeetCodeFragment();
//            default:
//                return null;
//        }
    }

    @Override
    public int getCount() {
        int cnt=0;

        return 6;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitlesArrayList.get(position);
//        switch (position) {
//            case 0:
//                return "All";
//            case 1:
//                return "Codeforces";
//            case 2:
//                return "Codechef";
//            case 3:
//                return "Leetcode";
//            default:
//                return null;
//        }
    }
}
