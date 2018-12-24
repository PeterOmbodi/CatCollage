package com.peterombodi.catcollage.presentation.screen.collage_create;

import android.util.Log;
import android.widget.ImageView;

import com.peterombodi.catcollage.Application;
import com.peterombodi.catcollage.ObjectGraph;
import com.peterombodi.catcollage.data.api.DownloadDataRx;
import com.peterombodi.catcollage.data.api.DownloadImage;
import com.peterombodi.catcollage.data.model.CatApiResponse;
import com.peterombodi.catcollage.data.model.ItemImage;
import com.peterombodi.catcollage.database.model.CollageItem;
import com.peterombodi.catcollage.presentation.base.BasePresenter;

import org.androidannotations.annotations.Bean;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.PublishSubject;

import static com.peterombodi.catcollage.constants.Constants.STATUS_DOWNLOAD_OK;
import static com.peterombodi.catcollage.constants.Constants.STATUS_WAIT_DOWNLOAD;

/**
 * Created by Admin on 24.01.2017.
 */

public class CollagePresenter extends BasePresenter implements CollageContract.CollagePresenter {


    private static final String TAG = "CreateCollagePresenter";

//    @Bean
//    ObjectGraph mGraph;


    private CollageContract.CreateCollageView view;
    private CollageContract.CollageModel model;

    private DownloadDataRx downloadDataRx;
    private Disposable subscription;
    private PublishSubject<Integer> subjectLoadImage;

    private DownloadImage downloadImage;
    private int itemsDownloaded;
    private int itemsNotDownloaded;
    private ArrayList<CollageItem> collageItemList;

    public CollagePresenter() {
    }

    @Override
    public void registerFragment(CollageContract.CreateCollageView view, CollageContract.CollageModel model) {
        this.view = view;
        this.model = model;
        this.view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        if (downloadDataRx == null) {
            //downloadDataRx = mGraph.getDownloadDataRx();
        }
        if (downloadImage == null) {
            downloadImage = new DownloadImage();
            subjectLoadImage = downloadImage.getPublishSubject();
        }
    }

    @Override
    public void downloadingSubscribe() {
        if (subjectLoadImage == null) subjectLoadImage = downloadImage.getPublishSubject();
        subscription = subjectLoadImage.subscribe(this::progressCheck);
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
        view.buildCollage(density);
    }

    @Override
    public void setCollage(ArrayList<CollageItem> collageItems) {
        collageItemList = collageItems;
    }


    @Override
    public void saveImages() {

    }

    @Override
    public void restoreCollage() {
        if (collageItemList != null && collageItemList.size()>0) {
            view.setCollageView(collageItemList);
            for (CollageItem item : collageItemList) {
                if (item.getUrl() != null) downloadItemImage(item);
            }
        } else {
            view.buildCollage(2);
        }
    }


    public void loadData(int count) {
        view.setViewsEnabled(false);
        subscription = subjectLoadImage.subscribe(this::progressCheck);

        Observable<CatApiResponse> observableRetrofit =
                downloadDataRx.getPreparedObservable(downloadDataRx.getAPI().getCats("xml", count, "small"),
                        CatApiResponse.class, true, false);

        observableRetrofit.subscribe(new DisposableObserver<CatApiResponse>() {
            @Override
            public void onComplete() {
                Log.d(TAG, "CatApiResponse.onComplete:");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "CatApiResponse.onError: " + e.toString());
            }

            @Override
            public void onNext(CatApiResponse response) {
                downloadImages(response.getImageList());
            }
        });
    }

    private void downloadImages(List<ItemImage> itemImages) {
        int i = 0;
        boolean waitForDownload = false;
        for (CollageItem item : collageItemList) {
            if (item.getLoadStatus() != STATUS_DOWNLOAD_OK) {
                waitForDownload = true;
                item.setUrl(itemImages.get(i++).getUrl());
                downloadItemImage(item);
            }
        }
        view.setViewsEnabled(!waitForDownload);
    }

    private void downloadItemImage(CollageItem collageItem) {
        ImageView imageView = view.getItemPlaceholder(collageItem.getViewId());
        if (imageView != null) {
            imageView.setTag(collageItem.getUrl());
            downloadImage.downloadImage(imageView, collageItem);
        } else {
            Log.d(TAG, "downloadImages: imageSwitcher = null");
        }
    }


    private void progressCheck(int _next) {
        boolean waitForDownload = false;
        //ArrayList<CollageItem> collageItemList = view.getItemList();
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
