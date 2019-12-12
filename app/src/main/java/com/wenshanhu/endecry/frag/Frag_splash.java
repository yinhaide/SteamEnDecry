package com.wenshanhu.endecry.frag;

import android.view.View;
import android.widget.TextView;

import com.de.rocket.ue.injector.BindView;
import com.de.rocket.ue.injector.Event;
import com.wenshanhu.endecry.R;
import com.wenshanhu.endecry.helper.EnDeCryHelper;

/**
 * 类作用描述
 * Created by haide.yin(haide.yin@tcl.com) on 2019/10/30 15:42.
 */
public class Frag_splash extends Frag_base {

    @BindView(R.id.tv_title)
    private TextView tvTitle;

    @Override
    public int onInflateLayout() {
        return R.layout.frag_splash;
    }

    @Override
    public void initViewFinish(View view) {
        tvTitle.setText("请在U盘根目录存放以下路径\n"
                +"/endecry/source-要加密还是解密的资源文件\n"
                +"/endecry/encry-加密之后输出的文件夹\n"
                +"/endecry/decry-解密之后输出的文件夹\n");
    }

    @Override
    public void onNexts(Object o) {

    }

    @Event(R.id.bt_encry)
    private void encry(View view){
        EnDeCryHelper.get().beginEncry(new EnDeCryHelper.CryCallback() {
            @Override
            public void onProgress(String msg) {
                toast(msg);
            }

            @Override
            public void onError(String msg) {
                toast(msg);
            }

            @Override
            public void onFinish() {
                toast("加密完成");
            }
        });
    }

    @Event(R.id.bt_decry)
    private void decry(View view){
        EnDeCryHelper.get().beginDecry(new EnDeCryHelper.CryCallback() {
            @Override
            public void onProgress(String msg) {
                toast(msg);
            }

            @Override
            public void onError(String msg) {
                toast(msg);
            }

            @Override
            public void onFinish() {
                toast("解密完成");
            }
        });
    }
}
