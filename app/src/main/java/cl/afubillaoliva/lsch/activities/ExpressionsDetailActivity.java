package cl.afubillaoliva.lsch.activities;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.util.Objects;

import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.models.Expressions;
import cl.afubillaoliva.lsch.tools.Player;
import cl.afubillaoliva.lsch.utils.SharedPreference;

//TODO: DESIGN LAYOUT

public class ExpressionsDetailActivity extends AppCompatActivity {

    private final Context context = this;

    Intent intent;
    Expressions expression;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        intent = getIntent();
        expression = (Expressions) intent.getSerializableExtra("position");

        SharedPreference mSharedPreferences = new SharedPreference(context);
        if(mSharedPreferences.loadNightModeState())
            setTheme(R.style.AppThemeDark);
        else setTheme(R.style.AppTheme);
        setContentView(R.layout.expression_detail_activity);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        final Toolbar mToolbar = findViewById(R.id.toolbar);
        if(mSharedPreferences.loadNightModeState())
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceDark);
        else
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceLight);
        mToolbar.setTitle(expression.getTitle());
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        final ProgressBar progressBar = findViewById(R.id.progressbar);
        //final VideoView videoView = findViewById(R.id.video);
        Player player = findViewById(R.id.player);

        progressBar.setVisibility(View.VISIBLE);

        String fileName = expression.getTitle();
        if(fileName.contains("/")){
            fileName = fileName.replaceAll("[^a-zA-Z0-9]", "");
        }
        File file = new File(getExternalFilesDir(null) + File.separator + fileName + ".mp4");
        final Uri uri;
        if(file.exists())
            uri = Uri.parse(file.toString());
        else
            if(!expression.getImages().isEmpty())
                uri = Uri.parse(expression.getImages().get(0));
            else
                uri = null;
        player.setDisplayMode(Player.DisplayMode.ORIGINAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            player.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE);
        player.setVideoURI(uri);
        player.start();
        player.changeVideoSize(544, 360);
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.setLooping(true);
                mp.setVolume(0f,0f);
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        progressBar.setVisibility(View.GONE);
                        mp.start();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_expression, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
