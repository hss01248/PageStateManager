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

    public static void setShowDialogImpl(IShowDialog showDialog) {
        NoNetworkHelper.showDialog = showDialog;
    }

    static IShowDialog showDialog;

    public interface IShowDialog{
        void showNoNetWorkDlg(final Context context);
    }


     static void showNoNetWorkDlg(final Context context) {
        if(showDialog != null){
            showDialog.showNoNetWorkDlg(context);
            return;
        }
        AlertDialog dialog = null;
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            dialog = builder
                    .setTitle(R.string.pagestate_no_network_title)
                    .setMessage(R.string.pagestate_no_network_msg).setPositiveButton(R.string.pagestate_go_setting, new DialogInterface.OnClickListener() {
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
                            context.startActivity(intent);
                            dialog.dismiss();
                        }
                    }).setNegativeButton(R.string.pagestate_cancel, null)
                    .show();
        }catch (Exception e){
            e.printStackTrace();
        }

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
