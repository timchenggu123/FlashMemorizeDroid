package me.timgu.flashmemorize;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class FlashcardActivity extends AppCompatActivity
                implements  FlashcardDialogFragment.FlashcardDialogListener {
    public List<Card> mCards;
    public Deck mDeck;
    public static final int MERGE_LIST_REQUEST_CODE = 2424;

    //Views
    private TextView mCanvas;
    private TextView mId_display;
    private TextView mTotal_cards_display;
    private TextView mSide_display;
    private ImageView mImage_display;
    private EditText mText_edit;
    private PopupWindow mStats_popupWindow;
    private Button mButton_good;
    private Button mButton_bad;
    private Button mButton_prev;
    private Button mButton_next;
    private Button mButton_cancel;
    private Button mButton_done;
    private ProgressBar mPBar;

    private String mCurrentFile;
    private int mCurrentCard = 0;
    private LocalDecksManager mDecksManager;
    private SettingsManager mSettingsManager;
    private String mFilename;

    private boolean editMode = false;

    private int IMAGE_SEARCH_REQUEST_CODE = 6937;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        //assigning toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_flashcard);
        setSupportActionBar(toolbar);

        //assigning progress bar
        mPBar = findViewById(R.id.flashcard_progressBar);
        mPBar.setVisibility(View.GONE);

        //Views in normal mode initializing views
        mCanvas = findViewById(R.id.flashcard_text_canvas);
        mText_edit = findViewById(R.id.flashcard_text_edit);
        mId_display = findViewById(R.id.flashcard_display_id_value);
        mTotal_cards_display = findViewById(R.id.flashcard_display_totalcards_value);
        mSide_display = findViewById(R.id.flashcard_display_side_value);
        mImage_display = findViewById(R.id.flashcard_image_display);
        mButton_good = findViewById(R.id.flashcard_button_good);
        mButton_bad = findViewById(R.id.flashcard_button_bad);
        mButton_next = findViewById(R.id.flashcard_button_next);
        mButton_prev = findViewById(R.id.flashcard_button_prev);
        mButton_cancel = findViewById(R.id.flashcard_button_cancel);
        mButton_done = findViewById(R.id.flashcard_button_done);

        //--Views in editing mode
        mText_edit.setVisibility(View.GONE);
        mButton_cancel.setVisibility(View.GONE);
        mButton_done.setVisibility(View.GONE);

        findViewById(R.id.flashcard_scroll_view).setOnTouchListener(new OnSwipeTouchListener(FlashcardActivity.this) {
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

        mCanvas.setOnTouchListener(new OnSwipeTouchListener(FlashcardActivity.this) {
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


        mImage_display.setOnTouchListener(new OnSwipeTouchListener(FlashcardActivity.this) {
            public void onSwipeRight() {
                prevCard();
            }
            public void onSwipeLeft() {
                nextCard();
            }
            public void onTwoTaps() {
                flipCard();
            }
            public void onLongTap() {
                Intent intent = new Intent(getContext(), ImageViewActivity.class);
                Bitmap pic = mCards.get(mCurrentCard).showImage();
                LocalDecksManager ldm = new LocalDecksManager(getContext());
                String filename = ldm.saveImageToCache(pic);
                intent.putExtra("image",filename);
                getContext().startActivity(intent);}
        });


        //Receiving intent from main
        Intent intent = getIntent();
        mFilename = intent.getStringExtra(MainActivity.EXTRA_FILENAME);
        mCurrentFile = mFilename;
        //initializing LocalDecksManager
        mSettingsManager = new SettingsManager(this);
        mDecksManager = new LocalDecksManager(this);
        try {
            mDeck = mDecksManager.loadDeck(mFilename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            finish();
        } catch(Exception e){
            finish();
        }

        mCards = mDeck.getDeck();

        //initialize

        showCard();
        showDeckStats();
        mCanvas.setTextSize(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,mSettingsManager.getFontSize(),
                        getResources().getDisplayMetrics()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_flashcard, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    public void ExitActivity(MenuItem item) {
        finish();
    }

    private Context getContext(){
        return this;
    }
    public void showCard() {
        //displaying text information
        String text = mCards.get(mCurrentCard).show();
        // somehow " -" 's space gets deleted in XML, have to hardcode it in here
        text = text.replaceAll(" -", Character.toString((char) 10) + (char) 10);
        //needs more work;
        if (editMode){
            mText_edit.setText(text);
        }else{
            mCanvas.setText(text);
        }


        //displaying image
        if (mCards.get(mCurrentCard).showImage() != null) {
            mImage_display.setImageBitmap(
                    mCards.get(mCurrentCard).showImage());
        } else {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.border_rectangle);
            mImage_display.setImageBitmap(bm);
        }
        //updating deck stats;

        mCards.get(mCurrentCard).viewed = 1;
        showDeckStats();
    }

    public void moveCurrentCard(int step) {
        /*this is a mutator that changes the mCurrentCard variable;
        It is how we can navigate through the deck;
         */
        //If in edit mode, save changes before moving on
        if (editMode){
            String txt = mText_edit.getText().toString();
            mCards.get(mCurrentCard).editText(txt);

            new ApplyDeckChanges(false).execute();
        }
        //
        int nCards = mDeck.getSize();
        mCurrentCard = mCurrentCard + step;
        //Toast.makeText(this, Integer.toString(mCurrentCard), Toast.LENGTH_SHORT).show();
        if (mCurrentCard == nCards) {
            mCurrentCard = 0;
            //Toast.makeText(this, "Reached End of Deck", Toast.LENGTH_SHORT).show();

        } else if (mCurrentCard < 0) {
            mCurrentCard = mDeck.getSize() - 1;
            //Toast.makeText(this, "Reached Beginning of Deck", Toast.LENGTH_SHORT).show();

        }
    }

    public void updateDeckStats(int correct) {
        //The reason [int correct] here is an int not a bool is for future implementation of different levels of correctness
        //Right now [int correct] should be either 0 or 1;

        if (this.mCurrentCard == mDeck.getSize()) {
            mCurrentCard = mCurrentCard - 1;
        }

        int curId = mCards.get(mCurrentCard).getId(); //gets the id of the card currently on display

        mDeck.cards.get(curId).timesStudied++;
        mDeck.cards.get(curId).timesCorrect += correct;
        mDeck.cards.get(curId).updateStudyTrend(correct);

        new ApplyDeckChanges(false).execute();
        showDeckStats();
    }


    public void showDeckStats() {
        int curId = mCards.get(mCurrentCard).getId(); //gets the id of the card currently on display
        int deckSize = mDeck.getSize();

        String dispId = String.valueOf(curId + 1);//+1 here because mCurrentCard starts from 0
        String dispTotalCards =
                String.valueOf(mCurrentCard + 1) + '/' + String.valueOf(deckSize);
        int s = mCards.get(mCurrentCard).side;
        String dispSide;
        if (s == 1){
            dispSide = "Front";
        }else{
            dispSide = "Back";
        }

        mSide_display.setText(dispSide);
        mId_display.setText(dispId);
        mTotal_cards_display.setText(dispTotalCards);

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
        //if in edit mode, save changes before mFlip
        if (editMode){
            String txt = mText_edit.getText().toString();
            mCards.get(mCurrentCard).editText(txt);

            new ApplyDeckChanges(false).execute();
        }
        //---------------------------------
        mCards.get(mCurrentCard).flip();
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
        mDeck.shuffle(1, 0, 0);
        mCards = mDeck.getDeck();
        mCurrentCard = 0;
        showCard();
    }

    public void shuffleCardsSmartLearn(MenuItem item) {
        DialogFragment dialog = new FlashcardDialogFragment();
        dialog.show(getSupportFragmentManager(),"flashcard_dialog");
    }

    public void shuffleCardsSmartLearn(int n_cards){
        mDeck.smartShuffle(n_cards, mSettingsManager.getAppearanceRate());
        mCards = mDeck.getDeck();
        mCurrentCard = 0;
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
        mDeck.randomFlip();
        mCards = mDeck.getDeck();
        //mCurrentCard = 0; //This a removed for improved user experience
        showCard();
    }

    public void shuffleCardsAllFront(MenuItem item) {
        mDeck.randomFlip(0);
        mCards = mDeck.getDeck();
        //mCurrentCard = 0; //This is removed for improved user experience
        showCard();
    }

    public void shuffleCardsAllBack(MenuItem item) {
        mDeck.randomFlip(2);
        mCards = mDeck.getDeck();
        mCurrentCard = 0;
        showCard();
    }

    public void shuffleCardsReset(MenuItem item) {
        mDeck.shuffle(0, 1, 0);
        mCards = mDeck.getDeck();
        mCurrentCard = 0;
        showCard();
    }

    public void currentCardStats(MenuItem item) {
        LayoutInflater inflater =
                (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View stats_popup = inflater.inflate(R.layout.flashcard_stats_popup,null);
        mStats_popupWindow = new PopupWindow(
                stats_popup,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT

        );

        if (Build.VERSION.SDK_INT >= 21){
            mStats_popupWindow.setElevation(10.0f);
        }

        ImageButton closeButton =
                stats_popup.findViewById(R.id.flashcard_stats_popup_close);
        TextView mTextView =
                 stats_popup.findViewById(R.id.flashcard_stats_popup_text);

        String card_accuracy =
                "Current Card Accuracy: "+ (int)(mCards.get(mCurrentCard).getStats()
                        * 100) + "%";
        String card_times_studied =
                "Current Card Times Studied: "+ mCards.get(mCurrentCard).timesStudied;
        String card_times_correct =
                "Current Card Times Correct: "+mCards.get(mCurrentCard).timesCorrect;
        double[] deck_stats = mDeck.getDeckStats();
        String deck_accuracy =
                "Deck Overall Accuracy: "+(int)(deck_stats[0] * 100) + "%";
        String deck_total_studied  =
                "Deck Total Times Studied: " + (int) deck_stats[1];
        String deck_total_viewed =
                "Unique Cards Viewed After Shuffle: "+(int) deck_stats[2]
                        + "/" + mDeck.getSize(false);

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
                mStats_popupWindow.dismiss();
            }
        });

        ConstraintLayout parent = findViewById(R.id.flash_card_constraint);
        mStats_popupWindow.showAtLocation(parent, Gravity.CENTER,0,0);

    }


    public void editCard(MenuItem item) {
        editCard();
    }

    public void editCard(){
        if (! editMode) {
            mCanvas.setVisibility(View.GONE);
            mButton_bad.setVisibility(View.GONE);
            mButton_good.setVisibility(View.GONE);

            mText_edit.setVisibility(View.VISIBLE);
            mButton_done.setVisibility(View.VISIBLE);
            mButton_cancel.setVisibility(View.VISIBLE);

            editMode = true;
            showCard();

        }else{
            mText_edit.setVisibility(View.GONE);
            mButton_done.setVisibility(View.GONE);
            mButton_cancel.setVisibility(View.GONE);

            mCanvas.setVisibility(View.VISIBLE);
            mButton_bad.setVisibility(View.VISIBLE);
            mButton_good.setVisibility(View.VISIBLE);

            editMode = false;

            showCard();
        }
    }

    public void editDone(View view) {
        String txt = mText_edit.getText().toString();
        mCards.get(mCurrentCard).editText(txt);

        new ApplyDeckChanges().execute();

        mText_edit.setVisibility(View.GONE);
        mButton_done.setVisibility(View.GONE);
        mButton_cancel.setVisibility(View.GONE);

        mCanvas.setVisibility(View.VISIBLE);
        mButton_next.setVisibility(View.VISIBLE);
        mButton_prev.setVisibility(View.VISIBLE);
        mButton_bad.setVisibility(View.VISIBLE);
        mButton_good.setVisibility(View.VISIBLE);

        editMode = false;

        showCard();
    }

    public void editCancel(View view) {
        mText_edit.setVisibility(View.GONE);
        mButton_done.setVisibility(View.GONE);
        mButton_cancel.setVisibility(View.GONE);

        mCanvas.setVisibility(View.VISIBLE);
        mButton_next.setVisibility(View.VISIBLE);
        mButton_prev.setVisibility(View.VISIBLE);
        mButton_bad.setVisibility(View.VISIBLE);
        mButton_good.setVisibility(View.VISIBLE);

        editMode = false;

        showCard();
    }


    public void addPic(MenuItem item) {
        performImageSearch();
    }


    public void performImageSearch(){
        Intent  intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_SEARCH_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        //for addPic method
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == IMAGE_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    mCards.get(mCurrentCard).addPic(bitmap);

                    new ApplyDeckChanges().execute();

                    showCard();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == MERGE_LIST_REQUEST_CODE && resultCode == RESULT_OK) {

            List<String> list = resultData.getStringArrayListExtra("Deck_List");
            for (String deck : list) {
                if (mDecksManager.getDeckList().getString(deck, "").equals(mCurrentFile)) {
                    continue;
                } else {
                    Deck dk = null;
                    try {
                        dk = mDecksManager.loadDeck(mDecksManager.getDeckList().getString(deck, ""));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Card card = mCards.get(mCurrentCard);
                    card.setId(dk.getSize());
                    dk.cards.add(card);
                    dk.shuffle(0, 1, 0);
                    try {
                        mDecksManager.saveDeckToLocal(dk, mDecksManager.getDeckList().getString(deck, ""));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void deletePic(MenuItem item) {
        mCards.get(mCurrentCard).deletePic();

        new ApplyDeckChanges().execute();
        showCard();
    }

    public void newCard(MenuItem item) {
        mDeck.cards.add(new Card("","", mDeck.cards.size(),null,null));
        mDeck.shuffle(0,1,0); //reset deck
        mCards = mDeck.getDeck();
        mCurrentCard = mDeck.cards.size() -1;  //set newly added card as the current card

        new ApplyDeckChanges().execute();
        showCard();//display card;
        editMode = false; //This might be confusing, but in order to turn on editMode, editMode has to be "false" before calling editCard.
        editCard();
    }


    public void removeCard(MenuItem item) {
        int id = mCards.get(mCurrentCard).getId();
        mDeck.cards.remove(id);
        for (int i = 0; i < mDeck.cards.size(); i++){
            mDeck.cards.get(i).setId(i);
        }
        mDeck.shuffle(0,1,0);
        mCards = mDeck.getDeck();
        mCurrentCard = 0;

        new ApplyDeckChanges().execute();
        showCard();
    }

    public void addCardToDeck(MenuItem item) {
        Intent intent = new Intent(this,MergeListActivity.class );
        intent.putExtra("REQUEST_CODE",MERGE_LIST_REQUEST_CODE);
        startActivityForResult(intent,MERGE_LIST_REQUEST_CODE);
    }

    private Context getActivityContext(){
        return (Context) this;
    }

    private class ApplyDeckChanges extends AsyncTask<String,Void,Void>{
        private Boolean mShowPBar;

        ApplyDeckChanges(Boolean showPBar){
            mShowPBar = showPBar;
        }

        ApplyDeckChanges(){
            mShowPBar = true;
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                mDecksManager.saveDeckToLocal(mDeck, mFilename);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mShowPBar){
                mPBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mShowPBar){
                mPBar.setVisibility(View.GONE);
                Toast.makeText(getActivityContext(), "Successfully applied changes!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
