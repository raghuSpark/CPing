package com.raghu.CPing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.raghu.CPing.fragments.AllFragment;
import com.raghu.CPing.fragments.CodechefFragment;
import com.raghu.CPing.fragments.CodeforcesFragment;
import com.raghu.CPing.fragments.KisckStartFragment;
import com.raghu.CPing.fragments.LeetcodeFragment;
import com.raghu.CPing.fragments.TopCoderFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    private final ArrayList<CharSequence> pageTitlesArrayList = new ArrayList<>(
            Arrays.asList("All", "Codeforces", "Codechef", "Leetcode", "Topcoder", "KickStart")
    );

    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>(
            Arrays.asList(new AllFragment(), new CodeforcesFragment(), new CodechefFragment(), new LeetcodeFragment(), new TopCoderFragment(), new KisckStartFragment())
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
//                return new CodeforcesFragment();
//            case 2:
//                return new CodechefFragment();
//            case 3:
//                return new LeetcodeFragment();
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
