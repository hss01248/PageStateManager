package com.hss01248.pagestate;

/**
 * time:2020/4/25
 * author:hss
 * desription:
 */
public interface IViewState {

     void showLoading();
     void showError(CharSequence msg);
     void showContent();
     void showEmpty();
}
