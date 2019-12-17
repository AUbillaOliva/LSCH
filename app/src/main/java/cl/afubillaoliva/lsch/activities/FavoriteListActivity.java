package cl.afubillaoliva.lsch.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.GenericAdapter;
import cl.afubillaoliva.lsch.models.Word;
import cl.afubillaoliva.lsch.utils.GenericViewHolder;
import cl.afubillaoliva.lsch.utils.databases.FavoriteDatabaseHelper;
import cl.afubillaoliva.lsch.utils.SharedPreference;

// TODO: IF ARE NOT FAVORITES YET, SHOW LAYOUT

public class FavoriteListActivity extends AppCompatActivity {

    private final Context context = this;
    private GenericAdapter<Word> adapter;
    private final FavoriteDatabaseHelper favoriteDatabaseHelper = new FavoriteDatabaseHelper(context);

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        final SharedPreference mSharedPreferences = new SharedPreference(context);
        if (mSharedPreferences.loadNightModeState()) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.favorite_activity_layout);

        final RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        final Toolbar mToolbar = findViewById(R.id.toolbar);

        if(mSharedPreferences.loadNightModeState())
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceDark);
        else
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceLight);
        mToolbar.setTitle(context.getString(R.string.favorites));
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        adapter = new GenericAdapter<Word>(favoriteDatabaseHelper.getAllFavorite()) {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack) {
                final View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
                return new GenericViewHolder(view, recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, Word val, int position) {
                GenericViewHolder viewHolder = (GenericViewHolder) holder;
                final TextView title = viewHolder.get(R.id.list_item_text);
                title.setText(val.getTitle());
            }

            @Override
            public RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack() {
                return new RecyclerViewOnClickListenerHack() {
                    @Override
                    public void onClickListener(View view, int position) {
                        Intent intent = new Intent(context, WordDetailActivity.class);
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter.getItemCount() != favoriteDatabaseHelper.getAllFavorite().size()){
            if(adapter.getItemCount() > favoriteDatabaseHelper.getAllFavorite().size()){
                adapter.clear();
                adapter.addItems(favoriteDatabaseHelper.getAllFavorite());
                adapter.notifyDataSetChanged();
            } else {
                adapter.addItems(favoriteDatabaseHelper.getAllFavorite());
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
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
            case R.id.report:
                startActivity(new Intent(context, ReportActivity.class));
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

}
