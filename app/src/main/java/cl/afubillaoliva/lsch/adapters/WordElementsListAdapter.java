package cl.afubillaoliva.lsch.adapters;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.R;

public class WordElementsListAdapter extends RecyclerView.Adapter<WordElementsListAdapter.MyViewHolder> {

    private ArrayList<String> dataset;
    private boolean numberedState = true, linkState = false;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;
    private Context context;

    public WordElementsListAdapter(Context context){
        this.dataset = new ArrayList<>();
        this.context = context;
    }

    public WordElementsListAdapter(){
        this.dataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public WordElementsListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        return new WordElementsListAdapter.MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.word_elemets_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WordElementsListAdapter.MyViewHolder myViewHolder, int position){
        final String content = dataset.get(position);
        //content = content.substring(0,1).toUpperCase() + content.substring(1);
        myViewHolder.listText.setText(content);
        myViewHolder.listNumber.setText(String.valueOf(position + 1));
        myViewHolder.isNumbered(isNumbered());
        myViewHolder.isLinked(isLinked());
    }

    private boolean isNumbered(){
        return numberedState;
    }

    public void setNumbered(boolean numberedState){
        this.numberedState = numberedState;
    }

    private boolean isLinked() {
        return linkState;
    }

    public void setLinks(boolean linkState) {
        this.linkState = linkState;
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void addData(ArrayList<String> list){
        for (String item : list){
            final String content = item.substring(0,1).toUpperCase() + item.substring(1).toLowerCase();
            dataset.add(content);
        }
        notifyDataSetChanged();
    }

    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack rvoclh) {
        mRecyclerViewOnClickListenerHack = rvoclh;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView listText, listNumber;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            listText = itemView.findViewById(R.id.element_text);
            listNumber = itemView.findViewById(R.id.element_number);
            itemView.setOnClickListener(this);
        }

        void isLinked(boolean state){
            if(state)
                listText.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

        void isNumbered(boolean state){
            if (state)
                listNumber.setVisibility(View.VISIBLE);
            else
                listNumber.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v){
            if(mRecyclerViewOnClickListenerHack != null)
                mRecyclerViewOnClickListenerHack.onClickListener(v, getLayoutPosition());
        }
    }

}
