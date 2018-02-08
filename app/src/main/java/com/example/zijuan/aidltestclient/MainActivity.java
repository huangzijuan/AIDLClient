package com.example.zijuan.aidltestclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.zijuan.aidltest.Book;
import com.example.zijuan.aidltest.BookController;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AIDLTestClient";

    private List<Book> bookList = new ArrayList<>();
    private BookController bookController;
    private boolean connect;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bookController = BookController.Stub.asInterface(service);
            connect = true;
            Log.e(TAG, "连接成功" + connect);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connect = false;
            Log.e(TAG, "连接失败" + connect);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.get_book_list).setOnClickListener(this);
        findViewById(R.id.add_book).setOnClickListener(this);

        bindService();
    }

    private void bindService() {
        Intent intent = new Intent();
        //在5.0以后为了确保应用的安全性，系统强制要求使用显式Intent启动或绑定Service，否则会报错：java.lang.IIIegalArgumentException: Service Intent must be explicit
        //intent.setPackage("com.example.zijuan.aidltest");
        //intent.setAction("com.example.zijuan.aidltest.action");
        intent.setComponent(new ComponentName("com.example.zijuan.aidltest", "com.example.zijuan.aidltest.AIDLService"));
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_book_list:
                Toast.makeText(this, "get list", Toast.LENGTH_SHORT).show();
                if (connect) {
                    try {
                        bookList = bookController.getBookList();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    for (Book book : bookList) {
                        Log.e(TAG, book.getName());
                    }
                }
                break;
            case R.id.add_book:
                Toast.makeText(this, "add book", Toast.LENGTH_SHORT).show();
                if (connect) {
                    Book book = new Book("新书");
                    try {
                        bookController.addBookInOut(book);
                        Log.e(TAG, "向服务器添加一本新书 " + book.getName());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connect) {
            unbindService(serviceConnection);
        }
    }
}
