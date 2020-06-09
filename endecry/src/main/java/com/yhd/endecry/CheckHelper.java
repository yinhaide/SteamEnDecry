package com.yhd.endecry;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.de.rocket.utils.SharePreUtil;
import com.yhd.utils.EnDecryUtil;
import com.yhd.utils.MacUtil;
import com.yhd.utils.WidgetUtil;
import com.yhd.widget.EditWidget;
import com.yhd.widget.TipsWidget;

import java.io.File;

/**
 * 类作用描述
 * Created by haide.yin(haide.yin@tcl.com) on 2019/12/18 15:47.
 */
public class CheckHelper {

    private static final String PASSWORD = "PASSWORD";
    private static CheckHelper singleton;//单例
    private Handler mainHandler;//主线程
    private String checkPath;//检测目录
    private final static String SUFFIX_MAC_PS = ".macps";//出厂mac地址和密码文件，mac和密码用&隔开
    private boolean savePassword = true;//是否需要保存密码

    /** 状态枚举 */
    public enum CallBackState{
        STATE_ERROR("设备异常，请尽快与客服人员联系"),
        STATE_FIX("状态符合"),
        STATE_RETRY("重试"),
        STATE_NOT_EXIST("路径不存在");

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
    public static synchronized CheckHelper get(){
        if(singleton == null){
            singleton = new CheckHelper();
        }
        return singleton;
    }

    /**
     * 构造函数
     */
    private CheckHelper(){
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 校验U盘
     */
    public CheckHelper checkUSBState(Activity activity, String checkPath){
        Toast.makeText(activity,"mac地址:"+MacUtil.getMacAddress(activity),Toast.LENGTH_LONG).show();
        this.checkPath = checkPath;
        //createSuffix("02:00:00:00:00:00&88888888");
        if(!new File(checkPath).exists()){//检测路径不存在
            showErrorDialog(activity,CallBackState.STATE_NOT_EXIST.toString()+":"+checkPath);
            onCallbackStateNext(CallBackState.STATE_NOT_EXIST);
        }else{//路径存在
            if(new File(checkPath+File.separator+SUFFIX_MAC_PS).exists()){//.macps文件存在
                //保存在mac和密码文件的信息，用&分割
                String macps = EnDecryUtil.getUTF8String(EnDecryUtil.deEncrypt(checkPath+File.separator+SUFFIX_MAC_PS));
                if(!TextUtils.isEmpty(macps)&&macps.contains("&")){
                    String[] mac_ps = macps.split("&");
                    if(mac_ps.length == 2){
                        //mac地址一致
                        if(mac_ps[0].equals(MacUtil.getMacAddress(activity))){
                            if(savePassword){
                                String oldPassword = SharePreUtil.getInstance().getString(activity,PASSWORD,"");
                                if(!TextUtils.isEmpty(oldPassword)&&oldPassword.equals(mac_ps[1])){
                                    //密码匹配
                                    onCallbackStateNext(CallBackState.STATE_FIX);
                                }else{
                                    //开始校验密码
                                    checkPassword(activity,mac_ps[1]);
                                }
                            }else{
                                //开始校验密码
                                checkPassword(activity,mac_ps[1]);
                            }
                        }else{
                            showErrorDialog(activity,"mac地址不一致");
                        }
                    }
                }
            }else{
                showErrorDialog(activity,"密钥文件不存在");
            }
        }
        return this;
    }

    /**
     * 校验密码信息
     */
    private void checkPassword(Activity activity, String finalOldPassword){
        EditWidget editWidget = WidgetUtil.showEdit(activity);
        editWidget.setSingleChoice();
        editWidget.getEtContent().setHint("请输入八位登录密码");
        editWidget.getTvOk().setOnClickListener(v -> {
            String password = editWidget.getEtContent().getText().toString();
            if(TextUtils.isEmpty(password)){
                Toast.makeText(activity,"密码不能为空",Toast.LENGTH_SHORT).show();
            }else if(password.length() != 8){
                Toast.makeText(activity,"请输入八位密码",Toast.LENGTH_SHORT).show();
            }else{
                //密码正确
                if(finalOldPassword.equals(password)){
                    SharePreUtil.getInstance().putString(activity,PASSWORD,password);
                    //隐藏
                    editWidget.hide();
                    //U盘匹配
                    onCallbackStateNext(CallBackState.STATE_FIX);
                }else{
                    Toast.makeText(activity,"密码错误，请尽快与客服人员联系",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showErrorDialog(Activity activity,String tips){
        TipsWidget tipsWidget = WidgetUtil.showTips(activity, CallBackState.STATE_ERROR.toString());
        tipsWidget.setSingleChoice();
        tipsWidget.getTvOk().setText("重试");
        tipsWidget.getTvOk().setOnClickListener(v -> {
            tipsWidget.hide();
            onCallbackStateNext(CallBackState.STATE_RETRY);
        });
    }

    /**
     * 校验默认密码
     */
    private void createSuffix(String content){
        String filePath = checkPath+File.separator+SUFFIX_MAC_PS;
        if(new File(filePath).exists()){
            new File(filePath).delete();
        }
        //写入密码文件
        if(!TextUtils.isEmpty(content)){
            EnDecryUtil.writeToLocal(EnDecryUtil.deEncrypt(content.getBytes()),checkPath+File.separator+SUFFIX_MAC_PS);
        }
    }

    /**
     * 读取根目录
     */
    public static String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if(sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

    /* ***************************** CallbackState ***************************** */

    private OnCallbackStateListener onCallbackStateListener;

    // 接口类 -> OnCallbackStateListener
    public interface OnCallbackStateListener {
        void onCallbackState(CallBackState callbackState);
    }

    // 对外暴露接口 -> setOnCallbackStateListener
    public CheckHelper setOnCallbackStateListener(OnCallbackStateListener onCallbackStateListener) {
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
