package com.xiao.mobiesafe.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
                        if (moreDatas.size() == 0) {
                            Toast.makeText(TelSmsSafeActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
                        }
                        pbTelsmsLoading.setVisibility(View.GONE);

                        lvTelsmsSafenumbers.setVisibility(View.VISIBLE);

                        tvTelsmsNodata.setVisibility(View.GONE);
                    }
                    break;


            }

        }
    };
    private List<BlackBean> moreDatas;
    private MyAdapter adapter;
    private View view;
    private ScaleAnimation sa;
    private PopupWindow pw;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        blackDao = new BlackDao(getApplicationContext());
        initView();
        initDate();
        initEvent();
        initPopupWindow();
    }

    private void initEvent() {

        lvTelsmsSafenumbers.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    int position = lvTelsmsSafenumbers.getLastVisiblePosition();
                    if (position == (datas.size() - 1)) {
                        initDate();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


    }

    private int DATASNUMBER = 20;

    private void initDate() {

        new Thread() {
            @Override
            public void run() {

                handler.obtainMessage(LOADING).sendToTarget();

                moreDatas = blackDao.getMoreDatas(DATASNUMBER, datas.size());

                datas.addAll(moreDatas);
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
        adapter = new MyAdapter();

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
        public View getView(final int position, View convertView, ViewGroup parent) {
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

            final BlackBean bean = datas.get(position);

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

            itemView.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder ab = new AlertDialog.Builder(TelSmsSafeActivity.this);

                    ab.setTitle("注意");
                    ab.setMessage("是否真的删除该数据？");
                    ab.setPositiveButton("真删", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            blackDao.delete(bean.getPhone());

                            datas.remove(position);

                            adapter.notifyDataSetChanged();

                        }
                    });
                    ab.setNegativeButton("点错了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ab.show();
                }
            });


            return convertView;
        }
    }

    public void addBlackNumber(View view) {
        showPopupWindow();
    }

    private void showPopupWindow() {
        if (pw != null && pw.isShowing()) {
            pw.dismiss();
        } else {
            int[] location = new int[2];
            btTelsmsAddsafenumber.getLocationInWindow(location);

            view.startAnimation(sa);
            pw.showAtLocation(btTelsmsAddsafenumber, Gravity.RIGHT | Gravity.TOP,
                    location[0] - (getWindowManager().getDefaultDisplay().getWidth() - btTelsmsAddsafenumber.getWidth()),
                    location[1] + btTelsmsAddsafenumber.getHeight());
        }
    }

    private void initPopupWindow() {

        view = View.inflate(this, R.layout.popup_blacknumber_item, null);

        TextView tv_shoudong = (TextView) view
                .findViewById(R.id.tv_popup_black_shoudong);
        TextView tv_contact = (TextView) view
                .findViewById(R.id.tv_popup_black_contacts);
        TextView tv_phonelog = (TextView) view
                .findViewById(R.id.tv_popup_black_phonelog);
        TextView tv_smslog = (TextView) view
                .findViewById(R.id.tv_popup_black_smslog);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_popup_black_contacts:
                        System.out.println("从联系人导入");
                        break;
                    case R.id.tv_popup_black_phonelog:
                        System.out.println("从电话日志导入");
                        break;
                    case R.id.tv_popup_black_shoudong:
                        addByHand();
                        System.out.println("手动导入");

                        break;
                    case R.id.tv_popup_black_smslog:
                        System.out.println("从短信导入");
                        break;

                    default:
                        break;
                }
                closePopupWindow();
            }
        };
        tv_smslog.setOnClickListener(listener);
        tv_contact.setOnClickListener(listener);
        tv_phonelog.setOnClickListener(listener);
        tv_shoudong.setOnClickListener(listener);

        pw = new PopupWindow(view, -2, -2);

        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sa = new ScaleAnimation(1, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0f);
        sa.setDuration(1000);
    }

    private void addByHand() {

        final AlertDialog.Builder ab = new AlertDialog.Builder(TelSmsSafeActivity.this);
        View contView = View.inflate(this, R.layout.dailog_addblack_hand, null);
        final EditText et_blackNumber = (EditText) contView
                .findViewById(R.id.et_addblack_hand_number);

        final CheckBox cb_sms = (CheckBox) contView
                .findViewById(R.id.cb_blackhand_smsmode);

        final CheckBox cb_phone = (CheckBox) contView
                .findViewById(R.id.cb_blackhand_phonemode);

        Button bt_add = (Button) contView.findViewById(R.id.btn_addbalck_hand_ok);

        Button bt_cancel = (Button) contView
                .findViewById(R.id.btn_addbalck_hand_cancel);


        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = et_blackNumber.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(TelSmsSafeActivity.this, "不能输入空号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!cb_sms.isChecked() && (!cb_phone.isChecked())) {
                    Toast.makeText(TelSmsSafeActivity.this, "必须选择一种屏蔽方式", Toast.LENGTH_SHORT).show();
                    return;
                }

                int mode = 0;
                if (cb_phone.isChecked()) {
                    mode |= BlackTable.TEL;
                }
                if (cb_sms.isChecked()) {
                    mode |= BlackTable.SMS;
                }

                BlackBean blackBean = new BlackBean();
                blackBean.setMode(mode);
                blackBean.setPhone(number);

                blackDao.add(blackBean);

                datas.remove(blackBean);
                datas.add(0, blackBean);
                adapter = new MyAdapter();
                lvTelsmsSafenumbers.setAdapter(adapter);

                dialog.dismiss();
            }
        });
        ab.setView(contView);
        dialog = ab.create();
        dialog.show();
    }

    private void closePopupWindow() {
        if (pw != null && pw.isShowing())
            pw.dismiss();
    }
}
