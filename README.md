# PageStateManager/StatefulFrameLayout
页面状态管理

[![](https://jitpack.io/v/hss01248/PageStateManager.svg)](https://jitpack.io/#hss01248/PageStateManager)

在张鸿洋的[LoadingAndRetryManager](https://github.com/hongyangAndroid/LoadingAndRetryManager)的基础上改写,修正一些bug,优化api,并提供使用时封装的例子

# 特性
* api超级简单
* 可以在xml中使用StatefulFrameLayout
* 也可以不改动xml,直接在代码里使用PageStateManager
* 错误页面和空白页面均提供了点击事件的回调,直接实现即可
* 不改动framlayout本身任何属性,依然可以添加多个子view



# 更新:fragment的操作改变了

由于fragment的生命周期引起的bug,已取消原先直接传入fragment对象的方式.

请改成传入组成fragment的view,注意该view对象传入时,其parent不能为空,也就是,该view不能是xml的根view,可以自己随便包一层.

ps.

其实fragment本质也是基于view包裹了一层api,搞点生命周期之类的,api难用得要死,还一大堆坑,还不如自己包装一个view,自己加点生命周期,高度可控,减少bug.我的项目中从来都不用fragment,都是自己把view包装成各种page.

# API

> 参考demo里的,自己封装一层(拷过去改一改)

## 接口

```
public interface IViewState {

     void showLoading();
     void showError(CharSequence msg);
     void showContent();
     void showEmpty();
}
```

# 四个级别的配置

### 库内默认

自带Loading,Empty,Error的xml:

```
public static int BASE_LOADING_LAYOUT_ID = R.layout.pager_loading;
public static int BASE_RETRY_LAYOUT_ID = R.layout.pager_error;
public static int BASE_EMPTY_LAYOUT_ID = R.layout.pager_empty;
```

## 使用时可全局配置

在application的oncreate里调用:

也就是修改上述的三个静态变量:

```
PageStateManager.initInAppOnCreate():
```

```
public static void initInApp(int layoutIdOfEmpty, int layoutIdOfLoading, int layoutIdOfError) {
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
```



## 单个页面的配置:

### 可配置的项目:

// PageConfig为抽象类: 

仅一个必须实现的方法:


    

    public abstract class PageConfig {
    
    public abstract void onRetry(View retryView);//必须实现
    
    public void onEmtptyViewClicked(View emptyView) {
        onRetry(emptyView);
    }
    
    public boolean isFirstStateLoading(){
        return true;
    }
    
    public String emptyMsg(){
        return "";
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
### xml里使用statefulFrameLayout时:

```
<com.hss01248.pagestate.StatefulFrameLayout
    android:id="@+id/pager"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <TextView
        android:id="@+id/context"
        android:background="#ffff00"
        android:text="i am the content!!!!!!!!!!!!!!!!!!!!!!!!!!!"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
    <Button
        android:layout_width="match_parent"
        android:id="@+id/btn"
        android:layout_marginTop="40dp"
        android:text="view2"
        android:layout_height="wrap_content"/>
</com.hss01248.pagestate.StatefulFrameLayout>
```

```
statefulFrameLayout.init(new PageConfig() {
            @Override
            public void onRetry(View retryView) {
                doNet();
            }
        });
        

```

### 或者使用PageStateManager:

```

 /**
     *
     * @param container  必须为activity或者view.如果是view,则该view对象必须有parent
     */
pageStateManager =   PageStateManager.initWhenUse(container,new MyPageConfig() {
    @Override
    protected void onReallyRetry() {
        doNet();
    }

    @Override
    public int customEmptyLayoutId() {
        return R.layout.pager_empty_2;
    }

    @Override
    public int customLoadingLayoutId() {
        return R.layout.pager_loading_2;
    }

    @Override
    public int customErrorLayoutId() {
        return R.layout.pager_error_2;
    }
});
```

## 控制页面状态的api:

```
public void showLoading()
public void showContent()
public void showEmpty()
public void showError(CharSequence errorMsg)
```



# demo中的默认的几个页面状态UI图

 

 ![loading](loading.jpg)

![empty](empty.jpg)



 ![error](error.jpg)



其中无网络时弹出dialog:

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
            compile 'com.github.hss01248:PageStateManager:3.0.1'
    }
```



## 示例代码(详见demo)

## xml里不写StatefulFramelayout时:

```
private void initView() {
        setContentView(R.layout.activity_main);
        pageStateManager =   PageStateManager.initWhenUse(this,new PageConfig() {

            @Override
            public int customEmptyLayoutId() {
                return R.layout.pager_empty_2;
            }

            @Override
            public void onRetry(View retryView) {
                doNet();
            }

            @Override
            public int customLoadingLayoutId() {
                return R.layout.pager_loading_2;
            }

            @Override
            public int customErrorLayoutId() {
                return R.layout.pager_error_2;
            }
        });

    }

    private void doNet() {
        pageStateManager.showLoading();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int  state = new Random().nextInt(3);
                switch (state){
                    case 0:
                        pageStateManager.showError("稍候重试");
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

## 在xml里直接写时:

```
......
statefulFrameLayout = (StatefulFrameLayout)findViewById(R.id.pager);
    statefulFrameLayout.init(new PageConfig() {
        @Override
        public void onRetry(View retryView) {
            doNet();
        }
    });
    doNet();
}



private void doNet() {
    statefulFrameLayout.showLoading();

    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            int  state = new Random().nextInt(3);
            switch (state){
                case 0:
                    statefulFrameLayout.showError("稍候重试222222");
                    break;
                case 1:
                    statefulFrameLayout.showEmpty();
                    break;
                case 2:
                    statefulFrameLayout.showContent();
            }

        }
    },2000);
}
```

# 注意事项

1.给view对象设置状态时,该对象必须有parent

2.





# blog

[介绍一下页面状态管理类PageStateManager,我实在看不下去你们直接用Layout](http://www.jianshu.com/p/665a69e9436b)
