package com.peterombodi.catcollage.data.api;

import com.peterombodi.catcollage.data.model.CatApiResponse;
import com.peterombodi.catcollage.presentation.base.ResponseCallback;

/**
 * Created by Admin on 04.01.2017.
 */

public interface DownloadData {
    void downloadData(ResponseCallback<CatApiResponse> _callback,int _cnt);

}
