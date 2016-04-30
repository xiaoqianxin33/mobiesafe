package com.xiao.mobiesafe.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiao.mobiesafe.R;
import com.xiao.mobiesafe.domain.TaskBean;
import com.xiao.mobiesafe.utils.TaskManageEnige;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TaskManageActivity extends AppCompatActivity {

    private static final int LOADING = 0;
    private static final int FINISH = 1;
    @Bind(R.id.tv_taskmanage_runningapk)
    TextView tvTaskmanageRunningapk;
    @Bind(R.id.tv_taskmanage_availmem)
    TextView tvTaskmanageAvailmem;
    @Bind(R.id.lv_taskmanage_taskdatas)
    ListView lvTaskmanageTaskdatas;
    @Bind(R.id.tv_taskmanage_listview_lable)
    TextView tvTaskmanageListviewLable;
    @Bind(R.id.pb_taskmanage_loading)
    ProgressBar pbTaskmanageLoading;
    private List<TaskBean> userList = new CopyOnWriteArrayList<>();
    private List<TaskBean> sysList = new CopyOnWriteArrayList<>();


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case LOADING:
                    pbTaskmanageLoading.setVisibility(View.VISIBLE);
                    tvTaskmanageAvailmem.setVisibility(View.GONE);
                    lvTaskmanageTaskdatas.setVisibility(View.GONE);
                    tvTaskmanageListviewLable.setVisibility(View.GONE);
                    tvTaskmanageRunningapk.setVisibility(View.GONE);
                    break;

                case FINISH:
                    pbTaskmanageLoading.setVisibility(View.GONE);
                    tvTaskmanageAvailmem.setVisibility(View.VISIBLE);
                    lvTaskmanageTaskdatas.setVisibility(View.VISIBLE);
                    tvTaskmanageListviewLable.setVisibility(View.VISIBLE);
                    tvTaskmanageRunningapk.setVisibility(View.VISIBLE);
                    setTileMessage();

                    adapt.notifyDataSetChanged();
                    break;

            }

        }
    };
    private SharedPreferences config;

    private void setTileMessage() {
        boolean showSysTask = config.getBoolean("showSysTask", true);
        if (!showSysTask) {
            tvTaskmanageRunningapk.setText("运行中的进程:" + userList.size());
        } else {
            tvTaskmanageRunningapk.setText("运行中的进程:" + (sysList.size() + userList.size()));
        }
        String avilMe = Formatter.formatFileSize(getApplicationContext(), availMemSize);
        String totalMe = Formatter.formatFileSize(getApplicationContext(), totalMemSize);

        tvTaskmanageAvailmem.setText("可用/总内存:" + avilMe + "/" + totalMe);
    }

    private long availMemSize;
    private long totalMemSize;
    private MyAdapt adapt;
    private TaskBean clickBean;
    private ActivityManager am;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        config = getSharedPreferences("config", MODE_PRIVATE);


        setContentView(R.layout.activity_task_manage);
        ButterKnife.bind(this);
    }

    private void initEvent() {
        lvTaskmanageTaskdatas.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem < userList.size() + 1) {
                    tvTaskmanageListviewLable.setText("用户软件(" + userList.size() + ")");
                } else {
                    tvTaskmanageListviewLable.setText("系统软件(" + sysList.size() + ")");
                }
            }
        });
    }

    private void initData() {

        new Thread() {

            @Override
            public void run() {
                handler.obtainMessage(LOADING).sendToTarget();
                sysList.clear();
                userList.clear();

                List<TaskBean> allRunningTask = TaskManageEnige.getAllRunningTask(getApplicationContext());

                for (TaskBean bean : allRunningTask) {
                    if (bean.isSystem()) {
                        sysList.add(bean);
                    } else {
                        userList.add(bean);
                    }
                }

                availMemSize = TaskManageEnige.getAvailMemSize(getApplicationContext());
                totalMemSize = TaskManageEnige.getTotalMemSize(getApplicationContext());
                handler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();


    }

    private void initView() {


        adapt = new MyAdapt();
        lvTaskmanageTaskdatas.setAdapter(adapt);

        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

    }


    class MyAdapt extends BaseAdapter {

        @Override
        public int getCount() {
            boolean showSysTask = config.getBoolean("showSysTask", true);
            if (!showSysTask) {
                return userList.size() + 1;
            }
            return userList.size() + sysList.size() + 2;
        }

        @Override
        public TaskBean getItem(int position) {
            TaskBean bean;
            if (position <= userList.size()) {
                bean = userList.get(position - 1);
            } else {
                bean = sysList.get(position - 1 - 1 - userList.size());
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
                tv_userTable.setText("系统软件(" + sysList.size() + ")");
                tv_userTable.setTextColor(Color.WHITE);// 文字为白色
                tv_userTable.setBackgroundColor(Color.GRAY);// 文字背景为灰色

                return tv_userTable;
            } else {
                ViewHolder viewHolder = new ViewHolder();
                if (convertView != null && convertView instanceof RelativeLayout) {
                    viewHolder = (ViewHolder) convertView.getTag();

                } else {
                    convertView = View.inflate(TaskManageActivity.this, R.layout.listview_item_taskmanage, null);

                    viewHolder.iv_item_taskmanage_icon = (ImageView) convertView.findViewById(R.id.iv_item_taskmanage_icon);
                    viewHolder.tv_item_taskmanage_name = (TextView) convertView.findViewById(R.id.tv_item_taskmanage_name);

                    viewHolder.tv_taskmanager_listview_item_memsize = (TextView) convertView.findViewById(R.id.tv_taskmanager_listview_item_memsize);

                    viewHolder.cb_item_taskmanage_check = (CheckBox) convertView.findViewById(R.id.cb_item_taskmanage_check);

                    convertView.setTag(viewHolder);
                }
                final ViewHolder mHolder = viewHolder;
                final TaskBean bean = getItem(position);

                viewHolder.iv_item_taskmanage_icon.setImageDrawable(bean.getIcon());
                viewHolder.tv_item_taskmanage_name.setText(bean.getName());
                viewHolder.tv_taskmanager_listview_item_memsize.setText(Formatter.formatFileSize(getApplicationContext(), bean.getMemSize()));

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (bean.getPackName().equals(getPackageName())) {
                            mHolder.cb_item_taskmanage_check.setChecked(false);
                        } else {
                            mHolder.cb_item_taskmanage_check.setChecked(!mHolder.cb_item_taskmanage_check
                                    .isChecked());
                        }
                    }
                });

                viewHolder.cb_item_taskmanage_check
                        .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                            @Override
                            public void onCheckedChanged(
                                    CompoundButton buttonView, boolean isChecked) {
                                bean.setChecked(isChecked);
                            }
                        });

                viewHolder.cb_item_taskmanage_check.setChecked(bean.isChecked());

                if (bean.getPackName().equals(getPackageName())) {
                    viewHolder.cb_item_taskmanage_check.setVisibility(View.GONE);
                } else {
                    viewHolder.cb_item_taskmanage_check.setVisibility(View.VISIBLE);
                }

                return convertView;
            }
        }
    }

    private class ViewHolder {
        ImageView iv_item_taskmanage_icon;
        TextView tv_item_taskmanage_name;
        TextView tv_taskmanager_listview_item_memsize;
        CheckBox cb_item_taskmanage_check;

    }


    public void clearTask(View view) {
        long clearMem = 0;
        int clearNum = 0;

        for (TaskBean bean : userList) {
            if (bean.isChecked()) {

                clearNum++;

                clearMem += bean.getMemSize();

                am.killBackgroundProcesses(bean.getPackName());

                userList.remove(bean);
            }
        }

        for (TaskBean bean : sysList) {
            if (bean.isChecked()) {

                clearNum++;

                clearMem += bean.getMemSize();

                am.killBackgroundProcesses(bean.getPackName());

                sysList.remove(bean);
            }
        }

        Toast.makeText(getApplicationContext(), "清理了" + clearNum +
                "个进程，释放了" + Formatter.formatFileSize(getApplicationContext(), clearMem), Toast.LENGTH_LONG).show();

        availMemSize += clearMem;
        setTileMessage();
        adapt.notifyDataSetChanged();
    }


    public void selectAll(View view) {
        for (TaskBean bean : userList) {
            if (bean.getPackName().equals(getPackageName())) {
                bean.setChecked(false);
                continue;
            }
            bean.setChecked(true);
        }

        for (TaskBean bean : sysList) {
            bean.setChecked(true);
        }

        adapt.notifyDataSetChanged();
    }


    public void fanSelect(View view) {
        for (TaskBean bean : userList) {
            if (bean.getPackName().equals(getPackageName())) {
                bean.setChecked(false);
                continue;
            }
            bean.setChecked(!bean.isChecked());
        }

        for (TaskBean bean : sysList) {
            bean.setChecked(!bean.isChecked());
        }

        adapt.notifyDataSetChanged();
    }

    public void setting(View view) {
        startActivity(new Intent(this, TaskManageSettingActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();

        initData();

        initView();

        initEvent();
    }
}
