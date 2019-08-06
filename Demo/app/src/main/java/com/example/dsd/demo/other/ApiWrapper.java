package com.example.dsd.demo.other;

import android.net.Uri;

import java.util.List;

/**
 * @author DSD on 04/01/2019
 * @see
 * @since
 */
public class ApiWrapper {
    Api mApi;

    public void queryCats(String query, final Callback<List<Cat>> callback) {
        mApi.queryCats(query, new Api.CatsQueryCallback() {
            @Override
            public void onCatListReceived(List<Cat> cats) {
                callback.onResult(cats);
            }

            @Override
            public void onQueryFailed(Exception e) {
                callback.onError(e);
            }
        });
    }

    public void store(Cat cat, final Callback<Uri> uriCallback) {
        mApi.store(cat, new Api.StoreCallback() {

            @Override
            public void onCatStored(Uri uri) {
                uriCallback.onResult(uri);
            }

            @Override
            public void onStoreFailed(Exception e) {
                uriCallback.onError(e);
            }
        });
    }
}
