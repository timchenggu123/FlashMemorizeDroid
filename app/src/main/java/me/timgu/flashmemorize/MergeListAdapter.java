package me.timgu.flashmemorize;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MergeListAdapter extends RecyclerView.Adapter<MergeListAdapter.ViewHolder> {
    private List<String> mDeckListKeys;
    private List<?> mDeckListValues;
    private Context mContext;
    private LocalDecksManager mLdm;
    private List<Boolean> mListChecked;

    public MergeListAdapter(Context context) {
        mContext = context;
        mLdm = new LocalDecksManager(context);
        mDeckListKeys = new ArrayList<>(mLdm.getDeckList().getAll().keySet());
        mDeckListValues = new ArrayList<>(mLdm.getDeckList().getAll().values());
        mListChecked = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.merge_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mDeckListKeys.get(position);
        holder.mContentView.setText(mDeckListKeys.get(position));
        holder.mCheckButton.setVisibility(View.GONE);
        mListChecked.add(false);
    }

    @Override
    public int getItemCount() {
        return mDeckListKeys.size();
    }

    public List<String> checkOutList(){
        List<String> returnList = new ArrayList<>();
        for (int i = 0; i < mListChecked.size(); i++){
            if (mListChecked.get(i)){
                returnList.add(mDeckListKeys.get(i));
            }
        }
        return returnList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{
        public final View mView;
        public final TextView mContentView;
        public String mItem;
        public Button mCheckButton;
        public Boolean mSelected ;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.merge_item_textView);
            mCheckButton = (Button) view.findViewById(R.id.merge_item_checkButton);
            mView.setOnClickListener(this);
            mSelected = false;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            if(this.mSelected){
                this.mCheckButton.setVisibility(View.GONE);
                mSelected = false;
            } else{
                this.mCheckButton.setVisibility(View.VISIBLE);
                mSelected = true;
            }
            mListChecked.set(getAdapterPosition(),mSelected);
        }
    }
}
