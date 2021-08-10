package com.hss01248.pagestate;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhy on 15/8/26.
 */
public class StatefulFrameLayout extends FrameLayout implements IViewState {
    public static final int STATUS_LOADING = 1;
    public static final int STATUS_CONTENT = 2;
    public static final int STATUS_EMPTY = 3;
    public static final int STATUS_ERROR = 4;
    private static final String TAG = StatefulFrameLayout.class.getSimpleName();
    int status;
    private View mLoadingView;
    private View mRetryView;

    private List<View> contentViews = new ArrayList<>();
    private View mEmptyView;
    private LayoutInflater mInflater;
     PageStateManager manager;


    public StatefulFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInflater = LayoutInflater.from(context);
        initView(context, attrs, defStyleAttr);
    }

    public StatefulFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }


    public StatefulFrameLayout(Context context) {
        this(context, null);
    }

    public void init(PageStateConfig listener){
        manager.pageListener = listener;
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        int childCount = this.getChildCount();
        if (childCount > 1) {
            throw new RuntimeException("content must be one");
        }
        //xml里的布局是在这个layout初始化完成后才加入的
        manager = new PageStateManager(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(manager.pageListener.isFirstStateLoading()){
            showLoading();
        }

    }

    private boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    @Override
    public void showLoading() {
        if (isMainThread()) {
            showView(mLoadingView, STATUS_LOADING);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showView(mLoadingView,STATUS_LOADING);
                }
            });
        }
    }

    @Override
    public void showLoading(int progress) {
        if (isMainThread()) {
            showView(mLoadingView, STATUS_LOADING);
            showProgress(mLoadingView,progress);

        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showView(mLoadingView,STATUS_LOADING);
                    showProgress(mLoadingView,progress);
                }
            });
        }
    }
    TextView tvLoading;
    private void showProgress(View mLoadingView, int progress) {
        try {
            if(manager == null || manager.pageListener == null){
                showP(mLoadingView,progress+"%");
                return;
            }
            if(!manager.pageListener.showProgress(mEmptyView,progress)){
                showP(mLoadingView,progress+"%");
            }
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }


    }

    private void showP(View mLoadingView, String str) {
        if(tvLoading != null){
            tvLoading.setText(str);
            return;
        }
        View tv = mLoadingView.findViewById(R.id.tv_msg_loading);
        if(tv instanceof TextView){
           tvLoading = (TextView) tv;
            tvLoading.setText(str);
        }

    }

    /**
     * 会找到第一个textview,设置text. 如果msg为空,则不设置
     *
     * @param msg
     */
    @Override
    public void showError(final CharSequence msg) {
        if (isMainThread()) {
            showView(mRetryView,STATUS_ERROR);
            setText(mRetryView, msg);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showView(mRetryView,STATUS_ERROR);
                    setText(mRetryView, msg);
                }
            });
        }
    }

    private void setText(View view1, CharSequence msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (view1 instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view1;
            int count = viewGroup.getChildCount();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    View view = viewGroup.getChildAt(i);
                    if (view instanceof TextView) {
                        TextView textView = (TextView) view;
                        textView.setText(msg);
                        return;
                    }
                }
            }
        }

    }


    @Override
    public void showContent() {
        if (isMainThread()) {
            showView(null,STATUS_CONTENT);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showView(null,STATUS_CONTENT);
                }
            });
        }
    }

    @Override
    public void showEmpty() {
        if (isMainThread()) {
            showView(mEmptyView,STATUS_EMPTY);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showView(mEmptyView,STATUS_EMPTY);
                }
            });
        }
    }


    private void showView(View view, int status) {
        if (this.status == status) {
            return;
        }
        this.status = status;
        if(status == STATUS_LOADING){
            if(mLoadingView == null){
              view =  setLoadingView(manager.pageListener.customLoadingLayoutId());
            }
        }else if(status == STATUS_EMPTY){
            if(mEmptyView == null){
                view =  setEmptyView(manager.pageListener.customEmptyLayoutId());

            }
        }else if(status == STATUS_ERROR){
            if(mRetryView == null){
                view =  setRetryView(manager.pageListener.customErrorLayoutId());

            }
        }
        if(contentViews.isEmpty()){
           findContentViews();
        }
        if(status == STATUS_CONTENT){
            if (mLoadingView != null)
                mLoadingView.setVisibility(View.GONE);
            if (mRetryView != null)
                mRetryView.setVisibility(View.GONE);
            if (mEmptyView != null)
                mEmptyView.setVisibility(View.GONE);
            showContentView();
            return;
        }

        if (view == mLoadingView) {
            mLoadingView.setVisibility(View.VISIBLE);
            if (mRetryView != null)
                mRetryView.setVisibility(View.GONE);
            hideContentView();
            if (mEmptyView != null)
                mEmptyView.setVisibility(View.GONE);
        } else if (view == mRetryView) {
            mRetryView.setVisibility(View.VISIBLE);
            if (mLoadingView != null)
                mLoadingView.setVisibility(View.GONE);
            hideContentView();
            if (mEmptyView != null)
                mEmptyView.setVisibility(View.GONE);
        }  else if (view == mEmptyView) {
            mEmptyView.setVisibility(View.VISIBLE);
            if (mLoadingView != null)
                mLoadingView.setVisibility(View.GONE);
            if (mRetryView != null)
                mRetryView.setVisibility(View.GONE);
            hideContentView();
        }


    }

    private void hideContentView() {
        for (int i = 0; i < contentViews.size(); i++) {
            contentViews.get(i).setVisibility(GONE);
        }
    }

    private void showContentView() {
        for (int i = 0; i < contentViews.size(); i++) {
            contentViews.get(i).setVisibility(VISIBLE);
        }
    }

    private void  findContentViews() {
        int count = getChildCount();
        int contentCount = 0;
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if(view.equals(mEmptyView)){
                continue;
            }
            if(view.equals(mLoadingView)){
                continue;
            }
            if(view.equals(mRetryView)){
                continue;
            }
            contentViews.add(view);
            contentCount++;
        }
       if(contentCount == 0){
            Log.w("pager","还没有设置contentview");
        }
    }

     View setLoadingView(int layoutId) {
        return setLoadingView(mInflater.inflate(layoutId, this, false));
    }

     View setEmptyView(int layoutId) {
        return setEmptyView(mInflater.inflate(layoutId, this, false));
    }

     View setRetryView(int layoutId) {
        return setRetryView(mInflater.inflate(layoutId, this, false));
    }

     View setLoadingView(View view) {
        View loadingView = mLoadingView;
        if (loadingView != null) {
            Log.w(TAG, "you have already set a loading view and would be instead of this new one.");
        }
        removeView(loadingView);
        addView(view,0);
        mLoadingView = view;
        return mLoadingView;
    }

     View setEmptyView(View view) {
        View emptyView = mEmptyView;
        if (emptyView != null) {
            Log.w(TAG, "you have already set a empty view and would be instead of this new one.");
        }
        removeView(emptyView);
        addView(view,0);
        mEmptyView = view;
         mEmptyView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (manager.pageListener != null) {
                     manager.pageListener.onEmtptyViewClicked(v);
                 }
             }
         });
        return mEmptyView;
    }

     View setRetryView(View view) {
        View retryView = mRetryView;
        if (retryView != null) {
            Log.w(TAG, "you have already set a retry view and would be instead of this new one.");
        }
        removeView(retryView);
        addView(view,0);
        mRetryView = view;
         mRetryView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (!NoNetworkHelper.isNetWorkAvailable(v.getContext())) {
                     NoNetworkHelper.showNoNetWorkDlg(v.getContext());
                 } else {
                     if (manager.pageListener != null) {
                         manager.pageListener.onRetry(v);
                     }
                 }
             }
         });
        return mRetryView;

    }


    //给外部使用
     View getErrorView() {
        if(mRetryView == null){
            setRetryView(manager.pageListener.customErrorLayoutId()).setVisibility(GONE);
        }
        return mRetryView;
    }

     View getLoadingView() {
        if(mLoadingView == null){
            setLoadingView(manager.pageListener.customLoadingLayoutId()).setVisibility(GONE);
        }
        return mLoadingView;
    }



     List<View> getContentView() {
        if(contentViews.isEmpty()){
            findContentViews();
        }
        return contentViews;
    }

     View getEmptyView() {
         if(mEmptyView == null){
             setEmptyView(manager.pageListener.customEmptyLayoutId()).setVisibility(GONE);
         }
        return mEmptyView;
    }

     void setContentView(View oldContent) {
        addView(oldContent);
        contentViews.add(oldContent);
    }
}
