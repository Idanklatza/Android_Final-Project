package com.example.travelapp;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    static private Context context;

    public static Context getMyContext(){
        return MyApplication.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
