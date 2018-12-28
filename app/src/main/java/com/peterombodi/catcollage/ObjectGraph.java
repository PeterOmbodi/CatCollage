package com.peterombodi.catcollage;

import com.peterombodi.catcollage.presentation.screen.collage_create.CollagePresenter;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;

/**
 * Created by Admin on 10.01.2017.
 */
@EBean(scope = EBean.Scope.Singleton)
public class ObjectGraph {

    private CollagePresenter collagePresenter;

    @AfterInject
    void init() {
        collagePresenter = new CollagePresenter();
    }

    public final CollagePresenter getCollagePresenter(){
        return collagePresenter;
    }
}
