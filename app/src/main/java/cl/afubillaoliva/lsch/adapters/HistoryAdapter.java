package cl.afubillaoliva.lsch.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.utils.SharedPreference;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder>{

    private ArrayList<String> dataset;
    private Context context;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;

    public HistoryAdapter(Context context){
        this.context = context;
        this.dataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_list_item, viewGroup, false);
        return new HistoryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.MyViewHolder myViewHolder, int position) {
        final SharedPreference sharedPreference = new SharedPreference(context);
        if(sharedPreference.loadNightModeState()){
            myViewHolder.textView.setText(dataset.get(position));
            myViewHolder.imageView.setImageResource(R.drawable.ic_access_time_white_24dp);
        } else {
            myViewHolder.textView.setText(dataset.get(position));
            myViewHolder.imageView.setImageResource(R.drawable.ic_access_time_black_24dp);
        }

    }

    @Override
    public int getItemCount() {
        int limit = 5;
        if(dataset.size() > limit)
            return limit;
        else
            return dataset.size();
    }

    public void addData(ArrayList<String> s){
        dataset.addAll(s);
        notifyDataSetChanged();
    }

    public void clear(){
        dataset.clear();
        notifyDataSetChanged();
    }

    public String get(int position){
        return dataset.get(position);
    }

    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack rvoclh) {
        mRecyclerViewOnClickListenerHack = rvoclh;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private TextView textView;
        private ImageView imageView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.list_item_text);
            imageView = itemView.findViewById(R.id.search_suggest_type);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mRecyclerViewOnClickListenerHack != null)
                mRecyclerViewOnClickListenerHack.onClickListener(v, getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            if(mRecyclerViewOnClickListenerHack != null)
                mRecyclerViewOnClickListenerHack.onLongPressClickListener(v, getLayoutPosition());
            return false;
        }
    }
}