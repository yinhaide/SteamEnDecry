package com.wenshanhu.endecry.bean;

import java.io.File;
import java.io.Serializable;

/**
 * 类作用描述
 * Created by haide.yin(haide.yin@tcl.com) on 2019/11/28 18:09.
 */
public class EnDeCryBean implements Serializable {

    private String encryPath;
    private String decryPath;
    private String sourcePath;

    public EnDeCryBean(String parentPath) {
        encryPath = parentPath + File.separator + "encry";
        encryPath = parentPath + File.separator + "decry";
        sourcePath = parentPath + File.separator + "source";
    }

    public String getEncryPath() {
        return encryPath;
    }

    public void setEncryPath(String encryPath) {
        this.encryPath = encryPath;
    }

    public String getDecryPath() {
        return decryPath;
    }

    public void setDecryPath(String decryPath) {
        this.decryPath = decryPath;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    @Override
    public String toString() {
        return "EnDeCryBean{" +
                "encryPath='" + encryPath + '\'' +
                ", decryPath='" + decryPath + '\'' +
                ", sourcePath='" + sourcePath + '\'' +
                '}';
    }
}
