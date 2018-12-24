package com.peterombodi.catcollage;

import android.content.Context;

import com.peterombodi.catcollage.data.api.DownloadDataRx;
import com.peterombodi.catcollage.presentation.screen.collage_create.CollagePresenter;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;

import me.eugeniomarletti.kotlin.metadata.shadow.javax.inject.Singleton;

/**
 * Created by Admin on 10.01.2017.
 */
@EBean(scope = EBean.Scope.Singleton)
public class ObjectGraph {

    private DownloadDataRx mDownloadDataRx;
    private CollagePresenter collagePresenter;

    @AfterInject
    void init() {
        mDownloadDataRx = new DownloadDataRx();
        collagePresenter = new CollagePresenter();
    }

    public final DownloadDataRx getDownloadDataRx() {
        return mDownloadDataRx;
    }


    public final CollagePresenter getCollagePresenter(){
        return collagePresenter;
    }
}
