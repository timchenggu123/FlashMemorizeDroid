package me.timgu.flashmemorize;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MainActivity extends AppCompatActivity
        implements MainListAdapter.OnListActionListener,
        NewDeckDialogueFragment.NewDeckDialogueListener {
    private static final int READ_REQUEST_CODE = 6936;
    public static final String EXTRA_FILENAME =
            "me.timgu.flashmemorize.extra.FILENAME";
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS =
            66;
    public static final int MERGE_LIST_REQUEST_CODE = 1313;

    //Declare RecyclerView
    private RecyclerView mRecyclerView;
    private MainListAdapter mAdapter;

    //Declare helper classes
    private LocalDecksManager mDecksManager;

    //Declare reference constants
    private boolean editMode = true;
    private boolean renameMode = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDecksManager = new LocalDecksManager(this);

        //initiate task bar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        //initiate recycler view
        mRecyclerView = findViewById(R.id.main_recyclerview);
        mAdapter = new MainListAdapter(this,mDecksManager.getDeckList().getAll());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onResume(){
        super.onResume();
        mAdapter.flashcard_launched = false;
        mAdapter.updateDeckList();
    }


    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    //----------------------Accessors--------------------------------------

    private Context getContext(){
        return this;
    }

    //----------------------Utility-----------------------------------------

    public void performFileSearch(View v){
        requestPermission();
        Intent  intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent,READ_REQUEST_CODE);
    }



    private class LoadDeckTask extends AsyncTask<Uri,Void,Void> {
        protected Void doInBackground(Uri...uri){
            for (Uri u: uri){
                try {
                    mDecksManager.addDeck(u);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(Void v){
            mAdapter.updateDeckList();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData){
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri uri = null;
            if(resultData != null){
                uri = resultData.getData();

                    //processing the uri to file
                    String deckName = mDecksManager.getDeckName(uri);
                    String filename = mDecksManager.getDeckList().getString(deckName,null);

                    if (!(deckName.substring(deckName.length() -4).equals(".txt") ||
                            deckName.substring(deckName.length() - 5).equals(".json"))) {
                        Toast.makeText(this,"File Format Not Supported",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    new LoadDeckTask().execute(uri);
                    //if (filename != null) {
                        //launchflashcard(filename);
                    //}
            }
        }
    }

    private class LaunchDeckTask extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            for (String filename: strings){
                Intent intent = new Intent(getContext(), FlashcardActivity.class);
                intent.putExtra(EXTRA_FILENAME,filename);
                startActivity(intent);
            }
            return null;
        }

    }


    public void editDeckList(MenuItem item) {
        mAdapter.setEditMode(editMode);
        editMode = !editMode;
    }

    public void exportAdk(MenuItem item) {
        mAdapter.setExportMode(true);
        Toast.makeText(this, "Choose a deck to export...", Toast.LENGTH_SHORT).show();
    }

    public void newDeck(MenuItem item) {
        DialogFragment dialog = new NewDeckDialogueFragment();
        dialog.show(getSupportFragmentManager(),"NewDeckDialogue");
    }

    public void mergeDecks(MenuItem item) {
        Intent intent = new Intent(this, MergeListActivity.class);
        intent.putExtra("REQUEST_CODE", MERGE_LIST_REQUEST_CODE);
        startActivity(intent);
    }
    private class CreateNewDeck extends AsyncTask<String,Void,Void> {
        protected Void doInBackground(String...deckName){
            for (String s: deckName){
                try {
                    mDecksManager.newDeck(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(Void v){
            mAdapter.updateDeckList();
        }
    }

    @Override
    public void onNewDeckDialogPositiveClick(DialogFragment dialog, String msg) throws IOException {
        new CreateNewDeck().execute(msg);
        dialog.dismiss();
    }
    @Override
    public void onNewDeckDialogNegativeClick(DialogFragment dialog){
        dialog.dismiss();
    }

    public void renameDeck(MenuItem item) {
        mAdapter.setRenameMode(! mAdapter.getRenameMode());
    }


    // -------------------for MainListAdapter.OnListActionListener-----------------------------
    @Override
    public void launchDeck(String filename) {
        new LaunchDeckTask().execute(filename);
    }

    @Override
    public void deleteDeck(String deckName) {
        mDecksManager.removeDeck(deckName);
    }
    // ------------------end for MainListAdapter.OnListActionListener--------------------------
    private void requestPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

}


