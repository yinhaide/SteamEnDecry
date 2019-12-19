package com.yhd.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.de.rocket.utils.ViewAnimUtil;
import com.yhd.endecry.R;


public class EditWidget extends RelativeLayout {

    private EditText etContent;//提示内容
    private TextView tvOk;//确定
    private TextView tvCancel;//取消
    private View tvSplit;//分割线
    private ImageView ivBg;//背景

    public EditWidget(Context context) {
        super(context, null);
        // 在构造函数中将Xml中定义的布局解析出来。
        View inflate = LayoutInflater.from(context).inflate(R.layout.endecry_widget_password, this, true);
        etContent = inflate.findViewById(R.id.et_content);
        tvOk = inflate.findViewById(R.id.tv_click_ok);
        tvCancel = inflate.findViewById(R.id.tv_click_cancel);
        tvSplit = inflate.findViewById(R.id.v_bottom_split);
        ivBg = inflate.findViewById(R.id.iv_bg);
        ivBg.setOnClickListener(null);
        tvCancel.setOnClickListener( view -> hide());
        tvOk.setOnClickListener( view -> hide());
    }

    /**
     * 显示
     */
    public void show() {
        ViewAnimUtil.showFade(this,0f,1f,400);
    }

    /**
     * 隐藏
     */
    public void hide() {
        ViewAnimUtil.hideFade(this,1f,0f,400);
    }

    /**
     * 单选：只有tvOk可以用
     */
    public void setSingleChoice(){
        tvSplit.setVisibility(GONE);
        tvCancel.setVisibility(GONE);
        tvOk.setVisibility(VISIBLE);
    }

    /**
     * 双选
     */
    public void setDoubleChoice(){
        tvSplit.setVisibility(VISIBLE);
        tvCancel.setVisibility(VISIBLE);
        tvOk.setVisibility(VISIBLE);
    }

    /**
     * 内容
     *
     * @return the text view
     */
    public EditText getEtContent() {
        return this.etContent;
    }

    /**
     * 确定
     *
     * @return the text view
     */
    public TextView getTvOk() {
        return this.tvOk;
    }

    /**
     * 取消
     *
     * @return the text view
     */
    public TextView getTvCancel() {
        return this.tvCancel;
    }

    /**
     * 设置内容
     * @param resourceId 内容id
     */
    public EditWidget setEtContent(int resourceId){
        etContent.setText(resourceId);
        return this;
    }

    /**
     * 设置内容
     * @param content 内容
     */
    public EditWidget setEtContent(String content){
        etContent.setText(content);
        return this;
    }

    /**
     * 设置ok
     * @param resourceId 内容id
     * @return
     */
    public EditWidget setTvOk(int resourceId){
        tvOk.setText(resourceId);
        return this;
    }

    /**
     * 设置cancel
     * @param resourceId 内容id
     * @return
     */
    public EditWidget setTvCancel(int resourceId) {
        tvCancel.setText(resourceId);
        return this;
    }
}
