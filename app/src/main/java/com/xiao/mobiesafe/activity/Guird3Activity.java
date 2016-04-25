package com.xiao.mobiesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.xiao.mobiesafe.R;

public class Guird3Activity extends BaseGuidActivity {

    private EditText et_phone;
    private SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guird3);

        et_phone = (EditText) findViewById(R.id.et_phone);
        config = getSharedPreferences("config", MODE_PRIVATE);
        String phone = config.getString("safe_phone", "");
        et_phone.setText(phone);

    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(this, Guird2Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
    }

    @Override
    public void showNextPage() {
        String phone = et_phone.getText().toString();
        config.edit().putString("safe_phone",phone).apply();
        startActivity(new Intent(this, Guird4Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    public void contacts(View view){
        startActivityForResult(new Intent(this, ContactActivity.class), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== RESULT_OK){
            String phone = data.getStringExtra("phone");
            phone = phone.replaceAll("-", "").replaceAll(" ", "");
            et_phone.setText(phone);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
