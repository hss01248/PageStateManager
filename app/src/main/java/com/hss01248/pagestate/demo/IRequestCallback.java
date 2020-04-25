package com.hss01248.pagestate.demo;

/**
 * time:2020/4/26
 * author:hss
 * desription:
 */
public interface IRequestCallback {
    void onSuccess();
    void onError(String msg);
    void onEmpty();

}
