package cl.afubillaoliva.lsch.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.TextView;
import android.widget.Toast;

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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements RecyclerViewOnClickListenerHack {

    private final Context context = this;
    private SharedPreference mSharedPreferences;

    private final Network network = new Network(context);
    private final HistoryDatabaseHelper databaseHelper = new HistoryDatabaseHelper(context);

    private GenericAdapter<String> historyAdapter;
    private SearchAdapter adapter;
    private RecyclerView mRecyclerView;
    private FrameLayout loadingFrame;
    private LinearLayout connectionFrame;

    private ArrayList<Word> apiResponse;

    private final LinearLayoutManager
            linearLayoutManager = new LinearLayoutManager(context),
            linearLayoutManager2 = new LinearLayoutManager(context);

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getData();

        mSharedPreferences = new SharedPreference(this);
        if (mSharedPreferences.loadNightModeState())
            setTheme(R.style.AppThemeDark);
        else
            setTheme(R.style.AppTheme);
        setContentView(R.layout.search_activity_layout);

        final Toolbar mToolbar = findViewById(R.id.toolbar);
        final SearchView searchView = mToolbar.findViewById(R.id.search_view);
        final RecyclerView mRecyclerViewHistory = findViewById(R.id.recycler_view_2);
        mRecyclerView = findViewById(R.id.recycler_view);
        final TextView connectionText = findViewById(R.id.connection_search_result_text);
        final ImageView searchCheckImage = findViewById(R.id.search_check_image);
        loadingFrame = findViewById(R.id.loading_frame);
        connectionFrame = findViewById(R.id.connection_frame);

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if(mSharedPreferences.loadNightModeState()){
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceDark);
            searchCheckImage.setImageResource(R.drawable.ic_search_white_24dp);
        } else {
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceLight);
            searchCheckImage.setImageResource(R.drawable.ic_search_black_24dp);
        }

        final TextView searchQueryText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        final Typeface tf = ResourcesCompat.getFont(this,R.font.product_sans_regular);
        searchQueryText.setTextSize(16);
        searchQueryText.setTypeface(tf);
        searchView.setFocusable(true);
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String s){
                searchView.clearFocus();
                if(exists(s))
                    databaseHelper.addHistory(s);
                mSharedPreferences.setHistoryDisabled(false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s){
                @SuppressLint("SetTextI18n") Filter.FilterListener listener = count -> {
                    if(adapter.getItemCount() <= 0){
                        connectionFrame.setVisibility(View.VISIBLE);
                        connectionText.setText("No se encontraron resultados para \r\n" + '"' + s + '"');
                    } else
                        connectionFrame.setVisibility(View.GONE);
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
        historyAdapter = new GenericAdapter<String>(databaseHelper.getHistory()){
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack){
                return new GenericViewHolder(LayoutInflater.from(context).inflate(R.layout.history_list_item, parent, false), recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, String val, int position){
                final GenericViewHolder viewHolder = (GenericViewHolder) holder;
                final ImageView imageView = viewHolder.get(R.id.search_suggest_type);
                final TextView title = viewHolder.get(R.id.list_item_text);
                if(mSharedPreferences.loadNightModeState())
                    imageView.setImageResource(R.drawable.ic_access_time_white_24dp);
                else
                    imageView.setImageResource(R.drawable.ic_access_time_black_24dp);
                final ImageView deleteImage = viewHolder.get(R.id.delete_item);
                deleteImage.setOnClickListener(v -> {
                    databaseHelper.deleteHistory(val);
                    historyAdapter.addItems(databaseHelper.getHistory());
                });
                title.setText(val);
            }

            @Override
            public RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack(){
                return new RecyclerViewOnClickListenerHack(){
                    @Override
                    public void onClickListener(View view, int position){
                        searchView.setQuery(historyAdapter.getItem(position), false);
                    }

                    @Override
                    public void onLongPressClickListener(View view, int position){
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

    }

    public boolean exists(String searchItem) {

        final SQLiteDatabase database = databaseHelper.getWritableDatabase();

        final String[] projection = {
                HistoryContract.HistoryEntry._ID,
                HistoryContract.HistoryEntry.COLUMN_HISTORY_ID,
                HistoryContract.HistoryEntry.COLUMN_HISTORY_TITLE

        };

        final String selection = HistoryContract.HistoryEntry.COLUMN_HISTORY_ID + " =?";
        final String[] selectionArgs = { searchItem };
        final String limit = "1";

        final Cursor cursor = database.query(HistoryContract.HistoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null, limit);
        final boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return !exists;
    }

    private void getData(){
        final Cache cache = new Cache(getCacheDir(), MainActivity.cacheSize);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    int maxStale = 60 * 60 * 24 * 7; // tolerate 4-weeks stale \
                    if (network.isNetworkAvailable()){
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
                })
                .build();

        final ApiService.WordsService service = ApiClient.getClient(okHttpClient).create(ApiService.WordsService.class);
        final Call<ArrayList<Word>> responseCall = service.getWords(null, null);

        responseCall.enqueue(new Callback<ArrayList<Word>>(){
            @Override
            public void onResponse(@NonNull Call<ArrayList<Word>> call, @NonNull Response<ArrayList<Word>> response){
                if (response.isSuccessful()){
                    apiResponse = response.body();
                    adapter.setDataset(context,apiResponse);
                    mRecyclerView.setAdapter(adapter);
                } else
                    Toast.makeText(context, "Revisa tu conexión a internet", Toast.LENGTH_SHORT).show();
                loadingFrame.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Word>> call, @NonNull Throwable t){
                Toast.makeText(context, "No se pudo actualizar el feed", Toast.LENGTH_SHORT).show();
                loadingFrame.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_search,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(context, MainActivity.class));
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
    public void onBackPressed(){
        startActivity(new Intent(context, MainActivity.class));
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onClickListener(View view, int position){
        if(exists(adapter.get(position).getTitle())){
            databaseHelper.addHistory(adapter.get(position).getTitle());
            mSharedPreferences.setHistoryDisabled(false);
        }
        final Intent intent = new Intent(context, WordDetailActivity.class);
        intent.putExtra("position", adapter.get(position));
        intent.putExtra("id", position);
        startActivity(intent);
    }

    @Override
    public void onLongPressClickListener(View view, int position){}

    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(newBase);
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
    }
}