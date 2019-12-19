package com.yhd.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 * 类作用描述(默认的U盘路径:mnt/udisk/)
 * Created by haide.yin(haide.yin@tcl.com) on 2019/11/6 10:42.
 */
public class FileUtil {

    /**
     * 从文件中读取数据
     * @param filename 待读取的sd card
     * @return content
     */
    public static String readFromTxtFile(String filename) {
        StringBuilder sb = new StringBuilder();
        //打开文件输入流
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(filename);
            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            //读取文件内容
            while(len > 0){
                //txt在window下默认用ANSI编码，所以中文想不乱骂的话需要GB2312转码
                //sb.append(new String(buffer,0,len,"GB2312"));
                //默认情况用UTF-8
                sb.append(new String(buffer,0,len));
                //继续将数据放到buffer中
                len = inputStream.read(buffer);
            }
            //关闭输入流
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 从文件中读取数据
     * @param filepath 文件路径
     * @return content
     */
    public static void writeTxtFile(String filepath,String content) {
        try {
            File fs = new File(filepath);
            if(fs.exists()){
                fs.delete();
            }
            fs.createNewFile();
            FileOutputStream outputStream =new FileOutputStream(fs);
            outputStream.write(content.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取assets目录下的图片
     *
     * @param context 上下文
     * @param fileName 文件名
     * @return 是否存在
     */
    public static boolean isAssetsFileExist(Context context,String fileName){
        AssetManager am = context.getAssets();
        try {
            String[] names = am.list("");
            for (String name : names) {
                if (name.equals(fileName.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
