package com.peterombodi.catcollage.utils.picasso;

import android.widget.ImageView;

import com.peterombodi.catcollage.database.model.CollageItem;

import io.reactivex.subjects.PublishSubject;

/**
 * @author Peter Ombodi (Created on 26.12.2018).
 * Email:  p.ombodi@gmail.com.com
 */
public interface DownloadImage {

    PublishSubject<Integer> getDownloadingProgress();

    void downloadImage(ImageView view, CollageItem collageItem);
}
