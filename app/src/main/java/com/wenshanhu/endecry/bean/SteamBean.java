package com.wenshanhu.endecry.bean;

import java.io.Serializable;

/**
 * 类作用描述
 * Created by haide.yin(haide.yin@tcl.com) on 2019/11/12 14:10.
 */
public class SteamBean implements Serializable {

    private EnDeCryBean enDeCryBean;

    public SteamBean(String parentPath) {
        enDeCryBean = new EnDeCryBean(parentPath);
    }

    public EnDeCryBean getEnDeCryBean() {
        return enDeCryBean;
    }

    public void setEnDeCryBean(EnDeCryBean enDeCryBean) {
        this.enDeCryBean = enDeCryBean;
    }

    @Override
    public String toString() {
        return "SteamBean{" +
                "enDeCryBean=" + enDeCryBean +
                '}';
    }
}
