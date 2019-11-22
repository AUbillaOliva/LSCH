package cl.afubillaoliva.lsch.fragments;

import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.MainActivity;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.activities.AbecedaryListActivity;
import cl.afubillaoliva.lsch.adapters.AbecedaryCardListAdapter;
import cl.afubillaoliva.lsch.api.ApiClient;
import cl.afubillaoliva.lsch.api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Abecedary extends Fragment implements RecyclerViewOnClickListenerHack {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private AbecedaryCardListAdapter abecedaryListAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_layout, container,false);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view);
        mProgressBar = view.findViewById(R.id.progress_circular);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

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

    public void getData(){
        ApiService.AbecedaryService service = ApiClient.getClient().create(ApiService.AbecedaryService.class);
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
                        Toast.makeText(getContext(), "Abecedario Actualizado", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "No se pudo actualizar el Feed", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onClickListener(View view, int position) {
        Intent intent = new Intent(getContext(), AbecedaryListActivity.class);
        intent.putExtra("letter", abecedaryListAdapter.getLetter(position));
        startActivity(intent);
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
                getData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
