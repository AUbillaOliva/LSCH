package cl.afubillaoliva.lsch.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.GenericAdapter;
import cl.afubillaoliva.lsch.models.StorageOptionItem;
import cl.afubillaoliva.lsch.utils.GenericViewHolder;
import cl.afubillaoliva.lsch.utils.SharedPreference;

public class StorageOptionsActivity extends AppCompatActivity {

    final private Context context = this;
    public static final String ERROR = "ERROR";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        //ArrayList<String> data = intent.getStringArrayListExtra("data");

        final SharedPreference mSharedPreferences = new SharedPreference(context);
        if(mSharedPreferences.loadNightModeState())
            setTheme(R.style.AppThemeDark);
        else setTheme(R.style.AppTheme);
        setContentView(R.layout.list_activity);

        final Toolbar mToolbar = findViewById(R.id.toolbar);
        final ProgressBar progressBar = findViewById(R.id.progress_circular);
        final RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_layout);

        if(mSharedPreferences.loadNightModeState())
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceDark);
        else
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceLight);
        mToolbar.setTitle(intent.getStringExtra("activity"));
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        progressBar.setVisibility(View.GONE);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        final GenericAdapter<StorageOptionItem> aboutAdapter = new GenericAdapter<StorageOptionItem>(storageOptions()) {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack) {
                return new GenericViewHolder(LayoutInflater.from(context).inflate(R.layout.settings_list_item, parent, false), recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, StorageOptionItem val, int position) {
                final GenericViewHolder myViewHolder = (GenericViewHolder) holder;
                final TextView title = myViewHolder.get(R.id.list_item_title);
                title.setText(val.getTitle());
                final TextView subtitle = myViewHolder.get(R.id.list_item_subtitle);
                final String subtitleContent = val.getLocation() + "\n" + "Actualmente en uso: " + val.getUsed() + "\n" + "Disponible: " + val.getFree() + "Total: " + val.getTotal();
                subtitle.setText(subtitleContent);
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
        mRecyclerView.setAdapter(aboutAdapter);

        swipeRefreshLayout.setOnRefreshListener(() -> swipeRefreshLayout.setRefreshing(false));

    }

    public static boolean externalMemoryAvailable(){
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public long getAvailableInternalMemorySize(){
        File path = new File(getFilesDir().getAbsolutePath());
        final StatFs stat;
        if(path.exists())
            stat = new StatFs(path.getPath());
        else {
            path = Environment.getDataDirectory();
            stat = new StatFs(path.getPath());
        }
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return availableBlocks * blockSize;
    }

    public long getTotalInternalMemorySize(){
        File path = new File(getFilesDir().getAbsolutePath());
        final StatFs stat;
        if(path.exists())
            stat = new StatFs(path.getPath());
        else {
            path = Environment.getDataDirectory();
            stat = new StatFs(path.getPath());
        }
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return totalBlocks * blockSize;
    }

    public static long getAvailableExternalMemorySize(){
        if (externalMemoryAvailable()) {
            File path = new File(Environment.getExternalStorageDirectory().getPath());
            final StatFs stat;
            if(path.exists()){
                stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSizeLong();
                long availableBlocks = stat.getAvailableBlocksLong();
                return availableBlocks * blockSize;
            } else {
                path = Environment.getExternalStorageDirectory();
                stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSizeLong();
                long availableBlocks = stat.getAvailableBlocksLong();
                return availableBlocks * blockSize;
            }
        } else
            return 0;
    }

    public static long getTotalExternalMemorySize(){
        if (externalMemoryAvailable()){
            final File path = Environment.getExternalStorageDirectory();
            final StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            return totalBlocks * blockSize;
        } else
            return 0;
    }

    public static String floatForm (double d){
        return new DecimalFormat("#.##").format(d);
    }

    public static String bytesToHuman (long size){
        long Kb = 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size <  Kb)   return floatForm(        size     ) + " byte";
        if (size < Mb)    return floatForm((double)size / Kb) + " Kb";
        if (size < Gb)    return floatForm((double)size / Mb) + " Mb";
        if (size < Tb)    return floatForm((double)size / Gb) + " Gb";
        if (size < Pb)    return floatForm((double)size / Tb) + " Tb";
        if (size < Eb)    return floatForm((double)size / Pb) + " Pb";
        return floatForm((double)size / Eb) + " Eb";
    }

    public static String formatSize(long size){
        String suffix = null;
        if (size >= 1024){
            suffix = "KB";
            size /= 1024;
            if (size >= 1024){
                suffix = "MB";
                size /= 1024;
            }
        }

        final StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0){
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null)
            resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    private ArrayList<StorageOptionItem> storageOptions(){

        final ArrayList<StorageOptionItem> items = new ArrayList<>();
        StorageOptionItem item = new StorageOptionItem();

        item.setTitle("Almacenamiento en el dispositivo");
        long free = getAvailableInternalMemorySize();
        long total = getTotalInternalMemorySize();
        item.setFree(free);
        item.setUsed(total - free);
        item.setTotal(total);
        final File path = new File(getFilesDir().getAbsolutePath());
        if(path.exists())
            item.setLocation(path.getPath());
        else
            item.setLocation(Environment.getRootDirectory().getPath());
        items.add(item);

        Log.d(MainActivity.UI, item.getTitle() +
                "\n" + item.getLocation() +
                "\n" + "Actualmente en uso: " + bytesToHuman(item.getTotal() - item.getFree()) +
                "\n" + "Disponible: " + bytesToHuman(item.getFree()) +
                "\n" + "Total: " + bytesToHuman(item.getTotal()));

        if(externalMemoryAvailable()){
            item = new StorageOptionItem();
            item.setTitle("Tarjeta SD");
            long freeExt = getAvailableExternalMemorySize();
            long totalExt = getTotalExternalMemorySize();
            item.setFree(freeExt);
            item.setUsed(totalExt - freeExt);
            item.setTotal(totalExt);
            final File sdcard = new File(Environment.getExternalStorageDirectory().getPath());
            if(sdcard.exists())
                item.setLocation(sdcard.getPath());
            else
                item.setLocation(Environment.getExternalStorageDirectory().getPath());
            items.add(item);

            Log.d(MainActivity.UI, item.getTitle() +
                    "\n" + item.getLocation() +
                    "\n" + "Actualmente en uso: " + bytesToHuman(item.getTotal() - item.getFree()) +
                    "\n" + "Disponible: " + bytesToHuman(item.getFree()) +
                    "\n" + "Total: " + bytesToHuman(item.getTotal()));

        }

        return items;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
    }

}
