package me.timgu.flashmemorize;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MainActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 6936;
    public static final String EXTRA_FILENAME =
            "me.timgu.flashmemorize.extra.FILENAME";

    //Declare RecyclerView
    private RecyclerView mRecyclerView;
    private MainListAdapter mAdapter;

    //Declare helper classes
    private LocalDecksManager mDecksManager;


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
        mAdapter = new MainListAdapter(this,mDecksManager.mDeckList.getAll());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }


    public void performFileSearch(MenuItem item){
        Intent  intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        startActivityForResult(intent,READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData){
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri uri = null;
            if(resultData != null){
                uri = resultData.getData();

                try {
                    //processing the uri to file
                    String deckName = mDecksManager.getDeckName(uri);
                    String filename = mDecksManager.mDeckList.getString(deckName,null);
                    mDecksManager.addDeck(uri);
                    mDecksManager.mDeckList.getString(deckName,null);

                    if (filename != null) {
                        launchflashcard(filename);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void launchflashcard (String filename){

        Toast.makeText(this, "Deck Loaded", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,flashcard.class);
        intent.putExtra(EXTRA_FILENAME,filename);
        startActivity(intent);
    }


}
