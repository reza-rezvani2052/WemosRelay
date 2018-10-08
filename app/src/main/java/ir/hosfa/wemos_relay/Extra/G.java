package ir.hosfa.wemos_relay.Extra;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class G extends Application {

    @SuppressLint("StaticFieldLeak")
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        //...
        context = getApplicationContext();
    }

}
