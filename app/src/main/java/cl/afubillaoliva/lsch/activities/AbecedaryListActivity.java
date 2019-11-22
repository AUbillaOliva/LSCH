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
import cl.afubillaoliva.lsch.adapters.WordListAdapter;
import cl.afubillaoliva.lsch.api.ApiService;
import cl.afubillaoliva.lsch.models.Word;
import cl.afubillaoliva.lsch.utils.SharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AbecedaryListActivity extends AppCompatActivity implements RecyclerViewOnClickListenerHack {

    private Retrofit retrofit;
    private Intent intent;
    private WordListAdapter adapter;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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

        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mProgressBar = findViewById(R.id.progress_circular);
        mSwipeRefreshLayout = findViewById(R.id.swipe_layout);

        mToolbar.setTitle(intent.getStringExtra("letter"));
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        adapter = new WordListAdapter();
        mRecyclerView.setAdapter(adapter);
        adapter.setRecyclerViewOnClickListenerHack(this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        getData();
    }

    public void getData(){
        ApiService.WordsOfLetterService service = retrofit.create(ApiService.WordsOfLetterService.class);
        Call<ArrayList<Word>> responseCall = service.getWords(intent.getStringExtra("letter"));

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
        startActivity(intent);
    }

    @Override
    public void onLongPressClickListener(View view, int position) {

    }
}
