package me.timgu.flashmemorize;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
import java.io.OutputStreamWriter;
import java.io.Writer;
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

    }

    public SharedPreferences getDeckList(){
        mDeckList = context.getSharedPreferences(deckListFile, Context.MODE_PRIVATE);
        return mDeckList;
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

    public Deck readTxtDeck(String deck,String name,Uri uri){
        Scanner scanner = new Scanner(deck);
        String line;
        int indx;
        String str = uri.getPath();
        File f = new File(str);
        f = f.getParentFile();
        String parentFolder = f.toString();
        parentFolder = parentFolder.replaceAll("/document/raw:","");



        String front;
        String back;
        int ID = 0;
        List<Card> all_cards = new ArrayList<>();


        while(scanner.hasNextLine()){
            line = scanner.nextLine();
            indx = line.indexOf((char) 9);
            if (indx >=0){
                front = line.substring(0,indx);

                File front_pic_file = null;
                int bracket1 = front.indexOf("{");
                int bracket2 = front.indexOf("}");
                if (bracket1 >= 0 && bracket2 >= 0){
                    String file = front.substring(bracket1 + 1, bracket2);
                    file = parentFolder + "/" + file;
                    front_pic_file = new File(file);
                }

                File back_pic_file = null;
                back = line.substring(indx+1);
                bracket1 = back.indexOf("{");
                bracket2 = back.indexOf("}");
                if (bracket1 >= 0 && bracket2 >= 0){
                    String file = back.substring(bracket1 + 1, bracket2);
                    file = parentFolder + "/" + file;
                    back_pic_file = new File(file);
                }

                //front = front.replaceAll(getString(R.string.new_line_keyword), Character.toString((char) 10)); //will this work?
                //back = back.replaceAll(getString(R.string.new_line_keyword), Character.toString((char) 10)); //will this work?

                all_cards.add(new Card(front,back,ID,front_pic_file,back_pic_file));
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

    public void saveDeckToLocalJson(Deck deck, String filename) throws IOException{
        //this saves the object to local as a json object.
        Writer output = null;
        try {
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(deck.onSave().toString());
            outputStreamWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void addDeck(Uri uri) throws IOException, JSONException {
        String deckName = getDeckName(uri);
        String filename = deckName + ".adk";
        String textDeck = uri2Text(uri);
        Deck deck = null;
        if (deckName.substring(deckName.length() -4).equals(".txt")) {
            deck = readTxtDeck(textDeck, deckName, uri);
        } else if (deckName.substring(deckName.length() - 5).equals(".json")){
            deck = loadJsonDeck(uri);
        }
        SharedPreferences.Editor mDeckListEditor = getDeckList().edit();

        mDeckListEditor.putString(deckName,filename);
        mDeckListEditor.apply();

        saveDeckToLocal(deck,filename);
    }


    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public void removeDeck(String deckName){
        String filename = getDeckList().getString(deckName,null);
        SharedPreferences.Editor mDeckListEditor = getDeckList().edit();
        mDeckListEditor.remove(deckName);
        mDeckListEditor.apply();
        File dir = context.getFilesDir();
        File file = new File(dir, filename);
        file.delete();
    }


    //This method is old way of saving the deck object. Obsolete.
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




    public Deck loadJsonDeck(InputStream inputStream) throws FileNotFoundException, JSONException {

        String ret = "";

        try {
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject obj = new JSONObject(ret);

        return new Deck(obj);

    }

    public Deck loadJsonDeck(String filename) throws FileNotFoundException, JSONException {
        //This function was intended for loading decks saved as Json objects locally.
        //It is currently not being used.
        InputStream inputStream = context.openFileInput(filename);
        return loadJsonDeck(inputStream);
    }

    public Deck loadJsonDeck(Uri uri) throws FileNotFoundException, JSONException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        return loadJsonDeck(inputStream);
    }

    public void exportDeck(String filename) throws IOException {
        Deck dk = loadDeck(filename);
        String tempFilename = filename + ".json";
        saveDeckToLocalJson(dk,tempFilename);
        File dir = context.getFilesDir();
        final File file = new File(dir, tempFilename);
        shareFile(file);
        new AlertDialog.Builder(context)
                .setMessage("Export complete")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        file.delete();
                    }
                })
        .show();
    }

    private void shareFile(File file) {

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        intentShareFile.setType("application/json");
        Uri contentUri = FileProvider.getUriForFile(context, "me.timgu.fileprovider",file);

        intentShareFile.putExtra(Intent.EXTRA_STREAM,
               contentUri);
        intentShareFile.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        List<ResolveInfo> resolvedInfoActivities = context.getPackageManager().queryIntentActivities(intentShareFile, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolvedInfoActivities) {
            context.grantUriPermission(ri.activityInfo.packageName,contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        context.startActivity(Intent.createChooser(intentShareFile, "Share File"));
    }
}
