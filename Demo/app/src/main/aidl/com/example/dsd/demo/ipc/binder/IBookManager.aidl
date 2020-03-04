package com.example.dsd.demo.ipc.binder;

import java.util.List;
// 此处引入的是相同目录下的 Book.aidl 文件
import com.example.dsd.demo.ipc.binder.Book;

/**
 * aidl 的 interface 没有 public 功能
 * Created by im_dsd on 2019-09-28
 */
interface IBookManager {
    List<Book> getBookList();

    /**
     AIDL中的定向 tag 表示了在跨进程通信中数据的流向。其中，数据流向是针对在客户端中的那个传入方法的对象而言的。

            in ：表示数据只能由客户端流向服务端。
            　　表现为服务端将会接收到一个那个对象的完整数据，但是客户端的那个对象不会因为服务端对传参的修改而发生变动。
            　　
            out ：表示数据只能由服务端流向客户端。
            　　表现为服务端将会接收到那个对象的的空对象，但是在服务端对接收到的空对象有任何修改之后客户端将会同步变动。
            　　
            inout ：表示数据可在服务端与客户端之间双向流通。
            　　表现为服务端将会接收到客户端传来对象的完整信息，并且客户端将会同步服务端对该对象的任何变动。
    */
    void addBook(in Book book);
}
