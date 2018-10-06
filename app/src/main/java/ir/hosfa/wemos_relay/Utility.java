package ir.hosfa.wemos_relay;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utility {

    static Typeface typeface;

    //-------------------------------------------------------------------------------------------

    public static Typeface getTypeFace() {
        //Typeface  tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Yekan.TTF");
        //Typeface  tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/vazir/Vazir.ttf");

        typeface = Typeface.createFromAsset(G.context.getAssets(), "fonts/Yekan.ttf");
        return typeface;
    }

    //-------------------------------------------------------------------------------------------

    static boolean isConnectedToInternet() {
        ConnectivityManager manager = (ConnectivityManager) G.context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (manager == null)
            return false;

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    //-------------------------------------------------------------------------------------------

}
