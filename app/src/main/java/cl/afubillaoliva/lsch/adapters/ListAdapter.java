package cl.afubillaoliva.lsch.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.R;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> implements RecyclerViewOnClickListenerHack {

    private ArrayList<String> dataset;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;

    public ListAdapter(){
        this.dataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        String content = dataset.get(position);
        content = content.substring(0,1).toUpperCase() + content.substring(1);
        myViewHolder.textView.setText(content);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void addData(ArrayList<String> list){
        dataset.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(String s){
        dataset.add(s);
    }

    public void updateData(ArrayList<String> list){
        dataset.clear();
        addData(list);
        notifyDataSetChanged();
    }

    public String getCategory(int position){
        return dataset.get(position);
    }

    @Override
    public void onClickListener(View view, int position) {

    }
    @Override
    public void onLongPressClickListener(View view, int position) {

    }

    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack rvoclh) {
        mRecyclerViewOnClickListenerHack = rvoclh;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.list_item_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mRecyclerViewOnClickListenerHack != null)
                mRecyclerViewOnClickListenerHack.onClickListener(v, getLayoutPosition());        }
    }
}
