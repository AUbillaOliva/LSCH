package cl.afubillaoliva.lsch.activities;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.GenericAdapter;
import cl.afubillaoliva.lsch.api.ApiClient;
import cl.afubillaoliva.lsch.api.ApiService;
import cl.afubillaoliva.lsch.models.Expressions;
import cl.afubillaoliva.lsch.utils.GenericViewHolder;
import cl.afubillaoliva.lsch.utils.Network;
import cl.afubillaoliva.lsch.utils.SharedPreference;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpressionsListActivity extends AppCompatActivity {

    private final Context context = this;
    private final Network network = new Network(context);

    private GenericAdapter<Expressions> adapter;
    private ArrayList<Expressions> apiResponse;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private String category;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        category = intent.getStringExtra("expression");
        getData();

        final SharedPreference mSharedPreferences = new SharedPreference(this);
        if (mSharedPreferences.loadNightModeState()) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.list_activity);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        final Toolbar mToolbar = findViewById(R.id.toolbar);
        final RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = findViewById(R.id.swipe_layout);
        mProgressBar = findViewById(R.id.progress_circular);

        if(mSharedPreferences.loadNightModeState())
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceDark);
        else
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceLight);
        final String title = category.substring(0,1).toUpperCase() + category.substring(1);
        mToolbar.setTitle(title);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        adapter = new GenericAdapter<Expressions>() {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack) {
                View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
                return new GenericViewHolder(view, recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, Expressions val, int position) {
                GenericViewHolder viewHolder = (GenericViewHolder) holder;
                final TextView title = viewHolder.get(R.id.list_item_text);
                title.setText(val.getTitle());
            }

            @Override
            public RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack() {
                return new RecyclerViewOnClickListenerHack() {
                    @Override
                    public void onClickListener(View view, int position) {
                        Intent intent = new Intent(context, ExpressionsDetailActivity.class);
                        intent.putExtra("position", adapter.getItem(position));
                        startActivity(intent);
                    }

                    @Override
                    public void onLongPressClickListener(View view, int position) {

                    }
                };
            }
        };
        mRecyclerView.setAdapter(adapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
    }

    public void getData(){
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

        final ApiService.ExpressionsServiceCategories service = ApiClient.getClient(okHttpClient).create(ApiService.ExpressionsServiceCategories.class);
        final Call<ArrayList<Expressions>> responseCall = service.getExpressionsOfCategories(category);

        responseCall.enqueue(new Callback<ArrayList<Expressions>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Expressions>> call, @NonNull Response<ArrayList<Expressions>> response) {
                mSwipeRefreshLayout.setRefreshing(false);
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    apiResponse = response.body();
                    adapter.addItems(apiResponse);
                } else {
                    Log.e(MainActivity.TAG, "onResponse: " + response.errorBody());
                    Toast.makeText(context, "Revisa tu conexión a internet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Expressions>> call, @NonNull Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                Log.i(MainActivity.TAG, "onFailure: " + t.getMessage());
                Toast.makeText(ExpressionsListActivity.this, "No se pudo actualizar el feed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list,menu);
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
