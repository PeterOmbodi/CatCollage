package com.peterombodi.catcollage.presentation.screen.collage_create;

import android.graphics.Bitmap;

import com.peterombodi.catcollage.data.model.CatApiResponse;
import com.peterombodi.catcollage.database.model.CollageItem;
import com.peterombodi.catcollage.presentation.base.IBaseModel;
import com.peterombodi.catcollage.presentation.base.IBasePresenter;
import com.peterombodi.catcollage.presentation.base.IBaseView;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Admin on 24.01.2017.
 */

public interface CollageContract {

    interface CollagePresenter extends IBasePresenter {

        void registerFragment(CollageContract.CreateCollageView view,
                              CollageContract.CollageModel model,
                              PublishSubject<Integer> downloadingProgress);

        void setCollageDensity(int density);
        void setCollageItems(ArrayList<CollageItem> collageItems);
        void restoreCollage();

        void loadData(int count);
        void downloadingSubscribe();
        boolean disposeDownloading();

        void saveImages();
        void createCollageImage(Bitmap bitmap);
    }

    interface CreateCollageView extends IBaseView<CollagePresenter> {
        void showProgress(boolean show);
        void setViewsEnabled(boolean enabled);
        void buildCollage(int density);

        void setCollageView(ArrayList<CollageItem> collageItems);
        void setItemImage(CollageItem collageItem);
        void shareImage(File file);
    }

    interface CollageModel extends IBaseModel {
        Observable<CatApiResponse> getCats(int count);
    }
}
