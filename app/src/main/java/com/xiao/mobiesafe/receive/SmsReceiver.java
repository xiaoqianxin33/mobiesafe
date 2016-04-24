package com.xiao.mobiesafe.receive;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.xiao.mobiesafe.service.LocationService;
import com.xiao.telephony.R;

public class SmsReceiver extends BroadcastReceiver {

    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;

    @Override
    public void onReceive(Context context, Intent intent) {

        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        for (Object obj : pdus) {
            SmsMessage message = SmsMessage.createFromPdu((byte[]) obj);
            String messageBody = message.getMessageBody();
            String originatingAddress = message.getDisplayOriginatingAddress();

            if ("#*alarm*#".equals(messageBody)) {
                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                player.setVolume(1f, 1f);
                player.setLooping(true);
                player.start();
                abortBroadcast();

            } else if ("#*location*#".equals(messageBody)) {
                context.startService(new Intent(context, LocationService.class));
                SharedPreferences sp = context.getSharedPreferences("config",
                        Context.MODE_PRIVATE);
                String location = sp.getString("location",
                        "getting location...");
                String phone = sp.getString("safe_phone", "");
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, location, null, null);
                abortBroadcast();

            } else if ("#*wipedata*#".equals(messageBody)) {

                devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                componentName = new ComponentName(context, AdminReceiver.class);
                clearData(context);
                abortBroadcast();
            } else if ("#*lockscreen*#".equals(messageBody)) {

                devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                componentName = new ComponentName(context, AdminReceiver.class);
                lockScreen(context);
                abortBroadcast();

            }

        }
    }

    public void lockScreen(Context context) {

        if (!devicePolicyManager.isAdminActive(componentName)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "哈哈哈, 我们有了超级设备管理器, 好NB!");
            context.startActivity(intent);
        }

        devicePolicyManager.lockNow();
        devicePolicyManager.resetPassword("123456", 0);
    }


    public void clearData(Context context) {

        if (!devicePolicyManager.isAdminActive(componentName)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "哈哈哈, 我们有了超级设备管理器, 好NB!");
            context.startActivity(intent);
        }

        devicePolicyManager.wipeData(0);// 清除数据,恢复出厂设置
    }
}
