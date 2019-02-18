package me.timgu.flashmemorize;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.support.annotation.RequiresApi;

import java.io.BufferedReader;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LocalDecksManager {
    private Context context;
    //mDecklist is a shared preference file that stores a list of opened decks
    public SharedPreferences mDeckList;
    private String deckListFile = "me.timgu.decklist";
    //----------------------------------------------------

    public LocalDecksManager(Context context){
        this.context = context;
        mDeckList = context.getSharedPreferences(deckListFile, Context.MODE_PRIVATE);
    }

    private String uri2Text(Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public String getDeckName(Uri uri) {
        //***Code not original, retrieved from https://developer.android.com/guide/topics/providers/document-provider#java

        // The query, since it only applies to a single document, will only return
        // one row. There's no need to filter, sort, or select fields, since we want
        // all fields for one document.
        Cursor cursor = context.getContentResolver()
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

    public void saveDeckToLocal(Deck deck, String filename) throws IOException {

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
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(bos.toByteArray());
            outputStream.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void addDeck(Uri uri) throws IOException {
        String deckName = getDeckName(uri);
        String filename = deckName + ".adk";
        String textDeck = uri2Text(uri);
        Deck deck = readTxtDeck(textDeck,deckName);

        SharedPreferences.Editor mDeckListEditor = mDeckList.edit();

        mDeckListEditor.putString(deckName,filename);
        mDeckListEditor.apply();

        saveDeckToLocal(deck,filename);
    }

    public Deck loadDeck(String filename) throws FileNotFoundException {
        FileInputStream inputStream;
        Deck deck = null;
        if (filename != null){
            inputStream = context.openFileInput(filename);

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
