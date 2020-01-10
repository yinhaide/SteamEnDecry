package com.yhd.endecry;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.yhd.utils.EnDecryUtil;
import com.yhd.utils.WidgetUtil;
import com.yhd.widget.EditWidget;
import com.yhd.widget.TipsWidget;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 类作用描述
 * Created by haide.yin(haide.yin@tcl.com) on 2019/12/18 15:47.
 */
public class EnDecryHelper {

    public final static String TEMP_MP4 = ".mp4";//视频临时文件
    public final static String TEMP_MP3 = ".mp3";//音频临时文件

    private static EnDecryHelper singleton;//单例
    private Handler mainHandler;//主线程
    private String USBPath;//U盘目录
    //private final static String DEFAULT_PASSWORD = "88888888";//默认密码
    private final static String SUFFIX_UUID = ".uuid";//出厂UUID文件
    private final static String SUFFIX_MAINBOARD = ".mainboard";//主板信息自动生成文件
    private final static String SUFFIX_PASSWORD = ".password";//用户密码自动生成文件
    private final static String SUFFIX_DEFAULT = ".default";//出厂密码文件

    /** 状态枚举 */
    public enum CallBackState{
        USB_ERROR("设备异常，请尽快与客服人员联系"),
        USB_FIX("U盘符合"),
        USB_RETRY("重试"),
        USB_NOT_EXIST("U盘不存在");

        private final String state;

        CallBackState(String var3) {
            this.state = var3;
        }

        public String toString() {
            return this.state;
        }
    }

    /** 文件类型枚举 */
    public enum PlayType {
        NONE("文件不存在，播放默认值"),
        URL_MUSIC("播放链接的音频"),
        URL_VIDEO("播放链接的视频"),
        BUFFER_MUSIC("播放音乐字节流"),
        BUFFER_VIDEO("播放视频字节流"),
        URL_TXT("加载链接的文本"),
        BUFFER_TXT("加载字节流的文本"),
        URL_PNG("加载链接的PNG"),
        BUFFER_PNG("加载字节流的PNG"),
        URL_JPG("加载链接的JPG"),
        BUFFER_JPG("加载字节流的JPG");

        private final String state;

        PlayType(String var3) {
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
     * 构造函数
     */
    private EnDecryHelper(){
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 处理加密视频的链接
     */
    public void getDeVideo(String videoPath,OnPlayCallBackListener onPlayCallBackListener){
        //回调不为空
        if(onPlayCallBackListener == null || TextUtils.isEmpty(videoPath)){
            playMainCallBack(onPlayCallBackListener,PlayType.NONE,null,null);
        }else{
            File videoFile = new File(videoPath);
            if(videoFile.exists()){
                if(videoPath.endsWith(EnDecryUtil.SUFFIX_V)){//有加密的视频
                    new Thread(() -> {
                        byte[] bufferData;
                        try {
                            // 拿到输入流
                            FileInputStream input = new FileInputStream(videoPath);
                            // 建立存储器
                            bufferData = new byte[input.available()];
                            // 读取到存储器
                            input.read(bufferData);
                            // 关闭输入流
                            input.close();
                            //检查当前的版本
                            int version = android.os.Build.VERSION.SDK_INT;
                            //得到解密的视频流
                            byte[] decryBuffer = EnDecryUtil.deEncrypt(bufferData);
                            //如果会Android6.0及以上则解密流进行播放
                            if (version >= Build.VERSION_CODES.M) {
                                // 播放加密的视频流
                                playMainCallBack(onPlayCallBackListener,PlayType.BUFFER_VIDEO,null,decryBuffer);
                            }else{
                                //如果是Android6.0以下，则先解密然后存到本地播放
                                //为了不让用户看到，存缓存文件为.mp4，名字唯一
                                String tempVideoPath = USBPath+File.separator+TEMP_MP4;
                                File tempVideoFile = new File(tempVideoPath);
                                EnDecryUtil.writeToLocal(decryBuffer,tempVideoPath);
                                if(tempVideoFile.exists()){
                                    playMainCallBack(onPlayCallBackListener,PlayType.URL_VIDEO,tempVideoPath,null);
                                }else{
                                    playMainCallBack(onPlayCallBackListener,PlayType.NONE,videoPath,null);
                                }
                            }
                        }catch(Exception e){
                            playMainCallBack(onPlayCallBackListener,PlayType.NONE,videoPath,null);
                        }
                    }).start();
                }else if(videoPath.endsWith(EnDecryUtil.MP4)){//没有加密的MP4格式
                     playMainCallBack(onPlayCallBackListener,PlayType.URL_VIDEO,videoPath,null);
                }else{//其他格式不支持
                     playMainCallBack(onPlayCallBackListener,PlayType.NONE,videoPath,null);
                }
            }else{
                //找不到以.vhd结尾的尝试找.mp4结尾的
                String mp4VideoPath = videoPath;
                if(mp4VideoPath.contains(".")){
                    String[] nameArray = mp4VideoPath.split("\\.");
                    if(nameArray.length > 0){
                        //统一更换加密后的后缀
                        mp4VideoPath = mp4VideoPath.replace(nameArray[nameArray.length - 1],EnDecryUtil.MP4);
                    }
                }
                if(new File(mp4VideoPath).exists()){
                     playMainCallBack(onPlayCallBackListener,PlayType.URL_VIDEO,mp4VideoPath,null);
                }else{
                     playMainCallBack(onPlayCallBackListener,PlayType.NONE,null,null);
                }
            }
        }
    }

    /**
     * 处理加密的音频链接
     */
    public void getDeMusic(String musicPath,OnPlayCallBackListener onPlayCallBackListener){
        //回调不为空
        if(onPlayCallBackListener == null || TextUtils.isEmpty(musicPath)){
             playMainCallBack(onPlayCallBackListener,PlayType.NONE,musicPath,null);
        }else{
            File musicFile = new File(musicPath);
            if(musicFile.exists()){
                if(musicPath.endsWith(EnDecryUtil.SUFFIX_M)){//有加密的音频
                    new Thread(() -> {
                        byte[] bufferData;
                        try {
                            // 拿到输入流
                            FileInputStream input = new FileInputStream(musicPath);
                            // 建立存储器
                            bufferData = new byte[input.available()];
                            // 读取到存储器
                            input.read(bufferData);
                            // 关闭输入流
                            input.close();
                            //检查当前的版本
                            int version = android.os.Build.VERSION.SDK_INT;
                            //得到解密的音频流
                            byte[] decryBuffer = EnDecryUtil.deEncrypt(bufferData);
                            //如果会Android6.0及以上则解密流进行播放
                            if (version >= Build.VERSION_CODES.M) {
                                // 播放加密的视频流
                                playMainCallBack(onPlayCallBackListener,PlayType.BUFFER_MUSIC,null,decryBuffer);
                            }else{
                                //如果是Android6.0以下，则先解密然后存到本地播放
                                //为了不让用户看到，存缓存文件为.mp3，名字唯一
                                String tempMusicPath = USBPath+File.separator+TEMP_MP3;
                                EnDecryUtil.writeToLocal(decryBuffer,tempMusicPath);
                                if(new File(tempMusicPath).exists()){
                                    playMainCallBack(onPlayCallBackListener,PlayType.URL_MUSIC,tempMusicPath,null);
                                }else{
                                    playMainCallBack(onPlayCallBackListener,PlayType.NONE,musicPath,null);
                                }
                            }
                        }catch(Exception e){
                            playMainCallBack(onPlayCallBackListener,PlayType.NONE,musicPath,null);
                        }
                    }).start();
                }else if(musicPath.endsWith(EnDecryUtil.MP3)){//没有加密的MP3格式
                     playMainCallBack(onPlayCallBackListener,PlayType.URL_MUSIC,musicPath,null);
                }else{//其他格式不支持
                     playMainCallBack(onPlayCallBackListener,PlayType.NONE,musicPath,null);
                }
            }else{
                //找不到以.mhd结尾的尝试找.mp3结尾的
                String mp3MusicPath = musicPath;
                if(mp3MusicPath.contains(".")){
                    String[] nameArray = mp3MusicPath.split("\\.");
                    if(nameArray.length > 0){
                        //统一更换加密后的后缀
                        mp3MusicPath = mp3MusicPath.replace(nameArray[nameArray.length - 1],EnDecryUtil.MP3);
                    }
                }
                if(new File(mp3MusicPath).exists()){
                     playMainCallBack(onPlayCallBackListener,PlayType.URL_MUSIC,mp3MusicPath,null);
                }else{
                     playMainCallBack(onPlayCallBackListener,PlayType.NONE,musicPath,null);
                }
            }
        }
    }

    /**
     * 处理加密的文本链接
     */
    public void getDeTxt(String txtPath,OnPlayCallBackListener onPlayCallBackListener){
        //回调不为空
        if(onPlayCallBackListener == null || TextUtils.isEmpty(txtPath)){
             playMainCallBack(onPlayCallBackListener,PlayType.NONE,txtPath,null);
        }else{
            File txtFile = new File(txtPath);
            if(txtFile.exists()){
                if(txtPath.endsWith(EnDecryUtil.SUFFIX_T)){//有加密的文本
                    new Thread(() -> {
                        byte[] bufferData;
                        try {
                            // 拿到输入流
                            FileInputStream input = new FileInputStream(txtFile);
                            // 建立存储器
                            bufferData = new byte[input.available()];
                            // 读取到存储器
                            input.read(bufferData);
                            // 关闭输入流
                            input.close();
                            //得到解密的音频流
                            byte[] decryBuffer = EnDecryUtil.deEncrypt(bufferData);
                            //解密回调
                            playMainCallBack(onPlayCallBackListener,PlayType.BUFFER_TXT,null,decryBuffer);
                        }catch(Exception e){
                            playMainCallBack(onPlayCallBackListener,PlayType.NONE,txtPath,null);
                        }
                    }).start();
                }else if(txtPath.endsWith(EnDecryUtil.TXT)){//没有加密的txt格式
                     playMainCallBack(onPlayCallBackListener,PlayType.URL_TXT,txtPath,null);
                }else{//其他格式不支持
                     playMainCallBack(onPlayCallBackListener,PlayType.NONE,txtPath,null);
                }
            }else{
                //找不到以.thd结尾的尝试找.txt结尾的
                String newTxtPath = txtPath;
                if(newTxtPath.contains(".")){
                    String[] nameArray = newTxtPath.split("\\.");
                    if(nameArray.length > 0){
                        //统一更换加密后的后缀
                        newTxtPath = newTxtPath.replace(nameArray[nameArray.length - 1],EnDecryUtil.TXT);
                    }
                }
                if(new File(newTxtPath).exists()){
                     playMainCallBack(onPlayCallBackListener,PlayType.URL_TXT,newTxtPath,null);
                }else{
                     playMainCallBack(onPlayCallBackListener,PlayType.NONE,txtPath,null);
                }
            }
        }
    }

    /**
     * 处理加密的PNG链接
     */
    public void getDePng(String pngPath,OnPlayCallBackListener onPlayCallBackListener){
        //回调不为空
        if(onPlayCallBackListener == null || TextUtils.isEmpty(pngPath)){
             playMainCallBack(onPlayCallBackListener,PlayType.NONE,pngPath,null);
        }else{
            File pngFile = new File(pngPath);
            if(pngFile.exists()){
                if(pngPath.endsWith(EnDecryUtil.SUFFIX_P)){//有加密的PNG
                    new Thread(() -> {
                        byte[] bufferData;
                        try {
                            // 拿到输入流
                            FileInputStream input = new FileInputStream(pngFile);
                            // 建立存储器
                            bufferData = new byte[input.available()];
                            // 读取到存储器
                            input.read(bufferData);
                            // 关闭输入流
                            input.close();
                            //得到解密的流
                            byte[] decryBuffer = EnDecryUtil.deEncrypt(bufferData);
                            //解密回调
                            playMainCallBack(onPlayCallBackListener,PlayType.BUFFER_PNG,null,decryBuffer);
                        }catch(Exception e){
                            playMainCallBack(onPlayCallBackListener,PlayType.NONE,pngPath,null);
                        }
                    }).start();
                }else if(pngPath.endsWith(EnDecryUtil.PNG)){//没有加密的png格式
                     playMainCallBack(onPlayCallBackListener,PlayType.URL_PNG,pngPath,null);
                }else{//其他格式不支持
                     playMainCallBack(onPlayCallBackListener,PlayType.NONE,pngPath,null);
                }
            }else{
                //找不到以.phd结尾的尝试找.png结尾的
                String newPngPath = pngPath;
                if(newPngPath.contains(".")){
                    String[] nameArray = newPngPath.split("\\.");
                    if(nameArray.length > 0){
                        //统一更换加密后的后缀
                        newPngPath = newPngPath.replace(nameArray[nameArray.length - 1],EnDecryUtil.PNG);
                    }
                }
                if(new File(newPngPath).exists()){
                     playMainCallBack(onPlayCallBackListener,PlayType.URL_PNG,newPngPath,null);
                }else{
                     playMainCallBack(onPlayCallBackListener,PlayType.NONE,pngPath,null);
                }
            }
        }
    }

    /**
     * 处理加密的JPG链接
     */
    public void getDeJpg(String jpgPath,OnPlayCallBackListener onPlayCallBackListener){
        //回调不为空
        if(onPlayCallBackListener == null || TextUtils.isEmpty(jpgPath)){
             playMainCallBack(onPlayCallBackListener,PlayType.NONE,jpgPath,null);
        }else{
            File jpgFile = new File(jpgPath);
            if(jpgFile.exists()){
                if(jpgPath.endsWith(EnDecryUtil.SUFFIX_J)){//有加密的PNG
                    new Thread(() -> {
                        byte[] bufferData;
                        try {
                            // 拿到输入流
                            FileInputStream input = new FileInputStream(jpgFile);
                            // 建立存储器
                            bufferData = new byte[input.available()];
                            // 读取到存储器
                            input.read(bufferData);
                            // 关闭输入流
                            input.close();
                            //得到解密的流
                            byte[] decryBuffer = EnDecryUtil.deEncrypt(bufferData);
                            //解密回调
                            playMainCallBack(onPlayCallBackListener,PlayType.BUFFER_JPG,null,decryBuffer);
                        }catch(Exception e){
                            playMainCallBack(onPlayCallBackListener,PlayType.NONE,jpgPath,null);
                        }
                    }).start();
                }else if(jpgPath.endsWith(EnDecryUtil.JPG)){//没有加密的JPG格式
                     playMainCallBack(onPlayCallBackListener,PlayType.URL_JPG,jpgPath,null);
                }else{//其他格式不支持
                     playMainCallBack(onPlayCallBackListener,PlayType.NONE,jpgPath,null);
                }
            }else{
                //找不到以.jhd结尾的尝试找.jpg结尾的
                String newJpgPath = jpgPath;
                if(newJpgPath.contains(".")){
                    String[] nameArray = newJpgPath.split("\\.");
                    if(nameArray.length > 0){
                        //统一更换加密后的后缀
                        newJpgPath = newJpgPath.replace(nameArray[nameArray.length - 1],EnDecryUtil.JPG);
                    }
                }
                if(new File(newJpgPath).exists()){
                     playMainCallBack(onPlayCallBackListener,PlayType.URL_JPG,newJpgPath,null);
                }else{
                     playMainCallBack(onPlayCallBackListener,PlayType.NONE,jpgPath,null);
                }
            }
        }
    }

    /**
     * 校验U盘
     */
    public EnDecryHelper checkUSBState(Activity activity,String USBPath){
        deleteTemp(USBPath);
        this.USBPath = USBPath;
        if(!new File(USBPath).exists()){//U盘不存在
            TipsWidget tipsWidget = WidgetUtil.showTips(activity,CallBackState.USB_NOT_EXIST.toString()+":"+USBPath);
            tipsWidget.setSingleChoice();
            tipsWidget.getTvOk().setText("重试");
            tipsWidget.getTvOk().setOnClickListener(v -> {
                tipsWidget.hide();
                onCallbackStateNext(CallBackState.USB_RETRY);
            });
            onCallbackStateNext(CallBackState.USB_NOT_EXIST);
        }else{//U盘存在
            if(new File(USBPath+File.separator+SUFFIX_UUID).exists()){//uuid文件存在
                //保存在U盘文件的UUID信息
                String saveUUID = EnDecryUtil.getUTF8String(EnDecryUtil.deEncrypt(USBPath+File.separator+SUFFIX_UUID));
                //实时获取的U盘的UUID
                String usbUUID = getUSBUUID(activity);
                Toast.makeText(activity,"U盘的UUID信息:"+usbUUID+" 出厂的UUID信息:"+saveUUID,Toast.LENGTH_SHORT).show();
                //usb的UUID与上次存储的UUID完全匹配
                if(!TextUtils.isEmpty(saveUUID) && !TextUtils.isEmpty(usbUUID) && usbUUID.equals(saveUUID)){
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
        return this;
    }

    /**
     * 校验主板信息
     */
    private void checkMainBoard(Activity activity){
        if(new File(USBPath+File.separator+SUFFIX_MAINBOARD).exists()){//mainboard文件存在
            //保存在U盘文件的MainBoard信息
            String saveMainBoard = EnDecryUtil.getUTF8String(EnDecryUtil.deEncrypt(USBPath+File.separator+SUFFIX_MAINBOARD));
            //实时获取的U盘的UUID
            String tvMainBoard = getDeviceMainBoard();
            Toast.makeText(activity,"U盘的主板信息:"+tvMainBoard+" 保存的主板信息:"+saveMainBoard,Toast.LENGTH_SHORT).show();
            //usb的mainBoard与上次存储的MainBoard完全匹配
            if(!TextUtils.isEmpty(saveMainBoard) && !TextUtils.isEmpty(tvMainBoard) && tvMainBoard.equals(saveMainBoard)){
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
        if(!new File(USBPath+File.separator+SUFFIX_DEFAULT).exists()){//default文件存在
            TipsWidget tipsWidget = WidgetUtil.showTips(activity,CallBackState.USB_ERROR.toString());
            tipsWidget.setSingleChoice();
            tipsWidget.getTvOk().setText("重试");
            tipsWidget.getTvOk().setOnClickListener(v -> {
                tipsWidget.hide();
                onCallbackStateNext(CallBackState.USB_RETRY);
            });
            onCallbackStateNext(CallBackState.USB_ERROR);
            return;
        }
        //拿到默认密码
        String defaultPassword = EnDecryUtil.getUTF8String(EnDecryUtil.deEncrypt(USBPath+File.separator+SUFFIX_DEFAULT));
        String oldPassword = "";
        if(new File(USBPath+File.separator+SUFFIX_PASSWORD).exists()){//password文件存在
            //保存在U盘文件的Password信息
            oldPassword = EnDecryUtil.getUTF8String(EnDecryUtil.deEncrypt(USBPath+File.separator+SUFFIX_PASSWORD));
        }
        Toast.makeText(activity,"用户的密码:"+oldPassword+" 出厂的密码:"+defaultPassword,Toast.LENGTH_SHORT).show();
        EditWidget editWidget = WidgetUtil.showEdit(activity);
        editWidget.setSingleChoice();
        if(!TextUtils.isEmpty(oldPassword)){
            editWidget.getEtContent().setHint("请输入八位登录密码");
        }else{//如果没有设置过密码则需要设置开机密码
            editWidget.getEtContent().setHint("请输入八位出厂密码");
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
                    if(finalOldPassword.equals(password)){
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
                    if(password.equals(defaultPassword)){
                        //隐藏
                        editWidget.hide();
                        //设置新的密码
                        createPassword(activity);
                    }else{
                        Toast.makeText(activity,"设备异常，请尽快与客服人员联系",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * 校验密码信息
     */
    private void createPassword(Activity activity){
        EditWidget editWidget = WidgetUtil.showEdit(activity);
        editWidget.setSingleChoice();
        editWidget.getEtContent().setText("");
        editWidget.getEtContent().setHint("请输设置新的八位登录密码");
        editWidget.getTvOk().setOnClickListener(v -> {
            String password = editWidget.getEtContent().getText().toString();
            if(TextUtils.isEmpty(password)){
                Toast.makeText(activity,"密码不能为空",Toast.LENGTH_SHORT).show();
            }else if(password.length() != 8){
                Toast.makeText(activity,"请输入八位密码",Toast.LENGTH_SHORT).show();
            }else{
                //隐藏
                editWidget.hide();
                //U盘匹配
                onCallbackStateNext(CallBackState.USB_FIX);
                //创建记录文件
                createSuffix(activity,password);
            }
        });

    }

    /**
     * 校验默认密码
     */
    private void createSuffix(Activity activity,String password){
        //写入密码文件
        if(!TextUtils.isEmpty(password)){
            EnDecryUtil.writeToLocal(EnDecryUtil.deEncrypt(password.getBytes()),USBPath+File.separator+SUFFIX_PASSWORD);
        }
        //写入UUID文件
        /*if(!TextUtils.isEmpty(getUSBUUID(activity))){
            EnDecryUtil.writeToLocal(EnDecryUtil.deEncrypt(getUSBUUID(activity).getBytes()),USBPath+File.separator+SUFFIX_UUID);
        }*/
        //写入MainBoard文件
        if(!TextUtils.isEmpty(getDeviceMainBoard())){
            EnDecryUtil.writeToLocal(EnDecryUtil.deEncrypt(getDeviceMainBoard().getBytes()),USBPath+File.separator+SUFFIX_MAINBOARD);
        }
    }

    /**
     * 读取U盘的UUID，读取最后一个
     */
    private static String getUSBUUID(Context context){
        String msg = "unkow";
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
        String mainBoard = "unkow";
        if(!TextUtils.isEmpty(Build.BOARD)){
            mainBoard = Build.BOARD;
        }
        return mainBoard;
    }

    /**
     * 删除缓存文件
     * @param usbPath U盘路径
     */
    public void deleteTemp(String usbPath){
        String tempMusicPath = usbPath+File.separator+TEMP_MP3;
        String tempVideoPath = usbPath+File.separator+TEMP_MP4;
        if(new File(tempMusicPath).exists()){
            new File(tempMusicPath).delete();
        }
        if(new File(tempVideoPath).exists()){
            new File(tempVideoPath).delete();
        }
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

    /* ***************************** PlayCallBack ***************************** */

    private OnPlayCallBackListener onPlayCallBackListener;

    // 主线程运行 -> playMainCallBack
    private void playMainCallBack(OnPlayCallBackListener playCallBackListener,PlayType playType, String url, byte[] buffer){
        if(playCallBackListener != null){
            mainHandler.post(() -> playCallBackListener.onPlayCallBack(playType,url,buffer));
        }
    }

    // 接口类 -> OnPlayCallBackListener
    public interface OnPlayCallBackListener {
        void onPlayCallBack(PlayType playType, String url, byte[] buffer);
    }

    // 对外暴露接口 -> setOnPlayCallBackListener
    public void setOnPlayCallBackListener(OnPlayCallBackListener onPlayCallBackListener) {
        this.onPlayCallBackListener = onPlayCallBackListener;
    }

    // 内部使用方法 -> PlayCallBackNext
    private void onPlayCallBackNext(PlayType playType, String url, byte[] buffer) {
        if (onPlayCallBackListener != null) {
             playMainCallBack(onPlayCallBackListener,playType,url,buffer);
        }
    }
}
