package me.timgu.flashmemorize;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class flashcard extends AppCompatActivity {
    public List<Card> cards;
    public Deck dk;

    private TextView canvas;
    private TextView flip;
    private TextView id_display;
    private TextView total_cards_display;
    private ImageView image_display;

    private int current_card = 0;
    private LocalDecksManager mDecksManager;
    private String filename;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        //assigning toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_flashcard);
        setSupportActionBar(toolbar);

        //initializing views
        canvas = findViewById(R.id.text_canvas);
        flip = findViewById(R.id.text_flip);
        id_display = findViewById(R.id.flashcard_display_id_value);
        total_cards_display = findViewById(R.id.flashcard_display_totalcards_value);
        image_display = findViewById(R.id.flashcard_image_display);
        //mPreference


        canvas.setOnTouchListener(new OnSwipeTouchListener(flashcard.this) {
            public void onSwipeRight() {
                prevCard();
            }

            public void onSwipeLeft() {
                nextCard();
            }

            public void onTwoTaps() {
                flipCard();
            }
        });
        flip.setOnTouchListener(new OnSwipeTouchListener(flashcard.this) {
            public void onSwipeRight() {
                prevCard();
            }

            public void onSwipeLeft() {
                nextCard();
            }

            public void onTwoTaps() {
                flipCard();
            }
        });
        //Receiving intent from main
        Intent intent = getIntent();
        filename = intent.getStringExtra(MainActivity.EXTRA_FILENAME);

        //initializing LocalDecksManager
        mDecksManager = new LocalDecksManager(this);
        try {
            dk = mDecksManager.loadDeck(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        cards = dk.getDeck();
        showCard();
        showDeckStats();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_flashcard, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            mDecksManager.saveDeckToLocal(dk, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showCard() {
        //displaying text information
        String text = cards.get(current_card).show();
        // somehow " -" 's space gets deleted in XML, have to hardcode it in here
        text = text.replaceAll(" -", Character.toString((char) 10) + (char) 10);
        //needs more work;
        canvas.setText(text);

        //displaying image
        if (cards.get(current_card).showImage() != null) {
            image_display.setImageBitmap(
                    cards.get(current_card).showImage());
        } else {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.border_rectangle);
            image_display.setImageBitmap(bm);
        }
        //updating deck stats;
        showDeckStats();
    }

    public void moveCurrentCard(int step) {
        /*this is a mutator that changes the current_card variable;
        It is how we can navigate through the deck;
         */
        //needs more work;
        int nCards = dk.getSize();
        current_card = current_card + step;
        //Toast.makeText(this, Integer.toString(current_card), Toast.LENGTH_SHORT).show();
        if (current_card == nCards) {
            current_card = 0;
            //Toast.makeText(this, "Reached End of Deck", Toast.LENGTH_SHORT).show();

        } else if (current_card < 0) {
            current_card = dk.getSize() - 1;
            //Toast.makeText(this, "Reached Beginning of Deck", Toast.LENGTH_SHORT).show();

        }
    }

    public void updateDeckStats(int correct) {
        //The reason [int correct] here is an int not a bool is for future implementation of different levels of correctness
        //Right now [int correct] should be either 0 or 1;

        if (this.current_card == dk.getSize()) {
            current_card = current_card - 1; //why again? don't remember, just copied over...
        }

        int curId = cards.get(current_card).getId(); //gets the id of the card currently on display

        dk.cards.get(curId).timesStudied++;
        dk.cards.get(curId).timesCorrect += correct;
        dk.cards.get(curId).updateStudyTrend(correct);

        showDeckStats();
    }


    public void showDeckStats() {
        int curId = cards.get(current_card).getId(); //gets the id of the card currently on display
        int deckSize = dk.getSize();

        String dispId = String.valueOf(curId + 1);//+1 here because current_card starts from 0
        String dispTotalCards =
                String.valueOf(current_card + 1) + '/' + String.valueOf(deckSize);

        id_display.setText(dispId);
        total_cards_display.setText(dispTotalCards);

    }


    public void prevCard() {
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

    public void flipCard() {
        cards.get(current_card).flip();
        showCard();
    }

    public void flipCard(View view) {
        flipCard();
    }

    public void goodCard(View view) {
        updateDeckStats(1);
        moveCurrentCard(1);
        showCard();
    }

    public void badCard(View view) {
        updateDeckStats(0);
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
        dk.shuffle(0, 1, 0);
        cards = dk.getDeck();
        current_card = 0;
        showCard();
    }

}
