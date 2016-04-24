package com.xiao.mobiesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiao.telephony.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PrevetionActivity extends AppCompatActivity {

    @Bind(R.id.tv_safephone)
    TextView tvSafephone;
    @Bind(R.id.iv_lock)
    ImageView ivLock;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        boolean gurid = sharedPreferences.getBoolean("gurid", false);
        if (!gurid) {
            startActivity(new Intent(this, Guird1Activity.class));
            finish();
        } else {
            setContentView(R.layout.activity_prevetion);
            ButterKnife.bind(this);
            String safe_phone = sharedPreferences.getString("safe_phone", "");
            boolean protect = sharedPreferences.getBoolean("protect", false);
            tvSafephone.setText(safe_phone);
            if(protect){
                ivLock.setImageResource(R.drawable.lock);
            }else {
                ivLock.setImageResource(R.drawable.unlock);
            }
        }
    }

    public void reEnter(View view) {
        startActivity(new Intent(this, Guird1Activity.class));
        finish();
    }
}
