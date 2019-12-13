package cl.afubillaoliva.lsch.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.GenericAdapter;
import cl.afubillaoliva.lsch.models.SettingsItem;
import cl.afubillaoliva.lsch.utils.SharedPreference;

public class SettingsActivity extends AppCompatActivity {

    private final Context context = this;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        final SharedPreference mSharedPreferences = new SharedPreference(context);
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
        GenericAdapter<SettingsItem> aboutAdapter = new GenericAdapter<SettingsItem>(context, aboutListItems()) {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack) {
                final View view = LayoutInflater.from(context).inflate(R.layout.settings_list_item, parent, false);
                return new MyViewHolder(view, recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, SettingsItem val) {
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                myViewHolder.title.setText(val.getTitle());
                myViewHolder.subtitle.setText(val.getSubtitle());
            }

            @Override
            public RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack() {
                return new RecyclerViewOnClickListenerHack() {
                    @Override
                    public void onClickListener(View view, int position) {

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
        GenericAdapter<SettingsItem> searchAdapter = new GenericAdapter<SettingsItem>(context, searchListItems()) {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack) {
                final View view = LayoutInflater.from(context).inflate(R.layout.settings_list_item, parent, false);
                return new MyViewHolder(view, recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, SettingsItem val) {
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                myViewHolder.title.setText(val.getTitle());
                myViewHolder.subtitle.setText(val.getSubtitle());
            }

            @Override
            public RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack() {
                return new RecyclerViewOnClickListenerHack() {
                    @Override
                    public void onClickListener(View view, int position) {
                        switch (position){
                            case 0:
                                if(context.getDatabasePath("history.db").exists()) {
                                    context.deleteDatabase("history.db");
                                    Toast.makeText(context, "Historial eliminado", Toast.LENGTH_SHORT).show();
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

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView title, subtitle;
        private RecyclerViewOnClickListenerHack rvoclh;

        MyViewHolder(@NonNull View itemView, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack) {
            super(itemView);
            title = itemView.findViewById(R.id.list_item_title);
            subtitle = itemView.findViewById(R.id.list_item_subtitle);
            this.rvoclh = recyclerViewOnClickListenerHack;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(rvoclh != null){
                        rvoclh.onClickListener(v, getLayoutPosition());
                    }
                }
            });
        }

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
