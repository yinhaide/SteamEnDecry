package com.wenshanhu.endecry.frag;

import android.view.View;
import android.widget.TextView;

import com.de.rocket.ue.injector.BindView;
import com.de.rocket.ue.injector.Event;
import com.wenshanhu.endecry.R;
import com.wenshanhu.endecry.helper.SteamHelper;
import com.yhd.endecry.EnDecryHelper;

/**
 * 类作用描述
 * Created by haide.yin(haide.yin@tcl.com) on 2019/10/30 15:42.
 */
public class Frag_endecry extends Frag_base {

    @BindView(R.id.tv_title)
    private TextView tvTitle;

    @Override
    public int onInflateLayout() {
        return R.layout.frag_endecry;
    }

    @Override
    public void initViewFinish(View view) {
        tvTitle.setText("请在U盘根目录存放以下路径\n"
                +"/endecry/source-要加密还是解密的资源文件，以.mp4、.mp3、.png、.jpg、.txt结尾或者.vhd、.mhd、.jhd、.phd、.thd结尾\n"
                +"/endecry/encry-加密之后输出的文件夹，以.vhd、.mhd、.jhd、.phd、.thd结尾输出，source文件夹放.mp4、.mp3、.png、.jpg、.txt结尾的文件\n"
                +"/endecry/decry-解密之后输出的文件夹，以.mp4、.mp3、.png、.jpg、.txt结尾输出，source文件夹放.vhd、.mhd、.jhd、.phd、.thd结尾的文件\n");
    }

    @Override
    public void onNexts(Object o) {

    }

    @Event(R.id.bt_encry)
    private void encry(View view){
        SteamHelper.get().beginEncry(new SteamHelper.CryCallback() {
            @Override
            public void onProgress(String msg) {
                toast(msg);
            }

            @Override
            public void onError(String msg) {
                toast(msg);
            }

            @Override
            public void onFinish(String msg) {
                toast(msg);
            }
        });
    }

    @Event(R.id.bt_decry)
    private void decry(View view){
        SteamHelper.get().beginDecry(new SteamHelper.CryCallback() {
            @Override
            public void onProgress(String msg) {
                toast(msg);
            }

            @Override
            public void onError(String msg) {
                toast(msg);
            }

            @Override
            public void onFinish(String msg) {
                toast(msg);
            }
        });
    }

    @Event(R.id.bt_play)
    private void play(View view){
        toFrag(Frag_play.class);
    }
}
