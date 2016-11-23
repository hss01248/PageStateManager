package com.hss01248.pagestate;

import android.view.View;

public abstract class PageStateListener
{


    public abstract void setRetryEvent(View retryView);

    public void setLoadingEvent(View loadingView)
    {
    }

    public void setEmptyEvent(View emptyView)
    {
    }

    public int generateLoadingLayoutId()
    {
        return PageStateManager.NO_LAYOUT_ID;
    }

    public int generateRetryLayoutId()
    {
        return PageStateManager.NO_LAYOUT_ID;
    }

    public int generateEmptyLayoutId()
    {
        return PageStateManager.NO_LAYOUT_ID;
    }

    public View generateLoadingLayout()
    {
        return null;
    }

    public View generateRetryLayout()
    {
        return null;
    }

    public View generateEmptyLayout()
    {
        return null;
    }

    public boolean isSetLoadingLayout()
    {
        if (generateLoadingLayoutId() != PageStateManager.NO_LAYOUT_ID || generateLoadingLayout() != null)
            return true;
        return false;
    }

    public boolean isSetRetryLayout()
    {
        if (generateRetryLayoutId() != PageStateManager.NO_LAYOUT_ID || generateRetryLayout() != null)
            return true;
        return false;
    }

    public boolean isSetEmptyLayout()
    {
        if (generateEmptyLayoutId() != PageStateManager.NO_LAYOUT_ID || generateEmptyLayout() != null)
            return true;
        return false;
    }


}