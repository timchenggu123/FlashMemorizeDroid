package me.timgu.flashmemorize;

import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;
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

    private SerialBitmap front_pic;
    private SerialBitmap back_pic;

    public Card (String front_arg, String back_arg, int ID_arg, File front_pic_file, File back_pic_file){
        front = front_arg;
        back = back_arg;
        id = ID_arg;

        if (front_pic_file != null && front_pic_file.exists()){
            this.front_pic = new SerialBitmap(front_pic_file);
        }else{
            this.front_pic = null;
        }

        if (back_pic_file != null && back_pic_file.exists()){
            this.back_pic = new SerialBitmap(back_pic_file);
        }else{
            this.back_pic = null;
        }
    }

    //TODO: implement compatibility constructor


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

    public Bitmap showImage(){
        if (side == 1 && front_pic != null){
            return front_pic.bitmap;
        }else if(side == 0 && back_pic != null){
            return back_pic.bitmap;
        }
        return null;
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
