package cl.afubillaoliva.lsch.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.models.Abecedary;

public class AbecedaryListAdapter extends RecyclerView.Adapter<AbecedaryListAdapter.MyViewHolder> {

    private ArrayList<Abecedary> dataset;
    private Context context;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;

    public AbecedaryListAdapter(Context context){
        this.context = context;
        dataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_card, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Abecedary a = dataset.get(i);
        myViewHolder.textView.setText(a.getLetter());

        Glide.with(context)
                .load(a.getImg())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(myViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void addData(ArrayList<Abecedary> list){
        dataset.addAll(list);
        notifyDataSetChanged();
    }

    public void updateData(ArrayList<Abecedary> list){
        dataset.clear();
        addData(list);
        notifyDataSetChanged();
    }

    public String getLetter(int position){
        return dataset.get(position).getLetter();
    }

    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack rvoclh){
        mRecyclerViewOnClickListenerHack = rvoclh;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textView;
        private ImageView imageView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.abecedary_letter);
            imageView = itemView.findViewById(R.id.abecedary_img);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mRecyclerViewOnClickListenerHack != null)
                mRecyclerViewOnClickListenerHack.onClickListener(v, getPosition());
        }
    }
}
