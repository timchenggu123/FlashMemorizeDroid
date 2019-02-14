package me.timgu.flashmemorize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {
    public List<Card> cards;
    public int size;
    public  int[] order;
    public String name;

    private int last_card_drawn = -1;
    //Deck subdeck; //To be implemented in future

    public Deck(String name_arg, List<Card> cards_arg){
        name = name_arg;
        cards = cards_arg;
        size = cards.size();
        order = new int[size];

    }

    public void shuffle(int mode, int rndFlip, int reset, int draw){
    /*

    #Shuffle the deck. Mode = 1 shuffle a deck of ncards with every card included exactly once. All cards
    #having any other value will result in the deck shuffling cards based on the accuracy of each card. Draw = 1
    #returns one random card based on the card accuracy, but only work if allCards is set to 0

    #allCards<int/logical> [0,1]: 1 then the deck is shuffled such that all cards are included at least once
    #rndFlip<int> [0,1,2]: 0: all cards facing front; 1: all cards randomly flipped; 2: all cards facing back
    #set reset = 1 to reset deck in order with all cards facing front
     */
        order = new int[size];
        if (reset == 1){
            for(int i = 0; i < cards.size(); i++){
                cards.get(i).side = 1;
            }
            return;
        }

        if (mode == 1){
            Collections.shuffle(Arrays.asList(order));
            return;
        }

        int ncards = order.length;
        int[] order2 = new int[ncards];
        double[] accuracy = new double[ncards];
        int[] pool = new int[ncards];

        for (int c = 0; c < ncards; c ++) accuracy[c] = cards.get(c).getStats();

        double temp = 0;
        int card = -1;

        for (double a: accuracy) {
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
                    }
                }
                proceed = true;
            }

            if (mode == 2){
                order = new int[1];
                order[0] = nth;
                // The following code makes sure that the same card does not get drawn over and over again
                if (last_card_drawn > -1){
                    if (last_card_drawn == nth){
                        shuffle(mode, rndFlip,reset,draw);
                    }else{
                        last_card_drawn =  nth;
                    }
                }else{
                    last_card_drawn = nth;
                }
                break;
            }
            order = order2;
        }
        if (rndFlip == 0){
            for (int i = 0; i < cards.size(); i ++){
                cards.get(i).side = 1;
            }
        }else if (rndFlip == 1) {
            for (int i = 0; i < cards.size(); i++){
                int a = new Random().nextInt(2) + 1;
                for (int j = 0; j < a; j++) cards.get(i).flip();
            }
        }else{
            for (int i = 0; 9 < cards.size(); i++){
                cards.get(i).side = 1;
            }
        }
    }

    //public void append(self,cards){}

    public Card[] getdeck(){
        int nCards = order.length;
        Card[] dk = new Card[nCards];
        for (int i = 0; i < nCards; i ++){
            dk[i] = cards.get(i);
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
            accuracy = 0;
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

    //public void deleteCard(){};

}
