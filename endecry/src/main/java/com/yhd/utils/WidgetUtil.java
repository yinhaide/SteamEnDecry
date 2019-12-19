package com.yhd.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.de.rocket.utils.ViewAnimUtil;
import com.yhd.endecry.R;
import com.yhd.widget.TipsWidget;


/**
 * 吐司的工具类
 * Created by haide.yin(haide.yin@tcl.com) on 2019/6/21 13:24.
 */
public class WidgetUtil {

    private static final int TOAST_WIDGET_ID = R.id.endecry_tip;//用户缓存的ViewID
    private static final int FADE_DURATION = 800;//渐变显示与渐变隐藏的时间间隔

    /**
     * 渐变显示
     *
     * @param tips     提示语
     */
    public static void showTips(Activity activity,String tips) {
        if (activity != null) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            TipsWidget toastView = decorView.findViewById(TOAST_WIDGET_ID);
            if (toastView != null) {
                toastView.getTvContent().setText(tips);
            } else {
                toastView = new TipsWidget(activity);
                toastView.getTvContent().setText(tips);
                ViewGroup contentView = activity.findViewById(android.R.id.content);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                toastView.setLayoutParams(params);
                toastView.setId(TOAST_WIDGET_ID);
                contentView.addView(toastView);
                toastView.setVisibility(View.GONE);
            }
            if (toastView.getVisibility() == View.GONE || toastView.getVisibility() == View.INVISIBLE) {
                //渐变显示
                ViewAnimUtil.showFade(toastView, 0, 1, FADE_DURATION);
            }
        }
    }

    /**
     * 渐变隐藏
     */
    private static void hideTips(Activity activity) {
        if (activity != null) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            View toastView = decorView.findViewById(TOAST_WIDGET_ID);
            if (toastView != null) {
                //渐变显示
                ViewAnimUtil.hideFade(toastView, 1, 0, FADE_DURATION);
            }
        }
    }

    /**
     * 清除
     */
    private static void removeTips(Activity activity) {
        if (activity != null) {
            ViewGroup contentView = activity.findViewById(android.R.id.content);
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            View toastView = decorView.findViewById(TOAST_WIDGET_ID);
            if (toastView != null) {
                contentView.removeView(toastView);
            }
        }
    }
}
