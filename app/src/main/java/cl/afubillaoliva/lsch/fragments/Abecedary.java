package cl.afubillaoliva.lsch.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Objects;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.activities.AbecedaryListActivity;
import cl.afubillaoliva.lsch.adapters.GenericAdapter;
import cl.afubillaoliva.lsch.api.ApiClient;
import cl.afubillaoliva.lsch.api.ApiService;
import cl.afubillaoliva.lsch.utils.GenericViewHolder;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Abecedary extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private GenericAdapter<cl.afubillaoliva.lsch.models.Abecedary> adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        final View view = inflater.inflate(R.layout.fragment_layout, container,false);

        final RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view);
        final NestedScrollView nestedScrollView = view.findViewById(R.id.nested);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        mProgressBar = view.findViewById(R.id.progress_circular);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            nestedScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                int initialscrollY = 0;
                if (scrollY > initialscrollY){
                    ((MainActivity) Objects.requireNonNull(getActivity())).getAppBarLayout().setElevation(8);
                    ((MainActivity) getActivity()).getAppBarLayoutShadow().setVisibility(View.GONE);
                } else if(scrollY < oldScrollY - scrollY){
                    ((MainActivity) Objects.requireNonNull(getActivity())).getAppBarLayout().setElevation(2);
                    ((MainActivity) getActivity()).getAppBarLayoutShadow().setVisibility(View.VISIBLE);
                }
            });


        mRecyclerView.setHasFixedSize(true);
        adapter = new GenericAdapter<cl.afubillaoliva.lsch.models.Abecedary>(){
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack){
                return new GenericViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_card, parent, false), recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, cl.afubillaoliva.lsch.models.Abecedary val, int position){
                final GenericViewHolder viewHolder = (GenericViewHolder) holder;
                final TextView title = viewHolder.get(R.id.abecedary_letter);
                title.setText(val.getLetter());
                final ImageView image = viewHolder.get(R.id.abecedary_img);
                Glide.with(Objects.requireNonNull(getContext()))
                        .load(val.getImg())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(image);
            }

            @Override
            public RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack(){
                return new RecyclerViewOnClickListenerHack(){
                    @Override
                    public void onClickListener(View view, int position){
                        if(position == 24)
                            Toast.makeText(getContext(), "No hay palabras", Toast.LENGTH_SHORT).show();
                        else {
                            final Intent intent = new Intent(getContext(), AbecedaryListActivity.class);
                            intent.putExtra("letter", getItem(position).getLetter());
                            intent.putExtra("list", "letter_" + position);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onLongPressClickListener(View view, int position){}
                };
            }
        };
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setNestedScrollingEnabled(false);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mSwipeRefreshLayout.setOnRefreshListener(this::getData);
        getData();
    }

    private boolean isNetworkAvailable() {
        boolean isConnected = false;
        if(getActivity() != null){
            final ConnectivityManager connectivityManager = (ConnectivityManager) Objects.requireNonNull(getContext()).getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connectivityManager != null;
            final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected())
                isConnected = true;
        }
        return isConnected;
    }

    private void getData(){
        final Cache cache = new Cache(Objects.requireNonNull(getActivity()).getCacheDir(), MainActivity.cacheSize);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    int maxStale = 60 * 60 * 24 * 7;
                    if (isNetworkAvailable())
                        request = request
                                .newBuilder()
                                .header("Cache-Control", "public, max-stale=" + 60 * 5)
                                .build();
                    else
                        request = request
                                .newBuilder()
                                .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                .build();
                    return chain.proceed(request);
                })
                .build();

        final ApiService.AbecedaryService service = ApiClient.getClient(okHttpClient).create(ApiService.AbecedaryService.class);
        final Call<ArrayList<cl.afubillaoliva.lsch.models.Abecedary>> responseCall = service.getAbecedary();

        responseCall.enqueue(new Callback<ArrayList<cl.afubillaoliva.lsch.models.Abecedary>>(){
            @Override
            public void onResponse(@NonNull Call<ArrayList<cl.afubillaoliva.lsch.models.Abecedary>> call, @NonNull Response<ArrayList<cl.afubillaoliva.lsch.models.Abecedary>> response){
                mSwipeRefreshLayout.setRefreshing(false);
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                if(response.isSuccessful()){
                    final ArrayList<cl.afubillaoliva.lsch.models.Abecedary> apiResponse = response.body();
                    adapter.addItems(apiResponse);
                } else{
                    Log.e(MainActivity.TAG, "onResponse: " + response.errorBody());
                    Toast.makeText(getContext(), "Revisa tu conexión a internet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<cl.afubillaoliva.lsch.models.Abecedary>> call, @NonNull Throwable t){
                mSwipeRefreshLayout.setRefreshing(false);
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                Log.d(MainActivity.TAG, "onFailure: " + t.getMessage());
                Toast.makeText(getContext(), "No se pudo actualizar el feed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.swipe_layout)
            mSwipeRefreshLayout.setRefreshing(true);
        return super.onOptionsItemSelected(item);
    }

}
