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
    private LruCache<Class<?>, Observable<?>> apiObservables;
    private CatApiRestRx getData;

    public DownloadDataRx() {
        apiObservables = new LruCache<>(10);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PARAM_BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        getData = retrofit.create(CatApiRestRx.class);
        //Observable<CatApiResponse> observableRetrofit = getCatsData.connect(PARAM_FORMAT_XML,2);

    }

    public CatApiRestRx getAPI(){
        return getData;
    }


    public Observable<?> getPreparedObservable(Observable<?> unPreparedObservable, Class<?> clazz, boolean cacheObservable, boolean useCache){

        Observable<?> preparedObservable = null;

        if(useCache)//this way we don't reset anything in the cache if this is the only instance of us not wanting to use it.
            preparedObservable = apiObservables.get(clazz);

        if(preparedObservable!=null)
            return preparedObservable;

        //we are here because we have never created this observable before or we didn't want to use the cache...

        preparedObservable = unPreparedObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        if(cacheObservable){
            preparedObservable = preparedObservable.cache();
            apiObservables.put(clazz, preparedObservable);
        }
        return preparedObservable;
    }
}
