package com.xiao.mobiesafe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.xiao.mobiesafe.R;

public class Guird1Activity extends BaseGuidActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guird1);
    }

    public void showPreviousPage() {
    }

    public void showNextPage() {
        startActivity(new Intent(this, Guird2Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

}
