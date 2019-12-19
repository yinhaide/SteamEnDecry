package com.yhd.endecry;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.yhd.utils.EnDecryUtil;
import com.yhd.utils.FileUtil;
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

    private static EnDecryHelper singleton;//单例
    private String USBPath;//U盘目录
    private final static String DEFAULT_PASSWORD = "88888888";//默认密码
    private final static String SUFFIX_UUID = ".uuid";//UUID临时文件
    private final static String SUFFIX_MAINBOARD = ".mainboard";//主板信息临时文件
    private final static String SUFFIX_PASSWORD = ".password";//密码临时文件
    private final static String TEMP_MP4 = ".mp4";//视频临时文件
    private final static String TEMP_MP3 = ".mp3";//音频临时文件

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

    /** 文件类型枚举 */
    public enum PlayType {
        NONE("视频不存在，播放默认值"),
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
     * 处理加密视频的链接
     */
    public void getDeVideo(String videoPath,OnPlayCallBackListener onPlayCallBackListener){
        //回调不为空
        if(onPlayCallBackListener == null || TextUtils.isEmpty(videoPath)){
            onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
        }else{
            File videoFile = new File(videoPath);
            if(videoFile.exists()){
                if(videoPath.endsWith(EnDecryUtil.SUFFIX_V)){//有加密的视频
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
                            onPlayCallBackListener.onPlayCallBack(PlayType.BUFFER_VIDEO,videoPath,decryBuffer);
                        }else{
                            //如果是Android6.0以下，则先解密然后存到本地播放
                            //为了不让用户看到，存缓存文件为.mp4，名字唯一
                            String tempVideoPath = videoFile.getParent()+File.separator+TEMP_MP4;
                            File tempVideoFile = new File(tempVideoPath);
                            EnDecryUtil.writeToLocal(decryBuffer,tempVideoPath);
                            if(tempVideoFile.exists()){
                                onPlayCallBackListener.onPlayCallBack(PlayType.URL_VIDEO,tempVideoPath,null);
                            }else{
                                onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
                            }
                        }
                    }catch(Exception e){
                        onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
                    }
                }else if(videoPath.endsWith(EnDecryUtil.MP4)){//没有加密的MP4格式
                    onPlayCallBackListener.onPlayCallBack(PlayType.URL_VIDEO,videoPath,null);
                }else{//其他格式不支持
                    onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
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
                    onPlayCallBackListener.onPlayCallBack(PlayType.URL_VIDEO,mp4VideoPath,null);
                }else{
                    onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
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
            onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
        }else{
            File musicFile = new File(musicPath);
            if(musicFile.exists()){
                if(musicPath.endsWith(EnDecryUtil.SUFFIX_M)){//有加密的音频
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
                            onPlayCallBackListener.onPlayCallBack(PlayType.BUFFER_MUSIC,musicPath,decryBuffer);
                        }else{
                            //如果是Android6.0以下，则先解密然后存到本地播放
                            //为了不让用户看到，存缓存文件为.mp3，名字唯一
                            String tempMusicPath = musicFile.getParent()+File.separator+TEMP_MP3;
                            EnDecryUtil.writeToLocal(decryBuffer,tempMusicPath);
                            if(new File(tempMusicPath).exists()){
                                onPlayCallBackListener.onPlayCallBack(PlayType.URL_MUSIC,tempMusicPath,null);
                            }else{
                                onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
                            }
                        }
                    }catch(Exception e){
                        onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
                    }
                }else if(musicPath.endsWith(EnDecryUtil.MP3)){//没有加密的MP3格式
                    onPlayCallBackListener.onPlayCallBack(PlayType.URL_MUSIC,musicPath,null);
                }else{//其他格式不支持
                    onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
                }
            }else{
                //找不到以.mhd结尾的尝试找.mp3结尾的
                String mp3MusicPath = musicPath;
                if(mp3MusicPath.contains(".")){
                    String[] nameArray = mp3MusicPath.split("\\.");
                    if(nameArray.length > 0){
                        //统一更换加密后的后缀
                        mp3MusicPath = mp3MusicPath.replace(nameArray[nameArray.length - 1],EnDecryUtil.MP4);
                    }
                }
                if(new File(mp3MusicPath).exists()){
                    onPlayCallBackListener.onPlayCallBack(PlayType.URL_VIDEO,mp3MusicPath,null);
                }else{
                    onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
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
            onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
        }else{
            File txtFile = new File(txtPath);
            if(txtFile.exists()){
                if(txtPath.endsWith(EnDecryUtil.SUFFIX_T)){//有加密的文本
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
                        onPlayCallBackListener.onPlayCallBack(PlayType.BUFFER_TXT,txtPath,decryBuffer);
                    }catch(Exception e){
                        onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
                    }
                }else if(txtPath.endsWith(EnDecryUtil.TXT)){//没有加密的txt格式
                    onPlayCallBackListener.onPlayCallBack(PlayType.URL_TXT,txtPath,null);
                }else{//其他格式不支持
                    onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
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
                    onPlayCallBackListener.onPlayCallBack(PlayType.URL_TXT,newTxtPath,null);
                }else{
                    onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
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
            onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
        }else{
            File pngFile = new File(pngPath);
            if(pngFile.exists()){
                if(pngPath.endsWith(EnDecryUtil.SUFFIX_P)){//有加密的PNG
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
                        onPlayCallBackListener.onPlayCallBack(PlayType.BUFFER_PNG,pngPath,decryBuffer);
                    }catch(Exception e){
                        onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
                    }
                }else if(pngPath.endsWith(EnDecryUtil.PNG)){//没有加密的png格式
                    onPlayCallBackListener.onPlayCallBack(PlayType.URL_PNG,pngPath,null);
                }else{//其他格式不支持
                    onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
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
                    onPlayCallBackListener.onPlayCallBack(PlayType.URL_PNG,newPngPath,null);
                }else{
                    onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
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
            onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
        }else{
            File jpgFile = new File(jpgPath);
            if(jpgFile.exists()){
                if(jpgPath.endsWith(EnDecryUtil.SUFFIX_J)){//有加密的PNG
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
                        onPlayCallBackListener.onPlayCallBack(PlayType.BUFFER_JPG,jpgPath,decryBuffer);
                    }catch(Exception e){
                        onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
                    }
                }else if(jpgPath.endsWith(EnDecryUtil.PNG)){//没有加密的png格式
                    onPlayCallBackListener.onPlayCallBack(PlayType.URL_JPG,jpgPath,null);
                }else{//其他格式不支持
                    onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
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
                    onPlayCallBackListener.onPlayCallBack(PlayType.URL_JPG,newJpgPath,null);
                }else{
                    onPlayCallBackListener.onPlayCallBack(PlayType.NONE,null,null);
                }
            }
        }
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

    /* ***************************** PlayCallBack ***************************** */

    private OnPlayCallBackListener onPlayCallBackListener;

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
            onPlayCallBackListener.onPlayCallBack(playType,url,buffer);
        }
    }
}
