package com.xiao.mobiesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xiao.mobiesafe.R;

public class Guird2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guird2);

    }

    public void next(View view) {
        startActivity(new Intent(this, Guird3Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);

    }

    public void previous(View view) {
        startActivity(new Intent(this, Guird1Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
    }
}
