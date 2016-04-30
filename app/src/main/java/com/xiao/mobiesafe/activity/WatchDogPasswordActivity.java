package com.xiao.mobiesafe.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.xiao.mobiesafe.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WatchDogPasswordActivity extends AppCompatActivity {

    @Bind(R.id.iv_watchdog_icon)
    ImageView iv_icon;
    @Bind(R.id.et_watchdog_password)
    EditText et_password;
    @Bind(R.id.bt_watchdog_ok)
    Button bt_ok;
    private String packageName;
    private HomeReceiver homeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_dog_password);
        ButterKnife.bind(this);
        initView();

        initEvent();

        initData();
    }


    private class HomeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                goToHome();
            }
        }
    }

    private void initData() {

        homeReceiver = new HomeReceiver();

        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(homeReceiver, filter);
    }

    private void initView() {
        Intent intent = getIntent();

        packageName = intent.getStringExtra("packagename");

        PackageManager pm = getPackageManager();

        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            Drawable drawable = applicationInfo.loadIcon(pm);
            iv_icon.setImageDrawable(drawable);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initEvent() {
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = et_password.getText().toString().trim();

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "密码不能为空", 1).show();
                    return;
                }

                if ("123".equals(password)) {
                    Intent intent = new Intent("com.xiao.watchdog");
                    intent.putExtra("packname", packageName);
                    sendBroadcast(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "密码不正确", 1).show();
                    return;
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goToHome();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goToHome() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {

        unregisterReceiver(homeReceiver);
        super.onDestroy();
    }
}
