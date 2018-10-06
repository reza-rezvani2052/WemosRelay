package ir.hosfa.wemos_relay;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class Utility {

    public static boolean isConnectedToInternet() {
        ConnectivityManager manager = (ConnectivityManager) G.context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (manager == null)
            return false;

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

}
