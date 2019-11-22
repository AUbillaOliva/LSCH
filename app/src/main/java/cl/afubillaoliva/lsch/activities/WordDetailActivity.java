package cl.afubillaoliva.lsch.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.Objects;

import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.WordElementsListAdapter;
import cl.afubillaoliva.lsch.models.Word;
import cl.afubillaoliva.lsch.utils.SharedPreference;

public class WordDetailActivity extends AppCompatActivity {

    Word word;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        SharedPreference mSharedPreferences = new SharedPreference(this);
        if (mSharedPreferences.loadNightModeState()) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.word_detail_layout);
        Intent intent = getIntent();
        word = (Word) intent.getSerializableExtra("position");

        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(word.getTitle());
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        RecyclerView defintionList = findViewById(R.id.definitions_list);
        defintionList.setNestedScrollingEnabled(true);
        RecyclerView sinList = findViewById(R.id.sin_list);
        sinList.setNestedScrollingEnabled(true);
        RecyclerView antList = findViewById(R.id.ant_list);
        antList.setNestedScrollingEnabled(true);

        final VideoView videoView = findViewById(R.id.video);
        Uri uri = Uri.parse(word.getImages()[0]);
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoView.isPlaying()){
                    videoView.pause();
                } else {
                    videoView.start();
                }
            }
        });



        TextView description = findViewById(R.id.text_description);
        if(word.getDescription().size() == 0){
            defintionList.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
        } else {
            ArrayList<String> descriptions = word.getDescription();
            Log.i(MainActivity.TAG, String.valueOf(descriptions));
            WordElementsListAdapter adapter = new WordElementsListAdapter();
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            adapter.addData(descriptions);
            defintionList.setAdapter(adapter);
            defintionList.setLayoutManager(linearLayoutManager);
        }

        TextView sin = findViewById(R.id.text_synonyms);
        if(word.getSin().size() == 0){
            sinList.setVisibility(View.GONE);
            sin.setVisibility(View.GONE);
        } else {
            ArrayList<String> synonyms = word.getSin();
            WordElementsListAdapter adapter = new WordElementsListAdapter();
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            adapter.addData(synonyms);
            sinList.setAdapter(adapter);
            sinList.setLayoutManager(linearLayoutManager);
        }

        TextView ant = findViewById(R.id.text_antonyms);
        if(word.getAnt().size() == 0){
            ant.setVisibility(View.GONE);
            antList.setVisibility(View.GONE);
        } else {
            ArrayList<String> antonyms = word.getAnt();
            WordElementsListAdapter adapter = new WordElementsListAdapter();
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            adapter.addData(antonyms);
            antList.setAdapter(adapter);
            antList.setLayoutManager(linearLayoutManager);
        }

        /*TextView category = findViewById(R.id.text_category);
        if(word.getCategory().length == 0){
            category.setText("CATEGORY");
        } else {
            category.setText(word.getCategory()[0]);
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_word, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.report:
                Intent report = new Intent(this, ReportActivity.class);
                report.putExtra("element", word);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(report);
                break;
            case R.id.send:
                //TODO: IMPROVE TEXT
                Intent send = new Intent();
                send.setAction(Intent.ACTION_SEND);
                send.putExtra(Intent.EXTRA_TEXT, word.getTitle() + "\n" + word.getDescription() + "\n" + word
                .getSin() + "\n" + word.getAnt());
                send.setType("text/plain");
                startActivity(send);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}
