package com.hss01248.pagestate;

import android.view.View;

public abstract class PageStateConfig {

    public abstract void onRetry(View retryView);

    public void onEmtptyViewClicked(View emptyView) {
        onRetry(emptyView);
    }

    public boolean isFirstStateLoading(){
        return true;
    }

    public String emptyMsg(){
        return "";
    }

    public boolean darkMode(){
        return PageStateManager.isDarkMode;
    }

    public int customLoadingLayoutId() {
        return PageStateManager.BASE_LOADING_LAYOUT_ID;
    }

    public int customErrorLayoutId() {
        return PageStateManager.BASE_RETRY_LAYOUT_ID;
    }

    public int customEmptyLayoutId() {
        return PageStateManager.BASE_EMPTY_LAYOUT_ID;
    }

    public boolean showProgress(View emptyView,int progress){
        return false;
    }





}