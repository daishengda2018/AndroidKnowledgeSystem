package com.example.dsd.demo.ipc.binder;

import java.util.List;

/**
 * todo
 * Created by im_dsd on 2019-09-28
 */
public interface IBookManager {
    List<Book> getBookList();

    void addBook(Book book);
}
