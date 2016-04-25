package com.xiao.mobiesafe.activity;

import android.content.Intent;
import android.os.Bundle;

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
