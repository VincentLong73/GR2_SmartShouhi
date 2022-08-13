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
import retrofit2.http.POST;

public interface UserDbApi {
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-mm-dd HH:mm:ss")
            .create();

    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(600, TimeUnit.SECONDS)
            .readTimeout(600, TimeUnit.SECONDS)
            .writeTimeout(600, TimeUnit.SECONDS)
            .build();

    UserDbApi databaseApi = new Retrofit.Builder()
            .baseUrl("http://192.168.1.2/do_an/user/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(UserDbApi.class);

    @FormUrlEncoded
    @POST("update_password.php")
    Call<ResponseBody> updatePassword(@Field("email") String email,
                                      @Field("pass_word_old") String passWordOld,
                                      @Field("pass_word_new") String passWordNew );

    @FormUrlEncoded
    @POST("update_profile_user.php")
    Call<ResponseBody> updateProfile(@Field("email") String email,
                                      @Field("userName") String userName,
                                      @Field("fullName") String fullName,
                                      @Field("phone") String phone,
                                      @Field("dob") String dob);

    @FormUrlEncoded
    @POST("get_user_by_email.php")
    Call<ResponseBody> getUserByEmail(@Field("email") String email);

}
