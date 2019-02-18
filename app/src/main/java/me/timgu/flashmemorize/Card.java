package me.timgu.flashmemorize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class Card implements Serializable {

    public int side = 1;
    public int timesStudied = 0;
    public int timesCorrect = 0;
    public int viewed = 0;

    private String front;
    private String back;
    private int id ;
    private List<Integer> studyTrend = new ArrayList<>();
    private boolean front_pic_exist = false;
    private boolean back_pic_exist = false;


    public Card (String front_arg, String back_arg, int ID_arg){
        front = front_arg;
        back = back_arg;
        id = ID_arg;
    }

    public void flip(){
        side = abs(side -1);
    }

    public String show(){
        if (side == 1){
            return front;
        }else{
            return back;
        }
    }

    public double getStats(){
        if (timesStudied != 0){
            double stats;
            stats = timesCorrect*1.0/timesStudied; //1.0 here implicit cast to double
            return stats;
        }else{
            return 1.0;
        }
    }

    public void editText(String text){
        if (side == 1){
            front = text;
        }else{
            back = text;
        }
    }

    public int getId(){
        return id;
    }

    public void updateStudyTrend(int correct){
        studyTrend.add(correct);
    }
    /*
    //The following are place holders for future implementations.

    public double[] getStudyTrend(){}
    public void addPic(){}
    public void showPic() {}
     */

}
