package cl.afubillaoliva.lsch.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.WordElementsListAdapter;
import cl.afubillaoliva.lsch.api.ApiClient;
import cl.afubillaoliva.lsch.api.ApiService;
import cl.afubillaoliva.lsch.models.Word;
import cl.afubillaoliva.lsch.utils.FavoriteContract;
import cl.afubillaoliva.lsch.utils.FavoriteDatabaseHelper;
import cl.afubillaoliva.lsch.utils.Network;
import cl.afubillaoliva.lsch.utils.SharedPreference;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WordDetailActivity extends AppCompatActivity {

    private final Context context = this;

    private SharedPreference mSharedPreferences;
    private final FavoriteDatabaseHelper dbHelper = new FavoriteDatabaseHelper(context);
    private SQLiteDatabase mDb;
    private Word word;
    private Network network = new Network(context);

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

        String fileName = word.getTitle();
        if(fileName.contains("/")){
            fileName = fileName.replaceAll("[^a-zA-Z0-9]", "");
        }
        File file = new File(getExternalFilesDir(null) + File.separator + fileName + ".mp4");
        final Uri uri;
        if(file.exists())
            uri = Uri.parse(file.toString());
        else
            uri = Uri.parse(word.getImages().get(0));


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

    private void getData(final Word url) {
        Cache cache = new Cache(getCacheDir(), MainActivity.cacheSize);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public okhttp3.Response intercept(@NonNull Interceptor.Chain chain)
                            throws IOException {
                        Request request = chain.request();
                        int maxStale = 60 * 60 * 24 * 7; // tolerate 4-weeks stale \
                        if (network.isNetworkAvailable()) {
                            request = request
                                    .newBuilder()
                                    .header("Cache-Control", "public, max-age=" + 5)
                                    .build();
                            Log.d(MainActivity.TAG, "using cache that was stored 5 seconds ago");
                        } else {
                            request = request
                                    .newBuilder()
                                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                    .build();
                            Log.d(MainActivity.TAG, "using cache that was stored 7 days ago");
                        }
                        return chain.proceed(request);
                    }
                })
                .build();

        ApiService.WordService service = ApiClient.getClient(okHttpClient).create(ApiService.WordService.class);
        Call<ResponseBody> call = service.getVideo(url.getImages().get(0));

        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(MainActivity.TAG, "server contacted and has file");

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {

                            assert response.body() != null;
                            boolean writtenToDisk = writeResponseBodyToDisk(response.body(), word.getTitle());

                            Log.d(MainActivity.TAG, "file download was a success? " + writtenToDisk);

                            return null;
                        }
                    }.execute();
                }
                else {
                    Log.d(MainActivity.TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(MainActivity.TAG, "error");
            }
        });

    }

    private void deleteVideo(String fileName){
        File file = new File(getExternalFilesDir(null) + File.separator + fileName + ".mp4");
        if (file.exists()) {
            if(file.delete())
                Log.d(MainActivity.TAG, "File deleted");
        } else {
            Log.d(MainActivity.TAG, "File not deleted");
        }
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        if(fileName.contains("/")){
            fileName = fileName.replaceAll("[^a-zA-Z0-9]", "");
        }
        File file = new File(getExternalFilesDir(null) + File.separator + fileName + ".mp4");
        long fileSizeDownloaded = 0;
        long fileSize = body.contentLength();
        byte[] fileReader = new byte[4096*1000];
        try (InputStream inputStream = body.byteStream(); OutputStream outputStream = new FileOutputStream(file)) {

            while (true) {
                int read = inputStream.read(fileReader);

                if (read == -1)
                    break;

                outputStream.write(fileReader, 0, read);
                fileSizeDownloaded += read;
                Log.d(MainActivity.TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
            }
            outputStream.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
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
                deleteVideo(word.getTitle());
                Log.d(MainActivity.FAV, "DELETED: " + word.getTitle());
                item.setIcon(R.drawable.ic_favorite_border_black_24dp);
            }else{
                favoriteDatabaseHelper.addFavorite(word);
                getData(word);
                Log.d(MainActivity.FAV, "ADDED: " + word.getTitle());
                item.setIcon(R.drawable.ic_favorite_black_24dp);
            }
        } else {
            if(exists(word.getTitle())){
                deleteVideo(word.getTitle());
                favoriteDatabaseHelper.deleteFavorite(word.getTitle());
                Log.d(MainActivity.FAV, "DELETED: " + word.getTitle());
                item.setIcon(R.drawable.ic_favorite_border_white_24dp);
            }else{
                favoriteDatabaseHelper.addFavorite(word);
                getData(word);
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
