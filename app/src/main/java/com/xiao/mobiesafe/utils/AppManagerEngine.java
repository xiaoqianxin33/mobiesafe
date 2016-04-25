package com.xiao.mobiesafe.utils;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.xiao.mobiesafe.domain.AppBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppManagerEngine {


    public static List<AppBean> getAllApks(Context context) {

        List<AppBean> list = new ArrayList<>();

        PackageManager pm = context.getPackageManager();

        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);

        for (PackageInfo packageInfo : installedPackages) {
            AppBean appBean = new AppBean();

            appBean.setAppName(packageInfo.applicationInfo.loadLabel(pm) + "");

            appBean.setIcon(packageInfo.applicationInfo.loadIcon(pm));

            appBean.setPackName(packageInfo.packageName);

            String sourceDir = packageInfo.applicationInfo.sourceDir;
            File file = new File(sourceDir);

            appBean.setSize(file.length());

            int flag = packageInfo.applicationInfo.flags;

            if ((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                appBean.setSd(true);
            } else {
                appBean.setSd(false);
            }

            if ((flag & ApplicationInfo.FLAG_SYSTEM) != 0) {
                appBean.setSystem(true);
            } else {
                appBean.setSystem(false);
            }

            list.add(appBean);
        }
        return list;
    }


    public static long getRomAvail() {
        File dataDirectory = Environment.getDataDirectory();

        long freeSpace = dataDirectory.getFreeSpace();

        return freeSpace;
    }

    public static long getSdAvail(){
        File externalStorageDirectory = Environment.getExternalStorageDirectory();

        long freeSpace = externalStorageDirectory.getFreeSpace();

        return freeSpace;
    }
}
