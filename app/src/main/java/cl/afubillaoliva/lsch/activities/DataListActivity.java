package cl.afubillaoliva.lsch.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Objects;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.GenericAdapter;
import cl.afubillaoliva.lsch.api.ApiClient;
import cl.afubillaoliva.lsch.api.ApiService;
import cl.afubillaoliva.lsch.models.Word;
import cl.afubillaoliva.lsch.services.DownloadService;
import cl.afubillaoliva.lsch.tools.DownloadReceiver;
import cl.afubillaoliva.lsch.utils.GenericViewHolder;
import cl.afubillaoliva.lsch.utils.Network;
import cl.afubillaoliva.lsch.utils.SharedPreference;
import cl.afubillaoliva.lsch.utils.databases.DownloadDatabaseHelper;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataListActivity extends AppCompatActivity implements DownloadReceiver.Receiver {

    private Context context = this;
    private final Network network = new Network(this);
    private DownloadReceiver receiver;

    private final DownloadDatabaseHelper downloadDatabaseHelper = new DownloadDatabaseHelper(context);

    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String list, type, letter, category, theme;
    private Switch onDownload;

    private static GenericAdapter<Word> adapter;
    private ArrayList<Word> apiResponse;
    private SharedPreference mSharedPreferences;

    private int downloadQueueLength = 0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mSharedPreferences = new SharedPreference(context);
        receiver = new DownloadReceiver(new Handler(), context);
        receiver.setReceiver(this);
        if (mSharedPreferences.loadNightModeState())
            setTheme(R.style.AppThemeDark);
        else
            setTheme(R.style.AppTheme);
        setContentView(R.layout.list_activity);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        final Intent intent = getIntent();
        list = intent.getStringExtra("list");
        type = intent.getStringExtra("type");
        letter = intent.getStringExtra("letter");
        theme = intent.getStringExtra("theme");
        category = intent.getStringExtra("category");

        final RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        final Toolbar mToolbar = findViewById(R.id.toolbar);
        mProgressBar = findViewById(R.id.progress_circular);
        mSwipeRefreshLayout = findViewById(R.id.swipe_layout);
        onDownload = findViewById(R.id.download);

        if(mSharedPreferences.loadNightModeState())
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceDark);
        else
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceLight);
        if(list != null)
            mToolbar.setTitle(list.substring(0,1).toUpperCase() + list.substring(1));
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        adapter = new GenericAdapter<Word>() {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack) {
                return new GenericViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false), recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, Word val, int position){
                final GenericViewHolder viewHolder = (GenericViewHolder) holder;
                final TextView title = viewHolder.get(R.id.list_item_text);
                title.setText(val.getTitle());
                final ImageView downloadedIcon = viewHolder.get(R.id.downloaded_icon);
                if(downloadDatabaseHelper.exists(val.getTitle()))
                    downloadedIcon.setVisibility(View.VISIBLE);
                else
                    downloadedIcon.setVisibility(View.GONE);
            }

            @Override
            public RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack(){
                return new RecyclerViewOnClickListenerHack(){
                    @Override
                    public void onClickListener(View view, int position){
                        final Intent intent = new Intent(context, WordDetailActivity.class);
                        intent.putExtra("position", adapter.getItem(position));
                        intent.putExtra("list", adapter.getItem(position).getTitle());
                        intent.putExtra("type", type);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongPressClickListener(View view, int position){}
                };
            }
        };
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setOnRefreshListener(this::getData);

        if(mSharedPreferences.isDownloaded(list))
            onDownload.setChecked(true);
        else
            onDownload.setChecked(false);
        if(apiResponse == null)
            onDownload.setEnabled(false);
        else
            onDownload.setEnabled(true);
        onDownload.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked){
                mSharedPreferences.setDownloaded(list);
                downloadVideos();
            } else {
                mSharedPreferences.deleteDownloads(list);
                deleteVideos();
            }
        });
        getData();
    }

    public static String stripAccents(String s){
        s = s.replaceAll("/", "");
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    private void deleteVideo(String fileName){
        final File file = new File(getExternalFilesDir(null) + File.separator + stripAccents(fileName) + ".mp4");
        if (file.exists()){
            downloadDatabaseHelper.deleteDownload(fileName);
            file.delete();
        }
    }

    private void deleteVideos(){
        for (Word word : apiResponse){
            if(!word.getImages().isEmpty())
                deleteVideo(word.getTitle());
        }
        mSharedPreferences.deleteDownloads(list);
        adapter.notifyDataSetChanged();
    }

    public int getDownloadQueueLength(){
        for(Word word : apiResponse){
            final File file = new File(getExternalFilesDir(null) + File.separator + stripAccents(word.getTitle()) + ".mp4");
            if(!word.getImages().isEmpty())
                if(!file.exists())
                    downloadQueueLength++;
        }

        return downloadQueueLength;
    }

    private void downloadVideos(){
        downloadQueueLength = getDownloadQueueLength();

        for(Word word : apiResponse){
            if(!word.getImages().isEmpty()){
                final Intent service = new Intent(context, DownloadService.class);
                service.putExtra("data", word);
                service.putExtra("maxProgress", downloadQueueLength);
                service.putExtra("receiver", receiver);
                DownloadService.enqueueWork(context, service);
            }
        }
        downloadQueueLength = 0;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getData();
    }

    private void getData(){
        final Cache cache = new Cache(getCacheDir(), MainActivity.cacheSize);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    int maxStale = 60 * 60 * 24 * 7;
                    if (network.isNetworkAvailable())
                        request = request
                                .newBuilder()
                                .header("Cache-Control", "public, max-age=" + 5)
                                .build();
                    else
                        request = request
                                .newBuilder()
                                .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                .build();
                    return chain.proceed(request);
                })
                .build();

        Call<ArrayList<Word>> responseCall;
        if(type != null && type.contentEquals("expressions")){
            final ApiService.ExpressionsServiceCategories service = ApiClient.getClient(okHttpClient).create(ApiService.ExpressionsServiceCategories.class);
            responseCall = service.getExpressionsOfCategories(list);
        } else {
            final ApiService.WordsService service = ApiClient.getClient(okHttpClient).create(ApiService.WordsService.class);

            if(letter != null && category == null)
                responseCall = service.getWords(letter, null);
            else if(letter == null && category != null)
                responseCall = service.getWords(null, category);
            else if(letter == null && theme != null)
                responseCall = service.getWords(null, theme);
            else
                responseCall = service.getWords(null, null);
        }

        responseCall.enqueue(new Callback<ArrayList<Word>>(){
            @Override
            public void onResponse(@NonNull Call<ArrayList<Word>> call, @NonNull Response<ArrayList<Word>> response) {
                mSwipeRefreshLayout.setRefreshing(false);
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    apiResponse = response.body();
                    onDownload.setEnabled(true);
                    adapter.addItems(apiResponse);
                } else {
                    onDownload.setEnabled(false);
                    Log.e(MainActivity.TAG, "onResponse: " + response.errorBody());
                    Toast.makeText(context, "Revisa tu conexión a internet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Word>> call, @NonNull Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                onDownload.setEnabled(false);
                Log.e(MainActivity.TAG, "onFailure: " + t.getMessage());
                Toast.makeText(context, "No se pudo actualizar el feed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.report:
                startActivity(new Intent(context, ReportActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        adapter.notifyDataSetChanged();
    }
}