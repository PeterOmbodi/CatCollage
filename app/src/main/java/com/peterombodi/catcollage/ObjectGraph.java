package com.peterombodi.catcollage;

import android.content.Context;

import com.peterombodi.catcollage.data.api.DownloadDataRx;
import com.peterombodi.catcollage.presentation.screen.fragmentCreateCollage.CreateCollagePresenter;

/**
 * Created by Admin on 10.01.2017.
 */

public final class ObjectGraph {

    private static final String TAG = "ObjectGraph";
    private static ObjectGraph graph;

    public static final ObjectGraph getInstance(final Context _context) {
        if (graph == null) {
            graph = new ObjectGraph(_context);
        }
        return graph;
    }

    private final DownloadDataRx mDownloadDataRx;
    private final CreateCollagePresenter mCreateCollagePresenter;

    public ObjectGraph(final Context _context){
        mDownloadDataRx = new DownloadDataRx();
        mCreateCollagePresenter = new CreateCollagePresenter();
    }

    public final DownloadDataRx getDownloadDataRx() {
        return mDownloadDataRx;
    }


    public final CreateCollagePresenter getCreateCollagePresenter(){
        return mCreateCollagePresenter;
    }

}
