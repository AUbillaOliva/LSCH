package cl.afubillaoliva.lsch.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
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
import cl.afubillaoliva.lsch.utils.FavoriteContract;
import cl.afubillaoliva.lsch.utils.FavoriteDatabaseHelper;
import cl.afubillaoliva.lsch.utils.SharedPreference;

public class WordDetailActivity extends AppCompatActivity {

    private final Context context = this;

    private SharedPreference mSharedPreferences;
    private final FavoriteDatabaseHelper dbHelper = new FavoriteDatabaseHelper(context);
    private SQLiteDatabase mDb;
    private Word word;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mSharedPreferences = new SharedPreference(context);
        if (mSharedPreferences.loadNightModeState()) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.word_detail_layout);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        final Intent intent = getIntent();
        word = (Word) intent.getSerializableExtra("position");

        mDb = dbHelper.getWritableDatabase();

        final Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(word.getTitle());
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        final RecyclerView defintionList = findViewById(R.id.definitions_list);
        defintionList.setNestedScrollingEnabled(true);
        defintionList.setHasFixedSize(true);

        final RecyclerView sinList = findViewById(R.id.sin_list);
        sinList.setNestedScrollingEnabled(true);
        sinList.setHasFixedSize(true);

        final RecyclerView antList = findViewById(R.id.ant_list);
        antList.setNestedScrollingEnabled(true);
        antList.setHasFixedSize(true);

        final VideoView videoView = findViewById(R.id.video);
        final ArrayList<String> images = word.getImages();
        final Uri uri = Uri.parse(images.get(0));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            videoView.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE);
        }
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.setVolume(0f,0f);
            }
        });
        videoView.start();
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoView.isPlaying()) videoView.pause();
                else videoView.start();
            }
        });
        Log.d(MainActivity.TAG, word.getImages().get(0));

        final TextView description = findViewById(R.id.text_description);
        WordElementsListAdapter adapter;
        if(word.getDescription().size() == 0){
            defintionList.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
        } else {
            final ArrayList<String> descriptions = word.getDescription();
            Log.i(MainActivity.TAG, String.valueOf(descriptions));
            adapter = new WordElementsListAdapter();
            adapter.addData(descriptions);
            adapter.notifyDataSetChanged();
            defintionList.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            defintionList.setLayoutManager(linearLayoutManager);
        }

        final TextView sin = findViewById(R.id.text_synonyms);
        if(word.getSin().size() == 0){
            sinList.setVisibility(View.GONE);
            sin.setVisibility(View.GONE);
        } else {
            final ArrayList<String> synonyms = word.getSin();
            adapter = new WordElementsListAdapter();
            adapter.addData(synonyms);
            adapter.notifyDataSetChanged();
            sinList.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            sinList.setLayoutManager(linearLayoutManager);
        }

        final TextView ant = findViewById(R.id.text_antonyms);
        if(word.getAnt().size() == 0){
            ant.setVisibility(View.GONE);
            antList.setVisibility(View.GONE);
        } else {
            final ArrayList<String> antonyms = word.getAnt();
            adapter = new WordElementsListAdapter();
            adapter.addData(antonyms);
            adapter.notifyDataSetChanged();
            antList.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            antList.setLayoutManager(linearLayoutManager);
        }

        /*TextView category = findViewById(R.id.text_category);
        if(word.getCategory().length == 0){
            category.setText("CATEGORY");
        } else {
            category.setText(word.getCategory()[0]);
        }*/
    }

    public boolean exists(String searchItem) {

        final String[] projection = {
                FavoriteContract.FavoriteEntry._ID,
                FavoriteContract.FavoriteEntry.COLUMN_WORD_ID,
                FavoriteContract.FavoriteEntry.COLUMN_WORD_TITLE,
                FavoriteContract.FavoriteEntry.COLUMN_WORD_DESCRIPTIONS,
                FavoriteContract.FavoriteEntry.COLUMN_ANTONYMS,
                FavoriteContract.FavoriteEntry.COLUMN_SYNONYMS,
                FavoriteContract.FavoriteEntry.COLUMN_CATEGORY,
                FavoriteContract.FavoriteEntry.COLUMN_WORD_IMAGES

        };
        final String selection = FavoriteContract.FavoriteEntry.COLUMN_WORD_ID + " =?";
        final String[] selectionArgs = { searchItem };
        final String limit = "1";

        final Cursor cursor = mDb.query(FavoriteContract.FavoriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null, limit);
        final boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_word, menu);

        final MenuItem item = menu.findItem(R.id.favorite);
        if(!mSharedPreferences.loadNightModeState()){
            if(exists(word.getTitle())){
                Log.d(MainActivity.FAV, "onOptionsMenu, already added: " + word.getTitle());
                item.setIcon(R.drawable.ic_favorite_black_24dp);
            } else {
                Log.d(MainActivity.FAV, "onOptionsMenu, is not added: " + word.getTitle());
                item.setIcon(R.drawable.ic_favorite_border_black_24dp);
            }
        } else {
            if(exists(word.getTitle())){
                Log.d(MainActivity.FAV, "onOptionsMenu, already added: " + word.getTitle());
                item.setIcon(R.drawable.ic_favorite_white_24dp);
            } else {
                Log.d(MainActivity.FAV, "onOptionsMenu, is not added: " + word.getTitle());
                item.setIcon(R.drawable.ic_favorite_border_white_24dp);
            }
        }

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                setFavorite(item);
                return false;
            }
        });
        return true;
    }

    public void setFavorite(MenuItem item){
        final FavoriteDatabaseHelper favoriteDatabaseHelper = new FavoriteDatabaseHelper(context);
        if(!mSharedPreferences.loadNightModeState()){
            if(exists(word.getTitle())){
                favoriteDatabaseHelper.deleteFavorite(word.getTitle());
                Log.d(MainActivity.FAV, "DELETED: " + word.getTitle());
                item.setIcon(R.drawable.ic_favorite_border_black_24dp);
            }else{
                favoriteDatabaseHelper.addFavorite(word);
                Log.d(MainActivity.FAV, "ADDED: " + word.getTitle());
                item.setIcon(R.drawable.ic_favorite_black_24dp);
            }
        } else {
            if(exists(word.getTitle())){
                favoriteDatabaseHelper.deleteFavorite(word.getTitle());
                Log.d(MainActivity.FAV, "DELETED: " + word.getTitle());
                item.setIcon(R.drawable.ic_favorite_border_white_24dp);
            }else{
                favoriteDatabaseHelper.addFavorite(word);
                Log.d(MainActivity.FAV, "ADDED: " + word.getTitle());
                item.setIcon(R.drawable.ic_favorite_white_24dp);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.report:
                final Intent report = new Intent(context, ReportActivity.class);
                report.putExtra("element", word);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(report);
                break;
            case R.id.send:
                //TODO: IMPROVE TEXT
                final Intent send = new Intent();
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
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
