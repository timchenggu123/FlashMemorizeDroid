package me.timgu.flashmemorize;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class flashcard extends AppCompatActivity
                implements  FlashcardDialogFragment.FlashcardDialogListener{
    public List<Card> cards;
    public Deck dk;

    //Views
    private TextView canvas;
    private TextView flip;
    private TextView id_display;
    private TextView total_cards_display;
    private TextView side_display;
    private ImageView image_display;
    private EditText text_edit;
    private PopupWindow stats_popupWindow;
    private Button button_good;
    private Button button_bad;
    private Button button_prev;
    private Button button_next;
    private Button button_cancel;
    private Button button_done;


    private int current_card = 0;
    private LocalDecksManager mDecksManager;
    private String filename;

    private boolean editMode = false;

    private int READ_REQUEST_CODE = 6937;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        //assigning toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_flashcard);
        setSupportActionBar(toolbar);

        //Views in normal mode initializing views
        canvas = findViewById(R.id.flashcard_text_canvas);
        flip = findViewById(R.id.flashcard_text_flip);
        text_edit = findViewById(R.id.flashcard_text_edit);
        id_display = findViewById(R.id.flashcard_display_id_value);
        total_cards_display = findViewById(R.id.flashcard_display_totalcards_value);
        side_display = findViewById(R.id.flashcard_display_side_value);
        image_display = findViewById(R.id.flashcard_image_display);
        button_good = findViewById(R.id.flashcard_button_good);
        button_bad = findViewById(R.id.flashcard_button_bad);
        button_next = findViewById(R.id.flashcard_button_next);
        button_prev = findViewById(R.id.flashcard_button_prev);
        button_cancel = findViewById(R.id.flashcard_button_cancel);
        button_done = findViewById(R.id.flashcard_button_done);

        //--Views in editing mode
        text_edit.setVisibility(View.GONE);
        button_cancel.setVisibility(View.GONE);
        button_done.setVisibility(View.GONE);

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
        image_display.setOnTouchListener(new OnSwipeTouchListener(flashcard.this) {
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
        } /*catch (JSONException e) {
            e.printStackTrace();
        }*/
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
        if (editMode){
            text_edit.setText(text);
        }else{
            canvas.setText(text);
        }


        //displaying image
        if (cards.get(current_card).showImage() != null) {
            image_display.setImageBitmap(
                    cards.get(current_card).showImage());
        } else {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.border_rectangle);
            image_display.setImageBitmap(bm);
        }
        //updating deck stats;

        cards.get(current_card).viewed = 1;
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
            current_card = current_card - 1;
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
        int s = cards.get(current_card).side;
        String dispSide;
        if (s == 1){
            dispSide = "Front";
        }else{
            dispSide = "Back";
        }

        side_display.setText(dispSide);
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
        DialogFragment dialog = new FlashcardDialogFragment();
        dialog.show(getSupportFragmentManager(),"flashcard_dialog");
    }

    public void shuffleCardsSmartLearn(int n_cards){
        dk.smartShuffle(n_cards);
        cards = dk.getDeck();
        showCard();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog,String msg) {
        if (!msg.equals("")) {
            shuffleCardsSmartLearn(Integer.valueOf(msg));
        }
        dialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
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

    public void currentCardStats(MenuItem item) {
        LayoutInflater inflater =
                (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View stats_popup = inflater.inflate(R.layout.flashcard_stats_popup,null);
        stats_popupWindow = new PopupWindow(
                stats_popup,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT

        );

        if (Build.VERSION.SDK_INT >= 21){
            stats_popupWindow.setElevation(10.0f);
        }

        ImageButton closeButton =
                (ImageButton) stats_popup.findViewById(R.id.flashcard_stats_popup_close);
        TextView mTextView =
                (TextView) stats_popup.findViewById(R.id.flashcard_stats_popup_text);

        String card_accuracy =
                "Current Card Accuracy: "+String.valueOf((int)(cards.get(current_card).getStats()
                        * 100)) + "%";
        String card_times_studied =
                "Current Card Times Studied: "+String.valueOf(cards.get(current_card).timesStudied);
        String card_times_correct =
                "Current Card Times Correct: "+String.valueOf(cards.get(current_card).timesCorrect);
        double[] deck_stats = dk.getDeckStats();
        String deck_accuracy =
                "Deck Overall Accuracy: "+String.valueOf((int)(deck_stats[0] * 100)) + "%";
        String deck_total_studied  =
                "Deck Total Times Studied: " + String.valueOf((int) deck_stats[1]);
        String deck_total_viewed =
                "Unique Cards Viewed After Shuffle: "+String.valueOf((int) deck_stats[2])
                        + "/" + String.valueOf(dk.getSize(false));

        String txt = card_accuracy + (char) 10
                + card_times_correct + (char) 10
                + card_times_studied + (char) 10
                + deck_accuracy + (char) 10
                + deck_total_studied + (char) 10
                + deck_total_viewed + (char) 10;

        mTextView.setText(txt);

        closeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                stats_popupWindow.dismiss();
            }
        });

        ConstraintLayout parent = (ConstraintLayout) findViewById(R.id.flash_card_constraint);
        stats_popupWindow.showAtLocation(parent, Gravity.CENTER,0,0);

    }


    public void editCard(MenuItem item) {
        if (! editMode) {
            canvas.setVisibility(View.GONE);
            button_next.setVisibility(View.GONE);
            button_prev.setVisibility(View.GONE);
            button_bad.setVisibility(View.GONE);
            button_good.setVisibility(View.GONE);

            text_edit.setVisibility(View.VISIBLE);
            button_done.setVisibility(View.VISIBLE);
            button_cancel.setVisibility(View.VISIBLE);

            editMode = true;
            showCard();

        }else{
            text_edit.setVisibility(View.GONE);
            button_done.setVisibility(View.GONE);
            button_cancel.setVisibility(View.GONE);

            canvas.setVisibility(View.VISIBLE);
            button_next.setVisibility(View.VISIBLE);
            button_prev.setVisibility(View.VISIBLE);
            button_bad.setVisibility(View.VISIBLE);
            button_good.setVisibility(View.VISIBLE);

            editMode = false;

            showCard();
        }
    }

    public void editDone(View view) {
        String txt = text_edit.getText().toString();
        cards.get(current_card).editText(txt);

        text_edit.setVisibility(View.GONE);
        button_done.setVisibility(View.GONE);
        button_cancel.setVisibility(View.GONE);

        canvas.setVisibility(View.VISIBLE);
        button_next.setVisibility(View.VISIBLE);
        button_prev.setVisibility(View.VISIBLE);
        button_bad.setVisibility(View.VISIBLE);
        button_good.setVisibility(View.VISIBLE);

        editMode = false;

        showCard();
    }

    public void editCancel(View view) {
        text_edit.setVisibility(View.GONE);
        button_done.setVisibility(View.GONE);
        button_cancel.setVisibility(View.GONE);

        canvas.setVisibility(View.VISIBLE);
        button_next.setVisibility(View.VISIBLE);
        button_prev.setVisibility(View.VISIBLE);
        button_bad.setVisibility(View.VISIBLE);
        button_good.setVisibility(View.VISIBLE);

        editMode = false;

        showCard();
    }

    public void addPic(MenuItem item) {
        performImageSearch();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void performImageSearch(){
        Intent  intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent,READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData){

        //for addPic method
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri uri = null;
            if(resultData != null){
                uri = resultData.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                    cards.get(current_card).addPic(bitmap);
                    showCard();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void deletePic(MenuItem item) {
        cards.get(current_card).deletePic();
        showCard();
    }

}
