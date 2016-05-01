package com.xiao.mobiesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xiao.mobiesafe.R;
import com.xiao.mobiesafe.domain.AppBean;
import com.xiao.mobiesafe.utils.AppManagerEngine;
import com.xiao.mobiesafe.utils.ConnectivityEngine;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ConnectivityActivity extends Activity {

    @Bind(R.id.lv_connectivity)
    ListView lv_connectivity;
    private Myadapt myadapt;
    private List<AppBean> allApks = new ArrayList<>();
    private ConnectivityManager cm;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            myadapt.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectivity);
        ButterKnife.bind(this);

        cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        initView();
        initData();
    }

    private void initView() {
        myadapt = new Myadapt();

        lv_connectivity.setAdapter(myadapt);
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                allApks = AppManagerEngine.getAllApks(getApplication());

                for (int i = 0; i < allApks.size(); i++) {
                    AppBean bean = allApks.get(i);

                    long receive=ConnectivityEngine.getReceive(bean.getUid());

                    if (receive == -1) {
                        allApks.remove(i);
                        i--;
                    }
                }
                handler.obtainMessage().sendToTarget();
            }
        }.start();

    }

    private class Myadapt extends BaseAdapter {

        @Override
        public int getCount() {
            return allApks.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_connectivity_listview, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_liuliang__listview_item_title);
                viewHolder.iv__icon = (ImageView) convertView.findViewById(R.id.iv_liuliang__listview_item_icon);
                viewHolder.iv_lock = (ImageView) convertView.findViewById(R.id.iv_liuliang__listview_lock);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final AppBean bean = allApks.get(position);

            viewHolder.tv_title.setText(bean.getAppName());

            viewHolder.iv__icon.setImageDrawable(bean.getIcon());

            viewHolder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long receivel = ConnectivityEngine.getReceive(bean.getUid());

                    long sendl = ConnectivityEngine.getSend(bean.getUid());

                    String receive = Formatter.formatFileSize(getApplicationContext(), receivel);

                    String send = Formatter.formatFileSize(getApplicationContext(), sendl);

                    showConnectivityMess(cm.getActiveNetworkInfo().getTypeName() + "\n" + "接收的流量：" + receive + "\n发送的流量:" + send);
                }
            });

            return convertView;
        }
    }


    private class ViewHolder {

        ImageView iv__icon;

        TextView tv_title;

        ImageView iv_lock;
    }

    private void showConnectivityMess(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("流量统计");
        builder.setMessage(message);
        builder.setPositiveButton("确定", null);
        builder.show();
    }
}
