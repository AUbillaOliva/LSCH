package cl.afubillaoliva.lsch.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;

public abstract class GenericAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<T> items;
    private Context context;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;

    public abstract RecyclerView.ViewHolder setViewHolder(ViewGroup parent, RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack);

    public abstract void onBindData(RecyclerView.ViewHolder holder, T val);

    public abstract RecyclerViewOnClickListenerHack onGetRecyclerViewOnClickListenerHack();

    protected GenericAdapter(Context context, ArrayList<T> items){
        this.context = context;
        this.items = items;
        this.mRecyclerViewOnClickListenerHack = onGetRecyclerViewOnClickListenerHack();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return setViewHolder(parent, mRecyclerViewOnClickListenerHack);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        onBindData(holder,items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItems( ArrayList<T> savedCardItems){
        items = savedCardItems;
        this.notifyDataSetChanged();
    }

    public T getItem(int position){
        return items.get(position);
    }
}
