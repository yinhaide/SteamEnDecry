package com.wenshanhu.endecry.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.de.rocket.Rocket;
import com.de.rocket.bean.ActivityParamBean;
import com.de.rocket.bean.RecordBean;
import com.de.rocket.bean.StatusBarBean;
import com.de.rocket.ue.activity.RoActivity;
import com.de.rocket.utils.SharePreUtil;
import com.wenshanhu.endecry.R;
import com.wenshanhu.endecry.frag.Frag_base;
import com.wenshanhu.endecry.frag.Frag_play;
import com.wenshanhu.endecry.frag.Frag_endecry;
import com.wenshanhu.endecry.helper.SteamHelper;
import com.wenshanhu.endecry.receiver.USBReceiver;

import java.io.File;

public class MainActivity extends RoActivity {

    // 全部显示的Frag都需要在这里注册
    private Class[] frags = {
            Frag_endecry.class,//动画页面
            Frag_play.class,//播放页面
    };

    @Override
    public ActivityParamBean initProperty() {
        ActivityParamBean activityParamBean = new ActivityParamBean();
        activityParamBean.setLayoutId(R.layout.activity_main);//Activity布局
        activityParamBean.setFragmentContainId(R.id.fl_contain);//Fragment容器
        activityParamBean.setSaveInstanceState(false);//页面不要重新创建
        activityParamBean.setToastCustom(true);//是否用自定义的吐司风格
        activityParamBean.setRoFragments(frags);//需要注册Fragment列表
        activityParamBean.setShowViewBall(false);//是否显示悬浮球
        activityParamBean.setRecordBean(new RecordBean(true,true,true,7));//设置日志策略
        activityParamBean.setEnableCrashWindow(true);//是否隐藏框架自定义的崩溃的窗口
        activityParamBean.setStatusBar(new StatusBarBean(true, Color.TRANSPARENT));//状态栏
        return activityParamBean;
    }

    @Override
    public void initViewFinish() {
        //恢复状态栏,因为启动Activity的Theme里面清楚了状态栏,需要恢复
        //<item name="android:windowFullscreen">true</item>
        Rocket.clearWindowFullscreen(this);
        //注册U盘
        /*IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);//插
        //intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);//拔
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);  //完全拔出
        intentFilter.addDataScheme("file");//没有这行监听不起作用
        registerReceiver(new USBReceiver(), intentFilter);*/
        //读取U盘的路径
        USBReceiver.USB_PATH = SharePreUtil.getInstance().getString(this,USBReceiver.USB_PATH_KEY,"/storage/udisk0");
        if(!new File(USBReceiver.USB_PATH + File.separator + USBReceiver.STEAM_PATH).exists()){
            USBReceiver.USB_PATH = SharePreUtil.getInstance().getString(this,USBReceiver.USB_PATH_KEY,"/storage/1ACEB773CEB74633");
        }
        String steamPath = USBReceiver.USB_PATH + File.separator + USBReceiver.STEAM_PATH;
        //初始化路径结构
        //Toast.makeText(this,"正在加载文件资料:"+steamPath,Toast.LENGTH_LONG).show();
        //初始化路径结构
        SteamHelper.get().init((msec) -> {
            if(new File(steamPath).exists()){
                //Toast.makeText(this,"文件夹存在,加载完成,耗时:"+msec+"毫秒",Toast.LENGTH_LONG).show();
            }else{
                //Toast.makeText(this,"文件夹不存在,加载完成,耗时:"+msec+"毫秒",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onNexts(Object o) {
        //String upath0 = FileUtil.getStoragePath(this,FileUtil.USB);
        //String upath1 = FileUtil.getUsbPath();
        //Toast.makeText(this,"getStoragePath:"+upath0+"\n"+"getUsbPath:"+upath1,Toast.LENGTH_LONG).show();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){//只处理按下的动画,抬起的动作忽略
            //按键事件向Fragment分发
            Fragment topRocketStackFragment = Rocket.getTopRocketStackFragment(this);
            //页面在顶层才会分发
            if(topRocketStackFragment instanceof Frag_base){
                ((Frag_base)topRocketStackFragment).dispatchKeyEvent(event);
            }
        }
        return superDispatchKeyEvent(event);
    }
}
