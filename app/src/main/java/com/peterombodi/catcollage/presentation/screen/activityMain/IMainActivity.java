package com.peterombodi.catcollage.presentation.screen.activityMain;

import com.peterombodi.catcollage.presentation.customView.collageView.ICollageView;

/**
 * Created by Admin on 23.01.2017.
 */

public interface IMainActivity {
    interface IPresenter {
        void registerView(IView _view);

        void unRegisterView();

        void downloadingSubscribe();

        boolean downloadingDispose();

        int getProgressColor(int _progress);

        void buildCollage(int _density);

    }

    interface IView {
        void setViewsEnabled(boolean _enabled);
//        void progressCheck(int _next);
        ICollageView getICollageView();

    }

}
