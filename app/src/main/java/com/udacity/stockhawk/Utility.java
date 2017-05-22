package com.udacity.stockhawk;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.udacity.stockhawk.sync.QuoteSyncJob;


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

    /**
     *
     * @param c Context used to get the SharedPreferences
     * @return the server status integer type
     */
    @SuppressWarnings("ResourceType")
    static public @QuoteSyncJob.ServerStatus
    int getServerStatus(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_server_status_key),
                QuoteSyncJob.SERVER_STATUS_UNKNOWN);
    }

    /**
     * Resets the server status.  (Sets it to QuoteSyncJob.LOCATION_STATUS_UNKNOWN)
     * @param c Context used to get the SharedPreferences
     */
    static public void resetServerStatus(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_server_status_key), QuoteSyncJob.SERVER_STATUS_UNKNOWN);
        spe.apply();
    }
}
