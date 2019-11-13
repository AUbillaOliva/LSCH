package cl.afubillaoliva.lsch.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.AbecedaryListAdapter;
import cl.afubillaoliva.lsch.api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Abecedary extends Fragment implements RecyclerViewOnClickListenerHack {

    private static final String TAG = "API_RESPONSE";
    private static final String BASE_URL = "http://192.168.0.16:5000/api/";
    private SwipeRefreshLayout swipeRefreshLayout;
    private Retrofit retrofit;
    private AbecedaryListAdapter abecedaryListAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.abecedary_fragment, container,false);
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        abecedaryListAdapter = new AbecedaryListAdapter(getContext());
        mRecyclerView.setAdapter(abecedaryListAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(layoutManager);
        abecedaryListAdapter.setRecyclerViewOnClickListenerHack(this);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        getData();
        return view;
    }

    public void getData(){
        ApiService.LettersService service = retrofit.create(ApiService.LettersService.class);
        Call<ArrayList<cl.afubillaoliva.lsch.models.Abecedary>> responseCall = service.getAbecedary();

        responseCall.enqueue(new Callback<ArrayList<cl.afubillaoliva.lsch.models.Abecedary>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<cl.afubillaoliva.lsch.models.Abecedary>> call, @NonNull Response<ArrayList<cl.afubillaoliva.lsch.models.Abecedary>> response) {
                if(response.isSuccessful()){
                    ArrayList<cl.afubillaoliva.lsch.models.Abecedary> apiResponse = response.body();
                    if(abecedaryListAdapter.getItemCount() != 0){
                        abecedaryListAdapter.updateData(apiResponse);
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), "Abecedario Actualizado", Toast.LENGTH_SHORT).show();
                    } else {
                        abecedaryListAdapter.addData(apiResponse);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    Log.e(TAG, "onResponse: " + response.errorBody());
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<cl.afubillaoliva.lsch.models.Abecedary>> call, @NonNull Throwable t) {
                Log.i(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onClickListener(View view, int position) {
        Toast.makeText(getContext(), "" + abecedaryListAdapter.getLetter(position), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongPressClickListener(View view, int position) {
        Toast.makeText(getContext(), "Long", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.swipe_layout:
                swipeRefreshLayout.setRefreshing(true);
                getData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
