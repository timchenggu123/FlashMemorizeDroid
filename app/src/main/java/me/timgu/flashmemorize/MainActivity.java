package me.timgu.flashmemorize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.nio.file.FileVisitOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MainActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 6936;
    public static final String EXTRA_FILENAME =
            "me.timgu.flashmemorize.extra.FILENAME";

    //mDecklist is a shared preference file that stores a list of opened decks
    public SharedPreferences mDeckList;
    private String deckListFile = "me.timgu.decklist";
    //----------------------------------------------------

    //Initiate RecyclerView
    private RecyclerView mRecyclerView;
    private MainListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDeckList = getSharedPreferences(deckListFile,MODE_PRIVATE);

        //initiate task bar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        //initiate recycler view
        mRecyclerView = findViewById(R.id.main_recyclerview);
        mAdapter = new MainListAdapter(this,mDeckList.getAll());
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
                    String deckName = getDeckName(uri);
                    String filename = mDeckList.getString(deckName,null);
                    addDeck(uri);
                    mDeckList.getString(deckName,null);

                    if (filename != null) {
                        launchflashcard(filename);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String uri2Text(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null){
            line += (char) 10;
            stringBuilder.append(line);
        }
        reader.close();
        return stringBuilder.toString();
    }

    private void launchflashcard (String filename){

        Toast.makeText(this, "Deck Loaded", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,flashcard.class);
        intent.putExtra(EXTRA_FILENAME,filename);
        startActivity(intent);
    }


    public Deck readTxtDeck(String deck,String name){
        Scanner scanner = new Scanner(deck);
        String line;
        boolean b;
        int indx;
        String front;
        String back;
        int ID = 0;
        List<Card> all_cards = new ArrayList<>();

        while(scanner.hasNextLine()){
            line = scanner.nextLine();
            indx = line.indexOf((char) 9);
            if (indx >=0){
                front = line.substring(0,indx);
                back = line.substring(indx+1); //will this work?

                //front = front.replaceAll(getString(R.string.new_line_keyword), Character.toString((char) 10)); //will this work?
                //back = back.replaceAll(getString(R.string.new_line_keyword), Character.toString((char) 10)); //will this work?

                all_cards.add(new Card(front,back,ID));
                ID ++;
            }
        }
        Deck dk = new Deck(name,all_cards);
        return dk;
    }

    public String getDeckName(Uri uri) {
        //***Code not original, retrieved from https://developer.android.com/guide/topics/providers/document-provider#java

        // The query, since it only applies to a single document, will only return
        // one row. There's no need to filter, sort, or select fields, since we want
        // all fields for one document.
        Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null, null);
        String displayName = null;

        try {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name".  This is
                // provider-specific, and might not necessarily be the file name.
                displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

            }
        } finally {
            cursor.close();
        }
        return displayName;
    }

    private void saveDeckToLocal(Deck deck, String filename) throws IOException {

        //Converting deck object to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;

        try{
            out = new ObjectOutputStream(bos);
            out.writeObject(deck);
            out.flush();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }

        //writing byte array to file
        FileOutputStream outputStream;

        try{
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(bos.toByteArray());
            outputStream.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void addDeck(Uri uri) throws IOException {
        String deckName = getDeckName(uri);
        String filename = deckName + ".adk";
        String textDeck = uri2Text(uri);
        Deck deck = readTxtDeck(textDeck,deckName);

        SharedPreferences.Editor mDeckListEditor = mDeckList.edit();

        mDeckListEditor.putString(deckName,filename);
        mDeckListEditor.apply();

        saveDeckToLocal(deck,filename);
    }

    private Deck loadDeck(String filename) throws FileNotFoundException {
        FileInputStream inputStream;
        Deck deck = null;
        if (filename != null){
            inputStream = openFileInput(filename);

            ObjectInput in = null;
            try {
                in = new ObjectInputStream(inputStream);
                deck = (Deck) in.readObject();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                    // ignore close exception
                }
            }
        }else{
            return null;
        }

        return deck;
    }

}
