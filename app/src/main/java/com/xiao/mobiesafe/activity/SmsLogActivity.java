package com.xiao.mobiesafe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xiao.mobiesafe.utils.ReadContactEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class SmsLogActivity extends BaseContactCallSmsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public ArrayList<HashMap<String, String>> readContact() {
        return ReadContactEngine.readSmsLog(this);
    }
}
