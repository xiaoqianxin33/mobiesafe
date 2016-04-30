package com.xiao.mobiesafe.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import com.xiao.mobiesafe.activity.WatchDogPasswordActivity;
import com.xiao.mobiesafe.dao.LockDao;
import com.xiao.mobiesafe.domain.LockTable;

import java.util.List;

public class WatchDogService extends Service {

    private ActivityManager am;
    private WatchDogReceive receive;
    private String shuren = "";
    private List<String> allDatas;
    private boolean isWatch;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        LockDao lockDao = new LockDao(this);


        ContentObserver contentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                new Thread() {
                    @Override
                    public void run() {
                        LockDao lockDao = new LockDao(getApplication());
                        allDatas = lockDao.getAllDatas();
                    }
                }.start();
            }
        };
        getContentResolver().registerContentObserver(LockTable.uri, true, contentObserver);

        receive = new WatchDogReceive();
        IntentFilter intentFilter = new IntentFilter("com.xiao.watchdog");
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receive, intentFilter);

        allDatas = lockDao.getAllDatas();
        watchDog();
        super.onCreate();
    }

    private class WatchDogReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.xiao.watchdog")) {
                String packname = intent.getStringExtra("packname");
                shuren = packname;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                shuren = "";
                isWatch = false;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                watchDog();
            }
        }
    }


    private void watchDog() {

        new Thread() {
            @Override
            public void run() {
                isWatch = true;
                while (isWatch) {
                    List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);

                    RunningTaskInfo runningTaskInfo = runningTasks.get(0);

                    String packageName = runningTaskInfo.topActivity.getPackageName();

                    if (allDatas.contains(packageName)) {

                        if (shuren.equals(packageName)) {
                        } else {
                            Intent intent = new Intent(getApplicationContext(), WatchDogPasswordActivity.class);
                            intent.putExtra("packagename", packageName);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                    SystemClock.sleep(50);
                }
            }
        }.start();
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(receive);
        isWatch = false;
        super.onDestroy();
    }
}

