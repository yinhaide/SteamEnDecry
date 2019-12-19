package com.wenshanhu.endecry.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.de.rocket.utils.SharePreUtil;
import com.wenshanhu.endecry.helper.SteamHelper;

import java.io.File;

public class USBReceiver extends BroadcastReceiver {

    public static final String USB_PATH_KEY = "USB_PATH_KEY";
    public static String STEAM_PATH = "endecry";
    public static String USB_PATH = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.MEDIA_MOUNTED")) {//U盘插入
            String path = intent.getDataString();
            if(!TextUtils.isEmpty(path)){
                String[] pathArr = path.split("file://");
                if (pathArr.length >= 2) {
                    //拿第一个路径
                    String pathString = pathArr[1];//U盘路径
                    if (!TextUtils.isEmpty(pathString)) {
                        //Toast.makeText(context,"U盘插入:"+pathString,Toast.LENGTH_LONG).show();
                        //Toast.makeText(context,"U盘插入:"+path,Toast.LENGTH_LONG).show();
                        String steamPath = pathString + File.separator + STEAM_PATH;
                        //U盘下steam目录存在就更新
                        if(new File(steamPath).exists()){
                            USB_PATH = pathString;
                            //Toast.makeText(context,"正在加载文件资料:"+steamPath,Toast.LENGTH_LONG).show();
                            SharePreUtil.getInstance().putString(context,USB_PATH_KEY,USB_PATH);
                            //初始化路径结构
                            SteamHelper.get().init((msec) -> {
                                //Toast.makeText(context,"加载完成,耗时:"+msec+"毫秒",Toast.LENGTH_LONG).show();
                            });
                        }
                    }
                }
            }
        }else if (intent.getAction().equals("android.intent.action.MEDIA_REMOVED")) {//U盘拔出
            //Toast.makeText(context,"U盘拔出",Toast.LENGTH_LONG).show();
        }
    }
}
