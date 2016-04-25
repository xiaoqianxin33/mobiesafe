package com.xiao.mobiesafe.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiao.mobiesafe.domain.AppBean;
import com.xiao.mobiesafe.utils.AppManagerEngine;
import com.xiao.telephony.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AppManageActivity extends AppCompatActivity {

    private static final int LOADING = 0;
    private static final int FINISH = 1;
    @Bind(R.id.tv_appmanager_romsize)
    TextView tvAppmanagerRomsize;
    @Bind(R.id.tv_appmanager_sdsize)
    TextView tvAppmanagerSdsize;
    @Bind(R.id.lv_appmanager_appdatas)
    ListView lvAppmanagerAppdatas;
    @Bind(R.id.tv_appmanager_listview_lable)
    TextView tvAppmanagerListviewLable;
    @Bind(R.id.pb_appmanager_loading)
    ProgressBar pbAppmanagerLoading;
    private PackageManager pm;
    private List<AppBean> systemList = new ArrayList<>();
    private List<AppBean> userList = new ArrayList<>();


    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    lvAppmanagerAppdatas.setVisibility(View.GONE);
                    pbAppmanagerLoading.setVisibility(View.VISIBLE);
                    tvAppmanagerListviewLable.setVisibility(View.GONE);
                    break;

                case FINISH:
                    lvAppmanagerAppdatas.setVisibility(View.VISIBLE);
                    pbAppmanagerLoading.setVisibility(View.GONE);
                    tvAppmanagerListviewLable.setVisibility(View.VISIBLE);

                    tvAppmanagerSdsize.setText("SD卡可用空间:"
                            + Formatter.formatFileSize(getApplicationContext(),
                            sdAvail));

                    tvAppmanagerRomsize.setText("ROM可用空间:"
                            + Formatter.formatFileSize(getApplicationContext(),
                            romAvail));
                    tvAppmanagerListviewLable.setText("用户软件(" + userList.size() + ")");

                    adapter.notifyDataSetChanged();
                    break;

            }
        }
    };
    private long sdAvail;
    private long romAvail;
    private MyAdapter adapter;
    private AppBean clickBean;
    private View popupView;
    private PopupWindow pw;
    private ScaleAnimation sa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();

        initEvent();

        initPopupWindow();
    }

    private void initPopupWindow() {
        popupView = View.inflate(this, R.layout.popup_appmanager, null);

        LinearLayout ll_remove = (LinearLayout) popupView
                .findViewById(R.id.ll_appmanager_pop_remove);
        LinearLayout ll_setting = (LinearLayout) popupView
                .findViewById(R.id.ll_appmanager_pop_setting);
        LinearLayout ll_share = (LinearLayout) popupView
                .findViewById(R.id.ll_appmanager_pop_share);
        LinearLayout ll_start = (LinearLayout) popupView
                .findViewById(R.id.ll_appmanager_pop_start);

        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ll_appmanager_pop_remove:// 卸载软件
                        removeApk();// 卸载apk
                        break;
                    case R.id.ll_appmanager_pop_setting:// 设置中心
                        settingCenter();// 设置中心
                        break;
                    case R.id.ll_appmanager_pop_share:// 软件分享
                        shareApk();// 软件分享
                        break;
                    case R.id.ll_appmanager_pop_start:// 启动软件
                        startApk();// 启动软件
                        break;

                    default:
                        break;
                }
                closePopupWindow();// 关闭弹出窗体
            }
        };

        ll_remove.setOnClickListener(listener);
        ll_setting.setOnClickListener(listener);
        ll_share.setOnClickListener(listener);
        ll_start.setOnClickListener(listener);

        pw = new PopupWindow(popupView, -2, -2);

        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        sa = new ScaleAnimation(0, 1, 0.5f, 1, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(300);
    }


    private void startApk() {
        String packName = clickBean.getPackName();
        Intent launchIntentForPackage = pm.getLaunchIntentForPackage(packName);
        startActivity(launchIntentForPackage);
    }

    private void shareApk() {

    }

    private void settingCenter() {
        Intent intent = new Intent(
                "android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + clickBean.getPackName()));
        startActivity(intent);
    }

    private void closePopupWindow() {

        if (pw != null && pw.isShowing()) {
            pw.dismiss();
        }

    }

    private void removeApk() {

    }

    private void initEvent() {

        lvAppmanagerAppdatas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == userList.size() + 1) {
                    return;
                }

                clickBean = (AppBean) lvAppmanagerAppdatas.getItemAtPosition(position);
                int[] location = new int[2];
                view.getLocationInWindow(location);
                showPopupWindow(view, location[0] + 50, location[1]);
            }
        });

        lvAppmanagerAppdatas.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem < userList.size() + 1) {
                    tvAppmanagerListviewLable.setText("用户软件(" + userList.size() + ")");
                } else {
                    tvAppmanagerListviewLable.setText("系统软件(" + systemList.size() + ")");
                }
            }
        });
    }

    private void showPopupWindow(View view, int i, int i1) {

        closePopupWindow();
        pw.showAtLocation(view, Gravity.LEFT | Gravity.TOP, i, i1);
        popupView.startAnimation(sa);
    }


    private void initData() {
        new Thread() {
            @Override
            public void run() {

                handler.obtainMessage(LOADING).sendToTarget();
                SystemClock.sleep(1000);
                List<AppBean> beanList = AppManagerEngine.getAllApks(getApplicationContext());

                for (AppBean bean : beanList) {
                    if (bean.isSystem()) {
                        systemList.add(bean);
                    } else {
                        userList.add(bean);
                    }
                }
                sdAvail = AppManagerEngine.getSdAvail();
                romAvail = AppManagerEngine.getRomAvail();
                handler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();


    }

    private class ViewHolder {
        ImageView iv_app_icon;
        TextView tv_app_name;
        TextView tv_app_selection;
        TextView tv_app_size;

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userList.size() + systemList.size() + 2;
        }

        @Override
        public AppBean getItem(int position) {
            AppBean bean;
            if (position <= userList.size()) {
                bean = userList.get(position - 1);
            } else {
                bean = systemList.get(position - 1 - 1 - userList.size());
            }
            return bean;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                TextView tv_userTable = new TextView(getApplicationContext());
                tv_userTable.setText("个人软件(" + userList.size() + ")");
                tv_userTable.setTextColor(Color.WHITE);// 文字为白色
                tv_userTable.setBackgroundColor(Color.GRAY);// 文字背景为灰色
                return tv_userTable;
            } else if (position == userList.size() + 1) {
                TextView tv_userTable = new TextView(getApplicationContext());
                tv_userTable.setText("系统软件(" + systemList.size() + ")");
                tv_userTable.setTextColor(Color.WHITE);// 文字为白色
                tv_userTable.setBackgroundColor(Color.GRAY);// 文字背景为灰色

                return tv_userTable;
            } else {
                ViewHolder viewHolder = new ViewHolder();
                if (convertView != null && convertView instanceof RelativeLayout) {
                    viewHolder = (ViewHolder) convertView.getTag();

                } else {
                    convertView = View.inflate(AppManageActivity.this, R.layout.appmanage_listview_item, null);

                    viewHolder.iv_app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
                    viewHolder.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);

                    viewHolder.tv_app_selection = (TextView) convertView.findViewById(R.id.tv_app_selection);

                    viewHolder.tv_app_size = (TextView) convertView.findViewById(R.id.tv_app_size);

                    convertView.setTag(viewHolder);
                }

                AppBean bean = getItem(position);

                viewHolder.iv_app_icon.setImageDrawable(bean.getIcon());
                viewHolder.tv_app_name.setText(bean.getAppName());
                if (bean.isSd()) {
                    viewHolder.tv_app_selection.setText("SD存储");
                } else {
                    viewHolder.tv_app_selection.setText("Rom存储");
                }

                viewHolder.tv_app_size.setText(Formatter.formatFileSize(
                        getApplicationContext(), bean.getSize()));
                return convertView;

            }
        }

    }

    private void initView() {
        setContentView(R.layout.activity_app_manage);
        ButterKnife.bind(this);
        adapter = new MyAdapter();
        lvAppmanagerAppdatas.setAdapter(adapter);

    }


}
