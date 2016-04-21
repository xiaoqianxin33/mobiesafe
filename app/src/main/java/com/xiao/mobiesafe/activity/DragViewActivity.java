package com.xiao.mobiesafe.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiao.mobiesafe.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DragViewActivity extends AppCompatActivity {

    @Bind(R.id.tv_top)
    TextView tvTop;
    @Bind(R.id.tv_bottom)
    TextView tvBottom;
    @Bind(R.id.iv_drag)
    ImageView ivDrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view);
        ButterKnife.bind(this);

        ivDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                switch (action){
                    case  MotionEvent.ACTION_DOWN:



                        break;
                    case  MotionEvent.ACTION_MOVE:
                        break;
                    case  MotionEvent.ACTION_UP:
                        break;
                }


                return false;
            }
        });
    }
}
