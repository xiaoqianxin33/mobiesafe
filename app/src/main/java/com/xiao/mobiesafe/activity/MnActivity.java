package com.xiao.mobiesafe.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiao.mobiesafe.R;

public class MnActivity extends AppCompatActivity {

    private GridView gridView;
    private String[] mItems = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理",
            "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};

    private int[] mPics = new int[]{R.drawable.home_safe,
            R.drawable.home_callmsgsafe, R.drawable.home_apps,
            R.drawable.home_taskmanager, R.drawable.home_netmanager,
            R.drawable.home_trojan, R.drawable.home_sysoptimize,
            R.drawable.home_tools, R.drawable.home_settings};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mn);
        gridView = (GridView) findViewById(R.id.gv);
        gridView.setAdapter(new MnAdapter());

    }


    class MnAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public Object getItem(int position) {
            return mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(MnActivity.this, R.layout.gridview_list, null);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_mn);
            TextView tv = (TextView) view.findViewById(R.id.tv_mn);
            tv.setText(mItems[position]);
            iv.setImageResource(mPics[position]);
            return view;

        }
    }
}
