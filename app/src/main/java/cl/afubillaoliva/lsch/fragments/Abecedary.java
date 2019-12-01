package cl.afubillaoliva.lsch.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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
import cl.afubillaoliva.lsch.activities.AbecedaryListActivity;
import cl.afubillaoliva.lsch.adapters.AbecedaryCardListAdapter;
import cl.afubillaoliva.lsch.api.ApiClient;
import cl.afubillaoliva.lsch.api.ApiService;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Abecedary extends Fragment implements RecyclerViewOnClickListenerHack {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private AbecedaryCardListAdapter abecedaryListAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_layout, container,false);

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view);
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
        abecedaryListAdapter = new AbecedaryCardListAdapter(getContext());
        mRecyclerView.setAdapter(abecedaryListAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(layoutManager);
        abecedaryListAdapter.setRecyclerViewOnClickListenerHack(this);

        getData();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        getData();
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private boolean isNetworkAvailable() {
        if(getActivity() == null){
            return false;
        } else {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }

    private void getData(){
        try {
            Cache cache = new Cache(Objects.requireNonNull(getActivity()).getCacheDir(), MainActivity.cacheSize);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .addInterceptor(new Interceptor() {
                        @NonNull
                        @Override
                        public okhttp3.Response intercept(@NonNull Chain chain)
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

            ApiService.AbecedaryService service = ApiClient.getClient(okHttpClient).create(ApiService.AbecedaryService.class);
            Call<ArrayList<cl.afubillaoliva.lsch.models.Abecedary>> responseCall = service.getAbecedary();

            responseCall.enqueue(new Callback<ArrayList<cl.afubillaoliva.lsch.models.Abecedary>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<cl.afubillaoliva.lsch.models.Abecedary>> call, @NonNull Response<ArrayList<cl.afubillaoliva.lsch.models.Abecedary>> response) {
                    if(response.isSuccessful()){
                        ArrayList<cl.afubillaoliva.lsch.models.Abecedary> apiResponse = response.body();
                        if(abecedaryListAdapter.getItemCount() != 0){
                            mSwipeRefreshLayout.setRefreshing(false);
                            mProgressBar.setVisibility(View.GONE);
                            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            abecedaryListAdapter.updateData(apiResponse);
                        } else {
                            mSwipeRefreshLayout.setRefreshing(false);
                            mProgressBar.setVisibility(View.GONE);
                            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            abecedaryListAdapter.addData(apiResponse);
                        }
                    } else {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mProgressBar.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                        Log.e(MainActivity.TAG, "onResponse: " + response.errorBody());
                        Toast.makeText(getContext(), "Revisa tu conexion a internet", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<cl.afubillaoliva.lsch.models.Abecedary>> call, @NonNull Throwable t) {
                    Log.i(MainActivity.TAG, "onFailure: " + t.getMessage());
                    mSwipeRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "No se pudo actualizar el Feed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception error){
            Log.d(MainActivity.TAG + "Error", error.getMessage());
        }
    }

    @Override
    public void onClickListener(View view, int position) {
        if(position == 24){
            Toast.makeText(getContext(), "No hay palabras", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getContext(), AbecedaryListActivity.class);
            intent.putExtra("letter", abecedaryListAdapter.getLetter(position));
            Log.d(MainActivity.TAG, "letter: " + abecedaryListAdapter.getLetter(position));
            startActivity(intent);
        }
    }

    @Override
    public void onLongPressClickListener(View view, int position) {
        Toast.makeText(getContext(), "Long", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.swipe_layout:
                mSwipeRefreshLayout.setRefreshing(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
