package cl.afubillaoliva.lsch.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Objects;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.GenericAdapter;
import cl.afubillaoliva.lsch.models.Word;
import cl.afubillaoliva.lsch.utils.GenericViewHolder;
import cl.afubillaoliva.lsch.utils.databases.FavoriteDatabaseHelper;
import cl.afubillaoliva.lsch.utils.SharedPreference;

public class FavoriteListActivity extends AppCompatActivity {

    private final Context context = this;
    private SharedPreference mSharedPreferences;

    private GenericAdapter<Word> adapter;
    private final FavoriteDatabaseHelper favoriteDatabaseHelper = new FavoriteDatabaseHelper(context);
    private LinearLayout placeHolderLayout;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mSharedPreferences = new SharedPreference(context);
        if (mSharedPreferences.loadNightModeState())
            setTheme(R.style.AppThemeDark);
        else
            setTheme(R.style.AppTheme);
        setContentView(R.layout.favorite_activity_layout);

        final RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        final Toolbar mToolbar = findViewById(R.id.toolbar);
        final ImageView favImageHolder = findViewById(R.id.empty_image);
        final TextView title = findViewById(R.id.empty_title);
        final TextView message = findViewById(R.id.empty_message);
        placeHolderLayout = findViewById(R.id.empty_frame);

        title.setText(R.string.no_favorites);
        message.setText(R.string.add_favorites);

        if(mSharedPreferences.loadNightModeState()){
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceDark);
            favImageHolder.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        } else {
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceLight);
            favImageHolder.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
        mToolbar.setTitle(context.getString(R.string.favorites));
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        adapter = new GenericAdapter<Word>(favoriteDatabaseHelper.getAllFavorite()){
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack){
                return new GenericViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false), recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, Word val, int position){
                final GenericViewHolder viewHolder = (GenericViewHolder) holder;
                final TextView title = viewHolder.get(R.id.list_item_text);
                title.setText(val.getTitle());
            }

            @Override
            public RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack(){
                return new RecyclerViewOnClickListenerHack(){
                    @Override
                    public void onClickListener(View view, int position){
                        final Intent intent = new Intent(context, DataDetailActivity.class);
                        intent.putExtra("position", adapter.getItem(position));
                        startActivity(intent);
                    }

                    @Override
                    public void onLongPressClickListener(View view, int position){}
                };
            }
        };
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(adapter);

        if(adapter.getItemCount() < 1)
            placeHolderLayout.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onResume(){
        super.onResume();
        if(adapter.getItemCount() != favoriteDatabaseHelper.getAllFavorite().size())
            if(adapter.getItemCount() > favoriteDatabaseHelper.getAllFavorite().size()){
                adapter.clear();
                adapter.addItems(favoriteDatabaseHelper.getAllFavorite());
                adapter.notifyDataSetChanged();
            } else {
                adapter.addItems(favoriteDatabaseHelper.getAllFavorite());
                adapter.notifyDataSetChanged();
            }

        if(adapter.getItemCount() < 1)
            placeHolderLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_favorites, menu);

        final MenuItem delete = menu.findItem(R.id.delete);
        if (adapter.getItemCount() < 1)
            delete.setVisible(false);
        else
            delete.setVisible(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(context, MainActivity.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.delete:
                if(favoriteDatabaseHelper.getAllFavorite().size() > 0){
                    final AlertDialog dialog = new AlertDialog.Builder(context)
                            .setView(R.layout.confirmation_dialog_layout)
                            .show();
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    final ExtendedFloatingActionButton positiveButton = dialog.findViewById(R.id.confirm_button);
                    assert positiveButton != null;
                    positiveButton.setText(R.string.positive_delete_dialog_button);
                    positiveButton.setOnClickListener(v -> {
                        context.deleteDatabase("favorite.db");
                        if(adapter.getItemCount() != favoriteDatabaseHelper.getAllFavorite().size()){
                            if(adapter.getItemCount() > favoriteDatabaseHelper.getAllFavorite().size()){
                                adapter.clear();
                                adapter.addItems(favoriteDatabaseHelper.getAllFavorite());
                                if(adapter.getItemCount() < 1){
                                    placeHolderLayout.setVisibility(View.VISIBLE);
                                    item.setVisible(false);
                                } else {
                                    placeHolderLayout.setVisibility(View.GONE);
                                    item.setVisible(true);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                adapter.addItems(favoriteDatabaseHelper.getAllFavorite());
                                adapter.notifyDataSetChanged();
                            }
                        }
                        mSharedPreferences.setFavoriteDisabled(true);
                        dialog.dismiss();
                    });
                    mSharedPreferences.setFavoriteDisabled(false);
                    final TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
                    assert dialogTitle != null;
                    dialogTitle.setText(R.string.favorite_dialog_title);
                    final TextView dialogSubtitle = dialog.findViewById(R.id.dialog_subtitle);
                    assert dialogSubtitle != null;
                    dialogSubtitle.setText(R.string.favorite_dialog_subtitle);
                    final TextView negative = dialog.findViewById(R.id.negative_button);
                    assert negative != null;
                    negative.setText(R.string.negative_dialog_button);
                    negative.setOnClickListener(v -> dialog.dismiss());
                }
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
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(newBase);
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
    }

}
