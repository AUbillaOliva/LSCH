package cl.afubillaoliva.lsch.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cl.afubillaoliva.lsch.fragments.Abecedary;
import cl.afubillaoliva.lsch.fragments.Expressions;
import cl.afubillaoliva.lsch.fragments.Themes;

public class TabsAdapter extends FragmentPagerAdapter {
    private String[] titles = {"ABECEDARIO","ORDEN TEMÁTICO","EXPRESIONES DE USO COTIDIANO"};
    public TabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        switch(i){
            case 0:
                fragment = new Abecedary();
                break;
            case 1:
                fragment = new Themes();
                break;
            case 2:
                fragment = new Expressions();
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("position", i);
        assert fragment != null;
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int i){
        return titles[i];
    }
}
