package com.peterombodi.catcollage.presentation.base;

import io.reactivex.disposables.CompositeDisposable;

/**
 * @author Peter Ombodi (Created on 19.12.2018).
 * Company: p.ombodi
 * Email:  p.ombodi@gmail.com
 */
public abstract class BasePresenter implements IBasePresenter {

    protected CompositeDisposable compositeSubscriptions = new CompositeDisposable();

    @Override
    public void unsubscribe() {
        compositeSubscriptions.clear();
    }

    @Override
    public CompositeDisposable getCompositeSubscriptions() {
        return compositeSubscriptions;
    }
}

