package com.peterombodi.catcollage;

import android.content.Context;

/**
 * Created by Admin on 03.01.2017.
 */

public class Application extends android.app.Application {

    private static final String TAG = "Application";
    private static Context instance;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ObjectGraph.getInstance(getApplicationContext());   //init ObjectGraph when application created
    }


    public static Context getContext() {
        return instance;
    }


}