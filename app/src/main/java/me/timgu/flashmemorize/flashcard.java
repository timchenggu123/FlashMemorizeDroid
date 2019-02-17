package me.timgu.flashmemorize;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class flashcard extends AppCompatActivity {
    public List<Card> cards;
    public Deck dk;
    private TextView canvas;
    private int current_card = 0;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        //assigning view element id
        canvas = findViewById(R.id.text_canvas);
        Toolbar toolbar = findViewById(R.id.toolbar_flashcard);
        setSupportActionBar(toolbar);

        //setting onTouchEventListener for canvas
        TextView canvas = findViewById(R.id.text_canvas);
        TextView flip = findViewById(R.id.text_flip);

        //mPreference


        canvas.setOnTouchListener(new OnSwipeTouchListener(flashcard.this){
            public void onSwipeRight(){
                nextCard();
            }
            public void onSwipeLeft(){
                prevCard();
            }
            public void onTwoTaps(){
                flipCard();
            }
        });
        flip.setOnTouchListener(new OnSwipeTouchListener(flashcard.this){
            public void onSwipeRight(){
                nextCard();
            }
            public void onSwipeLeft(){
                prevCard();
            }
            public void onTwoTaps(){
                flipCard();
            }
        });
        //Receiving intent from main
        Intent intent = getIntent();
        String filename = intent.getStringExtra(MainActivity.EXTRA_FILENAME);

        try {
            dk = loadDeck(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        cards = dk.getDeck();
        showCard();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_flashcard,menu);
        return true;
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

                //front = front.replaceAll(getString(R.string.new_line_keyword), Character.toString((char) 10)); //will this work?
                //back = back.replaceAll(getString(R.string.new_line_keyword), Character.toString((char) 10)); //will this work?

                all_cards.add(new Card(front,back,ID));
                ID ++;
            }
        }
        Deck dk = new Deck("default_name",all_cards); //needs change name
        return dk;
    }

    public void showCard(){
        String text = cards.get(current_card).show();
        // somehow " -" 's space gets deleted in XML, have to hardcode it in here
        text = text.replaceAll(" -", Character.toString((char) 10) + (char)10);
        //needs more work;
        canvas.setText(text);
    }

    public void moveCurrentCard(int step){
        /*this is a mutator that changes the current_card variable;
        It is how we can navigate through the deck;
         */
        //needs more work;
        int nCards = dk.size;
        current_card = current_card + step;
        Toast.makeText(this, Integer.toString(current_card), Toast.LENGTH_SHORT).show();
        if (current_card == nCards){
            current_card = 0;
            Toast.makeText(this, "Reached End of Deck", Toast.LENGTH_SHORT).show();

        }else if (current_card < 0){
            current_card = dk.size - 1;
            Toast.makeText(this, "Reached Beginning of Deck", Toast.LENGTH_SHORT).show();

        }
    }

    public void prevCard(){
        moveCurrentCard(-1);
        showCard();
    }

    public void prevCard(View view) {
        prevCard();
    }

    public void nextCard() {
        moveCurrentCard(1);
        showCard();
    }
    public void nextCard(View view) {
        nextCard();
    }

    public void flipCard(){
        cards.get(current_card).flip();
        showCard();
    }
    public void flipCard(View view) {
        flipCard();
    }

    public void goodCard(View view) {
        moveCurrentCard(1);
        showCard();
    }

    public void badCard(View view) {
        moveCurrentCard(1);
        showCard();
    }

    public void shuffleCardsNoRepeat(MenuItem item) {
        //needs more work
        dk.shuffle(1, 0, 0);
        cards = dk.getDeck();
        current_card = 0;
        showCard();
    }

    public void shuffleCardsSmartLearn(MenuItem item) {
        dk.shuffle(0, 0, 0);
        cards = dk.getDeck();
        current_card = 0;
        showCard();
    }


    public void shuffleCardsRandomFlip(MenuItem item) {
        dk.randomFlip();
        cards = dk.getDeck();
        current_card = 0;
        showCard();
    }

    public void shuffleCardsAllFront(MenuItem item) {
        dk.randomFlip(0);
        cards = dk.getDeck();
        current_card = 0;
        showCard();
    }

    public void shuffleCardsAllBack(MenuItem item) {
        dk.randomFlip(2);
        cards = dk.getDeck();
        current_card = 0;
        showCard();
    }

    public void shuffleCardsReset(MenuItem item) {
        dk.shuffle(0,1,0);
        cards = dk.getDeck();
        current_card = 0;
        showCard();
    }
}
