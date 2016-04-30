package com.xiao.mobiesafe.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiao.mobiesafe.R;
import com.xiao.mobiesafe.dao.LockDao;
import com.xiao.mobiesafe.domain.AppBean;
import com.xiao.mobiesafe.utils.AppManagerEngine;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseLockOrUnlockFragment extends Fragment {


    protected static final int LOADING = 0;
    protected static final int FINISH = 1;
    protected List<String> allLockedDatas = new ArrayList<>();
    protected List<AppBean> userLockedDatas = new ArrayList<>();
    protected List<AppBean> sysLockedDatas = new ArrayList<>();
    @Bind(R.id.tv_fragment_locked_lab)
    TextView tv_lab;
    @Bind(R.id.lv_fragment_unlocked_datas)
    ListView lv_datas;
    @Bind(R.id.tv_fragment_unlocked_listview_tag)
    TextView tv_listview_tag;
    @Bind(R.id.pb_fragment_loading)
    ProgressBar pb_loading;
    protected MyAdapt adapt;
    protected LockDao dao;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dao = new LockDao(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unlock, container, false);
        ButterKnife.bind(this, view);
        adapt = new MyAdapt();
        lv_datas.setAdapter(adapt);
        return view;
    }


    protected class ViewHolder {

        ImageView iv_fragment_unlock__listview_item_icon;
        TextView tv_fragment_unlock__listview_item_title;
        ImageView iv_fragment_unlock__listview_lock;
    }


    class MyAdapt extends BaseAdapter {

        @Override
        public int getCount() {
            return userLockedDatas.size() + sysLockedDatas.size() + 2;
        }

        @Override
        public Object getItem(int position) {
            try {
                if (position <= userLockedDatas.size()) {
                    return userLockedDatas.get(position - 1);
                } else {
                    return sysLockedDatas.get(position - 2 - userLockedDatas.size());
                }
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                TextView tv_userTable = new TextView(getContext());
                tv_userTable.setText("个人软件(" + userLockedDatas.size() + ")");
                tv_userTable.setTextColor(Color.WHITE);
                tv_userTable.setBackgroundColor(Color.GRAY);
                return tv_userTable;
            } else if (position == userLockedDatas.size() + 1) {
                TextView tv_userTable = new TextView(getContext());
                tv_userTable.setText("系统软件(" + sysLockedDatas.size() + ")");
                tv_userTable.setTextColor(Color.WHITE);// 文字为白色
                tv_userTable.setBackgroundColor(Color.GRAY);// 文字背景为灰色

                return tv_userTable;
            } else {
                ViewHolder viewHolder = new ViewHolder();
                if (convertView != null && convertView instanceof RelativeLayout) {
                    viewHolder = (ViewHolder) convertView.getTag();

                } else {
                    convertView = View.inflate(getContext(), R.layout.item_fragment_lock_listview, null);

                    viewHolder.iv_fragment_unlock__listview_item_icon = (ImageView) convertView.findViewById(R.id.iv_fragment_unlock__listview_item_icon);
                    viewHolder.tv_fragment_unlock__listview_item_title = (TextView) convertView.findViewById(R.id.tv_fragment_unlock__listview_item_title);

                    viewHolder.iv_fragment_unlock__listview_lock = (ImageView) convertView.findViewById(R.id.iv_fragment_unlock__listview_lock);

                    convertView.setTag(viewHolder);
                }

                final AppBean bean = (AppBean) getItem(position);
                if (bean == null) {
                    return convertView;
                }
                viewHolder.iv_fragment_unlock__listview_item_icon.setImageDrawable(bean.getIcon());
                viewHolder.tv_fragment_unlock__listview_item_title.setText(bean.getAppName());
                setImageViewEventAndBg(viewHolder.iv_fragment_unlock__listview_lock, convertView, bean.getPackName());

                return convertView;
            }
        }
    }

    protected void setImageViewEventAndBg(ImageView iv_lock, final View convertView, final String packName) {

    }

    protected Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_loading.setVisibility(View.VISIBLE);
                    tv_listview_tag.setVisibility(View.GONE);
                    lv_datas.setVisibility(View.GONE);
                    tv_lab.setVisibility(View.GONE);

                    break;
                case FINISH:
                    pb_loading.setVisibility(View.GONE);
                    tv_listview_tag.setVisibility(View.VISIBLE);
                    lv_datas.setVisibility(View.VISIBLE);

                    setLockNumberTextView();

                    tv_listview_tag.setText("用户软件(" + userLockedDatas.size() + ")");
                    adapt.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }


    };

    protected void setLockNumberTextView() {

    }

    protected void initData() {
        new Thread() {
            public void run() {
                synchronized (new Object()) {
                    handler.obtainMessage(LOADING).sendToTarget();
                    List<AppBean> allApks = AppManagerEngine.getAllApks(getActivity());
                    sysLockedDatas.clear();
                    userLockedDatas.clear();
                    for (AppBean appBean : allApks) {
                        if (isMyData(appBean.getPackName())) {

                            if (appBean.isSystem()) {
                                sysLockedDatas.add(appBean);
                            } else {
                                userLockedDatas.add(appBean);
                            }
                        }
                    }
                    handler.obtainMessage(FINISH).sendToTarget();
                }

            }
        }.start();
    }

    protected boolean isMyData(String packName) {
        return false;
    }


    @Override
    public void onResume() {
        initData();
        initEvent();
        super.onResume();
    }

    protected void initEvent() {


        lv_datas.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem <= userLockedDatas.size()) {
                    tv_listview_tag.setText("用户软件(" + userLockedDatas.size() + ")");
                } else {
                    tv_listview_tag.setText("系统软件(" + sysLockedDatas.size() + ")");
                }
            }
        });
    }

    public void setAllLockedPacks(List<String> allLockedDatas) {
        this.allLockedDatas = allLockedDatas;
    }
}

