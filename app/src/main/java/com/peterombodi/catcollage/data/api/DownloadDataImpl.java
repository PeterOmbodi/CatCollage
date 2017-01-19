package com.peterombodi.catcollage.data.api;

import android.content.Context;
import android.util.Log;

import com.peterombodi.catcollage.Application;
import com.peterombodi.catcollage.data.model.CatApiResponse;
import com.peterombodi.catcollage.presentation.base.ResponseCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by Admin on 03.01.2017.
 */

public class DownloadDataImpl implements DownloadData{

    private static final String PARAM_BASE_URL = "http://thecatapi.com/";
    private static final String TAG = "DownloadDataImpl";
    private static final String PARAM_FORMAT_XML = "xml";
    private Retrofit retrofit;
    private Context context;
    private ResponseCallback<CatApiResponse> callback;


    public DownloadDataImpl() {

        retrofit = new Retrofit.Builder()
                .baseUrl(PARAM_BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        context = Application.getContext();
     }




    @Override
    public void downloadData(ResponseCallback<CatApiResponse> _callback,int _cnt) {
        callback = _callback;
        CatApiRest getData = retrofit.create(CatApiRest.class);
        Call<CatApiResponse> dataResponseCall = getData.connect(PARAM_FORMAT_XML,_cnt);

        dataResponseCall.enqueue(new Callback<CatApiResponse>() {

            @Override
            public void onResponse(Call<CatApiResponse> _call, Response<CatApiResponse> _response) {
                CatApiResponse dataResponse = _response.body();

                if (callback != null) callback.onRefreshResponse(dataResponse);

            }

            @Override
            public void onFailure(Call<CatApiResponse> _call, Throwable _t) {
                Log.d(TAG, "*-* " + "retrofit onRefreshFailure:" + _t.toString());
                if (callback != null) {
                    callback.onRefreshFailure();
                }
            }
        });

    }
}
