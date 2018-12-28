package com.peterombodi.catcollage.utils.picasso;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.peterombodi.catcollage.R;
import com.peterombodi.catcollage.database.model.CollageItem;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;

import io.reactivex.subjects.PublishSubject;

import static com.peterombodi.catcollage.constants.Constants.STATUS_DOWNLOAD_ERROR;
import static com.peterombodi.catcollage.constants.Constants.STATUS_DOWNLOAD_OK;
import static com.peterombodi.catcollage.constants.Constants.STATUS_WAIT_DOWNLOAD;

/**
 * @author Peter Ombodi (Created on 26.12.2018).
 * Email:  p.ombodi@gmail.com.com
 */
@EBean(scope = EBean.Scope.Singleton)
public class PicassoHelper implements DownloadImage{

    private PublishSubject<Integer> downloadingProgress;

    @AfterInject
    void init(){
        downloadingProgress = PublishSubject.create();
    }

    @Override
    public PublishSubject<Integer> getDownloadingProgress() {
        return downloadingProgress;

    }

    @Override
    public void downloadImage(ImageView view, CollageItem collageItem) {
        collageItem.setLoadStatus(STATUS_WAIT_DOWNLOAD);
        Picasso.get()
                .load(collageItem.getUrl())
                .transform(new CropSquareTransformation())
                .placeholder(R.drawable.ic_download)
                .error(R.drawable.ic_warning)
                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (collageItem.getLoadStatus() != STATUS_DOWNLOAD_OK) {
                            collageItem.setLoadStatus(STATUS_DOWNLOAD_OK);
                            downloadingProgress.onNext(-1);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        if (collageItem.getLoadStatus() != STATUS_DOWNLOAD_ERROR) {
                            collageItem.setLoadStatus(STATUS_DOWNLOAD_ERROR);
                            downloadingProgress.onNext(0);
                        }
                    }
                });
    }

    private class CropSquareTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
            if (result != source) {
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "square()";
        }
    }
}
