package com.xiao.mobiesafe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.xiao.mobiesafe.R;

public abstract class BaseGuidActivity extends AppCompatActivity {

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
                    Toast.makeText(BaseGuidActivity.this, "不能这样划哦!",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (Math.abs(velocityX) < 100) {
                    Toast.makeText(BaseGuidActivity.this, "滑动的太慢了!",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }


                if ((e2.getRawX() - e1.getRawX()) > 200) {
                    showPreviousPage();
                    return true;
                } else if ((e1.getRawX() - e2.getRawX()) > 200) {
                    showNextPage();
                    return true;
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
        super.onCreate(savedInstanceState);
    }

    public abstract void showPreviousPage();

    public abstract void showNextPage();

    public void next(View view) {
        showNextPage();
    }


    public void previous(View view) {
        showPreviousPage();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
