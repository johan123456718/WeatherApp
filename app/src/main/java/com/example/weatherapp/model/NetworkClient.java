package com.example.weatherapp.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * A manager that handles network
 *
 * NETWORK
 * --------
 * https://developer.android.com/reference/android/content/Context
 * https://developer.android.com/reference/android/net/ConnectivityManager
 * --------
 * Made by Johan Challita, Tidaa
 */

public class NetworkClient implements NetworkFeatures {

    private Context context;

    public NetworkClient(Context context){
        this.context = context;
    }

    /**
     * Checks if the client is connected
     * @return
     */
    @Override
    public boolean isConnected() {
        ConnectivityManager onlineManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = onlineManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }else{
            return false;
        }
    }
}
