package com.hss01248.pagestate.demo;

import android.app.Application;

import com.hss01248.pagestate.demo.pagemanager.MyPageManager;

/**
 * Created by huangshuisheng on 2017/10/16.
 */

public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MyPageManager.initWhenAppOnCreate(getApplicationContext(),R.layout.pager_empty,R.layout.pager_loading,R.layout.pager_error);
    }
}
