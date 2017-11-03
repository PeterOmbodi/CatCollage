package com.peterombodi.catcollage.presentation.screen.fragmentCreateCollage;

import com.peterombodi.catcollage.presentation.customView.collageView.ICollageView;

/**
 * Created by Admin on 24.01.2017.
 */

public interface ICreateCollage {
    interface IPresenter {

        void registerView(IView _view);

        void unRegisterView();

        void downloadingSubscribe();

        boolean downloadingDispose();

        void buildCollage(int _density);

        void saveImages();

        void restoreCollage();

        void saveCollage();

    }

    interface IView {

        void setViewsEnabled(boolean _enabled);

        ICollageView getICollageView();

    }
}
