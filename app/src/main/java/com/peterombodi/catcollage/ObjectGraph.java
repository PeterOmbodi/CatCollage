package com.peterombodi.catcollage;

import android.content.Context;
import android.util.Log;

import com.peterombodi.catcollage.data.api.DownloadDataRx;

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

    private final DownloadDataRx downloadDataRx;

    public ObjectGraph(final Context _context){
        downloadDataRx = new DownloadDataRx();
        Log.d(TAG, "ObjectGraph: -----------------------------------");
    }

    public final DownloadDataRx getDownloadDataRx() {
        Log.d(TAG, "getDownloadDataRx: ---------------------------------");
        return downloadDataRx;
    }

}
