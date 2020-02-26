package cl.afubillaoliva.lsch.adapters;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cl.afubillaoliva.lsch.R;

public class TabsAdapter extends FragmentStatePagerAdapter {

    private Context context;

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public TabsAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context = context;
    }

    public View getTabView(int position){
        @SuppressLint("InflateParams") View v = LayoutInflater.from(context).inflate(R.layout.tab_view, null);
        final TextView tv = v.findViewById(R.id.tv_tab);
        tv.setText(mFragmentTitleList.get(position));
        return v;
    }

    @NonNull
    @Override
    public Fragment getItem(int position){
        return mFragmentList.get(position);
    }
    public void addFragment(Fragment fragment, String title){
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position){
        return mFragmentTitleList.get(position);
    }

    @Override
    public int getCount(){
        return mFragmentList.size();
    }
}
