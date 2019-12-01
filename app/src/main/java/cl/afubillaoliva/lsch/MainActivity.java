package cl.afubillaoliva.lsch;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import cl.afubillaoliva.lsch.activities.FavoriteListActivity;
import cl.afubillaoliva.lsch.activities.SearchActivity;
import cl.afubillaoliva.lsch.activities.SettingsActivity;
import cl.afubillaoliva.lsch.adapters.TabsAdapter;
import cl.afubillaoliva.lsch.fragments.Abecedary;
import cl.afubillaoliva.lsch.fragments.Expressions;
import cl.afubillaoliva.lsch.fragments.Themes;
import cl.afubillaoliva.lsch.utils.SharedPreference;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "API_RESPONSE";
    public static final String FAV = "FAVORITE_RESPONSE";
    public static final String UI = "INTERFACE";
    public static int cacheSize = 10 * 1024 * 1024; // 10 MiB

    SharedPreference mSharedPreferences;
    public TabLayout mSlidingTabLayout;
    public ViewPager mViewPager;
    public Toolbar mToolbar;
    AppBarLayout appBarLayout;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = new SharedPreference(this);
        if (mSharedPreferences.loadNightModeState()) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_main);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        appBarLayout = findViewById(R.id.main_appbar_layout);

        StateListAnimator stateListAnimator = new StateListAnimator();
        stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(appBarLayout, "elevation", 2));
        appBarLayout.setStateListAnimator(stateListAnimator);

        mToolbar = findViewById(R.id.toolbar);
        if(mSharedPreferences.loadNightModeState())
            mToolbar.setTitleTextAppearance(this, R.style.ToolbarTypefaceDark);
        else
            mToolbar.setTitleTextAppearance(this, R.style.ToolbarTypefaceLight);
        setSupportActionBar(mToolbar);

        mViewPager = findViewById(R.id.vp_tabs);

        TabsAdapter adapter = new TabsAdapter(getSupportFragmentManager(), this);
        adapter.addFragment(new Abecedary(), "Abecedario");
        adapter.addFragment(new Expressions(), "Orden temático");
        adapter.addFragment(new Themes(), "Expresiones de uso cotidiano");
        mViewPager.setAdapter(adapter);

        mSlidingTabLayout = findViewById(R.id.stl_tabs);
        mSlidingTabLayout.setElevation(0);
        if (mSharedPreferences.loadNightModeState())
            mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorSecondaryDark)); //NIGHT MODE
        else mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorSecondaryLight)); //DAY MODE

        mSlidingTabLayout.setupWithViewPager(mViewPager);

        for (int i = 0; i < mSlidingTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mSlidingTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(adapter.getTabView(i));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mSharedPreferences.loadNightModeState()){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_main_light, menu);
        } else {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_main_dark, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.action_search:
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.favorites:
                startActivity(new Intent(MainActivity.this, FavoriteListActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public AppBarLayout getAppBarLayout(){
        return appBarLayout;
    }
}
