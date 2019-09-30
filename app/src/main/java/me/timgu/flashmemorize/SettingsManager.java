package me.timgu.flashmemorize;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import androidx.preference.PreferenceManager;

public class SettingsManager {
    private Context mContext;
    private SharedPreferences mSettings;


    //Here contains a list of modifiable default vals
    private String DEFAULT_FONT_SIZE = "8";


    public SettingsManager(Context context){
        mContext = context;
        mSettings = PreferenceManager.getDefaultSharedPreferences(mContext);
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
        return Integer.valueOf(mSettings.getString("font_size",DEFAULT_FONT_SIZE));
    }

    public double getAppearanceRate(){
        return Double.valueOf(mSettings.getString("card_appearance_rate", "0.1"));
    }

    public Boolean getUsingFourSpaces(){
        return mSettings.getBoolean("use_four_spaces",false);
    }

    public String getSideKey(){
        if (getUsingFourSpaces()){
            return "    ";
        }else{
            return String.valueOf((char) 9);
        }
    }
}
