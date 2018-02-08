// BookController.aidl
package com.example.zijuan.aidltest;
import com.example.zijuan.aidltest.Book;

interface BookController {

    List<Book> getBookList();

    void addBookInOut(inout Book book);

}
