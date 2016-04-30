package com.xiao.mobiesafe.fragment;


import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.xiao.mobiesafe.R;


public class LockedFragment extends BaseLockOrUnlockFragment {

    @Override
    protected void setImageViewEventAndBg(ImageView iv_lock, final View convertView, final String packName) {
        iv_lock.setImageResource(R.drawable.iv_unlock_selector);

        iv_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dao.delete(packName);
                TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, -1,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                ta.setDuration(300);
                convertView.startAnimation(ta);

                new Thread() {

                    @Override
                    public void run() {
                        SystemClock.sleep(300);
                        initData();
                    }
                }.start();
            }
        });
    }

    protected void setLockNumberTextView() {
        tv_lab.setText("已加锁软件(" + (sysLockedDatas.size() + userLockedDatas.size()) + ")");
        tv_lab.setVisibility(View.VISIBLE);
    }


    protected boolean isMyData(String packName) {
        return allLockedDatas.contains(packName);
    }
}
