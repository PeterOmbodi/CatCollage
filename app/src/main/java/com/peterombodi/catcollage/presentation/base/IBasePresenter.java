package com.peterombodi.catcollage.presentation.base;

import io.reactivex.disposables.CompositeDisposable;

/**
 * @author Peter Ombodi (Created on 19.12.2018).
 * Company: p.ombodi
 * Email:  p.ombodi@gmail.com
 */
public interface IBasePresenter {
    void subscribe();
    void unsubscribe();
    CompositeDisposable getCompositeSubscriptions();
}
