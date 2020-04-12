package cl.afubillaoliva.lsch.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Objects;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.WordElementsListAdapter;
import cl.afubillaoliva.lsch.models.Word;
import cl.afubillaoliva.lsch.services.DownloadService;
import cl.afubillaoliva.lsch.tools.DownloadReceiver;
import cl.afubillaoliva.lsch.tools.Player;
import cl.afubillaoliva.lsch.utils.databases.DownloadDatabaseHelper;
import cl.afubillaoliva.lsch.utils.databases.FavoriteDatabaseHelper;
import cl.afubillaoliva.lsch.utils.SharedPreference;

public class DataDetailActivity extends AppCompatActivity implements DownloadReceiver.Receiver {

    private final Context context = this;
    private SharedPreference mSharedPreferences;
    private final FavoriteDatabaseHelper favoriteDatabaseHelper = new FavoriteDatabaseHelper(context);
    private final DownloadDatabaseHelper downloadDatabaseHelper = new DownloadDatabaseHelper(context);
    private Word word;
    private DownloadReceiver receiver;

    private String type, list;

    private Menu mainMenu;
    private Player videoView;
    private ImageView errorThumb;
    private ProgressBar progressBar;

    private ConnectivityManager connMgr;
    private android.net.NetworkInfo wifi;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifi = Objects.requireNonNull(connMgr).getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        receiver = new DownloadReceiver(new Handler(), context);
        receiver.setReceiver(this);

        final Intent intent = getIntent();
        word = (Word) intent.getSerializableExtra("position");
        list = intent.getStringExtra("list");
        type = intent.getStringExtra("type");

        mSharedPreferences = new SharedPreference(context);
        if (mSharedPreferences.loadNightModeState())
            setTheme(R.style.AppThemeDark);
        else
            setTheme(R.style.AppTheme);
        setContentView(R.layout.word_detail_layout);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

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

        final File file = new File(getExternalFilesDir(null) + File.separator + stripAccents(word.getTitle()) + ".mp4");
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
                    final Intent intent = new Intent(context, DataListActivity.class);
                    intent.putExtra("list", word.getCategory().get(position));
                    intent.putExtra("theme", word.getCategory().get(position));
                    intent.putExtra("type", type);
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

    @Override
    protected void onResume() {
        super.onResume();

        if(!word.getImages().isEmpty())
            progressBar.setVisibility(View.VISIBLE);

        final File file = new File(getExternalFilesDir(null) + File.separator + stripAccents(word.getTitle()) + ".mp4");
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

    public static String stripAccents(String s){
        s = s.replaceAll("/", "");
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    private void deleteVideo(String fileName){
        final File file = new File(getExternalFilesDir(null) + File.separator  + stripAccents(fileName) + ".mp4");
        if (file.exists()){
            downloadDatabaseHelper.deleteDownload(fileName);
            file.delete();
        }
    }

    public void setFavorite(MenuItem item) {
        final MenuItem download = mainMenu.findItem(R.id.download);
        if(mSharedPreferences.loadNightModeState()){
            if(favoriteDatabaseHelper.exists(word.getTitle())){
                if(mSharedPreferences.loadAutoDownload()){
                    favoriteDatabaseHelper.deleteFavorite(word.getTitle());
                    deleteVideo(word.getTitle());
                    item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                    download.setIcon(R.drawable.ic_file_download_white_24dp);
                } else {
                    favoriteDatabaseHelper.deleteFavorite(word.getTitle());
                    item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                }
            } else {
                if(mSharedPreferences.isWifiOnly()){
                    connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
                    wifi = Objects.requireNonNull(connMgr).getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if(mSharedPreferences.loadAutoDownload() && word.getImages() != null){
                        if(wifi.isConnected()){
                            setDownloaded(item);
                            favoriteDatabaseHelper.addFavorite(word);
                            item.setIcon(R.drawable.ic_favorite_white_24dp);
                            download.setIcon(R.drawable.ic_file_downloaded_24dp);
                            mSharedPreferences.setDownloadDisabled(false);
                            mSharedPreferences.setFavoriteDisabled(false);
                        } else
                            Toast.makeText(context, context.getResources().getString(R.string.autodownlad_message), Toast.LENGTH_LONG).show();
                    } else {
                        favoriteDatabaseHelper.addFavorite(word);
                        item.setIcon(R.drawable.ic_favorite_white_24dp);
                        mSharedPreferences.setFavoriteDisabled(false);
                    }
                } else {
                    if(mSharedPreferences.loadAutoDownload() && word.getImages() != null){
                        setDownloaded(item);
                        favoriteDatabaseHelper.addFavorite(word);
                        item.setIcon(R.drawable.ic_favorite_white_24dp);
                        download.setIcon(R.drawable.ic_file_downloaded_24dp);
                        mSharedPreferences.setDownloadDisabled(false);
                        mSharedPreferences.setFavoriteDisabled(false);
                    } else {
                        favoriteDatabaseHelper.addFavorite(word);
                        item.setIcon(R.drawable.ic_favorite_white_24dp);
                        mSharedPreferences.setFavoriteDisabled(false);
                    }
                }
            }
        } else {
            if(favoriteDatabaseHelper.exists(word.getTitle())){
                if(mSharedPreferences.loadAutoDownload()){
                    favoriteDatabaseHelper.deleteFavorite(word.getTitle());
                    deleteVideo(word.getTitle());
                    item.setIcon(R.drawable.ic_favorite_border_black_24dp);
                    download.setIcon(R.drawable.ic_file_download_black_24dp);
                } else {
                    favoriteDatabaseHelper.deleteFavorite(word.getTitle());
                    item.setIcon(R.drawable.ic_favorite_border_black_24dp);
                }
            } else {
                if(mSharedPreferences.isWifiOnly()){
                    connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
                    wifi = Objects.requireNonNull(connMgr).getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if(mSharedPreferences.loadAutoDownload() && word.getImages() != null){
                        if(wifi.isConnected()){
                            setDownloaded(item);
                            favoriteDatabaseHelper.addFavorite(word);
                            item.setIcon(R.drawable.ic_favorite_black_24dp);
                            download.setIcon(R.drawable.ic_file_downloaded_24dp);
                            mSharedPreferences.setDownloadDisabled(false);
                            mSharedPreferences.setFavoriteDisabled(false);
                        } else
                            Toast.makeText(context, context.getResources().getString(R.string.autodownlad_message), Toast.LENGTH_LONG).show();
                    } else {
                        favoriteDatabaseHelper.addFavorite(word);
                        item.setIcon(R.drawable.ic_favorite_black_24dp);
                        mSharedPreferences.setFavoriteDisabled(false);
                    }
                } else {
                    if(mSharedPreferences.loadAutoDownload() && word.getImages() != null){
                        setDownloaded(item);
                        favoriteDatabaseHelper.addFavorite(word);
                        item.setIcon(R.drawable.ic_favorite_black_24dp);
                        download.setIcon(R.drawable.ic_file_downloaded_24dp);
                        mSharedPreferences.setDownloadDisabled(false);
                        mSharedPreferences.setFavoriteDisabled(false);
                    } else {
                        favoriteDatabaseHelper.addFavorite(word);
                        item.setIcon(R.drawable.ic_favorite_black_24dp);
                        mSharedPreferences.setFavoriteDisabled(false);
                    }
                }
            }
        }
    }

    public void setDownloaded(MenuItem item){
        final DownloadService downloadService = new DownloadService(list, context);
        final Intent service = new Intent(context, downloadService.getClass());
        service.putExtra("data", word);
        service.putExtra("maxProgress", 1);
        service.putExtra("list", list);
        service.putExtra("receiver", receiver);
        if(mSharedPreferences.isWifiOnly()){
            assert wifi != null;
            if(wifi.isConnected()){
                if(!mSharedPreferences.loadNightModeState()){
                    if(downloadDatabaseHelper.exists(word.getTitle())){
                        deleteVideo(word.getTitle());
                        downloadDatabaseHelper.deleteDownload(word.getTitle());
                        item.setIcon(R.drawable.ic_file_download_black_24dp);
                    } else {
                        ContextCompat.startForegroundService(context, service);
                        item.setIcon(R.drawable.ic_file_downloaded_24dp);
                        mSharedPreferences.setDownloadDisabled(false);
                    }
                } else {
                    if(downloadDatabaseHelper.exists(word.getTitle())){
                        deleteVideo(word.getTitle());
                        downloadDatabaseHelper.deleteDownload(word.getTitle());
                        item.setIcon(R.drawable.ic_file_download_white_24dp);
                    } else {
                        ContextCompat.startForegroundService(context, service);
                        item.setIcon(R.drawable.ic_file_downloaded_24dp);
                        mSharedPreferences.setDownloadDisabled(false);
                    }
                }
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.wifi_only_message), Toast.LENGTH_SHORT).show();
            }
        } else {
            if(!mSharedPreferences.loadNightModeState()){
                if(downloadDatabaseHelper.exists(word.getTitle())){
                    deleteVideo(word.getTitle());
                    downloadDatabaseHelper.deleteDownload(word.getTitle());
                    item.setIcon(R.drawable.ic_file_download_black_24dp);
                } else {
                    ContextCompat.startForegroundService(context, service);
                    item.setIcon(R.drawable.ic_file_downloaded_24dp);
                    mSharedPreferences.setDownloadDisabled(false);
                }
            } else {
                if(downloadDatabaseHelper.exists(word.getTitle())){
                    deleteVideo(word.getTitle());
                    downloadDatabaseHelper.deleteDownload(word.getTitle());
                    item.setIcon(R.drawable.ic_file_download_white_24dp);
                } else {
                    ContextCompat.startForegroundService(context, service);
                    item.setIcon(R.drawable.ic_file_downloaded_24dp);
                    mSharedPreferences.setDownloadDisabled(false);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_word, menu);

        mainMenu = menu;

        final MenuItem favoriteItem = menu.findItem(R.id.favorite);
        final MenuItem downloadItem = menu.findItem(R.id.download);

        if(!mSharedPreferences.loadNightModeState()){
            if(downloadDatabaseHelper.exists(word.getTitle())) {
                downloadItem.setIcon(R.drawable.ic_file_downloaded_24dp);
                downloadItem.setChecked(true);
            } else
                downloadItem.setIcon(R.drawable.ic_file_download_black_24dp);
            if(favoriteDatabaseHelper.exists(word.getTitle()))
                favoriteItem.setIcon(R.drawable.ic_favorite_black_24dp);
            else
                favoriteItem.setIcon(R.drawable.ic_favorite_border_black_24dp);
        } else {
            if(downloadDatabaseHelper.exists(word.getTitle())){
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
            setDownloaded(item);
            return false;
        });

        favoriteItem.setOnMenuItemClickListener(item -> {
            setFavorite(item);
            return false;
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
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData){}
}
