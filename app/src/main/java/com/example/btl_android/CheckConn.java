package com.example.btl_android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.widget.Toast;

public class CheckConn {
    public static boolean haveNetworkConn(Context context) {
        boolean haveConnWifi = false;
        boolean haveConnMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        Network[] networks = cm.getAllNetworks();
        for (Network network : networks) {
            NetworkInfo ni = cm.getNetworkInfo(network);
            if (ni != null) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI") && ni.isConnected()) {
                    haveConnWifi = true;
                }
                if (ni.getTypeName().equalsIgnoreCase("MOBILE") && ni.isConnected()) {
                    haveConnMobile = true;
                }
            }
        }

        return haveConnWifi || haveConnMobile;
    }

    public static void showToast(Context context, String noiti) {
        Toast.makeText(context, noiti, Toast.LENGTH_SHORT).show();
    }
}
