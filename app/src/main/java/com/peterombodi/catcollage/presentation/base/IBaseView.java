package com.peterombodi.catcollage.presentation.base;

/**
 * @author Peter Ombodi (Created on 19.12.2018).
 * Email:  p.ombodi@gmail.com.com
 */
public interface IBaseView<T extends IBasePresenter> {
    void initPresenter();
    void setPresenter(T presenter);
    String getScreenName();
}
