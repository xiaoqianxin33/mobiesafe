package com.xiao.mobiesafe.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.xiao.mobiesafe.domain.TaskBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TaskManageEnige {

    public static List<TaskBean> getAllRunningTask(Context context) {

        List<TaskBean> datas = new ArrayList<>();
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();

        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            TaskBean taskBean = new TaskBean();
            String processName = runningAppProcessInfo.processName;
            taskBean.setPackName(processName);
            PackageInfo packageInfo;
            try {
                packageInfo = pm.getPackageInfo(processName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }

            taskBean.setIcon(packageInfo.applicationInfo.loadIcon(pm));

            taskBean.setName(packageInfo.applicationInfo.loadLabel(pm) + "");

            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //系统apk
                taskBean.setSystem(true);
            } else {
                taskBean.setSystem(false);//用户apk
            }

            android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});

            long totalPrivateDirty = processMemoryInfo[0].getTotalPrivateDirty() * 1024;

            taskBean.setMemSize(totalPrivateDirty);

            datas.add(taskBean);

        }


        return datas;
    }


    public static long getAvailMemSize(Context context) {
        long size ;
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        // MemoryInfo 存放内存的信息
        am.getMemoryInfo(outInfo);

        size = outInfo.availMem;

        return size;
    }

    public static long getTotalMemSize(Context context) {
        long size = 0;
        File file = new File("/proc/meminfo");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file)));

            String totalMemInfo = reader.readLine();

            int startIndex = totalMemInfo.indexOf(':');
            int endIndex = totalMemInfo.indexOf('k');
            // 单位是kb
            totalMemInfo = totalMemInfo.substring(startIndex + 1, endIndex)
                    .trim();
            size = Long.parseLong(totalMemInfo);
            size *= 1024;// byte单位
        } catch (Exception e) {
            e.printStackTrace();
        }

        return size;
    }


}
