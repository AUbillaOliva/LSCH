package cl.afubillaoliva.lsch.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cl.afubillaoliva.lsch.R;

public class WordElementsListAdapter extends RecyclerView.Adapter<WordElementsListAdapter.MyViewHolder> {

    private ArrayList<String> dataset;

    public WordElementsListAdapter(){
        this.dataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public WordElementsListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.word_elemets_list, viewGroup, false);
        return new WordElementsListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordElementsListAdapter.MyViewHolder myViewHolder, int position) {
        String content = dataset.get(position);
        //content = content.substring(0,1).toUpperCase() + content.substring(1);
        myViewHolder.listText.setText(content);
        myViewHolder.listNumber.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void addData(ArrayList<String> list){
        dataset.addAll(list);
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView listText, listNumber;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            listText = itemView.findViewById(R.id.element_text);
            listNumber = itemView.findViewById(R.id.element_number);
        }
    }

}
