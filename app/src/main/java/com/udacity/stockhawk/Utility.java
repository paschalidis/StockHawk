package com.udacity.stockhawk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class Utility {

    /**
     * Function to check network connection
     *
     * @param context Context used to get the Connectivity Manager
     * @return True if network is available other side False
     */
    static public boolean isNetworkAvailable(Context context){
        boolean isAvailable = false;
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
            isAvailable = true;
        }

        return isAvailable;
    }
}
