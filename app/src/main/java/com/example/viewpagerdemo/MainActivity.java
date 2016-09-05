package com.example.viewpagerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.viewpagerdemo.view.TakeTurnsView;

public class MainActivity extends Activity {
    private TakeTurnsView takeTurnsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takeTurnsView = (TakeTurnsView) findViewById(R.id.default_take_turns_view);
        takeTurnsView.setSleepTime(4000);
        takeTurnsView.setViewpagerScrollTime(200);
        takeTurnsView.setImageViewIds(new int[]{R.drawable.i1, R.drawable.i2, R.drawable.i3, R.drawable.i4});
        takeTurnsView.setUpdateUI( new TakeTurnsView.UpdateUI() {
            @Override
            public void onUpdateUI(int position, ImageView imageView) {
                imageView.setOnClickListener(new OnClick(position));
            }
        });
    }

    private class OnClick implements View.OnClickListener {
        private int position;

        OnClick(int i) {
            position = i;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "当前图片位置：" + position, Toast.LENGTH_LONG).show();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        takeTurnsView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        takeTurnsView.onResume();
    }
}
