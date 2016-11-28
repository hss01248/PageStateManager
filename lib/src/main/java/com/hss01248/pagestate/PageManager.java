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
public class PageManager
{
    public static final int NO_LAYOUT_ID = 0;
    public static int BASE_LOADING_LAYOUT_ID = R.layout.pager_loading;
    public static int BASE_RETRY_LAYOUT_ID = R.layout.pager_error;
    public static int BASE_EMPTY_LAYOUT_ID = R.layout.pager_empty;



    public PageLayout mLoadingAndRetryLayout;

    private static Context appContext;


    private TextView tvError;

    public static void initInApp(Context appContext){
        initInApp(appContext,0,0,0);
    }


    /**
     * 如果需要后续调用自定义空白msg,错误msg字符串的api,则页面中显示该字符串的textview的id必须为tv_msg_empty,tv_msg_error
     * @param appContext
     * @param layoutIdOfEmpty
     * @param layoutIdOfLoading
     * @param layoutIdOfError
     */
    public static void initInApp(Context appContext,int layoutIdOfEmpty,int layoutIdOfLoading,int layoutIdOfError){

            PageManager.appContext = appContext;
        if(layoutIdOfEmpty > 0){
            BASE_EMPTY_LAYOUT_ID = layoutIdOfEmpty;
        }

        if(layoutIdOfLoading >0){
            BASE_LOADING_LAYOUT_ID = layoutIdOfLoading;
        }

        if(layoutIdOfError >0){
            BASE_RETRY_LAYOUT_ID = layoutIdOfError;
        }


    }


    /**
     *
     * @param container  必须为activity,fragment或者view.如果是view,则该view对象必须有parent
     * @param retryAction 点击重试的动作,注意,只需要关注有网络的情况,无网络状态时已经封装好:弹出对话框询问用户是否去设置网络
     * @param isShowLoadingOrContent 第一次是显示loading(true)还是content(false)
     * @return 当前页面的状态管理器
     */
    public static PageManager init(final Object container, boolean isShowLoadingOrContent ,final Runnable retryAction) {
        PageManager manager = generate(container, new PageListener() {
            @Override
            public void setRetryEvent(View retryView) {
                retryView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isNetWorkAvailable(appContext)) {
                            showNoNetWorkDlg(container);
                        } else {
                            retryAction.run();
                        }
                    }
                });
            }
        });
        if(isShowLoadingOrContent){
            manager.showLoading();
        }else {
            manager.showContent();
        }


        return manager;
    }

    /**
     *
     * @param container  必须为activity,fragment或者view.如果是view,则该view对象必须有parent
     * @param emptyMsg  自定义空白String
     * @param retryAction
     * @param isShowLoadingOrContent 第一次是显示loading(true)还是content(false)
     * @return
     */
    public static PageManager init(final Object container, final CharSequence emptyMsg, boolean isShowLoadingOrContent ,final Runnable retryAction){
        PageManager manager =  generate(container, new PageListener() {
            @Override
            public void setRetryEvent(View retryView) {
                retryView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isNetWorkAvailable(appContext)) {
                            showNoNetWorkDlg(container);
                        } else {
                            retryAction.run();
                        }
                    }
                });
            }

            @Override
            public View generateEmptyLayout() {
                return generateCustomEmptyView(emptyMsg);
            }
        });

        if(isShowLoadingOrContent){
            manager.showLoading();
        }else {
            manager.showContent();
        }


        return manager;
    }


    public void showLoading()
    {
        mLoadingAndRetryLayout.showLoading();
    }



    public void showError()
    {
        mLoadingAndRetryLayout.showRetry();
    }

    public void showContent()
    {
        mLoadingAndRetryLayout.showContent();
    }

    public void showEmpty()
    {
        mLoadingAndRetryLayout.showEmpty();
    }
    //每次显示实时的错误信息
    public void showError(CharSequence errorMsg){

        if(tvError != null){
            tvError.setText(errorMsg);
            mLoadingAndRetryLayout.showRetry();
            return;
        }
        ViewGroup view = (ViewGroup) mLoadingAndRetryLayout.getRetryView();
        tvError = (TextView) view.findViewById(R.id.tv_msg_error);
        tvError.setText(errorMsg);
        mLoadingAndRetryLayout.showRetry();
    }






    private static boolean isNetWorkAvailable(Context context) {
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
    }

    /**
     * 当判断当前手机没有网络时选择是否打开网络设置

     */
    private static AlertDialog showNoNetWorkDlg(final Object container) {
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
    }





    public static View generateCustomEmptyView(CharSequence word){
        ViewGroup view = (ViewGroup) View.inflate(appContext, BASE_EMPTY_LAYOUT_ID,null);

        TextView textView = (TextView) view.findViewById(R.id.tv_msg_empty);
        textView.setText(word);
        return view;
    }



    public PageListener DEFAULT_LISTENER = new PageListener()
    {
        @Override
        public void setRetryEvent(View retryView)
        {

        }
    };


    public PageManager(Object activityOrFragmentOrView, PageListener listener)
    {
        if (listener == null) listener = DEFAULT_LISTENER;

        ViewGroup contentParent = null;
        Context context;
        if (activityOrFragmentOrView instanceof Activity)
        {
            Activity activity = (Activity) activityOrFragmentOrView;
            context = activity;
            contentParent = (ViewGroup) activity.findViewById(android.R.id.content);
        } else if (activityOrFragmentOrView instanceof Fragment)
        {
            Fragment fragment = (Fragment) activityOrFragmentOrView;
            context = fragment.getActivity();
            contentParent = (ViewGroup) (fragment.getView().getParent());
        } else if (activityOrFragmentOrView instanceof View)
        {
            View view = (View) activityOrFragmentOrView;
            contentParent = (ViewGroup) (view.getParent());
            context = view.getContext();
        } else{
            throw new IllegalArgumentException("the container's type must be Fragment or Activity or a view which already has a parent ");
        }
        int childCount = contentParent.getChildCount();
        //get contentParent
        int index = 0;
        View oldContent;
        if (activityOrFragmentOrView instanceof View)
        {
            oldContent = (View) activityOrFragmentOrView;
            for (int i = 0; i < childCount; i++)
            {
                if (contentParent.getChildAt(i) == oldContent)
                {
                    index = i;
                    break;
                }
            }
        } else
        {
            oldContent = contentParent.getChildAt(0);
        }
        contentParent.removeView(oldContent);
        //setup content layout
        PageLayout loadingAndRetryLayout = new PageLayout(context);

        ViewGroup.LayoutParams lp = oldContent.getLayoutParams();
        contentParent.addView(loadingAndRetryLayout, index, lp);
        loadingAndRetryLayout.setContentView(oldContent);
        // setup loading,retry,empty layout
        setupLoadingLayout(listener, loadingAndRetryLayout);
        setupRetryLayout(listener, loadingAndRetryLayout);
        setupEmptyLayout(listener, loadingAndRetryLayout);
        //callback
        listener.setRetryEvent(loadingAndRetryLayout.getRetryView());
        listener.setLoadingEvent(loadingAndRetryLayout.getLoadingView());
        listener.setEmptyEvent(loadingAndRetryLayout.getEmptyView());
        mLoadingAndRetryLayout = loadingAndRetryLayout;
    }

    private void setupEmptyLayout(PageListener listener, PageLayout loadingAndRetryLayout)
    {
        if (listener.isSetEmptyLayout())
        {
            int layoutId = listener.generateEmptyLayoutId();
            if (layoutId != NO_LAYOUT_ID)
            {
                loadingAndRetryLayout.setEmptyView(layoutId);
            } else
            {
                loadingAndRetryLayout.setEmptyView(listener.generateEmptyLayout());
            }
        } else
        {
            if (BASE_EMPTY_LAYOUT_ID != NO_LAYOUT_ID)
                loadingAndRetryLayout.setEmptyView(BASE_EMPTY_LAYOUT_ID);
        }
    }

    private void setupLoadingLayout(PageListener listener, PageLayout loadingAndRetryLayout)
    {
        if (listener.isSetLoadingLayout())
        {
            int layoutId = listener.generateLoadingLayoutId();
            if (layoutId != NO_LAYOUT_ID)
            {
                loadingAndRetryLayout.setLoadingView(layoutId);
            } else
            {
                loadingAndRetryLayout.setLoadingView(listener.generateLoadingLayout());
            }
        } else
        {
            if (BASE_LOADING_LAYOUT_ID != NO_LAYOUT_ID)
                loadingAndRetryLayout.setLoadingView(BASE_LOADING_LAYOUT_ID);
        }
    }

    private void setupRetryLayout(PageListener listener, PageLayout loadingAndRetryLayout)
    {
        if (listener.isSetRetryLayout())
        {
            int layoutId = listener.generateRetryLayoutId();
            if (layoutId != NO_LAYOUT_ID)
            {
                loadingAndRetryLayout.setLoadingView(layoutId);
            } else
            {
                loadingAndRetryLayout.setLoadingView(listener.generateRetryLayout());
            }
        } else
        {
            if (BASE_RETRY_LAYOUT_ID != NO_LAYOUT_ID)
                loadingAndRetryLayout.setRetryView(BASE_RETRY_LAYOUT_ID);
        }
    }

    public static PageManager generate(Object activityOrFragment, PageListener listener)
    {
        return new PageManager(activityOrFragment, listener);
    }




}
