package cl.afubillaoliva.lsch.activities;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Objects;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.ExpressionsListAdapter;
import cl.afubillaoliva.lsch.api.ApiService;
import cl.afubillaoliva.lsch.models.Expressions;
import cl.afubillaoliva.lsch.utils.SharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExpressionsListActivity extends AppCompatActivity implements RecyclerViewOnClickListenerHack {

    private ExpressionsListAdapter adapter;
    private Retrofit retrofit;
    private Intent intent;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        SharedPreference mSharedPreferences = new SharedPreference(this);
        if (mSharedPreferences.loadNightModeState()) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.list_activity);
        intent = getIntent();

        Toolbar mToolbar = findViewById(R.id.toolbar);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = findViewById(R.id.swipe_layout);
        mProgressBar = findViewById(R.id.progress_circular);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        adapter = new ExpressionsListAdapter();
        adapter.setRecyclerViewOnClickListenerHack(this);
        mRecyclerView.setAdapter(adapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        String title = intent.getStringExtra("expression");
        title = title.substring(0,1).toUpperCase() + title.substring(1);
        mToolbar.setTitle(title);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        getData();
    }

    public void getData(){
        ApiService.ExpressionsServiceCategories service = retrofit.create(ApiService.ExpressionsServiceCategories.class);
        Log.i(MainActivity.TAG, intent.getStringExtra("expression"));
        Call<ArrayList<Expressions>> responseCall = service.getExpressionsOfCategories(intent.getStringExtra("expression"));

        responseCall.enqueue(new Callback<ArrayList<Expressions>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Expressions>> call, @NonNull Response<ArrayList<Expressions>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Expressions> apiResponse = response.body();

                    if (adapter.getItemCount() != 0) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mProgressBar.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                        adapter.updateData(apiResponse);
                        Toast.makeText(ExpressionsListActivity.this, "Abecedario Actualizado", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ExpressionsListActivity.this, "No se pudo actualizar el Feed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Expressions>> call, @NonNull Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                Log.i(MainActivity.TAG, "onFailure: " + t.getMessage());
                Toast.makeText(ExpressionsListActivity.this, "No se pudo actualizar el Feed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClickListener(View view, int position) {
        Intent intent = new Intent(ExpressionsListActivity.this, ExpressionsDetailActivity.class);
        intent.putExtra("position", adapter.getItem(position));
        startActivity(intent);
    }

    @Override
    public void onLongPressClickListener(View view, int position) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
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
}
