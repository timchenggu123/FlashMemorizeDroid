package me.timgu.flashmemorize;

import android.widget.Toast;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck implements Serializable {
    private int size;
    private  int[] order;

    public String name;
    public List<Card> cards;

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

    public void shuffle(int mode, int reset, int draw){
    /*

    #Shuffle the deck. Mode = 1 shuffle a deck of ncards with every card included exactly once. All cards
    #having any other value will result in the deck shuffling cards based on the accuracy of each card. Draw = 1
    #returns one random card based on the card accuracy, but only work if allCards is set to 0

    #allCards<int/logical> [0,1]: 1 then the deck is shuffled such that all cards are included at least once
    #rndFlip<int> [0,1,2]: 0: all cards facing front; 1: all cards randomly flipped; 2: all cards facing back
    #set reset = 1 to reset deck in order with all cards facing front
     */
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

        int ncards = order.length;
        int[] order2 = new int[ncards];
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
            double coeff = cards.get(card).timesStudied; //basically coeff the weight affecting the rate of appreance of cards
            if (coeff == 0) {
                coeff = 0.1; //to avoid div0 error, if the card has not been studied, set the weight tto 0
            } else {
                coeff = coeff * 0.1;
            }

            a = a * coeff;
            a = a + temp;
            temp = a;

            a = a * 100;
            pool[card] = (int) a;
        }
        temp = -1;
        for (int i = 0; i < ncards; i ++){
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
                order = new int[1];
                order[0] = nth;
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

    //public void append(self,cards){}

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
