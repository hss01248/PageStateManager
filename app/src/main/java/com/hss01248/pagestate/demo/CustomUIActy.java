package com.hss01248.pagestate.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.view.View;

import com.hss01248.pagestate.PageStateConfig;
import com.hss01248.pagestate.PageStateManager;


import java.util.Random;

/**
 * Created by huangshuisheng on 2017/10/16.
 */

public class CustomUIActy extends Activity {

    PageStateManager pageStateManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        doNet();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        pageStateManager =   PageStateManager.initWhenUse(this,new PageStateConfig() {

            @Override
            public int customEmptyLayoutId() {
                return R.layout.pager_empty_2;
            }

            @Override
            public void onRetry(View retryView) {
                doNet();
            }

            @Override
            public int customLoadingLayoutId() {
                return R.layout.pager_loading_2;
            }

            @Override
            public int customErrorLayoutId() {
                return R.layout.pager_error_2;
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
                        pageStateManager.showError("稍候重试");
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
