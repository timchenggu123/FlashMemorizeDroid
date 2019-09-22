package me.timgu.flashmemorize;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

public class SettingsManager {
    private Context mContext;
    private String mSettingsFile = "me.timgu.settings";
    private SharedPreferences mSettings;


    //Here contains a list of modifiable default vals
    private int DEFAULT_FONT_SIZE = 8;


    public SettingsManager(Context context){
        mContext = context;
        mSettings = mContext.getSharedPreferences(mSettingsFile,Context.MODE_PRIVATE);
    }


    public void setFirstTime(){
        /* This method should be called the first time the app is launched
        * on a new device*/
        mSettings.edit().putBoolean("first_time_launch", false).apply();
    }

    public Boolean getFirstTime(){
       return  mSettings.getBoolean("first_time_launch",true);
    }

    public void setFontSize(int size){
        mSettings.edit().putInt("font_size", size).apply();
    }

    public int getFontSize(){
        return mSettings.getInt("font_size", DEFAULT_FONT_SIZE);
    }
}
