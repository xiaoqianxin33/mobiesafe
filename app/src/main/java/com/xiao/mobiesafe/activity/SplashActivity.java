package com.xiao.mobiesafe.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xiao.mobiesafe.R;
import com.xiao.mobiesafe.utils.StreamUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.logging.LogRecord;

public class SplashActivity extends AppCompatActivity {

    private static final int CODE_UPDATE_DIALOG = 0;
    private static final int CODE_ENTER_HOME = 1;
    private static final int CODE_JSON_ERROR = 2;
    private static final int CODE_URL_ERROR = 3;
    private static final int CODE_NET_ERROR = 4;
    private TextView tvVersion;
    private OkHttpClient okHttpClient;
    private String mVersionName;
    private int mVersionCode;
    private String mDes;
    private String mDownloadUrl;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_UPDATE_DIALOG:
                    showUpdateDialog();
                    break;
                case CODE_ENTER_HOME:
                    enterHome();
                    break;
                case CODE_JSON_ERROR:
                    Toast.makeText(SplashActivity.this, "数据分析错误", Toast.LENGTH_SHORT);
                    break;
                case CODE_URL_ERROR:
                    Toast.makeText(SplashActivity.this, "url错误", Toast.LENGTH_SHORT);
                    break;
                case CODE_NET_ERROR:
                    Toast.makeText(SplashActivity.this, "网络连接错误", Toast.LENGTH_SHORT);
                    break;
            }
        }
    };

    private void showUpdateDialog() {
    }

    private void enterHome() {

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setText("版本号：" + getVersionName());
        checkVersionCode();
    }

    public String getVersionName() {

        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            return versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getVersionCode() {

        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            return versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public Object checkVersionCode() {

        long l = System.currentTimeMillis();
        okHttpClient = new OkHttpClient();
        final Message msg = new Message();
        try {
            URL url = new URL("http://192.168.56.1:8080/version.json");
            Request request = new Request.Builder().url(url).build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                public void onFailure(Request request, IOException e) {
                }

                public void onResponse(Response response) {
                    InputStream inputStream = null;
                    try {
                        inputStream = response.body().byteStream();
                        String s = StreamUtils.readFromStream(inputStream);
                        JSONObject jo = new JSONObject(s);
                        mVersionName = jo.getString("versionName");
                        mVersionCode = jo.getInt("versionCode");
                        mDes = jo.getString("description");
                        mDownloadUrl = jo.getString("downloadUrl");
                        if (mVersionCode > getVersionCode()) {
                            msg.what = CODE_UPDATE_DIALOG;
                        } else {
                            msg.what = CODE_ENTER_HOME;
                        }
                    } catch (IOException e) {
                        msg.what = CODE_NET_ERROR;
                        e.printStackTrace();
                    } catch (JSONException e) {
                        msg.what = CODE_JSON_ERROR;
                        e.printStackTrace();
                    }

                }
            });
        } catch (MalformedURLException e) {
            msg.what = CODE_URL_ERROR;
            e.printStackTrace();
        } finally {
            long lastTime = System.currentTimeMillis();
            if ((lastTime - l) < 2000) {
                try {
                    Thread.sleep(2000 - lastTime + l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }


        return null;
    }
}
