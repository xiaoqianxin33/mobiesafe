package com.xiao.mobiesafe.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.xiao.mobiesafe.R;
import com.xiao.mobiesafe.dao.KillVirusDao;
import com.xiao.mobiesafe.domain.AppBean;
import com.xiao.mobiesafe.utils.AppManagerEngine;
import com.xiao.mobiesafe.utils.MD5Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class KillVirusActivity extends AppCompatActivity {

    private static final int MESSAGE = 1;
    private static final int SCANNING = 2;
    private static final int FINISH = 3;
    @Bind(R.id.fl_killvirus)
    FrameLayout fl_virus;
    @Bind(R.id.tv_killvirus_title)
    TextView tv_Title;
    @Bind(R.id.pb_killvirus)
    ProgressBar pb_virus;
    @Bind(R.id.ll_killrus)
    LinearLayout ll_rus;
    @Bind(R.id.iv_killvirus_icon)
    ImageView iv_Icon;
    private List<AppBean> allApks = new ArrayList<>();
    private int progress;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MESSAGE:
                    TextView textView = new TextView(getApplicationContext());
                    pb_virus.setMax(allApks.size());
                    pb_virus.setProgress(progress);
                    AntiVirusBean bean = (AntiVirusBean) msg.obj;
                    if (bean.siVirus) {
                        textView.setTextColor(Color.RED);
                    } else {
                        textView.setTextColor(Color.BLACK);
                    }

                    textView.setText(bean.packName);

                    tv_Title.setText("正在扫描:" + bean.packName);

                    ll_rus.addView(textView, 0);
                    break;

                case SCANNING:
                    iv_Icon.startAnimation(ra);
                    break;
                case FINISH:
                    iv_Icon.clearAnimation();
                    break;
            }


            super.handleMessage(msg);
        }
    };
    private KillVirusDao dao;
    private RotateAnimation ra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kill_virus);
        ButterKnife.bind(this);
        dao = new KillVirusDao();
        initAnimation();
        checkVersion();
    }

    private void checkVersion() {

        final AlertDialog.Builder ab = new AlertDialog.Builder(this);
        final AlertDialog dialog = ab.setTitle("注意")
                .setMessage("正在联网")
                .create();
        dialog.show();

        HttpUtils utils = new HttpUtils();
        utils.configTimeout(5000);

        utils.send(HttpMethod.GET, "", new RequestCallBack<Object>() {
            @Override
            public void onSuccess(ResponseInfo<Object> responseInfo) {
                dialog.dismiss();
                String version = (String) responseInfo.result;

                if (dao.isNewVirus(Integer.parseInt(version))) {
                    Toast.makeText(getApplicationContext(), "病毒库最新", 1).show();
                    scanApp();
                } else {
                    isUpdateVirusDialog();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "联网失败", 1).show();
                scanApp();
            }
        });
    }

    private void isUpdateVirusDialog() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("有新病毒")
                .setMessage("是否更新病毒库？")
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HttpUtils utils = new HttpUtils();

                        utils.send(HttpMethod.GET, "", new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                String virusJson = responseInfo.result;

                                JSONObject jsonObj = null;
                                try {
                                    jsonObj = new JSONObject(virusJson);
                                    String md5 = jsonObj.getString("md5");
                                    String desc = jsonObj.getString("desc");

                                    dao.addVirus(md5, desc);

                                    Toast.makeText(getApplicationContext(), "更新病毒库成功", 1).show();
                                    scanApp();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                                scanApp();
                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scanApp();
                    }
                });

        ab.show();
    }

    private class AntiVirusBean {
        String packName;
        boolean siVirus;
    }

    private void scanApp() {

        new Thread() {

            @Override
            public void run() {
                allApks = AppManagerEngine.getAllApks(getApplication());
                AntiVirusBean bean = new AntiVirusBean();

                handler.obtainMessage(SCANNING).sendToTarget();

                for (AppBean appBean : allApks) {
                    bean.packName = appBean.getPackName();
                    String md5 = MD5Utils.encode(bean.packName);

                    if (dao.isVirus(md5)) {
                        bean.siVirus = true;
                    } else {
                        bean.siVirus = false;
                    }
                    progress++;
                    Message msg = handler.obtainMessage(MESSAGE);
                    msg.obj = bean;
                    handler.sendMessage(msg);
                    SystemClock.sleep(500);
                }
                handler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();
    }

    private void initAnimation() {

        ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(600);
        ra.setRepeatCount(Animation.INFINITE);
        ra.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float x) {
                return x;
            }
        });
    }
}
