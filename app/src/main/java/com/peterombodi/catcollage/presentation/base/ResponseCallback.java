package com.peterombodi.catcollage.presentation.base;

/**
 * Created by Admin on 04.01.2017.
 */

public interface ResponseCallback<V> {

    void onRefreshResponse(V _data);

    void onRefreshFailure();

}
