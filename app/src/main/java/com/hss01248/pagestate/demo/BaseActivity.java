package com.hss01248.pagestate.demo;

import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hss01248.pagestate.PageStateConfig;
import com.hss01248.pagestate.PageStateManager;

/**
 * time:2020/4/26
 * author:hss
 * desription:
 */
public class BaseActivity extends AppCompatActivity {

    BaseConfig config;
    TextView titlebar;
    LinearLayout linearLayout;
    PageStateManager stateManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);
        linearLayout = (LinearLayout) findViewById(R.id.ll_root);
        titlebar = (TextView) findViewById(R.id.titlebar);
        config = new BaseConfig();
        configPage(config);

        applyConfig(config);
    }

    protected void requestData(IRequestCallback callback){

    }

    private void applyConfig(BaseConfig config) {
        if(config.hasPageStatus ){
            stateManager = PageStateManager.initWhenUse(linearLayout, new PageStateConfig() {
                @Override
                public void onRetry(View retryView) {
                    stateManager.showLoading();
                    doNet();
                }
            });
            doNet();
        }
    }

    private void doNet() {
        requestData(new IRequestCallback() {
            @Override
            public void onSuccess() {
                stateManager.showContent();
            }

            @Override
            public void onError(String msg) {
                stateManager.showError(msg);
            }

            @Override
            public void onEmpty() {
                stateManager.showEmpty();
            }
        });
    }

    protected void configPage(BaseConfig config) {
        config.hasPageStatus = true;
    }

    public static class BaseConfig{
        boolean hasPageStatus = true;
        boolean hasTitlebar = true;
        PageStateConfig stateConfig;
    }
}
