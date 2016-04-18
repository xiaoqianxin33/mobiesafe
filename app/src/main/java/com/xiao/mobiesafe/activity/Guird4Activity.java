package com.xiao.mobiesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xiao.mobiesafe.R;

public class Guird4Activity extends AppCompatActivity {

    private SharedPreferences sconfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guird4);
        sconfig = getSharedPreferences("config", MODE_PRIVATE);

    }

    public void next(View view) {

        startActivity(new Intent(this, PrevetionActivity.class));
        finish();
        sconfig.edit().putBoolean("gurid", true).commit();

    }

    public void previous(View view) {
        startActivity(new Intent(this, Guird3Activity.class));
        finish();

    }
}
