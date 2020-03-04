package cl.afubillaoliva.lsch.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {
    private SharedPreferences mSharedPreferences;
    public SharedPreference(Context context){
        mSharedPreferences =  context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }
    public void setNightMode(boolean state){
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("NightMode",state);
        editor.apply();
    }

    public void setFavorite(boolean state){
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("Favorite", state);
        editor.apply();
    }

    public void setAutodownload(boolean state){
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("Autodownload", state);
        editor.apply();
    }

    public void setHistoryDisabled(boolean state){
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("historyDisabled", state);
        editor.apply();
    }

    public void setFavoriteDisabled(boolean state){
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("favoriteDisabled", state);
        editor.apply();
    }

    public void setDownloadDisabled(boolean state){
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("downloadDisabled", state);
        editor.apply();
    }

    public void setCacheDisabled(boolean state){
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("cacheDisbled", state);
        editor.apply();
    }

    public void setWifiOnly(boolean state){
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("wifiOnly", state);
        editor.apply();
    }

    public boolean loadNightModeState(){ return mSharedPreferences.getBoolean("NightMode",false); }
    public boolean isFavorite(){ return mSharedPreferences.getBoolean("Favorite", false); }
    public boolean loadAutoDownload(){ return  mSharedPreferences.getBoolean("Autodownload", false); }

    public boolean isHistoryDisabled(){ return mSharedPreferences.getBoolean("historyDisabled", true); }
    public boolean isFavoriteDisabled(){ return mSharedPreferences.getBoolean("favoriteDisabled", true); }
    public boolean isDownloadDisabled(){ return mSharedPreferences.getBoolean("downloadDisabled", true); }
    public boolean isCacheDisabled(){ return mSharedPreferences.getBoolean("cacheDisabled", false); }
    public boolean isWifiOnly(){ return mSharedPreferences.getBoolean("wifiOnly", false); }

}
