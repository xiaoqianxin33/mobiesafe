package com.xiao.mobiesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.xiao.mobiesafe.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Guird4Activity extends BaseGuidActivity {

    @Bind(R.id.cb_open)
    CheckBox cbOpen;
    private SharedPreferences sconfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guird4);
        ButterKnife.bind(this);

        sconfig = getSharedPreferences("config", MODE_PRIVATE);
        boolean protect = sconfig.getBoolean("protect", false);

        // 根据sp保存的状态,更新checkbox
        if (protect) {
            cbOpen.setText("防盗保护已经开启");
            cbOpen.setChecked(true);
        } else {
            cbOpen.setText("防盗保护没有开启");
            cbOpen.setChecked(false);
        }
        cbOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbOpen.setText("防盗保护已经开启");
                    sconfig.edit().putBoolean("protect", true).commit();
                } else {
                    cbOpen.setText("防盗保护没有开启");
                    sconfig.edit().putBoolean("protect", false).commit();
                }
            }
        });


    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(this, Guird3Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
    }

    @Override
    public void showNextPage() {
        startActivity(new Intent(this, PrevetionActivity.class));
        finish();
        sconfig.edit().putBoolean("gurid", true).commit();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }
}
