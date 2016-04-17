package com.xiao.mobiesafe.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xiao.mobiesafe.R;
import com.xiao.mobiesafe.utils.StreamUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("最新版本" + mVersionName);
        builder.setMessage(mDes);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                downloda();
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        builder.show();
    }

    private void downloda() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            HttpUtils utils = new HttpUtils();
            String target = Environment.getExternalStorageDirectory()
                    + "/update.apk";
            utils.download(mDownloadUrl, target, new RequestCallBack<File>() {
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(responseInfo.result),
                            "application/vnd.android.package-archive");
                    startActivityForResult(intent,0);
                }

                public void onFailure(HttpException e, String s) {
                    Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    enterHome();
                }
            });

        }else{
            Toast.makeText(this,"没有找到SD卡",Toast.LENGTH_SHORT).show();
            enterHome();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
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
