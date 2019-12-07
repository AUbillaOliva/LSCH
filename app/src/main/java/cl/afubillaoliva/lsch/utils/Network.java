package cl.afubillaoliva.lsch.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Network {

    private Context context;

    public Network(Context context){
        this.context = context;
    }

    public boolean isNetworkAvailable() {
        boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            isConnected = true;
        }
        return isConnected;
    }
}