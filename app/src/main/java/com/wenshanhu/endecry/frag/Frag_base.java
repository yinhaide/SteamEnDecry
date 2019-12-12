package com.wenshanhu.endecry.frag;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.de.rocket.ue.frag.RoFragment;

import java.io.IOException;

/**
 * Fragment的基类
 * Created by haide.yin() on 2019/9/30 11:01.
 */
public abstract class Frag_base extends RoFragment {

    //提示音
    private SoundPool mSoundPool = null;
    //音效ID
    private int soundid;

    @Override
    public void onViewCreated(View var1, Bundle var2) {
        super.onViewCreated(var1,var2);
        initSP();
    }

    /**
     * 初始化音效
     */
    private void initSP(){
        try {
            //设置最多可容纳音频流数量，音频的品质为5
            mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
            soundid = mSoundPool.load(activity.getAssets().openFd("tip.mp3") , 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 将按键事件分发给Fragment
     */
    public void dispatchKeyEvent(KeyEvent event) {

        //播放音效
        mSoundPool.play(soundid, 1, 1, 0, 0, 1);
    }

    @Override
    public void onDestroyView(){//释放资源
        super.onDestroyView();
        mSoundPool.release();
    }
}
