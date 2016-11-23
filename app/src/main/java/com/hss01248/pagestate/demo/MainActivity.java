package com.hss01248.pagestate.demo;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.hss01248.pagestate.PageStateManager;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    PageStateManager pageStateManager;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PageStateManager.initInApp(getApplicationContext());

        pageStateManager = PageStateManager.init(this, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,"点击重试了...",Toast.LENGTH_LONG).show();
                doNet();
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();

        doNet();




    }

    private void doNet() {
        pageStateManager.showLoading();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            int  state = new Random().nextInt(3);
                            switch (state){
                                case 0:
                                    pageStateManager.showRetry();
                                    break;
                                case 1:
                                    pageStateManager.showEmpty();
                                    break;
                                case 2:
                                    pageStateManager.showContent();
                            }

                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).run();
    }
}
