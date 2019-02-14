package me.timgu.flashmemorize;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MainActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 6936;
    public static final String EXTRA_TXTDECK =
            "me.timgu.flashmemorize.extra.TXTDECK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void performFileSearch(View view){
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
                    String txtDeck = uri2Text(uri);
                    loadDeck(txtDeck);
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
            stringBuilder.append(line);
        }
        reader.close();
        return stringBuilder.toString();
    }

    private void loadDeck(String txtDeck){
        Toast.makeText(this, "Deck Loaded", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,flashcard.class);
        intent.putExtra(EXTRA_TXTDECK,txtDeck);
        startActivity(intent);
    }

}
