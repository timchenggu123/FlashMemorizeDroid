package me.timgu.flashmemorize;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class ImageViewActivity extends AppCompatActivity {
//Code inspired from https://medium.com/quick-code/pinch-to-zoom-with-multi-touch-gestures-in-android-d6392e4bf52d

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.0f;
    private ImageView mImageView;

    private float mPosX = 0f;
    private float mPosY = 0f;

    private float mLastTouchX;
    private float mLastTouchY;
    private static final int INVALID_POINTER_ID = -1;
    private static final String LOG_TAG = "TouchImageView";

    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        Intent intent = getIntent();
        File imageFile = new File(getCacheDir(), intent.getStringExtra("image"));
        SerialBitmap pic = new SerialBitmap(imageFile);
        PhotoView photoView = (PhotoView) findViewById(R.id.imageView_photoView);
        photoView.setImageBitmap(pic.bitmap);

    }
}
