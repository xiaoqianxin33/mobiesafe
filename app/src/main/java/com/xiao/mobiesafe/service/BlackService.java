package com.xiao.mobiesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.xiao.mobiesafe.dao.BlackDao;
import com.xiao.mobiesafe.db.BlackDB;
import com.xiao.mobiesafe.domain.BlackTable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class BlackService extends Service {

    private SmsReceiver2 smsReceiver;
    private BlackDB blackDB;
    private BlackDao blackDao;
    private TelephonyManager tm;
    private PhoneStateListener listener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    class SmsReceiver2 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");

            for (Object obj : pdus) {
                SmsMessage message = SmsMessage.createFromPdu((byte[]) obj);
                String originatingAddress = message.getOriginatingAddress();
                int mode = blackDao.getMode(originatingAddress);

                if ((mode & BlackTable.SMS)!= 0) {
                    abortBroadcast();
                }
            }

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        blackDB = new BlackDB(this);

        blackDao = new BlackDao(this);
        smsReceiver = new SmsReceiver2();
        IntentFilter filter = new IntentFilter(
                "android.provider.Telephony.SMS_RECEIVED");

        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(smsReceiver, filter);

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        listener = new PhoneStateListener() {

            @Override
            public void onCallStateChanged(int state, final String incomingNumber) {

                if (state == TelephonyManager.CALL_STATE_RINGING) {

                    int mode = blackDao.getMode(incomingNumber);

                    if ((mode & BlackTable.TEL) != 0) {

                        getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true,
                                new ContentObserver(new Handler()) {
                                    @Override
                                    public void onChange(boolean selfChange) {
                                        deleteCalllog(incomingNumber);
                                        getContentResolver().unregisterContentObserver(this);
                                        super.onChange(selfChange);
                                    }

                                });
                        endCall();
                    }
                }
            }
        };

        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void deleteCalllog(String incomingNumber) {
        Uri uri = Uri.parse("content://call_log/calls");
        getContentResolver().delete(uri, "number=?", new String[]{incomingNumber});

    }

    private void endCall() {

        try {
            Class clazz = Class.forName("android.os.ServiceManager");

            Method method = clazz.getDeclaredMethod("getService", String.class);

            IBinder binder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);

            ITelephony iTelephony = ITelephony.Stub.asInterface(binder);
            iTelephony.endCall();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {

        tm.listen(listener, PhoneStateListener.LISTEN_NONE);

        unregisterReceiver(smsReceiver);
        super.onDestroy();
    }
}
