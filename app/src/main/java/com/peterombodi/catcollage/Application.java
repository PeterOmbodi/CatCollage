package com.peterombodi.catcollage;

import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by Admin on 03.01.2017.
 */

public class Application extends android.app.Application {

    private static final String TAG = "Application";
    private static Context instance;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = getApplicationContext();
        ObjectGraph.getInstance(instance);   //init ObjectGraph when application created
        FlowManager.init(this);
        //initFlowDb();
    }


    public static Context getContext() {
        return instance;
    }

//    private void initFlowDb() {
//        DatabaseConfig.OpenHelperCreator openHelperCreator = CatCollageDatabase::new;
//
//        DatabaseConfig databaseConfig = new DatabaseConfig.Builder(CatCollageDatabase.class)
//            .openHelper(openHelperCreator)
//            .build();
//
//        FlowConfig flowConfig = new FlowConfig.Builder(this)
//            .openDatabasesOnInit(true)
//            .addDatabaseConfig(databaseConfig)
//            .build();
//
//        FlowManager.init(flowConfig);
//    }
}
