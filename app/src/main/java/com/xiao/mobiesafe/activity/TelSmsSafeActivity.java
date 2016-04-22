package com.xiao.mobiesafe.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiao.mobiesafe.R;
import com.xiao.mobiesafe.dao.BlackDao;
import com.xiao.mobiesafe.domain.BlackBean;
import com.xiao.mobiesafe.domain.BlackTable;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TelSmsSafeActivity extends AppCompatActivity {

    @Bind(R.id.bt_telsms_addsafenumber)
    Button btTelsmsAddsafenumber;
    @Bind(R.id.lv_telsms_safenumbers)
    ListView lvTelsmsSafenumbers;
    @Bind(R.id.tv_telsms_nodata)
    TextView tvTelsmsNodata;
    @Bind(R.id.pb_telsms_loading)
    ProgressBar pbTelsmsLoading;
    BlackDao blackDao;
    protected static final int LOADING = 1;
    protected static final int FINISH = 2;
    List<BlackBean> datas = new ArrayList<>();

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case LOADING:
                    pbTelsmsLoading.setVisibility(View.VISIBLE);

                    lvTelsmsSafenumbers.setVisibility(View.GONE);

                    tvTelsmsNodata.setVisibility(View.GONE);
                    break;

                case FINISH:
                    if (datas.size() == 0) {
                        pbTelsmsLoading.setVisibility(View.GONE);

                        lvTelsmsSafenumbers.setVisibility(View.GONE);

                        tvTelsmsNodata.setVisibility(View.VISIBLE);
                    } else {
                        pbTelsmsLoading.setVisibility(View.GONE);

                        lvTelsmsSafenumbers.setVisibility(View.VISIBLE);

                        tvTelsmsNodata.setVisibility(View.GONE);
                    }
                    break;


            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        blackDao = new BlackDao(getApplicationContext());
        initView();
        initDate();
    }

    private void initDate() {

        new Thread() {
            @Override
            public void run() {

                handler.obtainMessage(LOADING).sendToTarget();

                SystemClock.sleep(2000);
                datas = blackDao.getAllDatas();
                handler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();


    }

    private class ItemView {
        TextView tv_phone;

        TextView tv_mode;

        ImageView iv_delete;
    }


    private void initView() {

        setContentView(R.layout.activity_tel_sms_safe);
        ButterKnife.bind(this);
        MyAdapter adapter = new MyAdapter();

        lvTelsmsSafenumbers.setAdapter(adapter);
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemView itemView = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_telsmssafe_listview, null);
                itemView = new ItemView();

                itemView.iv_delete = (ImageView) convertView.findViewById(R.id.iv_telsmssafe_listview_item_delete);
                itemView.tv_phone = (TextView) convertView.findViewById(R.id.tv_telsmssafe_listview_item_number);
                itemView.tv_mode = (TextView) convertView.findViewById(R.id.tv_telsmssafe_listview_item_mode);

                convertView.setTag(itemView);
            } else {
                itemView = (ItemView) convertView.getTag();
            }

            BlackBean bean = datas.get(position);

            itemView.tv_phone.setText(bean.getPhone());

            switch (bean.getMode()) {
                case BlackTable.SMS:
                    itemView.tv_mode.setText("短信拦截");
                    break;
                case BlackTable.TEL:
                    itemView.tv_mode.setText("电话拦截");
                    break;
                case BlackTable.ALL:
                    itemView.tv_mode.setText("全部拦截");
                    break;

                default:
                    break;
            }

            return convertView;
        }
    }
}
