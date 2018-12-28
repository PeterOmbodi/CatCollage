package com.peterombodi.catcollage.presentation.screen.collage_create;

import com.peterombodi.catcollage.data.api.ICatService;
import com.peterombodi.catcollage.data.api.Rest;
import com.peterombodi.catcollage.data.model.CatApiResponse;
import com.peterombodi.catcollage.presentation.base.BaseInteractor;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import io.reactivex.Observable;

/**
 * @author Peter Ombodi (Created on 20.12.2018).
 * Email:  p.ombodi@gmail.com.com
 */
@EBean
public class CollageInteractor extends BaseInteractor implements CollageContract.CollageModel {

    @Bean
    Rest rest;

    private ICatService service;

    @AfterInject
    void init() {
        service = rest.getCatService();
    }

    @Override
    public Observable<CatApiResponse> getCats(int count) {
        return getAsyncObservable(rest.getPreparedObservable(service.getCats("xml", count, "small"),
                CatApiResponse.class, true, false));
    }

}
