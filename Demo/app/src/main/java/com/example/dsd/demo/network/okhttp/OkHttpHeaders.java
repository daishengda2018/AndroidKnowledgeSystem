package com.example.dsd.demo.network.okhttp;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * todo
 * Created by im_dsd on 2019-04-27
 */
public class OkHttpHeaders {
    public static void main(String args[]) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
            .url("https://api.github.com/repos/square/okhttp/issues")
            //If there are exciting values, they will removed before the new value is added;
            .header("User-Agent", "OkHttp Headers.java")
            //This method only add a header without removing the headers already present.
            .addHeader("Accept", "application/json; q=5")
            .addHeader("Accept", "application/json; v3+json")
            .build();

        //create a realCall
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                System.out.println("Server: " + response.header("Server"));
                System.out.println("Date: " + response.header("Date"));
                //To read all the value as a list
                System.out.println("Vary: " + response.headers("Vary"));
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
