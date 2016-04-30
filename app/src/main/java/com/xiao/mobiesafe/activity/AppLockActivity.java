package com.xiao.mobiesafe.activity;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xiao.mobiesafe.R;
import com.xiao.mobiesafe.dao.LockDao;
import com.xiao.mobiesafe.domain.AppBean;
import com.xiao.mobiesafe.domain.LockTable;
import com.xiao.mobiesafe.fragment.LockedFragment;
import com.xiao.mobiesafe.fragment.UnlockFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AppLockActivity extends AppCompatActivity {

    @Bind(R.id.tv_lockedactivity_unlock)
    TextView tv_unlock;
    @Bind(R.id.tv_lockedactivity_locked)
    TextView tv_locked;
    @Bind(R.id.fl_lockedactivity_content)
    FrameLayout fl_Content;
    private FragmentManager fm;
    private UnlockFragment unlockFragment;
    private LockedFragment lockedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        ButterKnife.bind(this);

        fm = getSupportFragmentManager();
        initData();
        initEvent();
    }

    private void initData() {
        unlockFragment = new UnlockFragment();
        lockedFragment = new LockedFragment();
        new Thread() {
            @Override
            public void run() {
                LockDao lockDao = new LockDao(getApplicationContext());
                List<String> allDatas = lockDao.getAllDatas();
                unlockFragment.setAllLockedPacks(allDatas);
                lockedFragment.setAllLockedPacks(allDatas);
            }
        }.start();

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fl_lockedactivity_content, unlockFragment);
        transaction.commit();
    }


    private void initEvent() {

        ContentObserver contentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                new Thread() {
                    @Override
                    public void run() {
                        LockDao lockDao = new LockDao(getApplicationContext());
                        List<String> allDatas = lockDao.getAllDatas();
                        unlockFragment.setAllLockedPacks(allDatas);
                        lockedFragment.setAllLockedPacks(allDatas);
                    }
                }.start();
            }
        };

        getContentResolver().registerContentObserver(LockTable.uri, true, contentObserver);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = fm.beginTransaction();

                if (v.getId() == R.id.tv_lockedactivity_unlock) {

                    transaction.replace(R.id.fl_lockedactivity_content, unlockFragment);
                    tv_unlock.setBackgroundResource(R.drawable.tab_right_pressed);
                    tv_locked.setBackgroundResource(R.drawable.tab_left_default);
                } else if (v.getId() == R.id.tv_lockedactivity_locked) {

                    transaction.replace(R.id.fl_lockedactivity_content, lockedFragment);
                    tv_unlock.setBackgroundResource(R.drawable.tab_left_default);
                    tv_locked.setBackgroundResource(R.drawable.tab_left_pressed);
                }

                transaction.commit();
            }
        };

        tv_unlock.setOnClickListener(listener);
        tv_locked.setOnClickListener(listener);
    }
}
