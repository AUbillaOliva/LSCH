package cl.afubillaoliva.lsch.adapters;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager2.adapter.FragmentStateAdapter;
import cl.afubillaoliva.lsch.R;

public class TabsAdapter extends FragmentStateAdapter {

    private Context context;

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public TabsAdapter(FragmentActivity fragment, Context context){
        super(fragment);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position){
        return mFragmentList.get(position);
    }

    @Nullable
    public CharSequence getPageTitle(int position){
        return mFragmentTitleList.get(position);
    }

    @Override
    public int getItemCount(){
        return mFragmentList.size();
    }

    public View getTabView(int position){
        @SuppressLint("InflateParams") View v = LayoutInflater.from(context).inflate(R.layout.tab_view, null);
        final TextView tv = v.findViewById(R.id.tv_tab);
        tv.setText(mFragmentTitleList.get(position));
        return v;
    }

    public void addFragment(Fragment fragment, String title){
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

}
