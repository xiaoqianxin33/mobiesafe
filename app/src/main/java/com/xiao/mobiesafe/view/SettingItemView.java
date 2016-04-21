package com.xiao.mobiesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiao.mobiesafe.R;

public class SettingItemView extends RelativeLayout {

    private TextView tvTitle;
    private TextView tvDesc;

    public SettingItemView(Context context) {
        super(context);
        initView();
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        View.inflate(getContext(), R.layout.setting_item_view, this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setDesc(String desc) {
        tvDesc.setText(desc);
    }
}
