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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.GenericAdapter;
import cl.afubillaoliva.lsch.adapters.SearchAdapter;
import cl.afubillaoliva.lsch.api.ApiClient;
import cl.afubillaoliva.lsch.api.ApiService;
import cl.afubillaoliva.lsch.models.Word;
import cl.afubillaoliva.lsch.utils.GenericViewHolder;
import cl.afubillaoliva.lsch.utils.databases.HistoryContract;
import cl.afubillaoliva.lsch.utils.databases.HistoryDatabaseHelper;
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

    private boolean loaded = false;

    private final Network network = new Network(context);
    private final HistoryDatabaseHelper databaseHelper = new HistoryDatabaseHelper(context);


    private GenericAdapter<String> historyAdapter;
    private SearchAdapter adapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private FrameLayout loadingFrame;
    private LinearLayout connectionFrame;

    private ArrayList<Word> apiResponse;

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
        mRecyclerView = findViewById(R.id.recycler_view);
        final SearchView searchView = mToolbar.findViewById(R.id.search_view);
        final RecyclerView mRecyclerViewHistory = findViewById(R.id.recycler_view_2);
        mProgressBar = findViewById(R.id.progress_circular);
        loadingFrame = findViewById(R.id.loading_frame);
        connectionFrame = findViewById(R.id.connection_frame);
        final TextView connectionText = findViewById(R.id.connection_search_result_text);
        final ImageView searchCheckImage = findViewById(R.id.search_check_image);

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if(mSharedPreferences.loadNightModeState()) {
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceDark);
            searchCheckImage.setImageResource(R.drawable.ic_search_white_24dp);
        }
        else {
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceLight);
            searchCheckImage.setImageResource(R.drawable.ic_search_black_24dp);
        }

        final TextView searchQueryText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        final Typeface tf = ResourcesCompat.getFont(this,R.font.product_sans_regular);
        searchQueryText.setTextSize(16);
        searchQueryText.setTypeface(tf);
        searchView.setFocusable(true);
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.clearFocus();
                if(!exists(s)) //IF EXISTS IN HISTORY DB
                    databaseHelper.addHistory(s);
                Log.d(MainActivity.HIS, "ADDED: " + s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {
                Filter.FilterListener listener = new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int count) {
                        if(adapter.getItemCount() <= 0){
                            Log.d(MainActivity.UI, "NO RESULTS " + s);
                            connectionFrame.setVisibility(View.VISIBLE);
                            connectionText.setText("No se encontraron resultados para \r\n" + '"' + s + '"');
                        } else {
                            connectionFrame.setVisibility(View.GONE);
                            Log.d(MainActivity.UI, "RESULTS " + s);
                        }
                    }
                };
                if(s.length() < 1){
                    historyAdapter.addItems(databaseHelper.getHistory());
                    mRecyclerView.setVisibility(View.GONE);
                    mRecyclerViewHistory.setVisibility(View.VISIBLE);
                    connectionFrame.setVisibility(View.GONE);
                } else {
                    mRecyclerViewHistory.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    adapter.getFilter().filter(s, listener);
                }
                return false;
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SearchAdapter(context);
        adapter.setRecyclerViewOnClickListenerHack(this);


        mRecyclerViewHistory.setHasFixedSize(true);
        mRecyclerViewHistory.setNestedScrollingEnabled(true);
        mRecyclerViewHistory.setLayoutManager(linearLayoutManager2);
        historyAdapter = new GenericAdapter<String>(databaseHelper.getHistory()) {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack) {
                final View view = LayoutInflater.from(context).inflate(R.layout.history_list_item, parent, false);
                return new GenericViewHolder(view, recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, final String val, int position) {
                GenericViewHolder viewHolder = (GenericViewHolder) holder;
                final ImageView imageView = viewHolder.get(R.id.search_suggest_type);
                final TextView title = viewHolder.get(R.id.list_item_text);
                if(mSharedPreferences.loadNightModeState())
                    imageView.setImageResource(R.drawable.ic_access_time_white_24dp);
                else
                    imageView.setImageResource(R.drawable.ic_access_time_black_24dp);
                final ImageView deleteImage = viewHolder.get(R.id.delete_item);
                deleteImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        databaseHelper.deleteHistory(val);
                        historyAdapter.addItems(databaseHelper.getHistory());
                    }
                });
                title.setText(val);
            }

            @Override
            public RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack() {
                return new RecyclerViewOnClickListenerHack() {
                    @Override
                    public void onClickListener(View view, int position) {
                        //TODO: IF ITEM EXIST, GO DIRECTLY TO ACTIVITY
                        searchView.setQuery(historyAdapter.getItem(position), false);
                    }

                    @Override
                    public void onLongPressClickListener(View view, int position) {
                        Toast.makeText(context, "Eliminado del historial", Toast.LENGTH_SHORT).show();
                        Log.d(MainActivity.HIS, "DELETED: " + historyAdapter.getItem(position));
                        databaseHelper.deleteHistory(historyAdapter.getItem(position));
                        historyAdapter.addItems(databaseHelper.getHistory());
                    }
                };
            }
        };
        historyAdapter.addItems(databaseHelper.getHistory());
        mRecyclerViewHistory.setAdapter(historyAdapter);


        getData();
        if(loaded)
            Log.d(MainActivity.TAG, "data loaded");
        else
            Log.d(MainActivity.TAG, "data not loaded");
    }

    public boolean exists(String searchItem) {

        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String[] projection = {
                HistoryContract.HistoryEntry._ID,
                HistoryContract.HistoryEntry.COLUMN_HISTORY_ID,
                HistoryContract.HistoryEntry.COLUMN_HISTORY_TITLE

        };

        String selection = HistoryContract.HistoryEntry.COLUMN_HISTORY_ID + " =?";
        String[] selectionArgs = { searchItem.toLowerCase() };
        String limit = "1";

        Cursor cursor = database.query(HistoryContract.HistoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();

        return exists;
    }

    private void getData(){
        final Cache cache = new Cache(getCacheDir(), MainActivity.cacheSize);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
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
                                    .header("Cache-Control", "public, max-stale=" + 60 * 5)
                                    .build();
                            Log.d(MainActivity.TAG, "using cache that was stored 5 minutes ago");
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

        final ApiService.WordsService service = ApiClient.getClient(okHttpClient).create(ApiService.WordsService.class);
        final Call<ArrayList<Word>> responseCall = service.getWords(null, null);

        responseCall.enqueue(new Callback<ArrayList<Word>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Word>> call, @NonNull Response<ArrayList<Word>> response) {
                if (response.isSuccessful()) {
                    apiResponse = response.body();
                    adapter.setDataset(context,apiResponse);
                    mRecyclerView.setAdapter(adapter);
                } else
                    Toast.makeText(context, "Revisa tu conexión a internet", Toast.LENGTH_SHORT).show();
                loadingFrame.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Word>> call, @NonNull Throwable t) {
                Toast.makeText(context, "No se pudo actualizar el feed", Toast.LENGTH_SHORT).show();
                loadingFrame.setVisibility(View.GONE);
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
        if(!exists(adapter.get(position).getTitle()))
            databaseHelper.addHistory(adapter.get(position).getTitle());
        Intent intent = new Intent(context, WordDetailActivity.class);
        intent.putExtra("position", adapter.get(position));
        intent.putExtra("id", position);
        startActivity(intent);
    }

    @Override
    public void onLongPressClickListener(View view, int position) {

    }
}