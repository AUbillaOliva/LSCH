package cl.afubillaoliva.lsch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Objects;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.WordListAdapter;
import cl.afubillaoliva.lsch.utils.FavoriteDatabaseHelper;
import cl.afubillaoliva.lsch.utils.SharedPreference;

public class FavoriteListActivity extends AppCompatActivity implements RecyclerViewOnClickListenerHack {

    private WordListAdapter adapter;
    private FavoriteDatabaseHelper favoriteDatabaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        SharedPreference mSharedPreferences = new SharedPreference(this);
        if (mSharedPreferences.loadNightModeState()) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }

        setContentView(R.layout.favorite_activity_layout);

        favoriteDatabaseHelper = new FavoriteDatabaseHelper(this);

        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        Toolbar mToolbar = findViewById(R.id.toolbar);

        mToolbar.setTitle("Favoritos");
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        adapter = new WordListAdapter();
        adapter.addData(favoriteDatabaseHelper.getAllFavorite());
        mRecyclerView.setAdapter(adapter);
        adapter.setRecyclerViewOnClickListenerHack(this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // REFRESH LIST
        if(adapter.getItemCount() != favoriteDatabaseHelper.getAllFavorite().size()){
            if(adapter.dataset.size() > favoriteDatabaseHelper.getAllFavorite().size()){
                adapter.clear();
                adapter.addData(favoriteDatabaseHelper.getAllFavorite());
                adapter.notifyDataSetChanged();
            } else {
                adapter.addData(favoriteDatabaseHelper.getAllFavorite());
                adapter.notifyDataSetChanged();
            }
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                startActivity(new Intent(FavoriteListActivity.this, MainActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
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
        Intent intent = new Intent(this, WordDetailActivity.class);
        intent.putExtra("position", adapter.getItem(position));
        startActivity(intent);
    }

    @Override
    public void onLongPressClickListener(View view, int position) {

    }
}
