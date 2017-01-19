package com.peterombodi.catcollage.data.api;

import com.peterombodi.catcollage.data.model.CatApiResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Admin on 06.01.2017.
 */

public interface CatApiRestRx {
    //format=xml&results_per_page=20
    @GET("/api/images/get")
    Observable<CatApiResponse> connect(@Query("format") String _format,
                                       @Query("results_per_page") int _cnt,
                                       @Query("size") String _size);
}
