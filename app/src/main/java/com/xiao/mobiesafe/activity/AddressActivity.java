package com.xiao.mobiesafe.activity;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.xiao.mobiesafe.dao.AddressDao;
import com.xiao.mobiesafe.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddressActivity extends AppCompatActivity {

    @Bind(R.id.et_number)
    EditText etNumber;
    @Bind(R.id.tv_result)
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);

        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    String address = AddressDao.getAddress(s.toString());
                    tvResult.setText(address);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void query(View view) {
        String number = etNumber.getText().toString().trim();

        if (!TextUtils.isEmpty(number)) {
            String address = AddressDao.getAddress(number);
            tvResult.setText(address);
        } else {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);

            etNumber.startAnimation(shake);
            vibrate();
        }
    }

    private void vibrate() {

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{1000, 2000, 1000, 3000}, -1);
    }
}
