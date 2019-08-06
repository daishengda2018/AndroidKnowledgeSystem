package com.example.dsd.demo.other;

import android.net.Uri;

import java.util.Collections;
import java.util.List;

/**
 * @author DSD on 04/01/2019
 * @see
 * @since
 */
public class CatHelper {
    private Api mApi;

    public interface CutestCatCallback {
        void onCutestCatSaved(Uri uri);

        void onQueryFailed(Exception e);
    }

    public void saveTheCutestCat(String query, final CutestCatCallback cutestCatCallback) {
        mApi.queryCats(query, new Api.CatsQueryCallback() {
            @Override
            public void onCatListReceived(List<Cat> cats) {
//                Cat cutest = findCutest(cats);
//                Uri uri = mApi.store(cutest);
//                cutestCatCallback.onCutestCatSaved(uri);
            }

            @Override
            public void onQueryFailed(Exception e) {
                cutestCatCallback.onQueryFailed(e);
            }
        });
    }

    public Cat findCutest(List<Cat> cats) {
        return Collections.max(cats);
    }
}
