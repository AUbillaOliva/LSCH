package cl.afubillaoliva.lsch.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {
    private SharedPreferences mSharedPreferences;
    public SharedPreference(Context context){
        mSharedPreferences =  context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }
    public void setNightMode(Boolean state){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("NightMode",state);
        editor.apply();
    }

    public void setFavorite(Boolean state){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("Favorite", state);
        editor.apply();
    }

    public Boolean loadNightModeState(){
        return mSharedPreferences.getBoolean("NightMode",false);
    }
    public Boolean isFavorite(){
            return mSharedPreferences.getBoolean("Favorite", false);
    }
}
