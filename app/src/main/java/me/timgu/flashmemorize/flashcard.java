package me.timgu.flashmemorize;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class flashcard extends AppCompatActivity {
    public List<Card> cards;
    public Deck dk;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);
        Intent intent = getIntent();
        String txtDeck = intent.getStringExtra(MainActivity.EXTRA_TXTDECK);
        dk = readTxtDeck(txtDeck);
    }

    public Deck readTxtDeck(String deck){
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

                front = front.replaceAll(getString(R.string.new_line_keyword), Character.toString((char) 10)); //will this work?
                back = back.replaceAll(getString(R.string.new_line_keyword), Character.toString((char) 10)); //will this work?

                all_cards.add(new Card(front,back,ID));
                ID ++;
            }
        }
        Deck dk = new Deck("default_name",all_cards); //needs change name
        return dk;
    }
    

}
