package com.hss01248.pagestate.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.hss01248.pagestate.demo.pagemanager.MyPageListener;
import com.hss01248.pagestate.demo.pagemanager.MyPageManager;

import java.util.Random;

/**
 * Created by huangshuisheng on 2017/10/16.
 */

public class CustomUIActy extends Activity {

    MyPageManager pageStateManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        doNet();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        pageStateManager = MyPageManager.init(this, new MyPageListener() {
            @Override
            protected void onReallyRetry() {
                doNet();
            }

            @Override
            public int generateEmptyLayoutId() {
                return R.layout.pager_empty_aku;
            }

            @Override
            public int generateLoadingLayoutId() {
                return R.layout.pager_loading_aku;
            }

            @Override
            public int generateRetryLayoutId() {
                return R.layout.pager_error_aku;
            }
        });
    }

    private void doNet() {
        pageStateManager.showLoading();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int  state = new Random().nextInt(3);
                switch (state){
                    case 0:
                        pageStateManager.showError();
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
