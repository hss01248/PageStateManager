package com.hss01248.pagestate;

import android.view.View;

public abstract class PageListener {


    public abstract void onRetry(View retryView);

    /*public void onNoNetwork(View noNetworkView) {
    }*/

   /* public void onLoading(View loadingView) {
    }*/

    public void onEmtptyViewClicked(View emptyView) {
        onRetry(emptyView);
    }

    public int generateLoadingLayoutId() {
        return PageManager.BASE_LOADING_LAYOUT_ID;
    }

    public int generateRetryLayoutId() {
        return PageManager.BASE_RETRY_LAYOUT_ID;
    }

    public int generateEmptyLayoutId() {
        return PageManager.BASE_EMPTY_LAYOUT_ID;
    }

    public View generateLoadingLayout() {
        return null;
    }

    public View generateRetryLayout() {
        return null;
    }

    public View generateEmptyLayout() {
        return null;
    }

    public boolean isSetLoadingLayout() {
        if (generateLoadingLayoutId() != PageManager.NO_LAYOUT_ID || generateLoadingLayout() != null)
            return true;
        return false;
    }

    public boolean isSetRetryLayout() {
        if (generateRetryLayoutId() != PageManager.NO_LAYOUT_ID || generateRetryLayout() != null)
            return true;
        return false;
    }

    public boolean isSetEmptyLayout() {
        if (generateEmptyLayoutId() != PageManager.NO_LAYOUT_ID || generateEmptyLayout() != null)
            return true;
        return false;
    }


}