package com.example.dsd.demo.network.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * todo
 * Created by im_dsd on 2019/4/11
 */
public interface RetrofitDemoApi {

    @GET("users/{user}/repos")
    Call<ResponseBody> listRepons(@Path("user") String user);

    @GET("/")
    Call<ResponseBody> httpz();
}
