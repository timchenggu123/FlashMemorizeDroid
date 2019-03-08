package me.timgu.flashmemorize;

import android.content.Intent;
import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

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

    public Card(JSONObject obj){
        try{
            side = Integer.valueOf( obj.get("side").toString());
            timesStudied = Integer.valueOf(obj.get("timesStudied").toString());
            timesCorrect = Integer.valueOf(obj.get("timesCorrect").toString());
            viewed = Integer.valueOf(obj.get("viewed").toString());
            front = obj.get("front").toString();
            back = obj.get("back").toString();
            id = Integer.valueOf(obj.get("id").toString());
            front_pic =  new SerialBitmap(obj.get("front_pic").toString());
            back_pic = new SerialBitmap(obj.get("back_pic").toString());
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    //TODO: WIP
    public JSONObject onSave(){
        JSONObject obj = new JSONObject();

        try {
            obj.put("side", side);
            obj.put("timesStudied", timesStudied);
            obj.put("timesCorrect", timesCorrect);
            obj.put("viewed", viewed);
            obj.put("front", front);
            obj.put("back", back);
            obj.put("id", id);
            if (front_pic != null){obj.put("front_pic",front_pic.getAsString());}
                else{obj.put("front_pic","");}
            if (back_pic != null){obj.put("back_pic",back_pic.getAsString());}
                else{obj.put("back_pic","");}
            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void onRead(JSONObject obj){
        try{
            side = Integer.valueOf( obj.get("side").toString());
            timesStudied = Integer.valueOf(obj.get("timesStudied").toString());
            timesCorrect = Integer.valueOf(obj.get("timesCorrect").toString());
            viewed = Integer.valueOf(obj.get("viewed").toString());
            front = obj.get("front").toString();
            back = obj.get("back").toString();
            id = Integer.valueOf(obj.get("id").toString());
            front_pic =  new SerialBitmap(obj.get("front_pic").toString());
            back_pic = new SerialBitmap(obj.get("back_pic").toString());
        } catch (JSONException e){
            e.printStackTrace();
        }
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
