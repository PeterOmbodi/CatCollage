package com.peterombodi.catcollage.data.api;

import android.support.v4.util.LruCache;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.peterombodi.catcollage.data.model.CatApiResponse;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * @author Peter Ombodi (Created on 21.12.2018).
 * Email:  p.ombodi@gmail.com.com
 */
@EBean(scope = EBean.Scope.Singleton)
public class Rest {

    private static final String PARAM_BASE_URL = "http://thecatapi.com/";

    private static Observable<CatApiResponse> observableRetrofit;
    private LruCache<Class<CatApiResponse>, Observable<CatApiResponse>> apiObservables;
    private ICatService catService;

    @AfterInject
    void init() {
        apiObservables = new LruCache<>(10);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PARAM_BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        catService = retrofit.create(ICatService.class);
    }

    public ICatService getCatService() {
        return catService;
    }

    public Observable<CatApiResponse> getPreparedObservable(Observable<CatApiResponse> unPreparedObservable,
                                                            Class<CatApiResponse> responseClass,
                                                            boolean cacheObservable,
                                                            boolean useCache) {

        Observable<CatApiResponse> preparedObservable = null;

        if (useCache)//this way we don't reset anything in the cache if this is the only instance of us not wanting to use it.
            preparedObservable = apiObservables.get(responseClass);

        if (preparedObservable != null)
            return preparedObservable;

        //we are here because we have never created this observable before or we didn't want to use the cache...
        preparedObservable = unPreparedObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        if (cacheObservable) {
            preparedObservable = preparedObservable.cache();
            apiObservables.put(responseClass, preparedObservable);
        }
        return preparedObservable;
    }
}
