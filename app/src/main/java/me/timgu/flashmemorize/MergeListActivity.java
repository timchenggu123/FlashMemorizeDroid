package me.timgu.flashmemorize;

import android.content.Intent;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MergeListActivity extends AppCompatActivity
    implements NewDeckDialogueFragment.NewDeckDialogueListener {
    private RecyclerView mRecyclerView;
    private MergeListAdapter mAdapter;
    private LocalDecksManager mLdm;
    private int mRequestCode;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mRequestCode = intent.getIntExtra("REQUEST_CODE", 0);
        setContentView(R.layout.activity_merge_list);
        mLdm = new LocalDecksManager(this);
        mRecyclerView = findViewById(R.id.mergelist_recyclerview);
        mAdapter = new MergeListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    public void confirm(View view) throws IOException {
        if (mRequestCode == MainActivity.MERGE_LIST_REQUEST_CODE) {
            DialogFragment dialog = new NewDeckDialogueFragment();
            dialog.show(getSupportFragmentManager(), "NewDeckDialogue");
        } else if (mRequestCode == FlashcardActivity.MERGE_LIST_REQUEST_CODE){
            Intent intent = new Intent();
            intent.putStringArrayListExtra("Deck_List",(ArrayList<String>) mAdapter.checkOutList());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void cancel(View view){
        this.finish();
    }

    @Override
    public void onNewDeckDialogPositiveClick(DialogFragment dialog, String msg) throws IOException {
        //this method will only be called if the merge list activity is called from the main activity.
        List<String> mergeList = mAdapter.checkOutList();
        Intent intent = new Intent();
        intent.putStringArrayListExtra("Deck_List",(ArrayList<String>) mAdapter.checkOutList());
        intent.putExtra("Deck_Name", msg);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onNewDeckDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }
}
