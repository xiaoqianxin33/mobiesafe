package com.xiao.mobiesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.xiao.mobiesafe.utils.SmsEngine;
import com.xiao.telephony.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AToolsActivity extends AppCompatActivity {

    @Bind(R.id.pb_backupsms)
    ProgressBar pbBackupsms;
    @Bind(R.id.pb_revertsms)
    ProgressBar pbRevertsms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
        ButterKnife.bind(this);
    }


    public void numberAddressQuery(View view) {
        startActivity(new Intent(this, AddressActivity.class));
    }

    public void backupsms(View view) {
        SmsEngine.backupSms(this,pbBackupsms);
    }

    public void revertsms(View view){
        SmsEngine.smsResumnJson(this,pbRevertsms);
    }
}
