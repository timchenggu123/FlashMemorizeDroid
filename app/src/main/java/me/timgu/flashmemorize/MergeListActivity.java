package me.timgu.flashmemorize;

import android.app.Activity;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.IOException;
import java.util.List;

public class MergeListActivity extends AppCompatActivity
    implements NewDeckDialogueFragment.NewDeckDialogueListener {
    private RecyclerView mRecyclerView;
    private MergeListAdapter mAdapter;
    private LocalDecksManager mLdm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_list);
        mLdm = new LocalDecksManager(this);
        mRecyclerView = findViewById(R.id.mergelist_recyclerview);
        mAdapter = new MergeListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    public void confirm(View view) {
        DialogFragment dialog = new NewDeckDialogueFragment();
        dialog.show(getSupportFragmentManager(),"NewDeckDialogue");
    }

    public void cancel(View view){
        this.finish();
    }

    @Override
    public void onNewDeckDialogPositiveClick(DialogFragment dialog, String msg) throws IOException {
        List<String> mergeList = mAdapter.checkOutList();
        mLdm.mergeDecks(mergeList,msg);
        dialog.dismiss();
        this.finish();
    }

    @Override
    public void onNewDeckDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }
}
