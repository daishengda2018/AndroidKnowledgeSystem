package com.example.dsd.demo.ipc.binder;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 参见 《Android 开发艺术探索》第二章P48
 * Created by im_dsd on 2019-09-28
 */
public class Book implements Parcelable {
    public String bookId;
    public String bookName;

    protected Book(Parcel in) {
        bookId = in.readString();
        bookName = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookId);
        dest.writeString(bookName);
    }
}
