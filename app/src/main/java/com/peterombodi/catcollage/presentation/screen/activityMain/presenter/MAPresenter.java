package com.peterombodi.catcollage.presentation.screen.activityMain.presenter;

import android.graphics.Color;
import android.util.Log;

import com.peterombodi.catcollage.Application;
import com.peterombodi.catcollage.ObjectGraph;
import com.peterombodi.catcollage.data.api.DownloadDataRx;
import com.peterombodi.catcollage.presentation.customView.collageView.ICollageView;
import com.peterombodi.catcollage.presentation.screen.activityMain.IMainActivity;

import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Admin on 23.01.2017.
 */

public class MAPresenter implements IMainActivity.IPresenter {

    private static final String TAG = "MAPresenter";
    private IMainActivity.IView mView;
    private DownloadDataRx downloadDataRx;
    private Disposable subscription;
    private PublishSubject<Integer> subject;
    private ICollageView iCollageView;
    private int itemsQuantity;

    @Override
    public void registerView(IMainActivity.IView _view) {
        Log.d(TAG, "registerView: -------------------------");
        this.mView = _view;
        if (downloadDataRx == null) {
            ObjectGraph mGraph = ObjectGraph.getInstance(Application.getContext());
            downloadDataRx = mGraph.getDownloadDataRx();
        }
        iCollageView = mView.getICollageView();
    }

    @Override
    public void unRegisterView() {
        Log.d(TAG, "unRegisterView: -------------------------------");
        mView = null;
    }

    @Override
    public void downloadingSubscribe() {
        // TODO: 09.02.2018 rem
        //if (subject == null) subject = iCollageView.getSubjectLoadImage();
        Log.d(TAG, "downloadingSubscribe: +----");
        subscription = subject.subscribe(this::progressCheck);
    }

    @Override
    public boolean downloadingDispose() {
        boolean isSubscribe = false;
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
            isSubscribe = subscription.isDisposed();
            Log.d(TAG, "downloadingDispose: +---- " + subscription.isDisposed());
        }
        return isSubscribe;
    }

    @Override
    public int getProgressColor(int _progress) {
        int r = 0;
        int g = 0;
        int b = 0;
        if (_progress < 256) {
            b = 255 - _progress % 256;
            r = _progress;
        } else if (_progress < 256 * 2) {
            g = _progress % 256;
            r = 255 - _progress % 256;
        } else if (_progress < 256 * 3) {
            g = 255;
            r = _progress % 256;
        }
        return Color.argb(255, r, g, b);
    }

    @Override
    public void buildCollage(int _density) {
        downloadingDispose();
        iCollageView.setDragEnabled(false);
        iCollageView.setCollage(_density);
    }

//    public void loadData() {
//        mView.setViewsEnabled(false);
//
//        itemsQuantity = iCollageView.getItemsCount();
//        //if (subject == null) subject = iCollageView.getSubjectLoadImage();
//
//        subject = iCollageView.getSubjectLoadImage();
//        subscription = subject.subscribe(this::progressCheck);
//
//        Log.d(TAG, "+---- ---------------------- loadData: itemsCount=" + itemsQuantity + " / subscription.isDisposed() = " + subscription.isDisposed());
//
//        Observable<CatApiResponse> observableRetrofit = (Observable<CatApiResponse>)
//                downloadDataRx.getPreparedObservable(downloadDataRx.getAPI().connect("xml", itemsQuantity, "small"),
//                        CatApiResponse.class, true, false);
//
//        observableRetrofit.subscribe(new DisposableObserver<CatApiResponse>() {
//
//            @Override
//            public void onComplete() {
//                Log.d("CatApiResponse", "onComplete: ----------------------");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.d("CatApiResponse", "-------------------- onError: " + e.toString());
//            }
//
//            @Override
//            public void onNext(CatApiResponse response) {
//                List<Image> imageList = response.getImageList();
//                Log.d("CatApiResponse", "------------------------ images.size() = " + imageList.size());
//                iCollageView.downloadImages(imageList);
//
//            }
//        });
//    }

    private void progressCheck(int _next) {
        switch (_next) {
            case 0:
            case -1:
                itemsQuantity--;
                mView.setViewsEnabled(itemsQuantity == 0);
                break;
            default:
                mView.setViewsEnabled(false);
                itemsQuantity = _next;
        }

        Log.d(TAG, "+----progressCheck: itemsQuantity = " + itemsQuantity + " / _next = " + _next);

    }



}
