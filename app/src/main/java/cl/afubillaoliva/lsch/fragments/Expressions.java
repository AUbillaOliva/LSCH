package cl.afubillaoliva.lsch.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.activities.ExpressionsListActivity;
import cl.afubillaoliva.lsch.adapters.GenericAdapter;
import cl.afubillaoliva.lsch.api.ApiClient;
import cl.afubillaoliva.lsch.api.ApiService;
import cl.afubillaoliva.lsch.utils.GenericViewHolder;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Expressions extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private GenericAdapter<String> adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        setRetainInstance(true);
        getData();

        final View view = inflater.inflate(R.layout.fragment_layout, viewGroup, false);

        final RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        mProgressBar = view.findViewById(R.id.progress_circular);
        final NestedScrollView nestedScrollView = view.findViewById(R.id.nested);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    int initialscrollY = 0;
                    if (scrollY > initialscrollY){
                        ((MainActivity) Objects.requireNonNull(getActivity())).getAppBarLayout().setElevation(8);
                    } else if(scrollY < oldScrollY - scrollY){
                        ((MainActivity) Objects.requireNonNull(getActivity())).getAppBarLayout().setElevation(2);
                    }
                }
            });
        }

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        adapter = new GenericAdapter<String>() {
            @Override
            public RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack) {
                final View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
                return new GenericViewHolder(view, recyclerViewOnClickListenerHack);
            }

            @Override
            public void onBindData(RecyclerView.ViewHolder holder, String val, int position) {
                final GenericViewHolder viewHolder = (GenericViewHolder) holder;
                final TextView title = viewHolder.get(R.id.list_item_text);
                final String content = val.substring(0,1).toUpperCase() + val.substring(1).toLowerCase();
                title.setText(content);
            }

            @Override
            public RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack() {
                return new RecyclerViewOnClickListenerHack() {
                    @Override
                    public void onClickListener(View view, int position) {
                        Intent intent = new Intent(getContext(), ExpressionsListActivity.class);
                        intent.putExtra("expression", getItem(position));
                        startActivityForResult(intent, getTargetRequestCode());
                    }

                    @Override
                    public void onLongPressClickListener(View view, int position) {

                    }
                };
            }
        };
        mRecyclerView.setAdapter(adapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        return view;
    }

    private boolean isNetworkAvailable() {
        boolean isConnected = false;
        if(getActivity() != null){
            ConnectivityManager connectivityManager = (ConnectivityManager) Objects.requireNonNull(getContext()).getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()){
                isConnected = true;
            }
        }
        return isConnected;
    }

    private void getData(){
        final Cache cache = new Cache(Objects.requireNonNull(getActivity()).getCacheDir(), MainActivity.cacheSize);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public okhttp3.Response intercept(@NonNull Interceptor.Chain chain)
                            throws IOException {
                        Request request = chain.request();
                        int maxStale = 60 * 60 * 24 * 7; // tolerate 4-weeks stale \
                        if (isNetworkAvailable()) {
                            request = request
                                    .newBuilder()
                                    .header("Cache-Control", "public, max-stale=" + 60 * 5)
                                    .build();
                            Log.d(MainActivity.TAG, "EXPRESSIONS: using cache that was stored 5 minutes ago");
                        } else {
                            request = request
                                    .newBuilder()
                                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                    .build();
                            Log.d(MainActivity.TAG, "EXPRESSIONS: using cache that was stored 7 days ago");
                        }
                        return chain.proceed(request);
                    }
                })
                .build();

        final ApiService.ExpressionsCategoryService service = ApiClient.getClient(okHttpClient).create(ApiService.ExpressionsCategoryService.class);
        final Call<ArrayList<String>> responseCall = service.getExpressionsCategories();

        responseCall.enqueue(new Callback<ArrayList<String>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<String>> call, @NonNull Response<ArrayList<String>> response) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                    if(response.isSuccessful()){
                        ArrayList<String> apiResponse = response.body();
                        adapter.addItems(apiResponse);
                    } else {
                        Log.e(MainActivity.TAG, "onResponse: " + response.errorBody());
                        Toast.makeText(getContext(), "Revisa tu conexión a internet", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<String>> call, @NonNull Throwable t) {
                    Log.i(MainActivity.TAG, "onFailure: " + t.getMessage());
                    mSwipeRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "No se pudo actualizar el feed", Toast.LENGTH_SHORT).show();
                }
            });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.swipe_layout:
                mSwipeRefreshLayout.setRefreshing(true);
                getData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
