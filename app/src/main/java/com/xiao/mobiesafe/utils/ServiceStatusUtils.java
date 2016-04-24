package com.xiao.mobiesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class ServiceStatusUtils {

    public static boolean isServiceRunning(Context context,String serviceName){

        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100);

        for(ActivityManager.RunningServiceInfo serviceInfo:runningServices){
            String className = serviceInfo.service.getClassName();
            System.out.println(className);
            if(serviceName.equals(className)){
                return true;
            }
        }
        return false;
    }
}
