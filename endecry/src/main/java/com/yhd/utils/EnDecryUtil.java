package com.yhd.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * 字节流加密解密的方案
 * Created by haide.yin(haide.yin@tcl.com) on 2019/11/6 10:42.
 */
public class EnDecryUtil {

    //需要加密的字节流前多少位，这些字节流将和它对应的顺序下标index异或运算
    public static final int REVERSE_LENGTH = 100;
    //解密文件输出的后缀
    public static final String SUFFIX_V = "vhd";
    //解密文件输出的后缀
    public static final String SUFFIX_P = "phd";
    //解密文件输出的后缀
    public static final String SUFFIX_T = "thd";
    //解密文件输出的后缀
    public static final String SUFFIX_J = "jhd";
    //解密文件输出的后缀
    public static final String SUFFIX_M = "mhd";
    //mp4
    public static final String MP4 = "mp4";
    //png
    public static final String PNG= "png";
    //png
    public static final String TXT= "txt";
    //jpg
    public static final String JPG= "jpg";
    //jpg
    public static final String MP3= "mp3";

    /**
     * 加解密
     * @param strFile 源文件绝对路径
     * @return 对流加解密，返回加解密的字节流
     */
    public static byte[] deEncrypt(String strFile) {
        byte[] bufferData = null;
        try{
            // 拿到输入流
            FileInputStream input = new FileInputStream(strFile);
            // 建立存储器
            bufferData = new byte[input.available()];
            // 读取到存储器
            input.read(bufferData);
            // 关闭输入流
            input.close();
            // 返回数据
            return deEncrypt(bufferData);
        }catch(Exception e){
            e.printStackTrace();
        }
        return bufferData;
    }

    /**
     * 对流进行异或运算，两次运算就可以变回原来的
     *
     * @param bufferData 数据源字节流
     * @return 异或运算之后的字节流
     */
    public static byte[] deEncrypt(byte[] bufferData){
        if(bufferData != null){
            int maxLength = bufferData.length >= REVERSE_LENGTH ? REVERSE_LENGTH : bufferData.length;
            for(int i = 0;i < maxLength ; i++){
                bufferData[i] = (byte) (bufferData[i] ^ i);
            }
        }
        return bufferData;
    }

    /**
     * 将流写到指定文件
     *
     * @param buffer 数据源字节流
     * @param filePath 目标文件
     */
    public static void writeToLocal(byte[] buffer,String filePath){
        /*if(filePath.contains(".")){
            String[] nameArray = filePath.split("\\.");
            if(nameArray.length > 0){
                //统一更换加密后的后缀
                filePath = filePath.replace(nameArray[nameArray.length - 1],SUFFIX);
            }
        }*/
        OutputStream out = null;
        File file = new File(filePath);
        try {
            //创建文件
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            out = new FileOutputStream(file);
            out.write(buffer);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将InputStream转成字节流
     *
     * @param input 数据源输入流
     * @return 输出字节流
     */
    public static byte[] toByteArray(InputStream input){
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        try {
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            byte[] bufferArray = output.toByteArray();
            output.close();
            return bufferArray;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * 从字节流中读出UTF-8编码的的字符串
     * @param buffer 字节流
     * @return 转码的字符串
     */
    public static String getUTF8String(byte[] buffer){
        String result = "";
        result = new String(buffer, StandardCharsets.UTF_8);
        return result;
    }

    /**
     * 从字节流中转为Bitmap
     * @param buffer 字节流
     * @return 转码的字符串
     */
    public static Bitmap getBitmap(byte[] buffer){
        return BitmapFactory.decodeByteArray(buffer,0, buffer.length);
    }

    /**
     * 从字节流中转为Bitmap
     * @param buffer 字节流
     * @param times 压缩倍数
     * @return 转码的字符串
     */
    public static Bitmap getBitmap(byte[] buffer,int times,boolean isPng){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = times;
        if(isPng){//这个值是设置色彩模式，默认值是ARGB_8888，在这个模式下，一个像素点占用4bytes空间
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        }else{//一般对透明度不做要求的话，一般采用RGB_565模式，这个模式下一个像素点占用2bytes
            options.inPreferredConfig = Bitmap.Config.RGB_565;
        }
        return BitmapFactory.decodeByteArray(buffer,0, buffer.length,options);
    }
}
