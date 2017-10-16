package com.hss01248.pagestate;

/**
 * Created by huangshuisheng on 2017/10/12.
 */

public class PageStateConfig {
    private static int loaingLayoutId;
    public static int emptyLayoutId;
    public static int errorLayoutId;
    public static int noNetworkLayoutId;
    public static PageStateConfig globalConfig;

    public static void setGlobal(int loaingLayoutId,int emptyLayoutId,int errorLayoutId,int noNetworkLayoutId){
        globalConfig = new PageStateConfig();
        globalConfig.loaingLayoutId = loaingLayoutId;
        globalConfig.emptyLayoutId = emptyLayoutId;
        globalConfig.errorLayoutId = errorLayoutId;
        globalConfig.noNetworkLayoutId = noNetworkLayoutId;
    }



}
