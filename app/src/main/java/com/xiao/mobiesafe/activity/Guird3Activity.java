package com.xiao.mobiesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xiao.mobiesafe.R;

public class Guird3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guird3);

    }

    public void next(View view){
        startActivity(new Intent(this,Guird4Activity.class));
        finish();

    }

    public void previous(View view){
        startActivity(new Intent(this,Guird2Activity.class));
        finish();
    }
}
