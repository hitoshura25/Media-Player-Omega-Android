package com.vmenon.mpo.service;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceFactory {
    private final static String endpoint = "http://ec2-54-209-237-209.compute-1.amazonaws.com/";

    public static MediaPlayerOmegaService newInstance() {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(endpoint)
                .build();

        return retrofit.create(MediaPlayerOmegaService.class);
    }
}
