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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.GenericAdapter;
import cl.afubillaoliva.lsch.api.ApiClient;
import cl.afubillaoliva.lsch.api.ApiService;
import cl.afubillaoliva.lsch.models.Word;
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

public class AbecedaryListActivity extends AppCompatActivity {

    private final Context context = this;
    private final Network network = new Network(this);

    private final DownloadDatabaseHelper downloadDatabaseHelper = new DownloadDatabaseHelper(context);

    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String letter, theme;

    private GenericAdapter<Word> adapter;
    private ArrayList<Word> apiResponse;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        final SharedPreference mSharedPreferences = new SharedPreference(context);
        if (mSharedPreferences.loadNightModeState())
            setTheme(R.style.AppThemeDark);
        else
            setTheme(R.style.AppTheme);
        setContentView(R.layout.list_activity);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        final Intent intent = getIntent();
        letter = intent.getStringExtra("letter");
        theme = intent.getStringExtra("theme");

        final RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        final Toolbar mToolbar = findViewById(R.id.toolbar);
        mProgressBar = findViewById(R.id.progress_circular);
        mSwipeRefreshLayout = findViewById(R.id.swipe_layout);

        if(mSharedPreferences.loadNightModeState())
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceDark);
        else
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceLight);
        if(letter != null)
            mToolbar.setTitle(letter.substring(0,1).toUpperCase() + letter.substring(1));
        if(theme != null)
            mToolbar.setTitle(theme.substring(0,1).toUpperCase() + theme.substring(1));
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
            public void onBindData(RecyclerView.ViewHolder holder, Word val, int position) {
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
            public RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack() {
                return new RecyclerViewOnClickListenerHack() {
                    @Override
                    public void onClickListener(View view, int position) {
                        final Intent intent = new Intent(context, WordDetailActivity.class);
                        intent.putExtra("position", adapter.getItem(position));
                        startActivity(intent);
                    }

                    @Override
                    public void onLongPressClickListener(View view, int position) {

                    }
                };
            }
        };
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setOnRefreshListener(this::getData);

        getData();
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

        final ApiService.WordsService service = ApiClient.getClient(okHttpClient).create(ApiService.WordsService.class);
        final Call<ArrayList<Word>> responseCall;

        if(letter == null && theme == null){
            responseCall = service.getWords(null, null);
            Log.d(MainActivity.TAG, "NULL PARAMETERS");
        } else if(letter != null && theme == null){
            responseCall = service.getWords(letter, null);
            Log.d(MainActivity.TAG, "Letter: " + letter);
        } else if(letter == null){
            responseCall = service.getWords(null, theme);
            Log.d(MainActivity.TAG, "Category: " + theme);
        } else {
            responseCall = service.getWords(null,null);
            Log.d(MainActivity.TAG, "Letter: " + letter + " Category: " + theme);
        }

        responseCall.enqueue(new Callback<ArrayList<Word>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Word>> call, @NonNull Response<ArrayList<Word>> response) {
                mSwipeRefreshLayout.setRefreshing(false);
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    apiResponse = response.body();
                    adapter.addItems(apiResponse);
                } else
                    Toast.makeText(context, "Revisa tu conexión a internet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Word>> call, @NonNull Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
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
}