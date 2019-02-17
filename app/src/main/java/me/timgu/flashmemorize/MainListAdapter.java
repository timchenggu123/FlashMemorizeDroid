package me.timgu.flashmemorize;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainListAdapter extends
    RecyclerView.Adapter<MainListAdapter.ItemViewHolder>{
    private List<String> mDeckListKeys;
    private List<?> mDeckListValues;
    private LayoutInflater mInflater;
    public static final String EXTRA_FILENAME =
            "me.timgu.flashmemorize.extra.FILENAME";

    public MainListAdapter(Context context, Map<String,?> deckList){
        mInflater = LayoutInflater.from(context); //what the heck does this mean?\
        this.mDeckListKeys= new ArrayList<> (deckList.keySet());
        this.mDeckListValues= new ArrayList<>(deckList.values());
    }


    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView wordItemView;
        final MainListAdapter mAdapter;

        public ItemViewHolder(View itemView, MainListAdapter adapter){
            super(itemView);
            wordItemView = itemView.findViewById(R.id.main_list_item);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int mPosition = getLayoutPosition();
            String filename = (String) mDeckListValues.get(mPosition);
            Intent intent = new Intent(v.getContext(),flashcard.class);
            intent.putExtra(EXTRA_FILENAME,filename);
            v.getContext().startActivity(intent);
        }
    }
    @NonNull
    @Override
    public MainListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.main_list_item,parent,false);
        return new ItemViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull MainListAdapter.ItemViewHolder itemViewHolder, int i) {
        String mCurrent = (String) mDeckListKeys.get(i);
        itemViewHolder.wordItemView.setText(mCurrent);
    }

    @Override
    public int getItemCount() {
        int count = mDeckListValues.size();
        return count;
    }

    public void updateDeckList(Map<String,?> deckList){
        this.mDeckListKeys= new ArrayList<> (deckList.keySet());
        this.mDeckListValues= new ArrayList<>(deckList.values());
        this.notifyDataSetChanged();//can be more specific, but this is safest
    }
}
