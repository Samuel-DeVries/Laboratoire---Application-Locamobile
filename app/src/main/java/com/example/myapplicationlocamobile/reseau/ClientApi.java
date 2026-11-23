package com.example.myapplicationlocamobile.reseau;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ClientApi {
    private static final String URL_BASE = "http://192.168.2.155:8080/";
    private static LocaMobileApi instence;
    public static synchronized LocaMobileApi get() {
        if (instence == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            instence = retrofit.create(LocaMobileApi.class);
        }
        return instence;
    }
}
