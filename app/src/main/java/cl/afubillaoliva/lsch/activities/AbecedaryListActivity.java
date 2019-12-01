package cl.afubillaoliva.lsch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.WordListAdapter;
import cl.afubillaoliva.lsch.api.ApiClient;
import cl.afubillaoliva.lsch.api.ApiService;
import cl.afubillaoliva.lsch.models.Word;
import cl.afubillaoliva.lsch.utils.SharedPreference;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AbecedaryListActivity extends AppCompatActivity implements RecyclerViewOnClickListenerHack {

    private WordListAdapter adapter;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context context = this;
    private String letter, category;

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
        setContentView(R.layout.list_activity);

        Intent intent = getIntent();

        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mProgressBar = findViewById(R.id.progress_circular);
        mSwipeRefreshLayout = findViewById(R.id.swipe_layout);

        letter = intent.getStringExtra("letter");
        category = intent.getStringExtra("theme");

        if(letter != null)
            mToolbar.setTitle(letter.substring(0,1).toUpperCase() + letter.substring(1));
        if(category != null)
            mToolbar.setTitle(category.substring(0,1).toUpperCase() + category.substring(1));

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        adapter = new WordListAdapter();
        mRecyclerView.setAdapter(adapter);
        adapter.setRecyclerViewOnClickListenerHack(this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        getData();
    }

    private boolean isNetworkAvailable() {
        if(context == null){
            return true;
        } else {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo == null || !activeNetworkInfo.isConnected();
        }
    }

    private void getData(){
        try {
            Cache cache = new Cache(getCacheDir(), MainActivity.cacheSize);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .addInterceptor(new Interceptor() {
                        @NonNull
                        @Override
                        public okhttp3.Response intercept(@NonNull Interceptor.Chain chain)
                                throws IOException {
                            Request request = chain.request();
                            if (isNetworkAvailable()) {
                                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale \
                                request = request
                                        .newBuilder()
                                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                        .build();
                            }
                            return chain.proceed(request);
                        }
                    })
                    .build();

            ApiService.WordsService service;
            if(isNetworkAvailable()){
                service = ApiClient.getClient(okHttpClient).create(ApiService.WordsService.class);
            } else {
                service = ApiClient.getClient().create(ApiService.WordsService.class);
            }

            Call<ArrayList<Word>> responseCall;

            if(letter != null && category == null){
                responseCall = service.getWords(letter, null);
            } else if(category != null && letter == null){
                responseCall = service.getWords(null, category);
            } else {
                responseCall = service.getWords(null, null);
            }

            responseCall.enqueue(new Callback<ArrayList<Word>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<Word>> call, @NonNull Response<ArrayList<Word>> response) {
                    if (response.isSuccessful()) {
                        ArrayList<Word> apiResponse = response.body();

                        if (adapter.getItemCount() != 0) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            mProgressBar.setVisibility(View.GONE);
                            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            Toast.makeText(AbecedaryListActivity.this, "Abecedario Actualizado", Toast.LENGTH_SHORT).show();
                            adapter.updateData(apiResponse);
                        } else {
                            mSwipeRefreshLayout.setRefreshing(false);
                            mProgressBar.setVisibility(View.GONE);
                            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            adapter.addData(apiResponse);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mProgressBar.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                        Log.e(MainActivity.TAG, "onResponse: " + response.errorBody());
                        Toast.makeText(AbecedaryListActivity.this, "No se pudo actualizar el Feed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<Word>> call, @NonNull Throwable t) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                    Log.i(MainActivity.TAG, "onFailure: " + t.getMessage());
                    Toast.makeText(AbecedaryListActivity.this, "No se pudo actualizar el Feed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e){
            Log.d(MainActivity.TAG + "Error", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);

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
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onClickListener(View view, int position) {
        Toast.makeText(this, adapter.getTitle(position), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WordDetailActivity.class);
        intent.putExtra("position", adapter.getItem(position));
        intent.putExtra("id", position);
        startActivity(intent);
    }

    @Override
    public void onLongPressClickListener(View view, int position) {

    }
}