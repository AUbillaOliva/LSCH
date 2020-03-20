package cl.afubillaoliva.lsch.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.GenericAdapter;
import cl.afubillaoliva.lsch.models.ListItem;
import cl.afubillaoliva.lsch.utils.GenericViewHolder;
import cl.afubillaoliva.lsch.utils.SharedPreference;

public class HelpActivity extends AppCompatActivity {

    private final Context context = this;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        final SharedPreference mSharedPreferences = new SharedPreference(context);
        if(mSharedPreferences.loadNightModeState())
            setTheme(R.style.AppThemeDark);
        else
            setTheme(R.style.AppTheme);
        setContentView(R.layout.help_activity_layout);

        final Toolbar mToolbar = findViewById(R.id.toolbar);
        final TextView toolbarTitle = mToolbar.findViewById(R.id.toolbar_title);
        final RecyclerView mRecyclerView = findViewById(R.id.recycler_view);

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if(mSharedPreferences.loadNightModeState())
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceDark);
        else
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceLight);
        toolbarTitle.setText(R.string.help);


        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        GenericAdapter<ListItem> adapter = new GenericAdapter<ListItem>(helpListItems()) {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack) {
                return new GenericViewHolder(LayoutInflater.from(context).inflate(R.layout.expandable_list_item, parent, false), recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, ListItem val, int position) {
                final GenericViewHolder viewHolder = (GenericViewHolder) holder;
                final TextView title = viewHolder.get(R.id.item_title);
                title.setText(val.getTitle());
                final TextView subtitle = viewHolder.get(R.id.item_subtitle);
                subtitle.setText(val.getSubtitle());
                final ImageView anchor = viewHolder.get(R.id.anchor_dropdown);
                final boolean expanded = val.isExpanded();
                anchor.setImageDrawable(!expanded ? context.getDrawable(R.drawable.ic_arrow_down_24dp) : context.getDrawable(R.drawable.ic_arrow_up_24dp));
                final LinearLayout layout = viewHolder.get(R.id.sub_item);
                layout.setVisibility(expanded ? View.VISIBLE : View.GONE);
                final View divider = viewHolder.get(R.id.vw_divider);
                if (position == getItemCount() - 1)
                    divider.setVisibility(View.GONE);
                holder.itemView.setOnClickListener(v -> {
                    val.setExpanded(!expanded);
                    layout.setVisibility(expanded ? View.GONE : View.VISIBLE);
                    notifyItemChanged(position);
                });
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
        };
        final LinearLayoutManager aboutLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(aboutLayoutManager);
        mRecyclerView.setAdapter(adapter);

    }

    private ArrayList<ListItem> helpListItems(){

        final ArrayList<ListItem> list = new ArrayList<>();
        ListItem item = new ListItem();

        item.setTitle("¿Por qué algunos videos no se pueden ver?");
        item.setSubtitle("Actualmente se el diccionario de lengua de señas de la universidad metropolitana de las ciencias de la educación (UMCE), y lamentablemente no tenemos otro recurso para disponer de estos videos. Pronto nos encargaremos de este asunto con representantes oficiales de la universidad.");
        list.add(item);

        item = new ListItem();
        item.setTitle("¿Puedo utilizar la app sin internet?");
        item.setSubtitle("Puedes descargar los videos que quieras, para luego utilizarlas cuando no tengas internet.");
        list.add(item);

        item = new ListItem();
        item.setTitle("¿La aplicación envía reportes de error?");
        item.setSubtitle("Los reportes nos ayudan a identificar problemas en el funcionamiento de la aplicación. Estos son enviados de forma automática y anónima, por lo que no tienes por qué preocuparte =).");
        list.add(item);

        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId() == android.R.id.home){
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
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
