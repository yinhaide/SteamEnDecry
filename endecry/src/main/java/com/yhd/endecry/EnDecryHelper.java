package com.yhd.endecry;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import com.yhd.utils.WidgetUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 类作用描述
 * Created by haide.yin(haide.yin@tcl.com) on 2019/12/18 15:47.
 */
public class EnDecryHelper {

    private static EnDecryHelper singleton;
    private Handler mainHandler;//主线程
    private String USBPath;//U盘目录

    private final static String DEFAULT_PASSWORD = "888888";
    private final static String SUFFIX_UUID = ".uuid";
    private final static String SUFFIX_MAINBOARD = ".mainboard";
    private final static String SUFFIX_PASSWORD = ".password";

    /** 状态枚举 */
    public enum CallBackState{
        USB_FIX("U盘符合"),
        USB_NOT_EXIST("U盘不存在");

        private final String state;

        CallBackState(String var3) {
            this.state = var3;
        }

        public String toString() {
            return this.state;
        }
    }

    /**
     * 单例
     */
    public static synchronized EnDecryHelper get(){
        if(singleton == null){
            singleton = new EnDecryHelper();
        }
        return singleton;
    }

    public void checkUSBState(Activity activity,String USBPath){
        this.USBPath = USBPath;
        if(!new File(USBPath).exists()){
            WidgetUtil.showTips(activity,CallBackState.USB_NOT_EXIST.toString());
            onCallbackStateNext(CallBackState.USB_NOT_EXIST);
        }
    }

    /**
     * 读取U盘的UUID，读取最后一个
     */
    private static String getUSBUUID(Context context){
        String msg = "";
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class storeManagerClazz = Class.forName("android.os.storage.StorageManager");
            Method getVolumesMethod = storeManagerClazz.getMethod("getVolumes");
            List<?> volumeInfos = (List<?>)getVolumesMethod.invoke(storageManager);
            Class volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
            Method getFsUuidMethod = volumeInfoClazz.getMethod("getFsUuid");
            if(volumeInfos != null){
                for(Object volumeInfo:volumeInfos){
                    String uuid = (String)getFsUuidMethod.invoke(volumeInfo);
                    if(!TextUtils.isEmpty(uuid)){
                        msg = uuid;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return msg;
    }

    /**
     * 获取设备主板信息
     * @return 主板信息信息
     */
    private static String getDeviceMainBoard() {
        String mainBoard = "";
        if(!TextUtils.isEmpty(Build.BOARD)){
            mainBoard = Build.BOARD;
        }
        return mainBoard;
    }

    /* ***************************** CallbackState ***************************** */

    private OnCallbackStateListener onCallbackStateListener;

    // 接口类 -> OnCallbackStateListener
    public interface OnCallbackStateListener {
        void onCallbackState(CallBackState callbackState);
    }

    // 对外暴露接口 -> setOnCallbackStateListener
    public EnDecryHelper setOnCallbackStateListener(OnCallbackStateListener onCallbackStateListener) {
        this.onCallbackStateListener = onCallbackStateListener;
        return singleton;
    }

    // 内部使用方法 -> CallbackStateNext
    private void onCallbackStateNext(CallBackState callbackState) {
        if (onCallbackStateListener != null) {
            onCallbackStateListener.onCallbackState(callbackState);
        }
    }
}
