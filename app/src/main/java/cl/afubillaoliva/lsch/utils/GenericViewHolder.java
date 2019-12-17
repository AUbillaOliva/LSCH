package cl.afubillaoliva.lsch.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;

/***********************
 * FOR TESTING
 ***********************/

public class GenericViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> holder = new SparseArray<>();

    public GenericViewHolder(@NonNull View itemView, final RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack) {
        super(itemView);
        deepScan((ViewGroup)itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recyclerViewOnClickListenerHack != null)
                    recyclerViewOnClickListenerHack.onClickListener(v, getLayoutPosition());
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(recyclerViewOnClickListenerHack != null)
                    recyclerViewOnClickListenerHack.onLongPressClickListener(v, getLayoutPosition());
                return false;
            }
        });
    }

    private void deepScan(ViewGroup v) {
        if (v.getChildCount() == 0) return;
        for(int i=0; i< v.getChildCount(); ++i) {
            View nextChild = v.getChildAt(i);
            put(nextChild);
            if (nextChild instanceof ViewGroup) deepScan((ViewGroup)nextChild);
        }
    }

    public <T extends View> T get(Integer id) {
        return (T) holder.get(id);
    }

    public void put(View view) { put(view, view.getId()); }

    public void put(View view, Integer id) {
        if (id == -1) return;  // Mean that hasn't id
        holder.put(id, view);
    }

    public void clear() {
        holder.clear();
    }

}
