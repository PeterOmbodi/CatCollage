package com.peterombodi.catcollage.presentation.screen.collage_create;

import android.widget.ImageView;

import com.peterombodi.catcollage.database.model.CollageItem;
import com.peterombodi.catcollage.presentation.base.IBaseModel;
import com.peterombodi.catcollage.presentation.base.IBasePresenter;
import com.peterombodi.catcollage.presentation.base.IBaseView;

import java.util.ArrayList;

/**
 * Created by Admin on 24.01.2017.
 */

public interface CollageContract {

    interface CollagePresenter extends IBasePresenter {

        void registerFragment(CollageContract.CreateCollageView view, CollageContract.CollageModel model);
        void setCollageDensity(int density);
        void setCollage(ArrayList<CollageItem> collageItems);

        void loadData(int count);
        void downloadingSubscribe();
        boolean disposeDownloading();

        void saveImages();
        void restoreCollage();

    }

    interface CreateCollageView extends IBaseView<CollagePresenter> {
        void setViewsEnabled(boolean enabled);
        void buildCollage(int density);

        void setCollageView(ArrayList<CollageItem> collageItems);

        ImageView getItemPlaceholder(int viewId);
    }

    interface CollageModel extends IBaseModel {

    }
}
