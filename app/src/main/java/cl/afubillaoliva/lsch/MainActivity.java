package cl.afubillaoliva.lsch;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

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
    public static final String HIS = "HISTORY";

    public static final int cacheSize = 10 * 1024 * 1024; // 10 MiB

    private final Context context = this;
    private static MainActivity instance;
    private SharedPreference mSharedPreferences;

    public Toolbar mToolbar;
    public AppBarLayout appBarLayout;
    public View appBarLayoutShadow;
    private TextView toolbarTitle;
    private ImageView toolbarIcon;
    private Toolbar.LayoutParams llp;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = new SharedPreference(context);
        if (mSharedPreferences.loadNightModeState()) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.main_appbar_layout);
        appBarLayoutShadow = findViewById(R.id.divider);
        final ViewPager mViewPager = findViewById(R.id.vp_tabs);
        final StateListAnimator stateListAnimator = new StateListAnimator();
        toolbarTitle = mToolbar.findViewById(R.id.toolbar_title);

        stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(appBarLayout, "elevation", 2));
        appBarLayout.setStateListAnimator(stateListAnimator);

        if(mSharedPreferences.loadNightModeState())
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceDark);
        else
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceLight);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SearchActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
        animateTitleChange(context.getString(R.string.search));

        toolbarTitle.setText(R.string.app_name);
        toolbarIcon = mToolbar.findViewById(R.id.toolbar_icon);
        toolbarIcon.setVisibility(View.GONE);
        llp = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        llp.gravity = Gravity.CENTER;
        toolbarTitle.setLayoutParams(llp);

        final TabsAdapter adapter = new TabsAdapter(getSupportFragmentManager(), context);
        adapter.addFragment(new Abecedary(), "Abecedario");
        adapter.addFragment(new Themes(), "Orden temático");
        adapter.addFragment(new Expressions(), "Expresiones de uso cotidiano");
        mViewPager.setAdapter(adapter);

        final TabLayout mSlidingTabLayout = findViewById(R.id.stl_tabs);
        mSlidingTabLayout.setElevation(0);
        if (mSharedPreferences.loadNightModeState())
            mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorSecondaryDark)); //NIGHT MODE
        else mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorSecondaryLight)); //DAY MODE

        mSlidingTabLayout.setupWithViewPager(mViewPager);
        mSlidingTabLayout.setUnboundedRipple(true);

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
            getMenuInflater().inflate(R.menu.menu_main_light, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main_dark, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(context, SettingsActivity.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.favorites:
                startActivity(new Intent(context, FavoriteListActivity.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public static MainActivity getInstance(){
        return instance;
    }

    public AppBarLayout getAppBarLayout(){ return appBarLayout; }

    public View getAppBarLayoutShadow(){ return appBarLayoutShadow; }

    private void animateTitleChange(final String newTitle) {
        final View view = mToolbar.findViewById(R.id.toolbar_title);

        if (view instanceof TextView) {
            new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
                        fadeOut.setDuration(250);
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                toolbarTitle.setText(newTitle);
                                toolbarTitle.setTextSize(16);
                                toolbarTitle.setTextColor(Color.GRAY);

                                if(mSharedPreferences.loadNightModeState())
                                    toolbarIcon.setImageResource(R.drawable.ic_search_white_24dp);
                                else
                                    toolbarIcon.setImageResource(R.drawable.ic_search_black_24dp);

                                toolbarIcon.setVisibility(View.VISIBLE);


                                llp.gravity = Gravity.START;
                                toolbarTitle.setLayoutParams(llp);

                                AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
                                fadeIn.setDuration(250);
                                view.startAnimation(fadeIn);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        view.startAnimation(fadeOut);
                    }

                }, 3500
            );
        }
    }

}
