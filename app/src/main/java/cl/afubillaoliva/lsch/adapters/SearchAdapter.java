package cl.afubillaoliva.lsch.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.models.Word;
import cl.afubillaoliva.lsch.utils.SharedPreference;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> implements Filterable{

    private ArrayList<Word> dataset;
    private ArrayList<Word> wordListFiltered;
    private Context context;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;

    public SearchAdapter(Context context){
        this.context = context;
    }

    public void setDataset(Context context,final ArrayList<Word> dataset){
        this.context = context;
        if(this.dataset == null){
            this.dataset = dataset;
            this.wordListFiltered = dataset;
            notifyItemChanged(0, wordListFiltered.size());
        } else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return SearchAdapter.this.dataset.size();
                }

                @Override
                public int getNewListSize() {
                    return dataset.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return SearchAdapter.this.dataset.get(oldItemPosition).getTitle().equals(dataset.get(newItemPosition).getTitle());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Word newWord = SearchAdapter.this.dataset.get(oldItemPosition);
                    Word oldWord = dataset.get(newItemPosition);
                    return newWord.getTitle().equals(oldWord.getTitle());
                }
            });
            this.dataset = dataset;
            this.wordListFiltered = dataset;
            result.dispatchUpdatesTo(this);
        }
    }

    public Word get(int position){
        return wordListFiltered.get(position);
    }

    @NonNull
    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.MyViewHolder holder, int position) {
        final SharedPreference mSharedPreferences = new SharedPreference(context);
        if(wordListFiltered.get(position) != null){
            holder.title.setText(wordListFiltered.get(position).getTitle());
            if(mSharedPreferences.loadNightModeState())
                holder.image.setImageResource(R.drawable.ic_search_white_24dp);
            else
                holder.image.setImageResource(R.drawable.ic_search_black_24dp);
        }
    }

    @Override
    public int getItemCount() {
        if(dataset != null){
            int limit = 5;
            return Math.min(wordListFiltered.size(), limit);
        } else {
            return 0;
        }
    }

    private static final Pattern DIACRITICS
            = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private static String stripDiacritics(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = DIACRITICS.matcher(str).replaceAll("");
        return str;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    wordListFiltered = dataset;
                } else {
                    ArrayList<Word> filteredList = new ArrayList<>();
                    for (Word word : dataset) {
                        String title = word.getTitle().toLowerCase();
                        if(charString.length() < 4){
                            if (stripDiacritics(title).startsWith(stripDiacritics(charString.toLowerCase()))) filteredList.add(word);
                        } else {
                            if (stripDiacritics(title).contains(stripDiacritics(charString.toLowerCase()))) filteredList.add(word);
                        }
                    }
                    wordListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = wordListFiltered;
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                wordListFiltered = (ArrayList<Word>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack rvoclh) {
        mRecyclerViewOnClickListenerHack = rvoclh;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        ImageView image;

        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.list_item_text);
            image = view.findViewById(R.id.search_suggest_type);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mRecyclerViewOnClickListenerHack != null)
                mRecyclerViewOnClickListenerHack.onClickListener(v, getLayoutPosition());
        }
    }
}