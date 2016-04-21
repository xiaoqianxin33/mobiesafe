package com.xiao.mobiesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiao.mobiesafe.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DragViewActivity extends Activity {

    @Bind(R.id.tv_top)
    TextView tvTop;
    @Bind(R.id.tv_bottom)
    TextView tvBottom;
    @Bind(R.id.iv_drag)
    ImageView ivDrag;
    private int startX;
    private int startY;
    private SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view);
        ButterKnife.bind(this);
        config = getSharedPreferences("config", MODE_PRIVATE);

        int lastX = config.getInt("lastX", 0);
        int lastY = config.getInt("lastY", 0);
        final int winWidth = getWindowManager().getDefaultDisplay().getWidth();
        final int winHeight = getWindowManager().getDefaultDisplay()
                .getHeight();

        if (lastY > winHeight / 2) {
            tvTop.setVisibility(View.VISIBLE);
            tvBottom.setVisibility(View.INVISIBLE);
        } else {
            tvTop.setVisibility(View.INVISIBLE);
            tvBottom.setVisibility(View.VISIBLE);
        }

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivDrag
                .getLayoutParams();
        layoutParams.leftMargin = lastX;
        layoutParams.topMargin = lastY;
        ivDrag.setLayoutParams(layoutParams);

        ivDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        int dx = endX - startX;
                        int dy = endY - startY;
                        int t = ivDrag.getTop() + dy;
                        int b = ivDrag.getBottom() + dy;
                        int l = ivDrag.getLeft() + dx;
                        int r = ivDrag.getRight() + dx;

                        if (l < 0 || r > winWidth || t < 0 || b > winHeight - 20) {
                            break;
                        }

                        if (t > winHeight / 2) {// 上边显示,下边隐藏
                            tvTop.setVisibility(View.VISIBLE);
                            tvBottom.setVisibility(View.INVISIBLE);
                        } else {
                            tvTop.setVisibility(View.INVISIBLE);
                            tvBottom.setVisibility(View.VISIBLE);
                        }

                        ivDrag.layout(l, t, r, b);
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:

                        SharedPreferences.Editor edit = config.edit();
                        edit.putInt("lastX", ivDrag.getLeft());
                        edit.putInt("lastY", ivDrag.getTop());
                        edit.commit();
                        break;
                }
                return true;
            }
        });
    }
}
