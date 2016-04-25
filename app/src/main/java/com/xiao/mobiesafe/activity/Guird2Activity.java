package com.xiao.mobiesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.xiao.mobiesafe.view.SettingView;
import com.xiao.mobiesafe.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Guird2Activity extends BaseGuidActivity {

    @Bind(R.id.siv_sim)
    SettingView sivSim;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guird2);
        ButterKnife.bind(this);
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        String sim = sharedPreferences.getString("sim", null);
        if (!TextUtils.isEmpty(sim)) {
            sivSim.setChecked(true);
        } else {
            sivSim.setChecked(false);
        }

        sivSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivSim.isChecked()) {
                    sivSim.setChecked(false);
                    sharedPreferences.edit().remove("sim").apply();
                } else {
                    sivSim.setChecked(true);
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber = tm.getSimSerialNumber();
                    sharedPreferences.edit().putString("sim", simSerialNumber).apply();
                }
            }
        });

    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(this, Guird1Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
    }

    @Override
    public void showNextPage() {
        startActivity(new Intent(this, Guird3Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }


}
