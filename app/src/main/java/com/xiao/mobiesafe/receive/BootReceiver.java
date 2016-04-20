package com.xiao.mobiesafe.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class BootReceiver extends BroadcastReceiver {

    private SharedPreferences config;

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean protect = config.getBoolean("protect", false);
        if (protect) {
            config = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            String sim = config.getString("sim", null);
            if (!TextUtils.isEmpty(sim)) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String currentNum = tm.getSimSerialNumber();
                if (!sim.equals(currentNum)) {
                    String phone = config.getString("safe_phone", "");
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone, null, "sim card changed!", null, null);
                }
            }
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }
}