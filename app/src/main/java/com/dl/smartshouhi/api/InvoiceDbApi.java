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
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface InvoiceDbApi {

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-mm-dd HH:mm:ss")
            .create();

    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(600, TimeUnit.SECONDS)
            .readTimeout(600, TimeUnit.SECONDS)
            .writeTimeout(600, TimeUnit.SECONDS)
            .build();

    InvoiceDbApi databaseApi = new Retrofit.Builder()
            .baseUrl("http://192.168.1.2/do_an/invoice/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(InvoiceDbApi.class);
    @Headers({"Accept: application/json"})
    @GET("get_invoice_by_userId.php")
    Call<ResponseBody> getListInvoice(@Query("id") int userId);

    @Headers({"Accept: application/json"})
    @GET("get_invoice_by_userId_year.php")
    Call<ResponseBody> getListInvoiceYear(@Query("id") int userId,
                                          @Query("year") int year);

    @FormUrlEncoded
    @POST("update_invoice.php")
    Call<ResponseBody> updateInvoice(@Field("invoiceId") int invoiceId,
                                     @Field("seller") String seller,
                                     @Field("address") String address,
                                     @Field("totalcost") String totalcost,
                                     @Field("timestamp") String timestamp);

    @FormUrlEncoded
    @POST("add_invoice.php")
    Call<ResponseBody> addInvoice(@Field("uid") int uid,
                                     @Field("seller") String seller,
                                     @Field("address") String address,
                                     @Field("totalcost") String totalcost,
                                     @Field("timestamp") String timestamp);

    @FormUrlEncoded
    @POST("delete_invoice.php")
    Call<ResponseBody> deleteInvoice(@Field("invoiceId") int invoiceId);
}
