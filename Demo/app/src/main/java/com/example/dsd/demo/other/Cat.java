package com.example.dsd.demo.other;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/**
 * @author DSD on 04/01/2019
 * @see
 * @since
 */
public class Cat implements Comparable<Cat> {
    private Bitmap mBitmap;
    private int cuteness;

    @Override
    public int compareTo(@NonNull Cat other) {
        return Integer.compare(cuteness,other.cuteness);
    }
}
