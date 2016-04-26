package com.xiao.mobiesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.List;

public class ClearTaskService extends Service {


    private ActivityManager am;
    private ClearTaskReceiver clearTaskService;

    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }


    private class ClearTaskReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                am.killBackgroundProcesses(runningAppProcessInfo.processName);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        clearTaskService = new ClearTaskReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(clearTaskService, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(clearTaskService);
    }
}
