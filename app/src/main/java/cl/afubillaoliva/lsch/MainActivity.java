package cl.afubillaoliva.lsch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import cl.afubillaoliva.lsch.activities.SettingsActivity;
import cl.afubillaoliva.lsch.adapters.TabsAdapter;
import cl.afubillaoliva.lsch.extras.SlidingTabsLayout;
import cl.afubillaoliva.lsch.utils.SharedPreference;

public class MainActivity extends AppCompatActivity {
    SharedPreference mSharedPreferences;
    public SlidingTabsLayout mSlidingTabLayout;
    public ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSharedPreferences = new SharedPreference(this);
        if (mSharedPreferences.loadNightModeState()) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mViewPager = findViewById(R.id.vp_tabs);
        mViewPager.setAdapter(new TabsAdapter(getSupportFragmentManager(), this));
        mSlidingTabLayout = findViewById(R.id.stl_tabs);
        if (mSharedPreferences.loadNightModeState())
            mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark)); //NIGHT MODE
        else mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight)); //DAY MODE
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorAccent));
        mSlidingTabLayout.setCustomTabView(R.layout.tab_view,R.id.tv_tab);
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
