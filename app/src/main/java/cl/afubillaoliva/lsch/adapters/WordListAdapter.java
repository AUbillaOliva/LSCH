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
import cl.afubillaoliva.lsch.models.Word;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.MyViewHolder> implements RecyclerViewOnClickListenerHack {

    public ArrayList<Word> dataset;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;

    public WordListAdapter(){
        this.dataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public WordListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        return new WordListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordListAdapter.MyViewHolder myViewHolder, int position) {
        Word content = dataset.get(position);
        myViewHolder.textView.setText(content.getTitle());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void clear(){
        dataset.clear();
        notifyDataSetChanged();
    }

    public void addData(ArrayList<Word> list){
        dataset.addAll(list);
        notifyDataSetChanged();
    }

    public void updateData(ArrayList<Word> list){
        dataset.clear();
        addData(list);
        notifyDataSetChanged();
    }

    public String getTitle(int position){
        return dataset.get(position).getTitle();
    }

    public Word getItem(int position){
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