package com.example.dsd.demo.network.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * todo
 * Created by im_dsd on 2019-12-04
 */
public class RetrofitDemo {

    public static void main(String[] args) {
        Retrofit.Builder mBuilder = new Retrofit.Builder();

        Retrofit retrofit = mBuilder.baseUrl("http://baidu.com").build();
        RetrofitDemoApi api = retrofit.create(RetrofitDemoApi.class);
        Call<ResponseBody> call = api.listRepons("123");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
