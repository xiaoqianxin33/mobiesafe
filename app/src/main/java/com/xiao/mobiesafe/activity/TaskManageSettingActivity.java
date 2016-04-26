package com.xiao.mobiesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.xiao.mobiesafe.R;
import com.xiao.mobiesafe.service.ClearTaskService;
import com.xiao.mobiesafe.utils.ServiceStatusUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TaskManageSettingActivity extends AppCompatActivity {

    @Bind(R.id.cb_taskmanager_settingcenter_lockscree_clear)
    CheckBox cbTaskmanagerSettingcenterLockscreeClear;
    @Bind(R.id.cb_taskmanager_settingcenter_lockscree_showsystemapp)
    CheckBox cbTaskmanagerSettingcenterLockscreeShowsystemapp;
    private SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        config = getSharedPreferences("config", MODE_PRIVATE);

        initView();

        initEvent();

        initData();
    }

    private void initData() {

        if (ServiceStatusUtils.isServiceRunning(this, "com.xiao.mobiesafe.service.ClearTaskService")) {
            cbTaskmanagerSettingcenterLockscreeClear.setChecked(true);
        } else {
            cbTaskmanagerSettingcenterLockscreeClear.setChecked(false);
        }

        boolean showSysTask = config.getBoolean("showSysTask", true);

        cbTaskmanagerSettingcenterLockscreeShowsystemapp.setChecked(showSysTask);

    }

    private void initEvent() {

        cbTaskmanagerSettingcenterLockscreeClear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent service = new Intent(TaskManageSettingActivity.this, ClearTaskService.class);
                    startService(service);
                } else {
                    Intent service = new Intent(TaskManageSettingActivity.this, ClearTaskService.class);
                    stopService(service);
                }
            }
        });


        cbTaskmanagerSettingcenterLockscreeShowsystemapp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                config.edit().putBoolean("showSysTask", isChecked).apply();
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_task_manage_setting);
        ButterKnife.bind(this);
    }
}
