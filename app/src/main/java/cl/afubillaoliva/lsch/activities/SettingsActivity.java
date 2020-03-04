package cl.afubillaoliva.lsch.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.GenericAdapter;
import cl.afubillaoliva.lsch.models.ListItem;
import cl.afubillaoliva.lsch.utils.GenericViewHolder;
import cl.afubillaoliva.lsch.utils.SharedPreference;
import cl.afubillaoliva.lsch.utils.databases.DownloadDatabaseHelper;
import cl.afubillaoliva.lsch.utils.databases.FavoriteDatabaseHelper;
import cl.afubillaoliva.lsch.utils.databases.HistoryDatabaseHelper;

public class SettingsActivity extends AppCompatActivity {

    private final Context context = this;
    private SharedPreference mSharedPreferences;
    private final FavoriteDatabaseHelper favoriteDatabaseHelper = new FavoriteDatabaseHelper(context);
    private final HistoryDatabaseHelper historyDatabaseHelper = new HistoryDatabaseHelper(context);
    private final DownloadDatabaseHelper downloadDatabaseHelper = new DownloadDatabaseHelper(context);

    private RecyclerView storageList, searchList, aboutList;
    private GenericAdapter<ListItem> storageAdapter, searchAdapter, aboutAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mSharedPreferences = new SharedPreference(context);
        if(mSharedPreferences.loadNightModeState())
            setTheme(R.style.AppThemeDark);
        else
            setTheme(R.style.AppTheme);
        setContentView(R.layout.settings_activity);

        final Toolbar mToolbar = findViewById(R.id.toolbar);
        final TextView toolbarTitle = mToolbar.findViewById(R.id.toolbar_title), versionTitle = findViewById(R.id.version_number);
        final Switch mDarkSwitch = findViewById(R.id.settings_dark_theme_switch),
                     mOfflineSwitch = findViewById(R.id.settings_offline_switch),
                     mWifiOnly = findViewById(R.id.settings_wifi_download_only);
        aboutList = findViewById(R.id.about_list);
        searchList = findViewById(R.id.search_list);
        storageList = findViewById(R.id.storage_list);
        //final Spinner storageSpinner = findViewById(R.id.storage_spinner);

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if(mSharedPreferences.loadNightModeState())
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceDark);
        else
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceLight);

        try {
            final PackageInfo pInfo = context.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionTitle.setText(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        toolbarTitle.setText(R.string.action_settings);

        if(mSharedPreferences.loadNightModeState())
            mDarkSwitch.setChecked(true);
        mDarkSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(!isChecked){
                mSharedPreferences.setNightMode(false);
                startActivity(new Intent(context, SettingsActivity.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                mSharedPreferences.setNightMode(true);
                startActivity(new Intent(context, SettingsActivity.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        if(mSharedPreferences.loadAutoDownload())
            mOfflineSwitch.setChecked(true);
        mOfflineSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(!isChecked)
                mSharedPreferences.setAutodownload(false);
            else
                mSharedPreferences.setAutodownload(true);
        });

        if(mSharedPreferences.isWifiOnly())
            mWifiOnly.setChecked(true);
        mWifiOnly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(!isChecked)
                mSharedPreferences.setWifiOnly(false);
            else
                mSharedPreferences.setWifiOnly(true);
        });

        aboutList.setHasFixedSize(true);
        aboutList.setNestedScrollingEnabled(true);
        final LinearLayoutManager aboutLayoutManager = new LinearLayoutManager(context);
        aboutList.setLayoutManager(aboutLayoutManager);
        aboutAdapter = new GenericAdapter<ListItem>(aboutListItems()){
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack){
                return new GenericViewHolder(LayoutInflater.from(context).inflate(R.layout.settings_list_item, parent, false), recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, ListItem val, int position){
                final GenericViewHolder myViewHolder = (GenericViewHolder) holder;
                final TextView title = myViewHolder.get(R.id.list_item_title);
                title.setText(val.getTitle());
                final TextView subtitle = myViewHolder.get(R.id.list_item_subtitle);
                subtitle.setText(val.getSubtitle());
            }

            @Override
            public RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack(){
                return new RecyclerViewOnClickListenerHack() {
                    @Override
                    public void onClickListener(View view, int position){
                        switch (position){
                            case 0:
                                startActivity(new Intent(context, ThirdPartyLibrariesActivity.class));
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                break;
                            case 1:
                                startActivity(new Intent(context, HelpActivity.class));
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                break;
                        }
                    }

                    @Override
                    public void onLongPressClickListener(View view, int position){}
                };
            }
        };
        aboutList.setAdapter(aboutAdapter);

        searchList.setHasFixedSize(true);
        searchList.setNestedScrollingEnabled(true);
        final LinearLayoutManager searchLayoutManager = new LinearLayoutManager(context);
        searchList.setLayoutManager(searchLayoutManager);
        searchAdapter = new GenericAdapter<ListItem>(searchListItems()){
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack){
                return new GenericViewHolder(LayoutInflater.from(context).inflate(R.layout.settings_list_item, parent, false), recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, ListItem val, int position){
                final GenericViewHolder myViewHolder = (GenericViewHolder) holder;
                final TextView title = myViewHolder.get(R.id.list_item_title);
                final TextView subtitle = myViewHolder.get(R.id.list_item_subtitle);
                title.setText(val.getTitle());
                subtitle.setText(val.getSubtitle());
                if(mSharedPreferences.loadNightModeState()){
                    if(getItem(position).isDisabled()){
                        title.setTextColor(getResources().getColor(R.color.textDisabledDark));
                        subtitle.setTextColor(getResources().getColor(R.color.textDisabledDark));
                    }
                } else {
                    if(getItem(position).isDisabled()){
                        title.setTextColor(getResources().getColor(R.color.textDisabledLight));
                        subtitle.setTextColor(getResources().getColor(R.color.textDisabledLight));
                    }
                }
            }

            @Override
            public RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack(){
                return new RecyclerViewOnClickListenerHack(){
                    @Override
                    public void onClickListener(final View view, final int position){
                        switch (position){
                            case 0:
                                if(historyDatabaseHelper.getHistory().size() > 0){
                                    final AlertDialog dialog = new AlertDialog.Builder(context)
                                            .setView(R.layout.confirmation_dialog_layout)
                                            .show();
                                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    final ExtendedFloatingActionButton positiveButton = dialog.findViewById(R.id.confirm_button);
                                    assert positiveButton != null;
                                    positiveButton.setText(R.string.positive_delete_dialog_button);
                                    positiveButton.setOnClickListener(v -> {
                                        context.deleteDatabase("history.db");
                                        mSharedPreferences.setHistoryDisabled(true);
                                        dialog.dismiss();
                                        onRestart();
                                    });
                                    mSharedPreferences.setHistoryDisabled(false);
                                    final TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
                                    assert dialogTitle != null;
                                    dialogTitle.setText(R.string.history_dialog_title);
                                    final TextView dialogSubtitle = dialog.findViewById(R.id.dialog_subtitle);
                                    assert dialogSubtitle != null;
                                    dialogSubtitle.setText(R.string.history_dialog_subtitle);
                                    final TextView negative = dialog.findViewById(R.id.negative_button);
                                    assert negative != null;
                                    negative.setText(R.string.negative_dialog_button);
                                    negative.setOnClickListener(v -> dialog.dismiss());
                                }
                                break;
                            case 1:
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
                                        mSharedPreferences.setFavoriteDisabled(true);
                                        notifyDataSetChanged();
                                        dialog.dismiss();
                                        onRestart();
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
                        }
                    }
                    @Override
                    public void onLongPressClickListener(View view, int position){}
                };
            }
        };
        searchList.setAdapter(searchAdapter);

        storageList.setHasFixedSize(true);
        storageList.setNestedScrollingEnabled(true);
        final LinearLayoutManager storageLayoutManager = new LinearLayoutManager(context);
        storageList.setLayoutManager(storageLayoutManager);
        storageAdapter = new GenericAdapter<ListItem>(storageListItems()){
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack){
                return new GenericViewHolder(LayoutInflater.from(context).inflate(R.layout.settings_list_item, parent, false), recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, ListItem val, int position){
                final GenericViewHolder myViewHolder = (GenericViewHolder) holder;
                final TextView title = myViewHolder.get(R.id.list_item_title);
                title.setText(val.getTitle());
                final TextView subtitle = myViewHolder.get(R.id.list_item_subtitle);
                subtitle.setText(val.getSubtitle());
                if(mSharedPreferences.loadNightModeState()){
                    if(getItem(position).isDisabled()){
                        title.setTextColor(getResources().getColor(R.color.textDisabledDark));
                        subtitle.setTextColor(getResources().getColor(R.color.textDisabledDark));
                    }
                } else {
                    if (getItem(position).isDisabled()) {
                        title.setTextColor(getResources().getColor(R.color.textDisabledLight));
                        subtitle.setTextColor(getResources().getColor(R.color.textDisabledLight));
                    }
                }
            }

            @Override
            public RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack(){
                return new RecyclerViewOnClickListenerHack(){
                    @Override
                    public void onClickListener(View view, int position){
                        switch (position){
                            case 0:
                                if(getCacheDir().length() > 0){
                                    try {
                                        final File cacheDir = context.getCacheDir();
                                        final AlertDialog dialog = new AlertDialog.Builder(context)
                                                .setView(R.layout.confirmation_dialog_layout)
                                                .show();
                                        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        final ExtendedFloatingActionButton positiveButton = dialog.findViewById(R.id.confirm_button);
                                        assert positiveButton != null;
                                        positiveButton.setText(R.string.positive_delete_dialog_button);
                                        positiveButton.setOnClickListener(v -> {
                                            deleteDir(cacheDir);
                                            mSharedPreferences.setCacheDisabled(true);
                                            dialog.dismiss();
                                        });
                                        mSharedPreferences.setCacheDisabled(false);
                                        final TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
                                        assert dialogTitle != null;
                                        dialogTitle.setText(R.string.cache_dialog_title);
                                        final TextView dialogSubtitle = dialog.findViewById(R.id.dialog_subtitle);
                                        assert dialogSubtitle != null;
                                        dialogSubtitle.setText(R.string.cache_dialog_subtitle);
                                        final TextView negative = dialog.findViewById(R.id.negative_button);
                                        assert negative != null;
                                        negative.setText(R.string.negative_dialog_button);
                                        negative.setOnClickListener(v -> dialog.dismiss());
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            case 1:
                                if(downloadDatabaseHelper.getAllDownload().size() > 0 || Objects.requireNonNull(context.getExternalFilesDir(null)).listFiles() == null){
                                    if(context.getDatabasePath("download.db").exists())
                                        Log.d(MainActivity.TAG, "database exists!");
                                    else if(Objects.requireNonNull(context.getExternalFilesDir(null)).listFiles() != null)
                                        Log.d(MainActivity.TAG, "file folder has no items!");
                                    else
                                        Log.d(MainActivity.TAG, "none");
                                    final AlertDialog dialog = new AlertDialog.Builder(context)
                                            .setView(R.layout.confirmation_dialog_layout)
                                            .show();
                                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    final ExtendedFloatingActionButton positiveButton = dialog.findViewById(R.id.confirm_button);
                                    assert positiveButton != null;
                                    positiveButton.setText(R.string.positive_delete_dialog_button);
                                    positiveButton.setOnClickListener(v -> {
                                        context.deleteDatabase("download.db");
                                        final File dir = new File(Environment.getExternalStorageDirectory()+"/Android/data/cl.afubillaoliva.lsch/files");
                                        if (dir.isDirectory()){
                                            final String[] children = dir.list();
                                            assert children != null;
                                            for (String child : children){
                                                new File(dir, child).delete();
                                            }
                                        }
                                        dir.delete();
                                        mSharedPreferences.setDownloadDisabled(true);
                                        dialog.dismiss();
                                        onRestart();
                                    });
                                    mSharedPreferences.setDownloadDisabled(false);
                                    final TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
                                    assert dialogTitle != null;
                                    dialogTitle.setText(R.string.download_dialog_title);
                                    final TextView dialogSubtitle = dialog.findViewById(R.id.dialog_subtitle);
                                    assert dialogSubtitle != null;
                                    dialogSubtitle.setText(R.string.download_dialog_subtitle);
                                    final TextView negative = dialog.findViewById(R.id.negative_button);
                                    assert negative != null;
                                    negative.setText(R.string.negative_dialog_button);
                                    negative.setOnClickListener(v -> dialog.dismiss());

                                } else
                                    mSharedPreferences.setDownloadDisabled(true);
                                break;
                            case 2:
                                // TODO
                                /*final Intent storageOptions = new Intent(context, StorageOptionsActivity.class);
                                storageOptions.putExtra("activity", "Almacenamiento");
                                startActivity(storageOptions);*/
                                Toast.makeText(context, "Storage options", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onLongPressClickListener(View view, int position){}
                };
            }
        };
        storageList.setAdapter(storageAdapter);

        /*final ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(context, R.array.storage_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storageSpinner.setAdapter(spinnerAdapter);*/

    }

    @Override
    protected void onRestart(){
        super.onRestart();

        aboutAdapter = new GenericAdapter<ListItem>(aboutListItems()){
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack){
                return new GenericViewHolder(LayoutInflater.from(context).inflate(R.layout.settings_list_item, parent, false), recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, ListItem val, int position){
                final GenericViewHolder myViewHolder = (GenericViewHolder) holder;
                final TextView title = myViewHolder.get(R.id.list_item_title);
                title.setText(val.getTitle());
                final TextView subtitle = myViewHolder.get(R.id.list_item_subtitle);
                subtitle.setText(val.getSubtitle());
            }

            @Override
            public RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack(){
                return new RecyclerViewOnClickListenerHack(){
                    @Override
                    public void onClickListener(View view, int position){
                        switch (position){
                            case 0:
                                startActivity(new Intent(context, ThirdPartyLibrariesActivity.class));
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                break;
                            case 1:
                                startActivity(new Intent(context, HelpActivity.class));
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                break;
                        }
                    }

                    @Override
                    public void onLongPressClickListener(View view, int position){}
                };
            }
        };
        aboutList.setAdapter(aboutAdapter);

        searchAdapter = new GenericAdapter<ListItem>(searchListItems()){
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack){
                return new GenericViewHolder(LayoutInflater.from(context).inflate(R.layout.settings_list_item, parent, false), recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, ListItem val, int position){
                final GenericViewHolder myViewHolder = (GenericViewHolder) holder;
                final TextView title = myViewHolder.get(R.id.list_item_title);
                final TextView subtitle = myViewHolder.get(R.id.list_item_subtitle);
                title.setText(val.getTitle());
                subtitle.setText(val.getSubtitle());
                if(mSharedPreferences.loadNightModeState()) {
                    if (getItem(position).isDisabled()) {
                        title.setTextColor(getResources().getColor(R.color.textDisabledDark));
                        subtitle.setTextColor(getResources().getColor(R.color.textDisabledDark));
                    }
                } else {
                    if(getItem(position).isDisabled()){
                        title.setTextColor(getResources().getColor(R.color.textDisabledLight));
                        subtitle.setTextColor(getResources().getColor(R.color.textDisabledLight));
                    }
                }
            }

            @Override
            public RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack(){
                return new RecyclerViewOnClickListenerHack(){
                    @Override
                    public void onClickListener(final View view, final int position){
                        switch (position){
                            case 0:
                                if(historyDatabaseHelper.getHistory().size() > 0){
                                    final AlertDialog dialog = new AlertDialog.Builder(context)
                                            .setView(R.layout.confirmation_dialog_layout)
                                            .show();
                                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    final ExtendedFloatingActionButton positiveButton = dialog.findViewById(R.id.confirm_button);
                                    assert positiveButton != null;
                                    positiveButton.setText(R.string.positive_delete_dialog_button);
                                    positiveButton.setOnClickListener(v -> {
                                        context.deleteDatabase("history.db");
                                        mSharedPreferences.setHistoryDisabled(true);
                                        dialog.dismiss();
                                        onRestart();
                                    });
                                    mSharedPreferences.setHistoryDisabled(false);
                                    final TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
                                    assert dialogTitle != null;
                                    dialogTitle.setText(R.string.history_dialog_title);
                                    final TextView dialogSubtitle = dialog.findViewById(R.id.dialog_subtitle);
                                    assert dialogSubtitle != null;
                                    dialogSubtitle.setText(R.string.history_dialog_subtitle);
                                    final TextView negative = dialog.findViewById(R.id.negative_button);
                                    assert negative != null;
                                    negative.setText(R.string.negative_dialog_button);
                                    negative.setOnClickListener(v -> dialog.dismiss());
                                }
                                break;
                            case 1:
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
                                        mSharedPreferences.setFavoriteDisabled(true);
                                        notifyDataSetChanged();
                                        dialog.dismiss();
                                        onRestart();
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
                        }
                    }
                    @Override
                    public void onLongPressClickListener(View view, int position){}
                };
            }
        };
        searchList.setAdapter(searchAdapter);

        storageAdapter = new GenericAdapter<ListItem>(storageListItems()){
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack){
                return new GenericViewHolder(LayoutInflater.from(context).inflate(R.layout.settings_list_item, parent, false), recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, ListItem val, int position){
                final GenericViewHolder myViewHolder = (GenericViewHolder) holder;
                final TextView title = myViewHolder.get(R.id.list_item_title);
                title.setText(val.getTitle());
                final TextView subtitle = myViewHolder.get(R.id.list_item_subtitle);
                subtitle.setText(val.getSubtitle());
                if(mSharedPreferences.loadNightModeState()){
                    if(getItem(position).isDisabled()){
                        title.setTextColor(getResources().getColor(R.color.textDisabledDark));
                        subtitle.setTextColor(getResources().getColor(R.color.textDisabledDark));
                    }
                } else {
                    if (getItem(position).isDisabled()){
                        title.setTextColor(getResources().getColor(R.color.textDisabledLight));
                        subtitle.setTextColor(getResources().getColor(R.color.textDisabledLight));
                    }
                }
            }

            @Override
            public RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack(){
                return new RecyclerViewOnClickListenerHack(){
                    @Override
                    public void onClickListener(View view, int position){
                        switch (position){
                            case 0:
                                if(getCacheDir().length() > 0){
                                    try {
                                        final File cacheDir = context.getCacheDir();
                                        final AlertDialog dialog = new AlertDialog.Builder(context)
                                                .setView(R.layout.confirmation_dialog_layout)
                                                .show();
                                        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        final ExtendedFloatingActionButton positiveButton = dialog.findViewById(R.id.confirm_button);
                                        assert positiveButton != null;
                                        positiveButton.setText(R.string.positive_delete_dialog_button);
                                        positiveButton.setOnClickListener(v -> {
                                            deleteDir(cacheDir);
                                            mSharedPreferences.setCacheDisabled(true);
                                            dialog.dismiss();
                                        });
                                        mSharedPreferences.setCacheDisabled(false);
                                        final TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
                                        assert dialogTitle != null;
                                        dialogTitle.setText(R.string.cache_dialog_title);
                                        final TextView dialogSubtitle = dialog.findViewById(R.id.dialog_subtitle);
                                        assert dialogSubtitle != null;
                                        dialogSubtitle.setText(R.string.cache_dialog_subtitle);
                                        final TextView negative = dialog.findViewById(R.id.negative_button);
                                        assert negative != null;
                                        negative.setText(R.string.negative_dialog_button);
                                        negative.setOnClickListener(v -> dialog.dismiss());
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            case 1:
                                if(downloadDatabaseHelper.getAllDownload().size() > 0 || Objects.requireNonNull(context.getExternalFilesDir(null)).listFiles() == null){
                                    final AlertDialog dialog = new AlertDialog.Builder(context)
                                            .setView(R.layout.confirmation_dialog_layout)
                                            .show();
                                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    final ExtendedFloatingActionButton positiveButton = dialog.findViewById(R.id.confirm_button);
                                    assert positiveButton != null;
                                    positiveButton.setText(R.string.positive_delete_dialog_button);
                                    positiveButton.setOnClickListener(v -> {
                                        context.deleteDatabase("download.db");
                                        final File dir = new File(Environment.getExternalStorageDirectory()+"/Android/data/cl.afubillaoliva.lsch/files");
                                        if (dir.isDirectory()){
                                            final String[] children = dir.list();
                                            assert children != null;
                                            for (String child : children){
                                                new File(dir, child).delete();
                                            }
                                        }
                                        dir.delete();
                                        mSharedPreferences.setDownloadDisabled(true);
                                        dialog.dismiss();
                                        onRestart();
                                    });
                                    mSharedPreferences.setDownloadDisabled(false);
                                    final TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
                                    assert dialogTitle != null;
                                    dialogTitle.setText(R.string.download_dialog_title);
                                    final TextView dialogSubtitle = dialog.findViewById(R.id.dialog_subtitle);
                                    assert dialogSubtitle != null;
                                    dialogSubtitle.setText(R.string.download_dialog_subtitle);
                                    final TextView negative = dialog.findViewById(R.id.negative_button);
                                    assert negative != null;
                                    negative.setText(R.string.negative_dialog_button);
                                    negative.setOnClickListener(v -> dialog.dismiss());
                                }
                                break;
                            case 2:
                                // TODO
                                /*final Intent storageOptions = new Intent(context, StorageOptionsActivity.class);
                                storageOptions.putExtra("activity", "Almacenamiento");
                                startActivity(storageOptions);*/
                                Toast.makeText(context, "Storage options", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onLongPressClickListener(View view, int position){}
                };
            }
        };
        storageList.setAdapter(storageAdapter);
    }

    public static boolean deleteDir(File dir){
        if (dir != null && dir.isDirectory()){
            final String[] children = dir.list();
            assert children != null;
            for (String child : children){
                final boolean success = deleteDir(new File(dir, child));
                if (!success)
                    return false;
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile())
            return dir.delete();
        else
            return false;
    }

    private ArrayList<ListItem> aboutListItems(){
        final ArrayList<ListItem> items = new ArrayList<>();

        ListItem item = new ListItem();
        item.setTitle("Librerias de terceros");
        item.setSubtitle("Con la gran ayuda de este software!");
        items.add(item);

        item = new ListItem();
        item.setTitle("Ayuda");
        item.setSubtitle("Estamos aqui para ayudarte.");
        items.add(item);

        item = new ListItem();
        item.setTitle("Soporte");
        item.setSubtitle("Obtén ayuda de nosotros y de la comunidad.");
        items.add(item);

        return items;
    }
    private ArrayList<ListItem> searchListItems(){
        final ArrayList<ListItem> items = new ArrayList<>();

        ListItem item = new ListItem();
        item.setTitle("Eliminar historial");
        item.setSubtitle("Elimina tus busquedas recientes del menu de busqueda.");
        item.setDisabled(mSharedPreferences.isHistoryDisabled());
        items.add(item);

        item = new ListItem();
        item.setTitle("Eliminar favoritos");
        item.setSubtitle("Elimina todos tus favoritos.");
        item.setDisabled(mSharedPreferences.isFavoriteDisabled());
        items.add(item);

        return items;
    }
    private ArrayList<ListItem> storageListItems(){

        final ArrayList<ListItem> items = new ArrayList<>();
        ListItem item = new ListItem();

        item.setTitle("Eliminar caché");
        item.setSubtitle("Elimina el caché utilizado por la app. Tus favoritos y descargas no serán eliminados.");
        item.setDisabled(mSharedPreferences.isCacheDisabled());
        items.add(item);

        item = new ListItem();
        item.setTitle("Eliminar descargas");
        item.setSubtitle("Elimina todas tus palabras descargadas.");
        item.setDisabled(mSharedPreferences.isDownloadDisabled());
        items.add(item);

        item = new ListItem();
        item.setTitle("Ubicación de almacenamiento");
        item.setSubtitle("Establece donde quieres que los videos descargados se almacenen.");
        item.setDisabled(false);
        items.add(item);

        return items;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //replace with switch if has more items
        if (item.getItemId() == android.R.id.home){
            startActivity(new Intent(context, MainActivity.class));
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
