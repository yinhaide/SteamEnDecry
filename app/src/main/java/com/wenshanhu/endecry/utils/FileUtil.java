package com.wenshanhu.endecry.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * 类作用描述(默认的U盘路径:mnt/udisk/)
 * Created by haide.yin(haide.yin@tcl.com) on 2019/11/6 10:42.
 */
public class FileUtil {

    public static int REVERSE_LENGTH = 100;

    /**
     * 加解密
     * @param strFile 源文件绝对路径
     * @return
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
        if(bufferData != null && bufferData.length > REVERSE_LENGTH){
            for(int i = 0;i < REVERSE_LENGTH ; ++i){
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
}
