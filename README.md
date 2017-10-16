# PageManager
页面状态管理

[![](https://jitpack.io/v/hss01248/PageStateManager.svg)](https://jitpack.io/#hss01248/PageStateManager)

在张鸿洋的[LoadingAndRetryManager](https://github.com/hongyangAndroid/LoadingAndRetryManager)的基础上改写,修正一些bug,优化api,并提供使用时封装的例子

# 特性
* api超级简单
* 无需更改layout文件
* 错误页面和空白页面均提供了点击事件的回调,直接实现即可



# 更新:fragment的操作改变了

由于fragment的生命周期引起的bug,已取消原先直接传入fragment对象的方式.

请改成传入组成fragment的view,注意该view对象传入时,其parent不能为空,也就是,该view不能是xml的根view,可以自己随便包一层.

ps.

其实fragment本质也是基于view包裹了一层api,搞点生命周期之类的,api难用得要死,还一大堆坑,还不如自己包装一个view,自己加点生命周期,高度可控,减少bug.我的项目中从来都不用fragment,都是自己把view包装成各种page.

# API

> 参考demo里的,自己封装一层(拷过去改一改)


## BaseApplication里的初始化

```

public static void initWhenAppOnCreate(Context application, int layoutResOfEmpty, int layoutResOfLoading, int layoutResOfError){
       PageManager.initInApp(application,layoutResOfEmpty,layoutResOfLoading,layoutResOfError);
   }
   
```



## 页面中生成PageManager对象

```
  /**
     *
     * @param container  必须为activity或者view.如果是view,则该view对象必须有parent
     * @param retryAction 点击重试的动作,注意,只需要关注有网络的情况,无网络状态时已经封装好:弹出对话框询问用户是否去设置网络
     * @param isShowLoadingOrContent 第一次是显示loading(true)还是content(false)
     * @return 当前页面的状态管理器
     */
    public static PageManager init(final Object container, boolean isShowLoadingOrContent ,final Runnable retryAction)
    
    
    //封装后使用:
    public static MyPageManager init(final Object container,  final MyPageListener pageListener){
            PageManager manager =  PageManager.generate(container, true,pageListener);
            MyPageManager myPageManager = new MyPageManager();
            myPageManager.pageManager = manager;
            return myPageManager;
        }
        
        //其中,MyPageListener继承PageListener,并提供一个全局处理retry时无网络的情况:
        
        @Override
            public void onRetry(View retryView) {
                if (!isNetWorkAvailable(retryView.getContext())) {
                    onNoNetWork(retryView);
                } else {
                    onReallyRetry();
                }
            }
        
            public void onNoNetWork(View retryView) {
                if(retryView.getContext() instanceof Activity){
                    showNoNetWorkDlg((Activity) retryView.getContext());
                }else {
                    //todo
                }
        
            }
        
            protected abstract void onReallyRetry();
        
```

## 控制页面状态

```
public void showLoading()
public void showContent()
public void showEmpty()
public void showError()
public void showError(CharSequence errorMsg)
```



# demo中的默认的几个页面状态UI图

 

 ![loading](loading.jpg)

![empty](empty.jpg)



 ![error](error.jpg)



demo中无网络时弹出dialog:

 ![error_dialog](error_dialog.jpg)

# 使用

## gradle

**Step 1.** Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```
    allprojects {
        repositories {
            ...
            maven { url "https://jitpack.io" }
        }
    }
```

**Step 2.** Add the dependency

```
    dependencies {
            compile 'com.github.hss01248:PageStateManager:2.0.0'
    }
```



## 示例代码(详见demo)

```
 pageStateManager = MyPageManager.init(this, new MyPageListener() {
             @Override
             protected void onReallyRetry() {
                 doNet();
 
             }});
        
        



 private void doNet() {
        pageStateManager.showLoading();
        
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int  state = new Random().nextInt(3);
                switch (state){
                    case 0:
                        pageStateManager.showError("哈哈哈哈哈错误了");
                        break;
                    case 1:
                        pageStateManager.showEmpty();
                        break;
                    case 2:
                        pageStateManager.showContent();
                }

            }
        },2000);
    }
```

# 注意事项

1.给view对象设置状态时,该对象必须有parent

2.失败页面的无网络状态在包装时提供全局默认的处理措施,这样使用listener时只需要实现有网络时的处理动作.





# blog

[介绍一下页面状态管理类PageStateManager,我实在看不下去你们直接用Layout](http://www.jianshu.com/p/665a69e9436b)
