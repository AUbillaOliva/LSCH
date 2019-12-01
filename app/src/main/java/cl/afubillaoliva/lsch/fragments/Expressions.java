package cl.afubillaoliva.lsch.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.activities.ExpressionsListActivity;
import cl.afubillaoliva.lsch.adapters.ListAdapter;
import cl.afubillaoliva.lsch.api.ApiClient;
import cl.afubillaoliva.lsch.api.ApiService;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Expressions extends Fragment implements RecyclerViewOnClickListenerHack {

    private static final String TAG = "API_RESPONSE";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private ListAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        setRetainInstance(true);

        View view = inflater.inflate(R.layout.fragment_layout, viewGroup, false);

        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        mProgressBar = view.findViewById(R.id.progress_circular);
        NestedScrollView nestedScrollView = view.findViewById(R.id.nested);

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
        adapter = new ListAdapter();
        adapter.setRecyclerViewOnClickListenerHack(this);
        mRecyclerView.setAdapter(adapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        getData();

        return view;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void getData(){
        try {
            Cache cache = new Cache(Objects.requireNonNull(getActivity()).getCacheDir(), MainActivity.cacheSize);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .addInterceptor(new Interceptor() {
                        @NonNull
                        @Override
                        public okhttp3.Response intercept(@NonNull Interceptor.Chain chain)
                                throws IOException {
                            Request request = chain.request();
                            if (!isNetworkAvailable()) {
                                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale \
                                request = request
                                        .newBuilder()
                                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                        .build();
                            }
                            return chain.proceed(request);
                        }
                    })
                    .build();

            ApiService.ExpressionsCategoryService service = ApiClient.getClient(okHttpClient).create(ApiService.ExpressionsCategoryService.class);
            Call<ArrayList<String>> responseCall = service.getExpressionsCategories();

            responseCall.enqueue(new Callback<ArrayList<String>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<String>> call, @NonNull Response<ArrayList<String>> response) {
                    if(response.isSuccessful()){
                        ArrayList<String> apiResponse = response.body();

                        if(adapter.getItemCount() != 0){
                            mSwipeRefreshLayout.setRefreshing(false);
                            mProgressBar.setVisibility(View.GONE);
                            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            adapter.updateData(apiResponse);
                            Toast.makeText(getContext(), "Abecedario Actualizado", Toast.LENGTH_SHORT).show();
                        } else {
                            mSwipeRefreshLayout.setRefreshing(false);
                            mProgressBar.setVisibility(View.GONE);
                            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            adapter.addData(apiResponse);
                        }
                    } else {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mProgressBar.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                        Log.e(TAG, "onResponse: " + response.errorBody());
                        Toast.makeText(getContext(), "No se pudo actualizar el Feed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<String>> call, @NonNull Throwable t) {
                    Log.i(TAG, "onFailure: " + t.getMessage());
                    mSwipeRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "No se pudo actualizar el Feed", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e){
            Log.d(MainActivity.TAG + "Error", e.getMessage());

        }
    }

    @Override
    public void onClickListener(View view, int position) {
        Intent intent = new Intent(getContext(), ExpressionsListActivity.class);
        intent.putExtra("expression", adapter.getCategory(position));
        startActivity(intent);
    }

    @Override
    public void onLongPressClickListener(View view, int position) {

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
