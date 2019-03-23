//Disclaimer: I am not the original author of this code. The code is found at
//https://stackoverflow.com/questions/6002800/android-serializable-problem
package me.timgu.flashmemorize;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class SerialBitmap implements Serializable {
    //This class is intended to make bitmaps serializable. Can also convert bitmaps to
    //encoded string.

    public Bitmap bitmap;


    public SerialBitmap(@NotNull File imgFile) {
        // Take your existing call to BitmapFactory and put it here
        bitmap = BitmapFactory.decodeFile(imgFile.getPath());
        int pause = 1;
    }

    public SerialBitmap(@NotNull String encodedString){
        //This constructor get bitmap from string
        if (encodedString.length()==0){
            bitmap = null;
        }else {

            try {
                byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    // Converts the Bitmap into a byte array for serialization
    private void writeObject(@NotNull java.io.ObjectOutputStream out) throws IOException {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteStream);
            byte bitmapBytes[] = byteStream.toByteArray();
            out.write(bitmapBytes, 0, bitmapBytes.length);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // Deserializes a byte array representing the Bitmap and decodes it
    private void readObject(@NotNull java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            int b;
            while ((b = in.read()) != -1)
                byteStream.write(b);
            byte bitmapBytes[] = byteStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String bitMapToString(Bitmap bitmap){
        if (bitmap == null){
            return null;
        }
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public String getAsString(){
        if (bitmap == null){
            return "";
        }
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}