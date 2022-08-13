package com.dl.smartshouhi.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AdminDbApi {

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-mm-dd HH:mm:ss")
            .create();

    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(600, TimeUnit.SECONDS)
            .readTimeout(600, TimeUnit.SECONDS)
            .writeTimeout(600, TimeUnit.SECONDS)
            .build();

    AdminDbApi databaseApi = new Retrofit.Builder()
            .baseUrl("http://192.168.1.2/do_an/admin/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(AdminDbApi.class);

    @Headers({"Accept: application/json"})
    @GET("get_count_user_invoice_item.php")
    Call<ResponseBody> getCountUserInvoiceItem();

    @Headers({"Accept: application/json"})
    @GET("get_user_not_admin.php")
    Call<ResponseBody> getUserNotAdmin();

    @Headers({"Accept: application/json"})
    @GET("get_user_by_admin.php")
    Call<ResponseBody> getUserAdmin();

    @Headers({"Accept: application/json"})
    @GET("get_user_not_active.php")
    Call<ResponseBody> getUserNotActive();


    @FormUrlEncoded
    @POST("block_user.php")
    Call<ResponseBody> blockUser(@Field("email") String email,
                                 @Field("email_admin") String emailAdmin);

    @FormUrlEncoded
    @POST("unblock_user.php")
    Call<ResponseBody> unblockUser(@Field("email") String email,
                                   @Field("email_admin") String emailAdmin);
}
