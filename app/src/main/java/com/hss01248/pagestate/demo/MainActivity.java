package com.hss01248.pagestate.demo;

import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.hss01248.pagestate.PageStateConfig;
import com.hss01248.pagestate.PageStateManager;


import java.util.Random;

public class MainActivity extends AppCompatActivity {

    PageStateManager pageStateManager;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    private void init() {
        pageStateManager = PageStateManager.initWhenUse(this, new PageStateConfig() {


            @Override
            public String emptyMsg() {
                return "自定义的空msg\n你可以点击重试一下";
            }

            @Override
            public void onRetry(View retryView) {
                doNet();
            }

            @Override
            public void onEmtptyViewClicked(View emptyView) {
                super.onEmtptyViewClicked(emptyView);

                doNet();
            }

            @Override
            public boolean darkMode() {
                return true;
            }

            @Override
            public boolean isFirstStateLoading() {
                return false;
            }
        });
        doNet();

    }

    @Override
    protected void onResume() {
        super.onResume();

       // doNet();




    }

    private void doNet() {
        pageStateManager.showLoading(50);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int  state = new Random().nextInt(3);
                switch (state){
                    case 0:
                        pageStateManager.showError("error occured!!!!!!!!!!!!");
                        break;
                    case 1:
                        pageStateManager.showEmpty();
                        break;
                    case 2:
                        pageStateManager.showContent();
                }

            }
        },2000);
    }
}
