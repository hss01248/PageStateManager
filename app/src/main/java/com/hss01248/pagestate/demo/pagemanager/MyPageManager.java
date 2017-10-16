package com.hss01248.pagestate.demo.pagemanager;

import android.content.Context;
import android.widget.TextView;

import com.hss01248.pagestate.PageManager;
import com.hss01248.pagestate.demo.R;


/**
 * Created by huangshuisheng on 2017/10/12.
 */

public class MyPageManager {

    private PageManager pageManager;

   public static void initWhenAppOnCreate(Context application, int layoutResOfEmpty, int layoutResOfLoading, int layoutResOfError){
       PageManager.initInApp(application,layoutResOfEmpty,layoutResOfLoading,layoutResOfError);
   }

    public static MyPageManager init(final Object container,  final MyPageListener pageListener){
        PageManager manager =  PageManager.generate(container, true,pageListener);
        MyPageManager myPageManager = new MyPageManager();
        myPageManager.pageManager = manager;
        return myPageManager;
    }

    public void showLoading(){
        if(pageManager!=null){
            pageManager.showLoading();
        }
    }

    public void showContent(){
        if(pageManager!=null){
            pageManager.showContent();
        }
    }

    public void showError(){
        if(pageManager!=null){
            pageManager.showError();
        }
    }

    public void showEmpty(){
        if(pageManager!=null){
            pageManager.showEmpty();
        }
    }

    public void showEmpty(CharSequence msg){
        if(pageManager!=null){
            pageManager.showEmpty();
            TextView textView = (TextView) pageManager.mLoadingAndRetryLayout.findViewById(R.id.tv_msg_empty);
            if(textView!=null){
                textView.setText(msg);
            }
        }
    }

    public void showError(CharSequence msg){
        if(pageManager!=null){
            pageManager.showError();
          TextView textView = (TextView) pageManager.mLoadingAndRetryLayout.findViewById(R.id.tv_msg_error);
            if(textView!=null){
                textView.setText(msg);
            }
        }
    }


}
