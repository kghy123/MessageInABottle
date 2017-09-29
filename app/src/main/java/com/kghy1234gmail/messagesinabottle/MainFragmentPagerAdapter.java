package com.kghy1234gmail.messagesinabottle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    String[] titles = new String[]{"읽기", "쓰기"};

    Fragment[] fragments = new Fragment[2];

    public MainFragmentPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments[0] = new Fragment_read();
        fragments[1] = new Fragment_send();

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
