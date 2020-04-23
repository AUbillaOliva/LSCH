package cl.afubillaoliva.lsch;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import androidx.viewpager2.widget.ViewPager2;
import cl.afubillaoliva.lsch.activities.DownloadListActivity;
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
    public static final String REP = "REPORT";
    public static final String DOWN = "DOWNLOAD";

    public static final String CHANNEL_ID = "channel1";
    public static final String GROUP_KEY = "cl.afubillaoliva.lsch";

    public static final int cacheSize = 10 * 1024 * 1024; // 10 MiB
    public static final int REQUEST_CODE = 1;

    private final Context context = this;
    private SharedPreference mSharedPreferences;

    public Toolbar mToolbar;
    public AppBarLayout appBarLayout;
    public View appBarLayoutShadow;
    private TextView toolbarTitle;
    private ImageView toolbarIcon;
    private Toolbar.LayoutParams llp;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        createNotificationChannel();

        mSharedPreferences = new SharedPreference(context);
        if (mSharedPreferences.loadNightModeState())
            setTheme(R.style.AppThemeDark);
        else
            setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);

        final ViewPager2 mViewPager = findViewById(R.id.vp_tabs);
        final StateListAnimator stateListAnimator = new StateListAnimator();
        final TabLayout mSlidingTabLayout = findViewById(R.id.stl_tabs);
        mToolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.main_appbar_layout);
        appBarLayoutShadow = findViewById(R.id.divider);
        toolbarTitle = mToolbar.findViewById(R.id.toolbar_title);

        stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(appBarLayout, "elevation", 2));
        appBarLayout.setStateListAnimator(stateListAnimator);

        if(mSharedPreferences.loadNightModeState())
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceDark);
        else
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceLight);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        mToolbar.setOnClickListener(v -> {
            startActivity(new Intent(context, SearchActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
        animateTitleChange(context.getString(R.string.search));

        toolbarTitle.setText(R.string.app_name);
        toolbarIcon = mToolbar.findViewById(R.id.toolbar_icon);
        toolbarIcon.setVisibility(View.GONE);
        llp = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        llp.gravity = Gravity.CENTER;
        toolbarTitle.setLayoutParams(llp);

        final TabsAdapter adapter = new TabsAdapter(this, context);
        adapter.addFragment(new Abecedary(), context.getResources().getString(R.string.abecedary));
        adapter.addFragment(new Themes(), context.getResources().getString(R.string.thematic));
        adapter.addFragment(new Expressions(), context.getResources().getString(R.string.expressions));

        mViewPager.setAdapter(adapter);
        mSlidingTabLayout.setElevation(0);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(mSlidingTabLayout, mViewPager, true, (tab, position) -> {
            tab.setText(adapter.getPageTitle(position));
            tab.setCustomView(adapter.getTabView(position));
        });
        tabLayoutMediator.attach();
        if (mSharedPreferences.loadNightModeState())
            mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorSecondaryDark)); //NIGHT MODE
        else
            mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorSecondaryLight)); //DAY MODE

        mSlidingTabLayout.setUnboundedRipple(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if (!mSharedPreferences.loadNightModeState())
            getMenuInflater().inflate(R.menu.menu_main_light, menu);
        else
            getMenuInflater().inflate(R.menu.menu_main_dark, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
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
            case R.id.downloads:
                startActivity(new Intent(context, DownloadListActivity.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    public AppBarLayout getAppBarLayout(){
        return appBarLayout;
    }

    public View getAppBarLayoutShadow(){
        return appBarLayoutShadow;
    }

    private void animateTitleChange(String newTitle){
        final View view = mToolbar.findViewById(R.id.toolbar_title);

        if (view instanceof TextView)
            new android.os.Handler().postDelayed(() -> {
                    AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
                    fadeOut.setDuration(250);
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation){}

                        @Override
                        public void onAnimationEnd(Animation animation){
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
                }, 3500
            );
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final CharSequence name = getString(R.string.download_channel_name);
            final String description = getString(R.string.download_channel_description);
            final int importance = NotificationManager.IMPORTANCE_LOW;
            final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            final NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(newBase);
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
    }

}
