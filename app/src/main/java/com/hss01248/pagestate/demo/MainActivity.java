package com.hss01248.pagestate.demo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hss01248.pagestate.demo.pagemanager.MyPageListener;
import com.hss01248.pagestate.demo.pagemanager.MyPageManager;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    MyPageManager pageStateManager;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    private void init() {
        pageStateManager = MyPageManager.init(this, new MyPageListener() {
            @Override
            protected void onReallyRetry() {
                doNet();

            }

            @Override
            public void onEmtptyViewClicked(View emptyView) {
                super.onEmtptyViewClicked(emptyView);

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

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int  state = new Random().nextInt(3);
                switch (state){
                    case 0:
                        pageStateManager.showError("error occured!!!!!!!!!!!!");
                        break;
                    case 1:
                        pageStateManager.showEmpty("没有东西,惊喜不惊喜?\n你可以点击重试一下");
                        break;
                    case 2:
                        pageStateManager.showContent();
                }

            }
        },2000);
    }
}
