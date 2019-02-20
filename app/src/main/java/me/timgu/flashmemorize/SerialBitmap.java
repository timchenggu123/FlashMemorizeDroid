//Disclaimer: I am not the original author of this code. The code is found at
//https://stackoverflow.com/questions/6002800/android-serializable-problem
package me.timgu.flashmemorize;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class SerialBitmap implements Serializable {

    public Bitmap bitmap;


    public SerialBitmap(@NotNull File imgFile) {
        // Take your existing call to BitmapFactory and put it here
        bitmap = BitmapFactory.decodeFile(imgFile.getPath());
        int pause = 1;
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
}