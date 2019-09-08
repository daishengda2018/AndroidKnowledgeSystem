package com.example.dsd.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.example.dsd.demo.handler.HandlerDemo;

/**
 * create by DSD
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new HandlerDemo().startThreadHandler();
        /*
        try {
            InputStream certificate = getResources().getAssets().open("for_server.cer");
            InputStream bksStream = getResources().getAssets().open("for_client.bks");
            HttpsUtils.SslParam sslParam = HttpsUtils.getInstance().generateSslParam(certificate, bksStream, "123456");
            OkHttpClient client = new OkHttpClient.Builder()
                                    .hostnameVerifier(new HostnameVerifier() {
                                        @Override
                                        public boolean verify(String hostname, SSLSession session) {
                                            return true;
                                        }
                                    })
                                    .sslSocketFactory(sslParam.socketFactory, sslParam.trustManagers)
                                    .build();
            Retrofit retrofit = new Retrofit.Builder().client(client)
                                    .baseUrl("https://10.0.0.188:8443").build();
            RetrofitDemoApi service = retrofit.create(RetrofitDemoApi.class);
            Call<ResponseBody> call = service.httpz();
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("------", response.toString());
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("------ error", t.toString());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

      */
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
