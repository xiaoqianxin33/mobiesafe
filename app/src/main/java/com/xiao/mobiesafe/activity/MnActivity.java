package com.xiao.mobiesafe.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiao.mobiesafe.R;
import com.xiao.mobiesafe.utils.MD5Utils;

public class MnActivity extends AppCompatActivity {

    private GridView gridView;
    private String[] mItems = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理",
            "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};

    private int[] mPics = new int[]{R.drawable.home_safe,
            R.drawable.home_callmsgsafe, R.drawable.home_apps,
            R.drawable.home_taskmanager, R.drawable.home_netmanager,
            R.drawable.home_trojan, R.drawable.home_sysoptimize,
            R.drawable.home_tools, R.drawable.home_settings};
    private SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mn);
        config = getSharedPreferences("config", MODE_PRIVATE);
        gridView = (GridView) findViewById(R.id.gv);
        gridView.setAdapter(new MnAdapter());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 8:
                        startActivity(new Intent(getApplication(), SettingActivity.class));
                        break;
                    case 0:
                        showPasswordDialog();
                        break;
                }
            }
        });

    }

    private void showPasswordDialog() {
        String password = config.getString("password", null);
        if (!TextUtils.isEmpty(password)) {
            showPasswordInputDialog();
        } else {
            showPasswordSetDailog();
        }

    }

    private void showPasswordSetDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();

        View view = View.inflate(this, R.layout.dailog_set_password, null);
        alertDialog.setView(view, 0, 0, 0, 0);
        final EditText etPassword = (EditText) view
                .findViewById(R.id.et_password);
        final EditText etPasswordConfirm = (EditText) view
                .findViewById(R.id.et_password_confirm);

        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        alertDialog.show();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String confirmPassword = etPasswordConfirm.getText().toString();
                if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)) {
                    if (password.equals(confirmPassword)) {
                        config.edit().putString("password", MD5Utils.encode(password)).commit();
                        startActivity(new Intent(getApplication(), PrevetionActivity.class));
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(MnActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MnActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    private void showPasswordInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();

        View view = View.inflate(this, R.layout.dailog_input_password, null);
        alertDialog.setView(view, 0, 0, 0, 0);
        final EditText etPassword = (EditText) view
                .findViewById(R.id.et_password);

        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        final String password = config.getString("password", null);
        alertDialog.show();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sPassword = etPassword.getText().toString();
                if (!TextUtils.isEmpty(sPassword) ) {
                    if (MD5Utils.encode(sPassword).equals(password)) {
                        startActivity(new Intent(getApplication(), PrevetionActivity.class));
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(MnActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MnActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

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
