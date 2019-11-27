package cl.afubillaoliva.lsch.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.ListAdapter;
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

public class SearchActivity extends AppCompatActivity implements RecyclerViewOnClickListenerHack {

    private ListAdapter historyAdapter;
    private WordListAdapter adapter;
    private Toolbar mToolbar;
    private Menu menu;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        SharedPreference mSharedPreferences = new SharedPreference(this);
        if (mSharedPreferences.loadNightModeState()) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.search_activity_layout);

        mRecyclerView = findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = findViewById(R.id.swipe_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Busca una palabra");
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        historyAdapter = new ListAdapter();
        adapter = new WordListAdapter();
        historyAdapter.setRecyclerViewOnClickListenerHack(this);
        mRecyclerView.setAdapter(historyAdapter);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager1);


        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        getData();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) Objects.requireNonNull(getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void getData(){
        try {
            Cache cache = new Cache(getCacheDir(), MainActivity.cacheSize);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Interceptor.Chain chain)
                                throws IOException {
                            Request request = chain.request();
                            if (!isNetworkAvailable()) {
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
            ApiService.WordsService service = ApiClient.getClient(okHttpClient).create(ApiService.WordsService.class);
            Call<ArrayList<Word>> responseCall = service.getWords();

            responseCall.enqueue(new Callback<ArrayList<Word>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<Word>> call, @NonNull Response<ArrayList<Word>> response) {
                    if (response.isSuccessful()) {
                        ArrayList<Word> apiResponse = response.body();

                        if (adapter.getItemCount() != 0) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            Toast.makeText(SearchActivity.this, "Abecedario Actualizado", Toast.LENGTH_SHORT).show();
                            adapter.updateData(apiResponse);
                        } else {
                            mSwipeRefreshLayout.setRefreshing(false);
                            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            adapter.addData(apiResponse);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                        Log.e(MainActivity.TAG, "onResponse: " + response.errorBody());
                        Toast.makeText(SearchActivity.this, "No se pudo actualizar el Feed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<Word>> call, @NonNull Throwable t) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                    Log.i(MainActivity.TAG, "onFailure: " + t.getMessage());
                    Toast.makeText(SearchActivity.this, "No se pudo actualizar el Feed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e){
            Log.d(MainActivity.TAG + "Error", e.getMessage());
        }
    }

    /* TODO:
        SET 2 ADAPTERS
        1.- For History Items
        2.- For Words Items
        ON onQueryTextSubmit: Add item to adapter
           onQueryTextChange: Hide the list, and show all words on another list
           clickSearchItem: Get Item position title, and add to history adapter after go to WordDetailActivity

     */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        this.menu = menu;
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Buscar");
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.setAdapter(historyAdapter);
            }
        });
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.i(MainActivity.TAG, s);
                historyAdapter.addItem(s);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mRecyclerView.setAdapter(adapter);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.action_search:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onClickListener(View view, int position) {

    }

    @Override
    public void onLongPressClickListener(View view, int position) {

    }
}
