package com.dl.smartshouhi.api;

import com.dl.smartshouhi.model.Invoice;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-mm-dd HH:mm:ss")
            .create();

    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(600, TimeUnit.SECONDS)
            .readTimeout(600, TimeUnit.SECONDS)
            .writeTimeout(600, TimeUnit.SECONDS)
            .build();

    ApiService apiService = new Retrofit.Builder()
//            .baseUrl("http://10.0.3.2:5000/")
            .baseUrl("http://d363-35-187-83-27.ngrok.io/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
            .create(ApiService.class);

    @Headers({"Accept: application/json"})
    @GET("api/get-in4")
    Call<Invoice> getInformationInvoice();

//    @Multipart
//    @POST("api/send-image")
//    Call<Image> getInformationInvoice1(@Part MultipartBody.Part img);

    @Multipart
    @POST("api-get-in4")
    Call<Invoice> getInformationInvoice2(@Part MultipartBody.Part img);
}
