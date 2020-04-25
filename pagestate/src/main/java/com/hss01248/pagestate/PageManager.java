package com.hss01248.pagestate;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by zhy on 15/8/27.
 */
public class PageManager implements IViewState{
    public static final int NO_LAYOUT_ID = 0;
    public static int BASE_LOADING_LAYOUT_ID = R.layout.pager_loading;
    public static int BASE_RETRY_LAYOUT_ID = R.layout.pager_error;
    public static int BASE_EMPTY_LAYOUT_ID = R.layout.pager_empty;
    private static Context appContext;
    public PageLayout mLoadingAndRetryLayout;

    /**
     * 当判断当前手机没有网络时选择是否打开网络设置
     */
   /* public static AlertDialog showNoNetWorkDlg(final Object container) {
        AlertDialog dialog = null;
         Context context = null;


        if(container instanceof  Activity){
           context = (Activity) container;

        }else if (container instanceof  Fragment){
            context =  ((Fragment) container).getActivity();

        }else if(container instanceof View){
            context = ((View) container).getContext();
        }

        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final Activity finalActivity = (Activity) context;

            dialog = builder        //
                    .setTitle("提示")            //
                    .setMessage("当前无网络").setPositiveButton("去设置", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 跳转到系统的网络设置界面
                            Intent intent = null;
                            // 先判断当前系统版本
                            if(android.os.Build.VERSION.SDK_INT > 10){  // 3.0以上

                                //intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                            }else{
                                intent = new Intent();
                                intent.setClassName("com.android.settings", "com.android.settings.WirelessSettings");
                            }
                            finalActivity.startActivity(intent);
                            dialog.dismiss();
                        }
                    }).setNegativeButton("知道了", null).show();
        }catch (Exception e){
            e.printStackTrace();
        }

        return dialog;
    }*/





    /*public static View generateCustomEmptyView(CharSequence word){
        ViewGroup view = (ViewGroup) View.inflate(appContext, BASE_EMPTY_LAYOUT_ID,null);

        TextView textView = (TextView) view.findViewById(R.id.tv_msg_empty);
        textView.setText(word);
        return view;
    }*/



     PageListener DEFAULT_LISTENER = new PageListener() {
        @Override
        public void onRetry(View retryView) {

        }
    };

     PageManager(Context context){

    }


    private PageManager(Object activityOrView, boolean showLoadingFirstIn, PageListener listener) {
        if (listener == null) listener = DEFAULT_LISTENER;

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
        PageLayout pageLayout = new PageLayout(context);

        ViewGroup.LayoutParams lp = oldContent.getLayoutParams();
        contentParent.addView(pageLayout, index, lp);
        pageLayout.setContentView(oldContent);
        // setup loading,retry,empty layout
        setupLoadingLayout(listener, pageLayout);
        setupRetryLayout(listener, pageLayout);
        setupEmptyLayout(listener, pageLayout);
        //callback
       /* listener.onRetry(pageLayout.getRetryView());
        listener.setLoadingEvent(pageLayout.getLoadingView());
        listener.setEmptyEvent(pageLayout.getEmptyView());*/
        final PageListener finalListener = listener;
        pageLayout.getRetryView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NoNetworkHelper.isNetWorkAvailable(v.getContext())) {
                    NoNetworkHelper.showNoNetWorkDlg(v.getContext());
                } else {
                    if (finalListener != null) {
                        finalListener.onRetry(v);
                    }
                }


            }
        });
        pageLayout.getEmptyView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalListener != null) {
                    finalListener.onEmtptyViewClicked(v);
                }
            }
        });
        mLoadingAndRetryLayout = pageLayout;
        //初始状态:loading进去
        if (showLoadingFirstIn) {
            mLoadingAndRetryLayout.showLoading();
        } else {
            mLoadingAndRetryLayout.showContent();
        }

    }

    /**
     * @param appContext
     * @param layoutIdOfEmpty
     * @param layoutIdOfLoading
     * @param layoutIdOfError
     */
    public static void initInApp(Context appContext, int layoutIdOfEmpty, int layoutIdOfLoading, int layoutIdOfError) {

        PageManager.appContext = appContext;
        if (layoutIdOfEmpty > 0) {
            BASE_EMPTY_LAYOUT_ID = layoutIdOfEmpty;
        }

        if (layoutIdOfLoading > 0) {
            BASE_LOADING_LAYOUT_ID = layoutIdOfLoading;
        }

        if (layoutIdOfError > 0) {
            BASE_RETRY_LAYOUT_ID = layoutIdOfError;
        }


    }

    public static PageManager generate(Object activityOrView, boolean showLoadingFirstIn, PageListener listener) {
        return new PageManager(activityOrView, showLoadingFirstIn, listener);
    }

    /**
     * @param container              必须为activity,fragment或者view.如果是view,则该view对象必须有parent
     * @param emptyMsg               自定义空白String
     * @param retryAction
     * @param isShowLoadingOrContent 第一次是显示loading(true)还是content(false)
     * @return
     *//*
    public static PageManager init(final Object container, final CharSequence emptyMsg, boolean isShowLoadingOrContent ,final Runnable retryAction){
        PageManager manager =  generate(container, isShowLoadingOrContent,new PageListener() {
            @Override
            public void onRetry(View retryView) {
                if (!isNetWorkAvailable(appContext)) {
                    showNoNetWorkDlg(container);
                } else {
                    retryAction.run();
                }
            }

            @Override
            public View generateEmptyLayout() {
                return generateCustomEmptyView(emptyMsg);
            }
        });

        return manager;
    }*/
    @Override
    public void showLoading() {
        mLoadingAndRetryLayout.showLoading();
    }

    @Override
    public void showError(CharSequence msg) {
        mLoadingAndRetryLayout.showError(msg);
    }
    //每次显示实时的错误信息
    /*public void showError(CharSequence errorMsg){

        if(tvError != null){
            tvError.setText(errorMsg);
            mLoadingAndRetryLayout.showRetry();
            return;
        }
        ViewGroup view = (ViewGroup) mLoadingAndRetryLayout.getRetryView();
        tvError = (TextView) view.findViewById(R.id.tv_msg_error);
        tvError.setText(errorMsg);
        mLoadingAndRetryLayout.showRetry();
    }*/






    /*public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info == null) {
                return false;
            } else {
                if (info.isAvailable()) {
                    return true;
                }
            }
        }
        return false;
    }*/



    @Override
    public void showContent() {
        mLoadingAndRetryLayout.showContent();
    }

    @Override
    public void showEmpty() {
        mLoadingAndRetryLayout.showEmpty();
    }

    private void setupEmptyLayout(PageListener listener, PageLayout loadingAndRetryLayout) {
        if (listener.isSetEmptyLayout()) {
            int layoutId = listener.generateEmptyLayoutId();
            if (layoutId != NO_LAYOUT_ID) {
                loadingAndRetryLayout.setEmptyView(layoutId);
            } else {
                loadingAndRetryLayout.setEmptyView(listener.generateEmptyLayout());
            }
        } else {
            if (BASE_EMPTY_LAYOUT_ID != NO_LAYOUT_ID)
                loadingAndRetryLayout.setEmptyView(BASE_EMPTY_LAYOUT_ID);
        }
    }

    private void setupLoadingLayout(PageListener listener, PageLayout loadingAndRetryLayout) {
        if (listener.isSetLoadingLayout()) {
            int layoutId = listener.generateLoadingLayoutId();
            if (layoutId != NO_LAYOUT_ID) {
                loadingAndRetryLayout.setLoadingView(layoutId);
            } else {
                loadingAndRetryLayout.setLoadingView(listener.generateLoadingLayout());
            }
        } else {
            if (BASE_LOADING_LAYOUT_ID != NO_LAYOUT_ID)
                loadingAndRetryLayout.setLoadingView(BASE_LOADING_LAYOUT_ID);
        }
    }

    private void setupRetryLayout(PageListener listener, PageLayout loadingAndRetryLayout) {
        if (listener.isSetRetryLayout()) {
            int layoutId = listener.generateRetryLayoutId();
            if (layoutId != NO_LAYOUT_ID) {
                loadingAndRetryLayout.setRetryView(layoutId);
            } else {
                loadingAndRetryLayout.setRetryView(listener.generateRetryLayout());
            }
        } else {
            if (BASE_RETRY_LAYOUT_ID != NO_LAYOUT_ID)
                loadingAndRetryLayout.setRetryView(BASE_RETRY_LAYOUT_ID);
        }
    }


}
