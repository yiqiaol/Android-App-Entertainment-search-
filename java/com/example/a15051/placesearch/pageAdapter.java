package com.example.a15051.placesearch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class pageAdapter extends FragmentPagerAdapter {
    private FragmentManager mfragmentManager;
    private List<Fragment> mlist;

    public pageAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.mlist = list;
    }

    @Override
    public Fragment getItem(int arg0) {
        return mlist.get(arg0);//显示第几个页面
    }

    @Override
    public int getCount() {
        return mlist.size();//有几个页面
    }
}
