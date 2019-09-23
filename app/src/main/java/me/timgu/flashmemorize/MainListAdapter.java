package me.timgu.flashmemorize;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
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
    private Boolean exportMode = false;
    private Boolean renameMode = false;
    private Boolean mergeMode = false;
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
        actionListener = (OnListActionListener) context;
    }

    public void setEditMode(Boolean editMode) {
        this.editMode = editMode;
        renameMode = false;
        exportMode = false;
        notifyDataSetChanged();
    }

    public void setExportMode(Boolean exportMode){
        this.exportMode = exportMode;
        renameMode = false;
        editMode = false;
    }

    public void setRenameMode(Boolean renameMode){
        this.renameMode = renameMode;
        editMode = false;
        exportMode = false;
        notifyDataSetChanged();
    }

    public void setMergeMode(Boolean mergeMode){
        this.mergeMode = mergeMode;
        editMode = false;
        exportMode = false;
        notifyDataSetChanged();

    }

    public Boolean getRenameMode(){
        return renameMode;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{
        public final TextView wordItemView;
        public final Button deleteButton;
        public final EditText editView;
        public final ImageButton saveButton;
        public final Button mergeButton;
        final MainListAdapter mAdapter;

        public ItemViewHolder(View itemView, MainListAdapter adapter){
            super(itemView);
            wordItemView = itemView.findViewById(R.id.main_list_text);
            deleteButton = itemView.findViewById(R.id.main_list_delete);
            editView = itemView.findViewById(R.id.main_list_edit);
            saveButton = itemView.findViewById(R.id.main_list_save);
            mergeButton = itemView.findViewById(R.id.main_list_merge);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            deleteButton.setVisibility(View.GONE);
            deleteButton.setOnClickListener(this);
            editView.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            saveButton.setOnClickListener(this);
            mergeButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int mPosition = getLayoutPosition();
            if (v.getId() == R.id.main_list_delete) {
                String deckName = mDeckListKeys.get(mPosition);
                actionListener.deleteDeck(deckName);

                mDeckListKeys.remove(mPosition);
                mDeckListValues.remove(mPosition);

                notifyDataSetChanged();
            }else if (v.getId() == R.id.main_list_save) {
                LocalDecksManager ldm = new LocalDecksManager(context);
                EditText editView = itemView.findViewById(R.id.main_list_edit);
                ldm.renameDeck(mDeckListKeys.get(mPosition), editView.getText().toString());
                renameMode = false;
                updateDeckList();
            }else if (exportMode) {
                LocalDecksManager ldm = new LocalDecksManager(context);
                String deckName = (String) mDeckListKeys.get(mPosition);
                try {
                    ldm.exportDeck(deckName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                exportMode = false;
            }else if (mergeMode){
                //TODO
            } else if (!flashcard_launched && ! renameMode){
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
        String current = (String) mDeckListKeys.get(i);
        itemViewHolder.wordItemView.setText(current);

        if (editMode){
            itemViewHolder.deleteButton.setVisibility(View.VISIBLE);
            itemViewHolder.deleteButton.setOnClickListener(itemViewHolder);
        } else{
            itemViewHolder.deleteButton.setVisibility(View.GONE);
        }

        if (renameMode){
            itemViewHolder.editView.setVisibility(View.VISIBLE);
            itemViewHolder.editView.setText(current);
            itemViewHolder.saveButton.setVisibility(View.VISIBLE);
            itemViewHolder.wordItemView.setVisibility(View.GONE);
        } else{
            itemViewHolder.editView.setVisibility(View.GONE);
            itemViewHolder.saveButton.setVisibility(View.GONE);
            itemViewHolder.wordItemView.setVisibility(View.VISIBLE);
        }

        if (mergeMode) {
            itemViewHolder.mergeButton.setVisibility(View.VISIBLE);
        }else{
            itemViewHolder.mergeButton.setVisibility(View.GONE);
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
