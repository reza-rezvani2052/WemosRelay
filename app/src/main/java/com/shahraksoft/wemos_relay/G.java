package com.shahraksoft.wemos_relay;

import android.app.Application;
import android.content.Context;

public class G extends Application {

    static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

}
