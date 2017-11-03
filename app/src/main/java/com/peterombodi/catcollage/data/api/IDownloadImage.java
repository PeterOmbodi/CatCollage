package com.peterombodi.catcollage.data.api;

import android.widget.ImageView;

import com.peterombodi.catcollage.database.model.CollageItem;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by Admin on 25.01.2017.
 */

public interface IDownloadImage {

    PublishSubject<Integer> getPublishSubject();

    void downloadImage(ImageView _view, CollageItem _item);

}
