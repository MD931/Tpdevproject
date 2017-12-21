package com.tpdevproject.tab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.tpdevproject.fragments.CategoryFragment;
import com.tpdevproject.fragments.HomeFragment;
import com.tpdevproject.fragments.NewFragment;

/**
 * Created by root on 06/12/17.
 */

public class AdapterTab extends FragmentPagerAdapter{
    private String[] titles = {"Home", "New", "Categorie"};
    public AdapterTab(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch(position){
            case 0: //FireApp f = (FireApp) context.getApplicationContext();
                    fragment = new HomeFragment();
                    break;
            case 1: fragment = new NewFragment();
                break;
            case 2: fragment = new CategoryFragment();
                break;
        }
        Bundle b = new Bundle();
        b.putInt("position", position);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
