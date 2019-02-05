package com.peterombodi.catcollage.presentation.screen.collage_create;

import android.graphics.Bitmap;
import android.util.Log;

import com.peterombodi.catcollage.data.model.ItemImage;
import com.peterombodi.catcollage.database.model.CollageItem;
import com.peterombodi.catcollage.presentation.base.BasePresenter;
import com.peterombodi.catcollage.utils.Helper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static com.peterombodi.catcollage.constants.Constants.STATUS_DOWNLOAD_OK;
import static com.peterombodi.catcollage.constants.Constants.STATUS_WAIT_DOWNLOAD;

/**
 * Created by Admin on 24.01.2017.
 */

public class CollagePresenter extends BasePresenter implements CollageContract.CollagePresenter {


    private static final String TAG = "CreateCollagePresenter";

    private CollageContract.CreateCollageView view;
    private CollageContract.CollageModel model;

    private Disposable subscription;
    private PublishSubject<Integer> downloadingProgress;

    private int itemsDownloaded;
    private int itemsNotDownloaded;
    private ArrayList<CollageItem> collageItemList;

    public CollagePresenter() {
    }

    @Override
    public void registerFragment(CollageContract.CreateCollageView view, CollageContract.CollageModel model, PublishSubject<Integer> downloadingProgress) {
        this.view = view;
        this.model = model;
        this.downloadingProgress = downloadingProgress;
        this.view.setPresenter(this);
    }

    @Override
    public void subscribe() {
//        if (downloadImage == null) {
//            downloadImage = new DownloadImage();
//        }
        restoreCollage();
    }

    @Override
    public void downloadingSubscribe() {
        subscription = downloadingProgress.subscribe(this::progressCheck);
    }

    @Override
    public boolean disposeDownloading() {
        boolean isDisposed = false;
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
            isDisposed = subscription.isDisposed();
        }
        return isDisposed;
    }

    @Override
    public void setCollageDensity(int density) {
        disposeDownloading();
        view.setViewsEnabled(false);
        view.buildCollage(density);
    }

    @Override
    public void setCollageItems(ArrayList<CollageItem> collageItems) {
        collageItemList = collageItems;
    }


    @Override
    public void saveImages() {

    }

    @Override
    public void createCollageImage(Bitmap bitmap) {
        view.setViewsEnabled(false);
        compositeSubscriptions.add(
                Helper.saveFile(bitmap, "catsCollage.jpg")
                        .subscribe(file -> {
                            view.shareImage(file);
                            view.setViewsEnabled(true);
                        }));
    }

    @Override
    public void restoreCollage() {
        if (collageItemList != null && collageItemList.size() > 0) {
            view.setCollageView(collageItemList);
            for (CollageItem item : collageItemList) {
                if (item.getUrl() != null) view.setItemImage(item);
            }
        } else {
           // view.buildCollage(2);
        }
    }


    public void loadData(int count) {
        view.setViewsEnabled(false);
        downloadingSubscribe();
        compositeSubscriptions.add(
                model.getCats(count)
                        .subscribe(response -> downloadImages(response.getImageList()),
                                throwable ->
                                        Log.d(TAG, "load data  error: " + throwable.getMessage())
                        ));
    }

    private void downloadImages(List<ItemImage> itemImages) {
        int i = 0;
        boolean waitForDownload = false;
        for (CollageItem item : collageItemList) {
            if (item.getLoadStatus() != STATUS_DOWNLOAD_OK) {
                waitForDownload = true;
                item.setUrl(itemImages.get(i++).getUrl());
                view.setItemImage(item);
            }
        }
        view.setViewsEnabled(!waitForDownload);
    }

    private void progressCheck(int _next) {
        boolean waitForDownload = false;
        for (CollageItem item : collageItemList) {
            if (item.getLoadStatus() == STATUS_WAIT_DOWNLOAD) {
                waitForDownload = true;
                break;
            }
        }
        view.setViewsEnabled(!waitForDownload);
        switch (_next) {
            case 0:
                itemsNotDownloaded++;
                break;
            case -1:
                itemsDownloaded++;
                break;
        }
    }
}
