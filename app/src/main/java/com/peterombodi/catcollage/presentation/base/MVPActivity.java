package com.peterombodi.catcollage.presentation.base;

import android.support.v7.app.AppCompatActivity;

/**
 * @author Peter Ombodi (Created on 19.12.2018).
 * Email:  p.ombodi@gmail.com.com
 */
public abstract class MVPActivity<T extends IBasePresenter> extends AppCompatActivity implements IBaseView<T> {

    protected T presenter;

    @Override
    public void setPresenter(T presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribe();
        presenter = null;
    }
}

