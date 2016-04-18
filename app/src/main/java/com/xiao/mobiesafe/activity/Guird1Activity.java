package com.xiao.mobiesafe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.xiao.mobiesafe.R;

public class Guird1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guird1);

    }

    public void next(View view) {
        startActivity(new Intent(this, Guird2Activity.class));
        finish();
    }
}
