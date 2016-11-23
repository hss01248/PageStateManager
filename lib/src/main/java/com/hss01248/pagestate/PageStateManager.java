package com.hss01248.pagestate;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;



/**
 * Created by zhy on 15/8/27.
 */
public class PageStateManager
{
    public static final int NO_LAYOUT_ID = 0;
    public static int BASE_LOADING_LAYOUT_ID = R.layout.pager_loading;
    public static int BASE_RETRY_LAYOUT_ID = R.layout.pager_error;
    public static int BASE_EMPTY_LAYOUT_ID = R.layout.pager_empty;

    public PageStateLayout mLoadingAndRetryLayout;

    private static Context appContext;

    public static void initInApp(Context appContext){
        initInApp(appContext,0,0,0);
    }
    public static void initInApp(Context appContext,int layoutIdOfEmpty,int layoutIdOfLoading,int layoutIdOfError){

            PageStateManager.appContext = appContext;
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

    public static PageStateManager init(final Object container, final Runnable retryAction) {
        return generate(container, new PageStateListener() {
            @Override
            public void setRetryEvent(View retryView) {
                retryView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isNetWorkAvailable(appContext)) {
                            if(container instanceof  Activity){
                                Activity activity = (Activity) container;
                                showNoNetWorkDlg(activity);
                            }else if (container instanceof  Fragment){
                                FragmentActivity activity =  ((Fragment) container).getActivity();
                                showNoNetWorkDlg(activity);
                            }else if(container instanceof View){
                                showNoNetWorkDlg(((View) container).getContext());
                            }
                        } else {
                            retryAction.run();
                        }
                    }
                });
            }
        });
    }

    public static PageStateManager init(final Object container, final CharSequence emptyMsg, final Runnable retryAction){
        return generate(container, new PageStateListener() {
            @Override
            public void setRetryEvent(View retryView) {
                retryView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isNetWorkAvailable(appContext)) {
                            if(container instanceof  Activity){
                                Activity activity = (Activity) container;
                                showNoNetWorkDlg(activity);
                            }else if (container instanceof  Fragment){
                                FragmentActivity activity =  ((Fragment) container).getActivity();
                                showNoNetWorkDlg(activity);
                            }else if(container instanceof View){
                                showNoNetWorkDlg(((View) container).getContext());
                            }

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
    }


    public void showLoading()
    {
        mLoadingAndRetryLayout.showLoading();
    }


    //todo 每次显示实时的错误信息
    public void showRetry()
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
     * @param context
     */
    private static AlertDialog showNoNetWorkDlg(final Context context) {
        AlertDialog dialog = null;
        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final Activity finalActivity = (Activity) context;

            dialog = builder        //
                    .setTitle("无网络")            //
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

      int count =   view.getChildCount();

        for(int i =0; i<count; i++){
            View child = view.getChildAt(i);
            if(child instanceof TextView && !(child instanceof Button)){
                TextView textView  = (TextView) child;
                textView.setText(word);
                break;

            }
        }
        return view;
    }



    public PageStateListener DEFAULT_LISTENER = new PageStateListener()
    {
        @Override
        public void setRetryEvent(View retryView)
        {

        }
    };


    public PageStateManager(Object activityOrFragmentOrView, PageStateListener listener)
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
        } else
        {
            throw new IllegalArgumentException("the argument's type must be Fragment or Activity: init(context)");
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
        PageStateLayout loadingAndRetryLayout = new PageStateLayout(context);

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

    private void setupEmptyLayout(PageStateListener listener, PageStateLayout loadingAndRetryLayout)
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

    private void setupLoadingLayout(PageStateListener listener, PageStateLayout loadingAndRetryLayout)
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

    private void setupRetryLayout(PageStateListener listener, PageStateLayout loadingAndRetryLayout)
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

    public static PageStateManager generate(Object activityOrFragment, PageStateListener listener)
    {
        return new PageStateManager(activityOrFragment, listener);
    }




}
