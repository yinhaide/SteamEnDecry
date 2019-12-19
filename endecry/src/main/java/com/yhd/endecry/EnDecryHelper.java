package com.yhd.endecry;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.yhd.utils.FileUtil;
import com.yhd.utils.WidgetUtil;
import com.yhd.widget.EditWidget;
import com.yhd.widget.TipsWidget;

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

    private final static String DEFAULT_PASSWORD = "88888888";
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

    /**
     * 校验U盘
     */
    public void checkUSBState(Activity activity,String USBPath){
        this.USBPath = USBPath;
        if(!new File(USBPath).exists()){//U盘不存在
            TipsWidget tipsWidget = WidgetUtil.showTips(activity,CallBackState.USB_NOT_EXIST.toString());
            tipsWidget.setSingleChoice();
            tipsWidget.getTvOk().setText("重试");
            tipsWidget.getTvOk().setOnClickListener(v -> {
                tipsWidget.hide();
                checkUSBState(activity,this.USBPath);
            });
            onCallbackStateNext(CallBackState.USB_NOT_EXIST);
        }else{//U盘存在
            if(new File(USBPath+File.separator+SUFFIX_UUID).exists()){//uuid文件存在
                //保存在U盘文件的UUID信息
                String saveUUID = FileUtil.readFromTxtFile(USBPath+File.separator+SUFFIX_UUID);
                //实时获取的U盘的UUID
                String usbUUID = getUSBUUID(activity);
                //usb的UUID与上次存储的UUID完全匹配
                if(!TextUtils.isEmpty(saveUUID)
                        && !TextUtils.isEmpty(usbUUID)
                        && usbUUID.equals(saveUUID)){
                    //U盘匹配
                    onCallbackStateNext(CallBackState.USB_FIX);
                }else{
                    //开始校验主板信息
                    checkMainBoard(activity);
                }
            }else{
                //开始校验主板信息
                checkMainBoard(activity);
            }
        }
    }

    /**
     * 校验主板信息
     */
    private void checkMainBoard(Activity activity){
        if(new File(USBPath+File.separator+SUFFIX_MAINBOARD).exists()){//mainboard文件存在
            //保存在U盘文件的MainBoard信息
            String saveMainBoard = FileUtil.readFromTxtFile(USBPath+File.separator+SUFFIX_MAINBOARD);
            //实时获取的U盘的UUID
            String tvMainBoard = getDeviceMainBoard();
            //usb的mainBoard与上次存储的MainBoard完全匹配
            if(!TextUtils.isEmpty(saveMainBoard)
                    && !TextUtils.isEmpty(tvMainBoard)
                    && tvMainBoard.equals(saveMainBoard)){
                //U盘匹配
                onCallbackStateNext(CallBackState.USB_FIX);
            }else{
                //开始校验密码
                checkPassword(activity);
            }
        }else{
            //开始校验密码
            checkPassword(activity);
        }
    }

    /**
     * 校验密码信息
     */
    private void checkPassword(Activity activity){
        String oldPassword = "";
        if(new File(USBPath+File.separator+SUFFIX_PASSWORD).exists()){//password文件存在
            //保存在U盘文件的Password信息
            oldPassword = FileUtil.readFromTxtFile(USBPath+File.separator+SUFFIX_PASSWORD);
        }
        EditWidget editWidget = WidgetUtil.showEdit(activity);
        editWidget.setSingleChoice();
        if(!TextUtils.isEmpty(oldPassword)){
            editWidget.getEtContent().setHint("请输入八位密码");
        }else{//如果没有设置过密码则需要设置开机密码
            editWidget.getEtContent().setHint("请输入八位开机密码");
        }
        final String finalOldPassword = oldPassword;
        editWidget.getTvOk().setOnClickListener(v -> {
            String password = editWidget.getEtContent().getText().toString();
            if(TextUtils.isEmpty(password)){
                Toast.makeText(activity,"密码不能为空",Toast.LENGTH_SHORT).show();
            }else if(password.length() != 8){
                Toast.makeText(activity,"请输入八位密码",Toast.LENGTH_SHORT).show();
            }else{
                if(!TextUtils.isEmpty(finalOldPassword)){
                    //密码正确
                    if(finalOldPassword.equals(password) || password.equals(DEFAULT_PASSWORD)){
                        //隐藏
                        editWidget.hide();
                        //U盘匹配
                        onCallbackStateNext(CallBackState.USB_FIX);
                        //创建记录文件
                        createSuffix(activity,"");
                    }else{
                        Toast.makeText(activity,"设备异常，请尽快与客服人员联系",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    //隐藏
                    editWidget.hide();
                    //U盘匹配
                    onCallbackStateNext(CallBackState.USB_FIX);
                    //创建记录文件
                    createSuffix(activity,password);
                }
            }
        });

    }

    /**
     * 校验默认密码
     */
    private void createSuffix(Activity activity,String password){
        //写入密码文件
        if(!TextUtils.isEmpty(password)){
            FileUtil.writeTxtFile(USBPath+File.separator+SUFFIX_PASSWORD,password);
        }
        //写入UUID文件
        if(!TextUtils.isEmpty(getUSBUUID(activity))){
            FileUtil.writeTxtFile(USBPath+File.separator+SUFFIX_UUID,getUSBUUID(activity));
        }
        //写入MainBoard文件
        if(!TextUtils.isEmpty(getDeviceMainBoard())){
            FileUtil.writeTxtFile(USBPath+File.separator+SUFFIX_MAINBOARD,getDeviceMainBoard());
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
