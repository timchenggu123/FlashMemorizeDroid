package me.timgu.flashmemorize;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private Context context;
    private Boolean editMode =false;
    public Boolean flashcard_launched = false;

    public interface OnListActionListener{
        public void launchDeck(String filename);
        public void deleteDeck(String deckName);
    }

    private OnListActionListener actionListener;

    public MainListAdapter(Context context, Map<String,?> deckList){
        mInflater = LayoutInflater.from(context); //what the heck does this mean?\
        this.mDeckListKeys= new ArrayList<> (deckList.keySet());
        this.mDeckListValues= new ArrayList<>(deckList.values());
        this.context = context;
    }

    public void editDeckList(Boolean editMode) {
        this.editMode = editMode;
        notifyDataSetChanged();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{
        public final TextView wordItemView;
        public final Button deleteButton;
        final MainListAdapter mAdapter;

        public ItemViewHolder(View itemView, MainListAdapter adapter){
            super(itemView);
            wordItemView = itemView.findViewById(R.id.main_list_text);
            deleteButton = itemView.findViewById(R.id.main_list_delete);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            deleteButton.setVisibility(View.GONE);
            deleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int mPosition = getLayoutPosition();

            if (v.getId() == R.id.main_list_delete){
                String deckName = mDeckListKeys.get(mPosition);
                actionListener.deleteDeck(deckName);

                mDeckListKeys.remove(mPosition);
                mDeckListValues.remove(mPosition);

                notifyDataSetChanged();
            }else if (!flashcard_launched){
                String filename = (String) mDeckListValues.get(mPosition);
                Toast.makeText(context, "Loading Deck...", Toast.LENGTH_SHORT).show();

                flashcard_launched = true; //to prevent flashcard_launched being called twice
                actionListener.launchDeck(filename);
            }

        }

        @Override
        public boolean onLongClick(View v) {
            editMode = !editMode;
            notifyDataSetChanged();
            return true;
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

        if (editMode){
            itemViewHolder.deleteButton.setVisibility(View.VISIBLE);
            itemViewHolder.deleteButton.setOnClickListener(itemViewHolder);
        } else{
            itemViewHolder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        int count = mDeckListValues.size();
        return count;
    }

    public void updateDeckList(){
        LocalDecksManager ldm = new LocalDecksManager(context);
        Map<String,?> deckList = ldm.getDeckList().getAll();

        this.mDeckListKeys= new ArrayList<> (deckList.keySet());
        this.mDeckListValues= new ArrayList<>(deckList.values());
        this.notifyDataSetChanged();//can be more specific, but this is safest
    }
}
