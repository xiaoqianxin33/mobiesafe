package com.xiao.mobiesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xiao.mobiesafe.R;

public class PrevetionActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        boolean gurid = sharedPreferences.getBoolean("gurid", false);
        if(gurid){
            startActivity(new Intent(this,Guird1Activity.class));
            finish();
        }else{
            setContentView(R.layout.activity_prevetion);

        }
    }
}
