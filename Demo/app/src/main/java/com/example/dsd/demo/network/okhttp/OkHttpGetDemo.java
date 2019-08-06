package com.example.dsd.demo.network.okhttp;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * todo
 * Created by im_dsd on 2019/4/20
 */
public class OkHttpGetDemo {
    public static void main(String args[]) {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
            .url("https://publicobject.com/helloworld.txt")
            .build();

        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // if this call is canceled, read this body will throws IoException.
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful()) {
                        System.out.println(responseBody.string());
                        Headers headers = response.headers();
                        for (int i = 0; i < headers.size(); i++) {
                            System.out.println(headers.name(i) + "    " + headers.value(i));
                        }
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                }
            }
        });

        //if cancel a call, it will has a IOException -> Canceled
//        call.cancel();
    }
}
