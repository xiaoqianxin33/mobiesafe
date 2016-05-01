package com.xiao.mobiesafe.utils;

import android.net.TrafficStats;

/**
 * 流量统计
 * Created by xiao on 2016/5/1.
 */
public class ConnectivityEngine {

    public static long getReceive( int uid) {

        long res ;
            res = TrafficStats.getUidRxBytes(uid);

        return res;
    }

    public static long getSend( int uid) {

        long res ;
        res = TrafficStats.getUidTxBytes(uid);
        return res;
    }

}
