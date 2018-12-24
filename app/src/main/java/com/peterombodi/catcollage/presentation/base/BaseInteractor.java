package com.peterombodi.catcollage.presentation.base;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Peter Ombodi (Created on 19.12.2018).
 * Company: p.ombodi
 * Email:  p.ombodi@gmail.com
 */
public abstract class BaseInteractor {
    public static <T> Observable<T> getAsyncObservable(Observable<T> observable) {
        return observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public static Completable getAsyncCompletable(Completable completable) {
        return completable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public static <T> Single<T> getAsyncSingle(Single<T> single) {
        return single.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public static <T> Maybe<T> getAsyncMaybe(Maybe<T> maybe) {
        return maybe.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public static <T> Flowable<T> getAsyncFlowable(Flowable<T> flowable) {
        return flowable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }
}
