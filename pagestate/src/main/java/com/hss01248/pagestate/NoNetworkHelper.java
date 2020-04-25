package com.hss01248.pagestate;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

/**
 * time:2020/4/25
 * author:hss
 * desription:
 */
public class NoNetworkHelper {

     static void showNoNetWorkDlg(final Context activity) {
        AlertDialog dialog = null;
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
                            activity.startActivity(intent);
                            dialog.dismiss();
                        }
                    }).setNegativeButton("知道了", null).show();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ;

    }

     static boolean isNetWorkAvailable(Context context) {
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
}
