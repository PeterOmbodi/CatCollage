package com.peterombodi.catcollage;

import android.annotation.SuppressLint;
import android.content.Context;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;


/**
 * Created by Admin on 03.01.2017.
 */

@SuppressLint("Registered")
@EApplication
public class Application extends android.app.Application {

    /**
     * Just start initialize
     **/
    @Bean
    ObjectGraph objectGraph;

    //private static Context instance;

    @AfterInject
    void init() {

        //instance = getApplicationContext();
    }

//    public static Context getContext() {
//        return instance;
//    }

}
