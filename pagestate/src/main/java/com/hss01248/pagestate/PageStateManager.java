package com.hss01248.pagestate;

import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by zhy on 15/8/27.
 */
public class PageStateManager implements IViewState{

     static int BASE_LOADING_LAYOUT_ID = R.layout.pager_loading;
     static int BASE_RETRY_LAYOUT_ID = R.layout.pager_error;
     static int BASE_EMPTY_LAYOUT_ID = R.layout.pager_empty;

    public StatefulFrameLayout getStatefulLayout() {
        return mLoadingAndRetryLayout;
    }

    StatefulFrameLayout mLoadingAndRetryLayout;
    PageStateConfig pageListener = new PageStateConfig() {
        @Override
        public void onRetry(View retryView) {

        }
    };

     PageStateManager(Context context){

    }


    private PageStateManager(Object activityOrView, PageStateConfig listener) {
        ViewGroup contentParent = null;
        Context context;
        if (activityOrView instanceof Activity) {
            Activity activity = (Activity) activityOrView;
            context = activity;
            contentParent = (ViewGroup) activity.findViewById(android.R.id.content);
        } else if (activityOrView instanceof Fragment) {

            Fragment fragment = (Fragment) activityOrView;
            context = fragment.getActivity();
            contentParent = (ViewGroup) (fragment.getView().getParent());
            if (contentParent == null) {
                throw new IllegalArgumentException("the fragment must already has a parent ,please do not invoke this in oncreateView,you should use this method in onActivityCreated() or onstart");
            }

            //throw new IllegalArgumentException("the support for fragment has been canceled,please use give me a view object which has a parent");

        } else if (activityOrView instanceof View) {
            View view = (View) activityOrView;
            contentParent = (ViewGroup) (view.getParent());
            if (contentParent == null) {
                throw new IllegalArgumentException("the view must already has a parent ");
            }
            context = view.getContext();
        } else {
            throw new IllegalArgumentException("the container's type must be Fragment or Activity or a view ");
        }


        int childCount = contentParent.getChildCount();
        //get contentParent
        int index = 0;
        View oldContent;
        if (activityOrView instanceof View) {
            oldContent = (View) activityOrView;
            for (int i = 0; i < childCount; i++) {
                if (contentParent.getChildAt(i) == oldContent) {
                    index = i;
                    break;
                }
            }
        } else {
            oldContent = contentParent.getChildAt(0);
        }
        contentParent.removeView(oldContent);
        //setup content layout
        StatefulFrameLayout statefulFrameLayout = new StatefulFrameLayout(context);
        statefulFrameLayout.manager = this;
        if(listener != null){
            this.pageListener = listener;
        }


        ViewGroup.LayoutParams lp = oldContent.getLayoutParams();
        contentParent.addView(statefulFrameLayout, index, lp);
        statefulFrameLayout.setContentView(oldContent);
        // setup loading,retry,empty layout
        //setupLoadingLayout(listener, pageLayout);
       // setupRetryLayout(listener, pageLayout);
        //setupEmptyLayout(listener, pageLayout);
        //callback
       /* listener.onRetry(pageLayout.getRetryView());
        listener.setLoadingEvent(pageLayout.getLoadingView());
        listener.setEmptyEvent(pageLayout.getEmptyView());*/

        mLoadingAndRetryLayout = statefulFrameLayout;
        //初始状态:loading进去
        if (pageListener.isFirstStateLoading()) {
            mLoadingAndRetryLayout.showLoading();
        } else {
            mLoadingAndRetryLayout.showContent();
        }

    }

    /**
     * @param layoutIdOfEmpty
     * @param layoutIdOfLoading
     * @param layoutIdOfError
     */
    public static void initInAppOnCreate(int layoutIdOfEmpty, int layoutIdOfLoading, int layoutIdOfError) {
        if (layoutIdOfEmpty != 0) {
            BASE_EMPTY_LAYOUT_ID = layoutIdOfEmpty;
        }
        if (layoutIdOfLoading != 0) {
            BASE_LOADING_LAYOUT_ID = layoutIdOfLoading;
        }
        if (layoutIdOfError != 0) {
            BASE_RETRY_LAYOUT_ID = layoutIdOfError;
        }
    }

    public static PageStateManager initWhenUse(Object activityOrView, PageStateConfig listener) {
        return new PageStateManager(activityOrView,  listener);
    }


    @Override
    public void showLoading() {
        mLoadingAndRetryLayout.showLoading();
    }

    @Override
    public void showLoading(int progress) {
        mLoadingAndRetryLayout.showLoading(progress);
    }

    @Override
    public void showError(CharSequence msg) {
        mLoadingAndRetryLayout.showError(msg);
    }

    @Override
    public void showContent() {
        mLoadingAndRetryLayout.showContent();
    }

    @Override
    public void showEmpty() {
        mLoadingAndRetryLayout.showEmpty();
    }




}
