package com.peterombodi.catcollage.data.api;

import android.support.v4.util.LruCache;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.peterombodi.catcollage.data.model.CatApiResponse;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;


/**
 * Created by Admin on 04.01.2017.
 */

public class DownloadDataRx {

    private static final String PARAM_BASE_URL = "http://thecatapi.com/";
    private static final String TAG = "DownloadDataImpl";
    private static final String PARAM_FORMAT_XML = "xml";

    private static Observable<CatApiResponse> observableRetrofit;
    private LruCache<Class<CatApiResponse>, Observable<CatApiResponse>> apiObservables;
    private ICatApiRest getData;

    public DownloadDataRx() {
        apiObservables = new LruCache<>(10);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PARAM_BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        getData = retrofit.create(ICatApiRest.class);
    }

    public ICatApiRest getAPI() {
        return getData;
    }

    public Observable<CatApiResponse> getPreparedObservable(Observable<CatApiResponse> unPreparedObservable,
                                                            Class<CatApiResponse> clazz,
                                                            boolean cacheObservable,
                                                            boolean useCache) {

        Observable<CatApiResponse> preparedObservable = null;

        if (useCache)//this way we don't reset anything in the cache if this is the only instance of us not wanting to use it.
            preparedObservable = (Observable<CatApiResponse>) apiObservables.get(clazz);

        if (preparedObservable != null)
            return preparedObservable;

        //we are here because we have never created this observable before or we didn't want to use the cache...

        preparedObservable = (Observable<CatApiResponse>) unPreparedObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        if (cacheObservable) {
            preparedObservable = preparedObservable.cache();
            apiObservables.put(clazz, preparedObservable);
        }
        return preparedObservable;
    }
}
