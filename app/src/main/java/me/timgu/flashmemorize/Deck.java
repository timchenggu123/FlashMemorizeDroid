package me.timgu.flashmemorize;

import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck implements Serializable {
    public static final int SHUFFLE_MODE_NO_REPEAT = 1;
    public static final int SHUFFLE_MODE_YES_REPEAT = 0;
    public static final int SHUFFLE_MODE_DRAW_ONE = 2;
    private int size;
    private  int[] order;

    public String name;
    public List<Card> cards = new ArrayList<>();

    private int last_card_drawn = -1;
    //Deck subdeck; //To be implemented in future

    public Deck(String name_arg, List<Card> cards_arg){
        name = name_arg;
        cards = cards_arg;
        size = cards.size();

        //initializing order
        order = new int[size];
        for (int i = 0; i< order.length; i++){
            order[i] = i;
        }

    }

    public Deck(String name_arg){
        //for creating a new empty deck
        name = name_arg;
        cards = new ArrayList<> ();
        cards.add(new Card("","",0,null,null));
        size = cards.size();

        //initializing order
        order = new int[size];
        order[0] = 0;
    }

    public Deck(JSONObject obj){
        try{
            size = obj.getInt("size");
        } catch(JSONException e){
            e.printStackTrace();
        }
        try{
            name = obj.getString("name");
        } catch(JSONException e){
            e.printStackTrace();
        }
        try{
            JSONObject card_objects = obj.getJSONObject("cards");
            for (int i = 0; i < card_objects.length(); i++){
                Card card =
                        new Card(card_objects.getJSONObject(Integer.toString(i)));
                cards.add(card);
            }
        } catch(JSONException e){
            e.printStackTrace();
        }
        try{
            String s = obj.getString("order");
            List<String> temp =
                    Arrays.asList(s.substring(1, s.length() - 1).split(", "));
            order = new int[size];
            for (int i = 0; i< order.length; i++){
                order[i] = Integer.valueOf(temp.get(i));
            }
            //TODO wip
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    public JSONObject onSave(){
        JSONObject obj = new JSONObject();
        try{
            obj.put("size",size);
            obj.put("order",Arrays.toString(order));
            obj.put("name",name);

            JSONObject cards_obj = new JSONObject();
            int indx = 0;
            for (Card card: cards){
                cards_obj.put(Integer.toString(indx), card.onSave());
                indx++;
            }
            obj.put("cards",cards_obj);
            return obj;
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }
    }


    public void shuffle(int mode, int reset, int draw){
        shuffle(mode,reset,draw,cards.size());
    }


    public void smartShuffle(int n_cards, double appearance_rate){
        //
        shuffle(Deck.SHUFFLE_MODE_YES_REPEAT,0,0,n_cards,appearance_rate);
    }

    public void shuffle (int mode, int reset, int draw, int n_cards){
        /*this sets default value for appearance rate*/
        shuffle(mode, reset, draw, n_cards, 0.1);
    }
    public void shuffle(int mode, int reset, int draw, int n_cards, double appearance_rate){
    /*

    #Shuffle the deck. Mode = 1 shuffle a deck of ncards with every card included exactly once. All cards
    #having any other value will result in the deck shuffling cards based on the accuracy of each card. Draw = 1
    #returns one random card based on the card accuracy, but only work if allCards is set to 0

    #allCards<int/logical> [0,1]: 1 then the deck is shuffled such that all cards are included at least once
    #rndFlip<int> [0,1,2]: 0: all cards facing front; 1: all cards randomly flipped; 2: all cards facing back
    #n_cards determines how many cards to shuffle

    #set reset = 1 to reset deck in order with all cards facing front
     */
        //before shuffle begins, first reset all viewed stats
        //This was originally not a problem in Python ,but since Java passes objects
        //by reference there is now a need to reset viewed.
        if (draw == 0){
            for (Card c: cards){
                c.viewed = 0;
            }
        }
        size = n_cards;

        //re-initialize order
        order = new int[size];
        for (int i = 0; i< order.length; i++){
            order[i] = i;
        }
        //end of re-initialize
        if (reset == 1){
            for(int i = 0; i < cards.size(); i++){
                cards.get(i).side = 1;
            }
            return;
        }

        if (mode == 1){
            List<Integer> temp = new ArrayList<>();
            for(int i:order){
                temp.add(i);
            }
            Collections.shuffle(temp);

            int count = 0;
            for (Integer i: temp){
                order[count] = i;
                count ++;
            }

            return;
        }

        //the following code executes if mode == 0;

        int ncards = cards.size();
        int[] order2 = new int[size];
        double[] accuracy = new double[ncards];
        int[] pool = new int[ncards];

        for (int c = 0; c < ncards; c ++) accuracy[c] = cards.get(c).getStats();

        double temp = 0;
        int card = -1;

        for (double a: accuracy){
            if (a == 0) {
                a = 0.01; //set to 0.01 to avoid div0 error
            }
            a = 1 / a;
            card = card + 1;
            double coeff = cards.get(card).timesStudied;
            /*basically coeff is the weight affecting the rate of appreance of cards,
            which is proportional to the times the card has been studied. We are going to
            modify coefficent by a factor to control how frequent the user wants to see
            the cards they fail at*/
            if (coeff == 0) {
                coeff = appearance_rate; //to avoid div0 error, if the card has not been studied, set the weight to 0
            } else {
                coeff = coeff * appearance_rate;
            }

            a = a * coeff;
            a = a + temp;
            temp = a;

            a = a * 100;
            pool[card] = (int) a;
        }
        temp = -1;
        for (int i = 0; i < size; i ++){
            boolean proceed = false;
            int nth = 0;

            while (!proceed){
                int toss = new Random().nextInt(pool[pool.length-1]);

                nth = -1;
                for (int zone: pool){
                    nth = nth +1;
                    if (toss < zone){
                        if (temp == nth){
                            break;
                        }
                        order2[i] = nth;
                        temp = nth;
                        proceed = true;
                        break;
                    }
                }
            }

            if (mode == 2){
                order2 = new int[1];
                order2[0] = nth;
                size = 1;
                // The following code makes sure that the same card does not get drawn over and over again
                if (last_card_drawn > -1){
                    if (last_card_drawn == nth){
                        shuffle(mode,reset,draw);
                    }else{
                        last_card_drawn =  nth;
                    }
                }else{
                    last_card_drawn = nth;
                }
                break;
            }

        }
        order = order2;
    }

    public void randomFlip(int mode){
        //0: set all side to 1
        //1: random flip
        //2: set all side to 2
        if (mode == 0){
            for (int i = 0; i < cards.size(); i ++){
                cards.get(i).side = 1;
            }
        }else if (mode == 1) {
            for (int i = 0; i < cards.size(); i++){
                int a = new Random().nextInt(2) + 1;
                for (int j = 0; j < a; j++) cards.get(i).flip();
            }
        }else{
            for (int i = 0; i < cards.size(); i++){
                cards.get(i).side = 0;
            }
        }
    }

    public void randomFlip(){randomFlip(1);}


    public List<Card> getDeck(){
        int nCards = order.length;
        List<Card> dk = new ArrayList<>();
        for (int i:order){
            dk.add(cards.get(i));
        }
        return dk;
    }

    //public void newSubdeck(){}

    public double[] getDeckStats(){
        int totalCorrect = 0;
        int totalStudied = 0;
        int totalViewed = 0;
        double accuracy;


        for (Card c: cards) {
            totalCorrect = totalCorrect + c.timesCorrect;
            totalStudied = totalStudied + c.timesStudied;
            totalViewed = totalViewed + c.viewed;
        }

        if (totalStudied == 0){
            accuracy = 1.0;
        }else{
            accuracy = 1.0*totalCorrect/totalStudied;
        }

        double[] return_package = {accuracy,totalStudied,totalViewed};

        return return_package;

    }

    public void resetViewed(){
        int nCards = cards.size();
        for (int i = 0; i < nCards; i++){
            cards.get(i).viewed = 0;
        }
    }

    public int getSize(boolean entire_deck){
        if (entire_deck){
            return size;
        }else{
            int[] o = order;
            Arrays.sort(o);
            int temp = -1;
            int uniqueCards =0;
            for (int i:o){
                if (i!=temp){
                    temp = i;
                    uniqueCards = uniqueCards+ 1;
                }
            }
            return uniqueCards;
        }
    }

    public int getSize(){return getSize(true);}

    public int[] getOrder(){return this.order;}


    //public void deleteCard(){};

}
