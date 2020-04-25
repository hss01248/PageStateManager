package com.hss01248.pagestate.demo;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hss01248.pagestate.PageStateConfig;
import com.hss01248.pagestate.StatefulFrameLayout;

import java.util.Random;

/**
 * time:2020/4/25
 * author:hss
 * desription:
 */
public class XmlActivity extends AppCompatActivity {

    StatefulFrameLayout statefulFrameLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inxml);
        statefulFrameLayout = (StatefulFrameLayout)findViewById(R.id.pager);
        statefulFrameLayout.init(new PageStateConfig() {
            @Override
            public void onRetry(View retryView) {
                doNet();
            }
        });
        doNet();



    }

    private void doNet() {
        statefulFrameLayout.showLoading();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int  state = new Random().nextInt(3);
                switch (state){
                    case 0:
                        statefulFrameLayout.showError("稍候重试222222");
                        break;
                    case 1:
                        statefulFrameLayout.showEmpty();
                        break;
                    case 2:
                        statefulFrameLayout.showContent();
                }

            }
        },2000);
    }
}
