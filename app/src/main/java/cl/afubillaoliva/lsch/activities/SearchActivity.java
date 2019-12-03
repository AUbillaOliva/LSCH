package cl.afubillaoliva.lsch.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.HistoryAdapter;
import cl.afubillaoliva.lsch.adapters.SearchAdapter;
import cl.afubillaoliva.lsch.api.ApiClient;
import cl.afubillaoliva.lsch.api.ApiService;
import cl.afubillaoliva.lsch.models.Word;
import cl.afubillaoliva.lsch.utils.HistoryContract;
import cl.afubillaoliva.lsch.utils.HistoryDatabaseHelper;
import cl.afubillaoliva.lsch.utils.Network;
import cl.afubillaoliva.lsch.utils.SharedPreference;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements RecyclerViewOnClickListenerHack {

    private final Context context = this;

    private final Network network = new Network(context);
    private final HistoryDatabaseHelper databaseHelper = new HistoryDatabaseHelper(context);

    private final HistoryAdapter historyAdapter = new HistoryAdapter(context);
    private SearchAdapter adapter;

    private final LinearLayoutManager
            linearLayoutManager = new LinearLayoutManager(context),
            linearLayoutManager2 = new LinearLayoutManager(context);

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        final SharedPreference mSharedPreferences = new SharedPreference(this);
        if (mSharedPreferences.loadNightModeState()) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.search_activity_layout);

        final Toolbar mToolbar = findViewById(R.id.toolbar);
        final RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        final SearchView searchView = mToolbar.findViewById(R.id.search_view);
        final RecyclerView mRecyclerViewHistory = findViewById(R.id.recycler_view_2);

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final TextView searchQueryText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        final Typeface tf = ResourcesCompat.getFont(this,R.font.product_sans_regular);
        searchQueryText.setTextSize(16);
        searchQueryText.setTypeface(tf);
        searchView.setFocusable(true);
        searchView.requestFocusFromTouch();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //TODO: IF WORD EXISTS, START ACTIVITY
                searchView.clearFocus();
                if(!exists(s))
                    databaseHelper.addHistory(s);
                historyAdapter.clear();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.length() < 1){
                    historyAdapter.clear();
                    historyAdapter.addData(databaseHelper.getHistory());
                    mRecyclerView.setVisibility(View.GONE);
                    mRecyclerViewHistory.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerViewHistory.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    adapter.getFilter().filter(s);
                }
                return false;
            }
        });

        adapter = new SearchAdapter(this);
        adapter.setRecyclerViewOnClickListenerHack(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(adapter);

        historyAdapter.setRecyclerViewOnClickListenerHack(new RecyclerViewOnClickListenerHack() {

            @Override
            public void onClickListener(View view, int position) {
                searchView.setQuery(historyAdapter.get(position), false);
            }

            @Override
            public void onLongPressClickListener(View view, int position) {
                Toast.makeText(context, "Eliminado del historial", Toast.LENGTH_SHORT).show();
                databaseHelper.deleteHistory(historyAdapter.get(position));
                historyAdapter.clear();
                historyAdapter.addData(databaseHelper.getHistory());
            }

        });

        mRecyclerViewHistory.setHasFixedSize(true);
        mRecyclerViewHistory.setNestedScrollingEnabled(true);
        mRecyclerViewHistory.setLayoutManager(linearLayoutManager2);
        historyAdapter.addData(databaseHelper.getHistory());
        mRecyclerViewHistory.setAdapter(historyAdapter);

        getData();
    }

    public boolean exists(String searchItem) {

        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String[] projection = {
                HistoryContract.HistoryEntry._ID,
                HistoryContract.HistoryEntry.COLUMN_HISTORY_ID,
                HistoryContract.HistoryEntry.COLUMN_HISTORY_TITLE

        };

        String selection = HistoryContract.HistoryEntry.COLUMN_HISTORY_ID + " =?";
        String[] selectionArgs = { searchItem };
        String limit = "1";

        Cursor cursor = database.query(HistoryContract.HistoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();

        return exists;
    }

    private void getData(){
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

        ApiService.WordsService service = ApiClient.getClient(okHttpClient).create(ApiService.WordsService.class);
        Call<ArrayList<Word>> responseCall = service.getWords(null, null);

        responseCall.enqueue(new Callback<ArrayList<Word>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Word>> call, @NonNull Response<ArrayList<Word>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Word> apiResponse = response.body();
                    if (adapter.getItemCount() != 0)
                        adapter.update(getApplicationContext(), apiResponse);
                    else
                        adapter.setDataset(getApplicationContext(), apiResponse);
                } else
                    Toast.makeText(context, "Revisa tu conexión a internet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Word>> call, @NonNull Throwable t) {
                Toast.makeText(context, "No se pudo actualizar el feed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(context, MainActivity.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(context, MainActivity.class));
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onClickListener(View view, int position) {
        Intent intent = new Intent(context, WordDetailActivity.class);
        intent.putExtra("position", adapter.get(position));
        intent.putExtra("id", position);
        startActivity(intent);
    }

    @Override
    public void onLongPressClickListener(View view, int position) {}

}