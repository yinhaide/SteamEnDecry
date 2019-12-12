package com.wenshanhu.endecry.helper;

import android.os.Handler;
import android.os.Looper;

import com.wenshanhu.endecry.bean.SteamBean;
import com.wenshanhu.endecry.receiver.USBReceiver;
import com.wenshanhu.endecry.utils.FileUtil;

import java.io.File;

/**
 * 类作用描述
 * Created by haide.yin(haide.yin@tcl.com) on 2019/11/12 14:50.
 */
public class EnDeCryHelper {

    private static EnDeCryHelper singleton;
    private SteamBean steamBean;
    private Handler mainHandler;//主线程

    /**
     * 单例
     */
    public static synchronized EnDeCryHelper get(){
        if(singleton == null){
            singleton = new EnDeCryHelper();
        }
        return singleton;
    }

    private EnDeCryHelper(){
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 更新资源路径机构
     */
    public void init(InitCallback initCallback){
        new Thread(() -> {
            long beginTime = System.currentTimeMillis();
            steamBean = new SteamBean(USBReceiver.USB_PATH + File.separator + USBReceiver.STEAM_PATH);
            long endTime = System.currentTimeMillis();
            if(initCallback != null){
                mainHandler.post(() -> initCallback.onFinish(endTime-beginTime));
            }
        }).start();
    }

    /**
     * 获取资源路径机构
     */
    public SteamBean getSteamBean() {
        if(steamBean == null){
            steamBean = new SteamBean(USBReceiver.USB_PATH + File.separator + USBReceiver.STEAM_PATH);
        }
        return steamBean;
    }

    /**
     * 开始加密
     */
    public void beginEncry(CryCallback cryCallback){
        new Thread(() -> {
            String sourcePath = steamBean.getEnDeCryBean().getSourcePath();
            String encryPath = steamBean.getEnDeCryBean().getEncryPath();
            File sourceFileFolder = new File(sourcePath);
            File encryFileFolder = new File(encryPath);
            if(!sourceFileFolder.exists() || !sourceFileFolder.isDirectory()){
                if(cryCallback != null){
                    mainHandler.post(() -> cryCallback.onError("source文件夹不存在"));
                }
                return;
            }
            if(!encryFileFolder.exists() || !encryFileFolder.isDirectory()){
                if(cryCallback != null){
                    mainHandler.post(() -> cryCallback.onError("encry文件夹不存在"));
                }
                return;
            }
            String[] sourceFiles = sourceFileFolder.list();
            if(sourceFiles == null || sourceFiles.length == 0){
                if(cryCallback != null){
                    mainHandler.post(() -> cryCallback.onError("source文件夹没有视频"));
                }
                return;
            }
            for(String fileName : sourceFiles){
                String sourceFilePath = sourcePath + File.separator + fileName;
                String encryFilePath = encryPath + File.separator + fileName;
                if(new File(sourceFilePath).exists()){
                    FileUtil.writeToLocal(FileUtil.deEncrypt(sourceFilePath),encryFilePath);
                    if(cryCallback != null){
                        mainHandler.post(() -> cryCallback.onProgress("正在加密:"+encryFilePath));
                    }
                }else{
                    if(cryCallback != null){
                        mainHandler.post(() -> cryCallback.onError("文件"+sourceFilePath+"不存在"));
                    }
                }
            }
            if(cryCallback != null){
                mainHandler.post(cryCallback::onFinish);
            }
        }).start();
    }

    /**
     * 开始解密
     */
    public void beginDecry(CryCallback cryCallback){
        new Thread(() -> {
            String sourcePath = steamBean.getEnDeCryBean().getSourcePath();
            String decryPath = steamBean.getEnDeCryBean().getDecryPath();
            File sourceFileFolder = new File(sourcePath);
            File decryFileFolder = new File(decryPath);
            if(!sourceFileFolder.exists() || !sourceFileFolder.isDirectory()){
                if(cryCallback != null){
                    mainHandler.post(() -> cryCallback.onError("source文件夹不存在"));
                }
                return;
            }
            if(!decryFileFolder.exists() || !decryFileFolder.isDirectory()){
                if(cryCallback != null){
                    mainHandler.post(() -> cryCallback.onError("decry文件夹不存在"));
                }
                return;
            }
            String[] sourceFiles = sourceFileFolder.list();
            if(sourceFiles == null || sourceFiles.length == 0){
                if(cryCallback != null){
                    mainHandler.post(() -> cryCallback.onError("source文件夹没有视频"));
                }
                return;
            }
            for(String fileName : sourceFiles){
                String sourceFilePath = sourcePath + File.separator + fileName;
                String decryFilePath = decryPath + File.separator + fileName;
                if(new File(sourceFilePath).exists()){
                    FileUtil.writeToLocal(FileUtil.deEncrypt(sourceFilePath),decryFilePath);
                    if(cryCallback != null){
                        mainHandler.post(() -> cryCallback.onProgress("正在解密:"+decryFilePath));
                    }
                }else{
                    if(cryCallback != null){
                        mainHandler.post(() -> cryCallback.onError("文件"+sourceFilePath+"不存在"));
                    }
                }
            }
            if(cryCallback != null){
                mainHandler.post(cryCallback::onFinish);
            }
        }).start();
    }


    /**
     * 初始化的回调
     */
    public interface InitCallback{
        void onFinish(long msec);
    }

    /**
     * 加解密的回调
     */
    public interface CryCallback{
        void onProgress(String msg);
        void onError(String msg);
        void onFinish();
    }
}
