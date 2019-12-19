package com.wenshanhu.endecry.frag;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.de.rocket.ue.injector.BindView;
import com.de.rocket.ue.injector.Event;
import com.wenshanhu.endecry.R;
import com.yhd.endecry.EnDecryHelper;
import com.yhd.utils.EnDecryUtil;

/**
 * 类作用描述
 * Created by haide.yin(haide.yin@tcl.com) on 2019/10/30 15:42.
 */
public class Frag_play extends Frag_base {

    @BindView(R.id.iv_img)
    private ImageView ivImg;

    @Override
    public int onInflateLayout() {
        return R.layout.frag_play;
    }

    @Override
    public void initViewFinish(View view) {

    }

    @Override
    public void onNexts(Object o) {

    }

    @Event(R.id.bt_vhd)
    private void vhd(View view){
        EnDecryHelper.get().getDeVideo("/mnt/sdcard/test.vhd", (playType, url, buffer) -> {
            toast(playType.toString());
            if(playType == EnDecryHelper.PlayType.BUFFER_VIDEO){
                Log.v("yhd-","BUFFER_VIDEO："+buffer.length);
            }else if(playType == EnDecryHelper.PlayType.URL_VIDEO){
                Log.v("yhd-","URL_VIDEO："+url);
            }else if(playType == EnDecryHelper.PlayType.NONE){
                Log.v("yhd-","NONE:"+url);
            }
        });
    }

    @Event(R.id.bt_mhd)
    private void mhd(View view){
        EnDecryHelper.get().getDeMusic("/mnt/sdcard/test.mhd", (playType, url, buffer) -> {
            toast(playType.toString());
            if(playType == EnDecryHelper.PlayType.BUFFER_MUSIC){
                Log.v("yhd-","BUFFER_MUSIC："+buffer.length);
            }else if(playType == EnDecryHelper.PlayType.URL_MUSIC){
                Log.v("yhd-","URL_MUSIC："+url);
            }else if(playType == EnDecryHelper.PlayType.NONE){
                Log.v("yhd-","NONE:"+url);
            }
        });
    }

    @Event(R.id.bt_thd)
    private void thd(View view){
        EnDecryHelper.get().getDeTxt("/mnt/sdcard/test.thd", (playType, url, buffer) -> {
            toast(playType.toString());
            if(playType == EnDecryHelper.PlayType.BUFFER_TXT){
                Log.v("yhd-","BUFFER_TXT："+buffer.length + " content:" + EnDecryUtil.getUTF8String(buffer));
            }else if(playType == EnDecryHelper.PlayType.URL_TXT){
                Log.v("yhd-","URL_TXT："+url);
            }else if(playType == EnDecryHelper.PlayType.NONE){
                Log.v("yhd-","NONE:"+url);
            }
        });
    }

    @Event(R.id.bt_phd)
    private void phd(View view){
        EnDecryHelper.get().getDePng("/mnt/sdcard/test.phd", (playType, url, buffer) -> {
            toast(playType.toString());
            if(playType == EnDecryHelper.PlayType.BUFFER_PNG){
                Log.v("yhd-","URL_PNG："+buffer.length);
                ivImg.setImageBitmap(EnDecryUtil.getBitmap(buffer));
            }else if(playType == EnDecryHelper.PlayType.URL_PNG){
                Log.v("yhd-","URL_PNG："+url);
            }else if(playType == EnDecryHelper.PlayType.NONE){
                Log.v("yhd-","NONE:"+url);
            }
        });
    }

    @Event(R.id.bt_jhd)
    private void jhd(View view){
        EnDecryHelper.get().getDeJpg("/mnt/sdcard/test.jhd", (playType, url, buffer) -> {
            toast(playType.toString());
            if(playType == EnDecryHelper.PlayType.BUFFER_JPG){
                Log.v("yhd-","BUFFER_JPG："+buffer.length);
            }else if(playType == EnDecryHelper.PlayType.URL_JPG){
                Log.v("yhd-","URL_JPG："+url);
            }else if(playType == EnDecryHelper.PlayType.NONE){
                Log.v("yhd-","NONE:"+url);
            }
        });
    }
}
