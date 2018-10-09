package com.peterombodi.catcollage.presentation.screen.fragmentCreateCollage;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.peterombodi.catcollage.Application;
import com.peterombodi.catcollage.ObjectGraph;
import com.peterombodi.catcollage.data.api.DownloadDataRx;
import com.peterombodi.catcollage.data.api.DownloadImage;
import com.peterombodi.catcollage.data.model.CatApiResponse;
import com.peterombodi.catcollage.data.model.ItemImage;
import com.peterombodi.catcollage.database.model.CollageItem;
import com.peterombodi.catcollage.database.model.CollageModel;
import com.peterombodi.catcollage.presentation.customView.collageView.ICollageView;
import com.peterombodi.catcollage.presentation.screen.fragmentCreateCollage.ICreateCollage;

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

public class CreateCollagePresenter implements ICreateCollage.IPresenter {
    private static final String TAG = "CreateCollagePresenter";
    private ICreateCollage.IView mView;
    private DownloadDataRx downloadDataRx;
    private Disposable subscription;
    private PublishSubject<Integer> subjectLoadImage;
    private ICollageView iCollageView;
    private DownloadImage downloadImage;
    private int itemsDownloaded;
    private int itemsNotDownloaded;
    private ArrayList<CollageItem> collageItemList;


    @Override
    public void registerView(ICreateCollage.IView _view) {
        this.mView = _view;

        if (downloadDataRx == null) {
            ObjectGraph mGraph = ObjectGraph.getInstance(Application.getContext());
            downloadDataRx = mGraph.getDownloadDataRx();
        }
        if (downloadImage == null) {
            //downloadImage = new DownloadImage(Application.getContext());
            downloadImage = new DownloadImage();
            subjectLoadImage = downloadImage.getPublishSubject();
        }

        iCollageView = mView.getICollageView();
    }

    @Override
    public void unRegisterView() {
        mView = null;
    }

    @Override
    public void downloadingSubscribe() {
        if (subjectLoadImage == null) subjectLoadImage = downloadImage.getPublishSubject();
        subscription = subjectLoadImage.subscribe(this::progressCheck);
    }

    @Override
    public boolean downloadingDispose() {
        boolean isSubscribe = false;
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
            isSubscribe = subscription.isDisposed();
        }
        return isSubscribe;
    }

    @Override
    public void buildCollage(int _density) {
        downloadingDispose();
        iCollageView.setDragEnabled(false);
        iCollageView.setCollage(_density);
    }

    @Override
    public void saveImages() {
        //DatabaseDefinition db = FlowManager.getDatabase(CatCollageDatabase.class);
        CollageModel collageModel = new CollageModel("test", iCollageView.getItemList());
        collageModel.save();
        // TODO: 24.01.2017 STOP!!!
    }

    @Override
    public void restoreCollage() {
        if (collageItemList != null) {
            iCollageView.setItemList(collageItemList);
            for (CollageItem item : collageItemList) {
                if (item.getUrl() != null) downloadItemImage(item);
            }
        }
    }

    @Override
    public void saveCollage() {
        collageItemList = iCollageView.getItemList();
    }

    public void loadData(int count) {
        mView.setViewsEnabled(false);
        subscription = subjectLoadImage.subscribe(this::progressCheck);

        Observable<CatApiResponse> observableRetrofit =
                downloadDataRx.getPreparedObservable(downloadDataRx.getAPI().connect("xml", count, "small"),
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
        ArrayList<CollageItem> collageItemList = iCollageView.getItemList();
        int i = 0;
        boolean waitForDownload = false;
        for (CollageItem item : collageItemList) {
            if (item.getLoadStatus() != STATUS_DOWNLOAD_OK) {
                waitForDownload = true;
                item.setUrl(itemImages.get(i++).getUrl());
                downloadItemImage(item);
            }
        }
        mView.setViewsEnabled(!waitForDownload);
    }

    private void downloadItemImage(CollageItem _item) {
        ImageView imageView = iCollageView.getCollageItemView(_item.getViewId());
        if (imageView != null) {
            imageView.setTag(_item.getUrl());
            downloadImage.downloadImage(imageView, _item);
        } else {
            Log.d(TAG, "downloadImages: imageSwitcher = null");
        }
    }


    private void progressCheck(int _next) {
        boolean waitForDownload = false;
        ArrayList<CollageItem> collageItemList = iCollageView.getItemList();
        for (CollageItem item : collageItemList) {
            if (item.getLoadStatus() == STATUS_WAIT_DOWNLOAD) {
                waitForDownload = true;
                break;
            }
        }
        mView.setViewsEnabled(!waitForDownload);
        switch (_next) {
            case 0:
                itemsNotDownloaded++;
                break;
            case -1:
                itemsDownloaded++;
                break;
        }
        Log.d(TAG, "progressCheck: size = " + iCollageView.getItemsCount() + " / itemsDownloaded = " + itemsDownloaded + " / itemsNotDownloaded = " + itemsNotDownloaded);
    }
}
