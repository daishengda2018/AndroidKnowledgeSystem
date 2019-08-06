package com.example.dsd.demo.other;

import android.net.Uri;

import java.util.List;

/**
 * @author DSD on 04/01/2019
 * @see
 * @since
 */
public interface Api {
    interface CatsQueryCallback {
        void onCatListReceived(List<Cat> cats);

        void onQueryFailed(Exception e);
    }

    interface StoreCallback {
        void onCatStored(Uri uri);

        void onStoreFailed(Exception e);
    }

    void queryCats(String query, CatsQueryCallback catsQueryCallback);

    Uri store(Cat cat, StoreCallback storeCallback);
}
