package com.peterombodi.catcollage.presentation.base;

import android.support.v4.app.Fragment;

import com.peterombodi.catcollage.utils.KeyboardManager;

import org.androidannotations.annotations.EFragment;

/**
 * @author Peter Ombodi (Created on 19.12.2018).
 * Email:  p.ombodi@gmail.com.com
 */
@EFragment
public abstract class MVPFragment<T extends IBasePresenter> extends Fragment implements IBaseView<T> {

    protected T presenter;

    @Override
    public void setPresenter(T presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.unsubscribe();
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        presenter = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        KeyboardManager.hideKeyboard(getActivity());
    }
}
