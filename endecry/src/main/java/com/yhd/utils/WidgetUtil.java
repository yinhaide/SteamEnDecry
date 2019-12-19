package com.yhd.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.de.rocket.utils.ViewAnimUtil;
import com.yhd.endecry.R;
import com.yhd.widget.EditWidget;
import com.yhd.widget.TipsWidget;


/**
 * 吐司的工具类
 * Created by haide.yin(haide.yin@tcl.com) on 2019/6/21 13:24.
 */
public class WidgetUtil {

    private static final int TIPS_WIDGET_ID = R.id.endecry_tip;//用户缓存的ViewID
    private static final int EDIT_WIDGET_ID = R.id.endecry_edit;//用户缓存的ViewID
    private static final int FADE_DURATION = 800;//渐变显示与渐变隐藏的时间间隔

    /**
     * 渐变显示框
     * @param tips 提示语
     */
    public static TipsWidget showTips(Activity activity,String tips) {
        if (activity != null) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            TipsWidget tipsWidget = decorView.findViewById(TIPS_WIDGET_ID);
            if (tipsWidget != null) {
                tipsWidget.getTvContent().setText(tips);
            } else {
                tipsWidget = new TipsWidget(activity);
                tipsWidget.getTvContent().setText(tips);
                ViewGroup contentView = activity.findViewById(android.R.id.content);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                tipsWidget.setLayoutParams(params);
                tipsWidget.setId(TIPS_WIDGET_ID);
                contentView.addView(tipsWidget);
                tipsWidget.setVisibility(View.GONE);
            }
            if (tipsWidget.getVisibility() == View.GONE || tipsWidget.getVisibility() == View.INVISIBLE) {
                //渐变显示
                ViewAnimUtil.showFade(tipsWidget, 0, 1, FADE_DURATION);
            }
            return tipsWidget;
        }
        return null;
    }

    /**
     * 渐变显示编辑框
     */
    public static EditWidget showEdit(Activity activity) {
        if (activity != null) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            EditWidget editWidget = decorView.findViewById(EDIT_WIDGET_ID);
            if (editWidget == null) {
                editWidget = new EditWidget(activity);
                ViewGroup contentView = activity.findViewById(android.R.id.content);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                editWidget.setLayoutParams(params);
                editWidget.setId(EDIT_WIDGET_ID);
                contentView.addView(editWidget);
                editWidget.setVisibility(View.GONE);
            }
            if (editWidget.getVisibility() == View.GONE || editWidget.getVisibility() == View.INVISIBLE) {
                //渐变显示
                ViewAnimUtil.showFade(editWidget, 0, 1, FADE_DURATION);
            }
            return editWidget;
        }
        return null;
    }

    /**
     * 渐变隐藏
     */
    private static void hideTips(Activity activity) {
        if (activity != null) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            View toastView = decorView.findViewById(TIPS_WIDGET_ID);
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
            View toastView = decorView.findViewById(TIPS_WIDGET_ID);
            if (toastView != null) {
                contentView.removeView(toastView);
            }
        }
    }
}
