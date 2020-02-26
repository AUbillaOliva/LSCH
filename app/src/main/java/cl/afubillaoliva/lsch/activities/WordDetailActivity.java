package cl.afubillaoliva.lsch.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.WordElementsListAdapter;
import cl.afubillaoliva.lsch.api.ApiClient;
import cl.afubillaoliva.lsch.api.ApiService;
import cl.afubillaoliva.lsch.models.Word;
import cl.afubillaoliva.lsch.tools.Player;
import cl.afubillaoliva.lsch.utils.databases.DownloadDatabaseHelper;
import cl.afubillaoliva.lsch.utils.databases.FavoriteDatabaseHelper;
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
    private final FavoriteDatabaseHelper favoriteDatabaseHelper = new FavoriteDatabaseHelper(context);
    private final DownloadDatabaseHelper downloadDatabaseHelper = new DownloadDatabaseHelper(context);
    private Word word;
    private Network network = new Network(context);

    private NotificationManagerCompat notificationManagerCompat;

    private Player videoView;
    private ImageView errorThumb;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        word = (Word) intent.getSerializableExtra("position");

        notificationManagerCompat = NotificationManagerCompat.from(context);

        mSharedPreferences = new SharedPreference(context);
        if (mSharedPreferences.loadNightModeState())
            setTheme(R.style.AppThemeDark);
        else
            setTheme(R.style.AppTheme);
        setContentView(R.layout.word_detail_layout);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        if(ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        final Toolbar mToolbar = findViewById(R.id.toolbar);
        final RecyclerView defintionList = findViewById(R.id.definitions_list),
                           sinList = findViewById(R.id.sin_list),
                           antList = findViewById(R.id.ant_list),
                           categoryList = findViewById(R.id.category_list);
        final LinearLayout categoryFrame = findViewById(R.id.category_frame),
                           descriptionFrame = findViewById(R.id.descriptions_frame),
                           synonymsFrame = findViewById(R.id.synonyms_frame),
                           antonymsFrame = findViewById(R.id.antonyms_frame);
        videoView = findViewById(R.id.player);
        errorThumb = findViewById(R.id.error_thumb);
        progressBar = findViewById(R.id.progressbar);


        if(mSharedPreferences.loadNightModeState())
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceDark);
        else
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceLight);
        mToolbar.setTitle(word.getTitle());
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        defintionList.setNestedScrollingEnabled(true);
        defintionList.setHasFixedSize(true);

        sinList.setNestedScrollingEnabled(true);
        sinList.setHasFixedSize(true);

        antList.setNestedScrollingEnabled(true);
        antList.setHasFixedSize(false);

        categoryList.setNestedScrollingEnabled(true);
        categoryList.setHasFixedSize(false);

        if(!word.getImages().isEmpty())
            progressBar.setVisibility(View.VISIBLE);

        final File file = new File(getExternalFilesDir(null) + File.separator + word.getTitle().replaceAll("[^a-zA-Z0-9]", "") + ".mp4");
        final Uri uri;
        if(file.exists())
            uri = Uri.parse(file.getPath());
        else
            if(!word.getImages().isEmpty())
                uri = Uri.parse(word.getImages().get(0));
            else
                uri = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            videoView.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE);
        if(uri != null)
            videoView.setVideoURI(uri);
        else {
            progressBar.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
            errorThumb.setVisibility(View.VISIBLE);
        }

        videoView.setOnPreparedListener(mp -> {
            progressBar.setVisibility(View.GONE);
            mp.setLooping(true);
            mp.setVolume(0f,0f);

        });
        videoView.changeVideoSize(544,360);
        videoView.start();
        videoView.setOnClickListener(v -> {
            if(videoView.isPlaying())
                videoView.pause();
            else
                videoView.start();
        });

        WordElementsListAdapter adapter;
        if(word.getCategory() == null || word.getCategory().size() == 0)
            categoryFrame.setVisibility(View.GONE);
        else {
            final ArrayList<String> categories = word.getCategory();
            adapter = new WordElementsListAdapter(context);
            adapter.setRecyclerViewOnClickListenerHack(new RecyclerViewOnClickListenerHack(){
                @Override
                public void onClickListener(View view, int position){
                    final Intent intent = new Intent(context, AbecedaryListActivity.class);
                    intent.putExtra("theme", word.getCategory().get(position));
                    startActivity(intent);
                }

                @Override
                public void onLongPressClickListener(View view, int position){}
            });
            adapter.setNumbered(true);
            adapter.setLinks(true);
            adapter.addData(categories);
            adapter.notifyDataSetChanged();
            categoryList.setAdapter(adapter);
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            categoryList.setLayoutManager(linearLayoutManager);
        }

        if(word.getDescription() == null || word.getDescription().size() == 0)
            descriptionFrame.setVisibility(View.GONE);
        else {
            final ArrayList<String> descriptions = word.getDescription();
            adapter = new WordElementsListAdapter();
            adapter.setNumbered(true);
            adapter.addData(descriptions);
            adapter.notifyDataSetChanged();
            defintionList.setAdapter(adapter);
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            defintionList.setLayoutManager(linearLayoutManager);
        }

        if(word.getSin() == null || word.getSin().size() == 0)
            synonymsFrame.setVisibility(View.GONE);
        else {
            final ArrayList<String> synonyms = word.getSin();
            adapter = new WordElementsListAdapter();
            adapter.addData(synonyms);
            adapter.notifyDataSetChanged();
            sinList.setAdapter(adapter);
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            sinList.setLayoutManager(linearLayoutManager);
        }

        if(word.getAnt() == null || word.getAnt().size() == 0)
            antonymsFrame.setVisibility(View.GONE);
        else {
            final ArrayList<String> antonyms = word.getAnt();
            adapter = new WordElementsListAdapter();
            adapter.addData(antonyms);
            adapter.notifyDataSetChanged();
            antList.setAdapter(adapter);
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            antList.setLayoutManager(linearLayoutManager);
        }

    }

    // TODO: DOWNLOAD VIDEO TO CACHE AND USE IT.

    @Override
    protected void onResume() {
        super.onResume();

        if(!word.getImages().isEmpty())
            progressBar.setVisibility(View.VISIBLE);

        final File file = new File(getExternalFilesDir(null) + File.separator + word.getTitle().replaceAll("[^a-zA-Z0-9]", "") + ".mp4");
        final Uri uri;
        if(file.exists())
            uri = Uri.parse(file.toString());
        else
            if(!word.getImages().isEmpty())
                uri = Uri.parse(word.getImages().get(0));
            else
                uri = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            videoView.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE);
        if(uri != null)
            videoView.setVideoURI(uri);
        else {
            videoView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            errorThumb.setVisibility(View.VISIBLE);
        }

        videoView.setOnPreparedListener(mp -> {
            progressBar.setVisibility(View.GONE);
            mp.setLooping(true);
            mp.setVolume(0f,0f);
        });
        videoView.changeVideoSize(544,360);
        videoView.start();
        videoView.setOnClickListener(v -> {
            if(videoView.isPlaying()) videoView.pause();
            else videoView.start();
        });
    }

    private ArrayList<String> errorOptions(){
        ArrayList<String> options = new ArrayList<>();
        options.add("Ortografía");
        options.add("Contenido");
        options.add("Video");
        return options;
    }

    private void getData(final Word url){
        final Cache cache = new Cache(getCacheDir(), MainActivity.cacheSize);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(chain -> {
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
                })
                .build();

        final ApiService.WordService service = ApiClient.getClient(okHttpClient).create(ApiService.WordService.class);
        final Call<ResponseBody> call = service.getVideo(url.getImages().get(0));

        call.enqueue(new Callback<ResponseBody>(){
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response){
                if (response.isSuccessful()) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            assert response.body() != null;
                            final NotificationCompat.Builder notification = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                                    .setShowWhen(false)
                                    .setContentTitle("Descarga")
                                    .setContentText(response.body().contentLength() / 1000 + " KB")
                                    .setSubText("Descarga en progreso...")
                                    .setPriority(NotificationCompat.PRIORITY_LOW)
                                    .setProgress(0,0,true);
                            if(mSharedPreferences.loadNightModeState())
                                notification.setSmallIcon(R.drawable.ic_app_icon_light);
                            else
                                notification.setSmallIcon(R.drawable.ic_app_icon_dark);
                            notificationManagerCompat.notify(1, notification.build());
                            final boolean writtenToDisk = writeResponseBodyToDisk(response.body(), word.getTitle());
                            Log.d(MainActivity.TAG, "file download was a success? " + writtenToDisk);
                            if(writtenToDisk){
                                notificationManagerCompat.cancel(1);
                                notification
                                        .setContentTitle("Descarga completada")
                                        .setSound(null)
                                        .setSubText(null)
                                        .setProgress(0,0,false);
                                notificationManagerCompat.notify(1, notification.build());
                            } else
                                deleteVideo(word.getTitle());
                            return null;
                        }
                    }.execute();
                }
                else Log.d(MainActivity.TAG, "server contact failed");
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t){
                Log.e(MainActivity.TAG, "Error: " + t.getMessage());
            }
        });

    }

    private void deleteVideo(String fileName){
        final File file = new File(getExternalFilesDir(null) + File.separator + fileName.replaceAll("[^a-zA-Z0-9]", "") + ".mp4");
        if (file.exists())
            downloadDatabaseHelper.deleteDownload(fileName);
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String fileName){
        final File file = new File(getExternalFilesDir(null) + File.separator + fileName.replaceAll("[^a-zA-Z0-9]", "") + ".mp4");
        long fileSizeDownloaded = 0;
        final long fileSize = body.contentLength();
        final byte[] fileReader = new byte[4096*1000];
        try (InputStream inputStream = body.byteStream(); OutputStream outputStream = new FileOutputStream(file)){
            while (true){
                final int read = inputStream.read(fileReader);
                if (read == -1)
                    break;
                outputStream.write(fileReader, 0, read);
                fileSizeDownloaded += read;
                Log.d(MainActivity.TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return true;
        } catch (IOException e){
            return false;
        }
    }

    public void setFavorite(MenuItem item){
        if(!mSharedPreferences.loadNightModeState())
            if(favoriteDatabaseHelper.exists(word.getTitle())){
                favoriteDatabaseHelper.deleteFavorite(word.getTitle());
                deleteVideo(word.getTitle());
                Log.d(MainActivity.FAV, "DELETED: " + word.getTitle());
                item.setIcon(R.drawable.ic_favorite_border_black_24dp);
            } else {
                if(mSharedPreferences.loadAutoDownload() && !word.getImages().isEmpty())
                    setDownloaded(item);
                favoriteDatabaseHelper.addFavorite(word);
                mSharedPreferences.setFavoriteDisabled(false);
                Log.d(MainActivity.FAV, "ADDED: " + word.getTitle());
                item.setIcon(R.drawable.ic_favorite_black_24dp);
            }
        else
            if(favoriteDatabaseHelper.exists(word.getTitle())){
                deleteVideo(word.getTitle());
                favoriteDatabaseHelper.deleteFavorite(word.getTitle());
                Log.d(MainActivity.FAV, "DELETED: " + word.getTitle());
                item.setIcon(R.drawable.ic_favorite_border_white_24dp);
            } else {
                if(mSharedPreferences.loadAutoDownload() && !word.getImages().isEmpty())
                    setDownloaded(item);
                favoriteDatabaseHelper.addFavorite(word);
                mSharedPreferences.setFavoriteDisabled(false);
                Log.d(MainActivity.FAV, "ADDED: " + word.getTitle());
                item.setIcon(R.drawable.ic_favorite_white_24dp);
            }
    }

    public void setDownloaded(MenuItem item){
        if(!mSharedPreferences.loadNightModeState())
            if(downloadDatabaseHelper.exists(word.getTitle())){
                deleteVideo(word.getTitle());
                downloadDatabaseHelper.deleteDownload(word.getTitle());
                item.setIcon(R.drawable.ic_file_download_black_24dp);
            } else {
                getData(word);
                mSharedPreferences.setDownloadDisabled(false);
                downloadDatabaseHelper.addDownload(word);
                item.setIcon(R.drawable.ic_file_downloaded_24dp);
            }
        else
            if(downloadDatabaseHelper.exists(word.getTitle())){
                deleteVideo(word.getTitle());
                downloadDatabaseHelper.deleteDownload(word.getTitle());
                item.setIcon(R.drawable.ic_file_download_white_24dp);
            } else {
                getData(word);
                mSharedPreferences.setDownloadDisabled(false);
                downloadDatabaseHelper.addDownload(word);
                item.setIcon(R.drawable.ic_file_downloaded_24dp);
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_word, menu);

        final MenuItem favoriteItem = menu.findItem(R.id.favorite);
        final MenuItem downloadItem = menu.findItem(R.id.download);

        final File file = new File(getExternalFilesDir(null) + File.separator + word.getTitle().replaceAll("[^a-zA-Z0-9]", "") + ".mp4");
        if(!mSharedPreferences.loadNightModeState()){
            if(file.exists()) {
                downloadItem.setIcon(R.drawable.ic_file_downloaded_24dp);
                downloadItem.setChecked(true);
            } else
                downloadItem.setIcon(R.drawable.ic_file_download_black_24dp);
            if(favoriteDatabaseHelper.exists(word.getTitle()))
                favoriteItem.setIcon(R.drawable.ic_favorite_black_24dp);
            else
                favoriteItem.setIcon(R.drawable.ic_favorite_border_black_24dp);
        } else {
            if(file.exists()){
                downloadItem.setIcon(R.drawable.ic_file_downloaded_24dp);
                downloadItem.setChecked(true);
            } else
                downloadItem.setIcon(R.drawable.ic_file_download_white_24dp);
            if(favoriteDatabaseHelper.exists(word.getTitle()))
                favoriteItem.setIcon(R.drawable.ic_favorite_white_24dp);
            else
                favoriteItem.setIcon(R.drawable.ic_favorite_border_white_24dp);
        }

        downloadItem.setOnMenuItemClickListener(item -> {
            /*if(file.exists()){
                deleteVideo(word.getTitle());
                if(!mSharedPreferences.loadNightModeState())
                    downloadItem.setIcon(R.drawable.ic_file_download_black_24dp);
                else
                    downloadItem.setIcon(R.drawable.ic_file_download_white_24dp);
            } else {
                if(!word.getImages().isEmpty()){
                    getData(word);
                    downloadItem.setIcon(R.drawable.ic_file_downloaded_24dp);
                }
            }*/
            setDownloaded(item);
            return false;
        });

        favoriteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                setFavorite(item);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.report:
                final Intent report = new Intent(context, ListActivity.class);
                report.putExtra("element", word);
                report.putExtra("data", errorOptions());
                startActivity(report);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            /*case R.id.send:
                TODO: IMPROVE TEXT, CREATE SHARING OPTION WITH VIDEO URL.

                final ContentValues contentValues = new ContentValues(4);
                contentValues.put(MediaStore.Video.VideoColumns.DATE_ADDED,
                        System.currentTimeMillis() / 1000);
                contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                contentValues.put(MediaStore.Video.Media.DATA, word.getImages().get(0));
                ContentResolver resolver = getBaseContext().getContentResolver();

                Uri uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);

                Intent send = new Intent(Intent.ACTION_SEND);
                send.setType("video/*");
                send.putExtra(Intent.EXTRA_SUBJECT, word.getTitle());
                send.putExtra(Intent.EXTRA_STREAM, uri);
                send.putExtra(Intent.EXTRA_TEXT, word.getTitle() + "\n" + word.getDescription() + "\n" + word
                        .getSin() + "\n" + word.getAnt());
                startActivity(Intent.createChooser(send, "Compartir en:"));

                final Intent send = new Intent(Intent.ACTION_SEND);
                send.putExtra(Intent.EXTRA_SUBJECT, word.getTitle());
                send.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("\n" + "DESCRIPCION: " + word.getDescription()+ "\n\n" + "SINÓNIMOS: " + word
                        .getSin() + "\n\n" + "ANTÓNIMOS: " + word.getAnt() + "\n\n" + word.getImages().get(0)));
                send.setType("text/plain");
                startActivity(Intent.createChooser(send, "Enviar a:"));
                break;*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
            } else {
                // User refused to grant permission.
            }
        }*/
    }

    @Override
    public void onBackPressed(){
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(newBase);
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
    }

}
