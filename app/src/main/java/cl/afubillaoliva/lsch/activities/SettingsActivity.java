package cl.afubillaoliva.lsch.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.GenericAdapter;
import cl.afubillaoliva.lsch.models.SettingsItem;
import cl.afubillaoliva.lsch.utils.GenericViewHolder;
import cl.afubillaoliva.lsch.utils.SharedPreference;
import cl.afubillaoliva.lsch.utils.databases.HistoryDatabaseHelper;

public class SettingsActivity extends AppCompatActivity {

    private final Context context = this;
    private SharedPreference mSharedPreferences;

    private final HistoryDatabaseHelper databaseHelper = new HistoryDatabaseHelper(context);

    private TextView title, subtitle;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mSharedPreferences = new SharedPreference(context);
        if(mSharedPreferences.loadNightModeState())
            setTheme(R.style.AppThemeDark);
        else setTheme(R.style.AppTheme);
        setContentView(R.layout.settings_activity);

        final Toolbar mToolbar = findViewById(R.id.toolbar);
        final TextView toolbarTitle = mToolbar.findViewById(R.id.toolbar_title), versionTitle = findViewById(R.id.version_number);
        final Switch mSwitch = findViewById(R.id.settings_dark_theme_switch);
        final RecyclerView aboutList = findViewById(R.id.about_list), searchList = findViewById(R.id.search_list);

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if(mSharedPreferences.loadNightModeState())
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceDark);
        else
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceLight);

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionTitle.setText(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        toolbarTitle.setText(R.string.action_settings);

        if(mSharedPreferences.loadNightModeState())
            mSwitch.setChecked(true);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
            }
        });

        aboutList.setHasFixedSize(true);
        aboutList.setNestedScrollingEnabled(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        aboutList.setLayoutManager(linearLayoutManager);
        final GenericAdapter<SettingsItem> aboutAdapter = new GenericAdapter<SettingsItem>(aboutListItems()) {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack) {
                final View view = LayoutInflater.from(context).inflate(R.layout.settings_list_item, parent, false);
                return new GenericViewHolder(view, recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, SettingsItem val, int position) {
                GenericViewHolder myViewHolder = (GenericViewHolder) holder;
                final TextView title = myViewHolder.get(R.id.list_item_title);
                title.setText(val.getTitle());
                final TextView subtitle = myViewHolder.get(R.id.list_item_subtitle);
                subtitle.setText(val.getSubtitle());
            }

            @Override
            public RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack() {
                return new RecyclerViewOnClickListenerHack() {
                    @Override
                    public void onClickListener(View view, int position) {
                        switch (position){
                            case 0:
                                Intent intent = new Intent(context, ListActivity.class);
                                intent.putExtra("activity", getItem(position).getTitle());
                                intent.putExtra("data", libraries());
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                break;
                        }
                    }

                    @Override
                    public void onLongPressClickListener(View view, int position) {

                    }
                };
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return super.onCreateViewHolder(parent, viewType);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }
        };
        aboutList.setAdapter(aboutAdapter);

        searchList.setHasFixedSize(true);
        searchList.setNestedScrollingEnabled(true);
        final LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(context);
        searchList.setLayoutManager(linearLayoutManager2);
        final GenericAdapter<SettingsItem> searchAdapter = new GenericAdapter<SettingsItem>(searchListItems()) {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack) {
                final View view = LayoutInflater.from(context).inflate(R.layout.settings_list_item, parent, false);
                return new GenericViewHolder(view, recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, SettingsItem val, int position) {
                GenericViewHolder myViewHolder = (GenericViewHolder) holder;
                title = myViewHolder.get(R.id.list_item_title);
                title.setText(val.getTitle());
                subtitle = myViewHolder.get(R.id.list_item_subtitle);
                subtitle.setText(val.getSubtitle());
                if(!context.getDatabasePath("history.db").exists()){
                    if(mSharedPreferences.loadNightModeState()){
                        title.setTextColor(getResources().getColor(R.color.textDisabledDark));
                        subtitle.setTextColor(getResources().getColor(R.color.textDisabledDark));
                    } else {
                        title.setTextColor(getResources().getColor(R.color.textDisabledLight));
                        subtitle.setTextColor(getResources().getColor(R.color.textDisabledLight));
                    }
                }
            }

            @Override
            public RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack() {
                return new RecyclerViewOnClickListenerHack() {
                    @Override
                    public void onClickListener(View view, int position) {
                        switch (position){
                            case 0:
                                //TODO: SHOW CONFIRMATION DIALOG

                                if(context.getDatabasePath("history.db").exists()) {
                                    final AlertDialog dialog = new AlertDialog.Builder(context)
                                            .setView(R.layout.confirmation_dialog_layout)
                                            .show();
                                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    final ExtendedFloatingActionButton positiveButton = dialog.findViewById(R.id.confirm_button);
                                    assert positiveButton != null;
                                    positiveButton.setText(R.string.positive_history_dialog_button);
                                    positiveButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            context.deleteDatabase("history.db");
                                            if(mSharedPreferences.loadNightModeState()){
                                                title.setTextColor(getResources().getColor(R.color.textDisabledDark));
                                                subtitle.setTextColor(getResources().getColor(R.color.textDisabledDark));
                                            } else {
                                                title.setTextColor(getResources().getColor(R.color.textDisabledLight));
                                                subtitle.setTextColor(getResources().getColor(R.color.textDisabledLight));
                                            }
                                            dialog.dismiss();
                                        }
                                    });
                                    final TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
                                    assert dialogTitle != null;
                                    dialogTitle.setText(R.string.history_dialog_title);
                                    final TextView dialogSubtitle = dialog.findViewById(R.id.dialog_subtitle);
                                    assert dialogSubtitle != null;
                                    dialogSubtitle.setText(R.string.history_dialog_subtitle);
                                    final TextView negative = dialog.findViewById(R.id.negative_button);
                                    assert negative != null;
                                    negative.setText(R.string.negative_dialog_button);
                                    negative.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });

                                }

                                break;
                        }
                    }
                    @Override
                    public void onLongPressClickListener(View view, int position) {}
                };
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return super.onCreateViewHolder(parent, viewType);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }

        };
        searchList.setAdapter(searchAdapter);
    }

    public ArrayList<String> libraries(){
        ArrayList<String> items = new ArrayList<>();
        items.add("Retrofit");
        items.add("Retrofit Converter Gson");
        items.add("Glide");
        items.add("Gson");
        items.add("RecyclerView-v7");
        items.add("CardView-v7");
        items.add("Android AppCompat-v7");
        items.add("Android Design Library");
        items.add("Android Support Compat");
        items.add("Android Support Library");
        return items;
    }
    private ArrayList<SettingsItem> aboutListItems(){
        ArrayList<SettingsItem> items = new ArrayList<>();

        SettingsItem item = new SettingsItem();
        item.setTitle("Librerias de Terceros");
        item.setSubtitle("Con la gran ayuda de este software!");
        //TODO: GET README FROM REPO, THIRD-PARTY LIBRARIES
        items.add(item);

        item = new SettingsItem();
        item.setTitle("Soporte");
        item.setSubtitle("Obtén ayuda de nosotros y de la comunidad.");
        items.add(item);

        return items;
    }
    private ArrayList<SettingsItem> searchListItems(){
        ArrayList<SettingsItem> items = new ArrayList<>();

        SettingsItem item = new SettingsItem();
        item.setTitle("Eliminar Historial");
        item.setSubtitle("Elimina tus busquedas recientes del menu de busqueda.");
        items.add(item);

        return items;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
}
