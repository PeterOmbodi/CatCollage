package com.peterombodi.catcollage.data.api;

import com.peterombodi.catcollage.data.model.CatApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Admin on 04.01.2017.
 */

public interface CatApiRest {
    //format=xml&results_per_page=20
    @GET("/api/images/get")
    Call<CatApiResponse> connect(@Query("format") String _format,
                                 @Query("results_per_page") int _cnt);
}
