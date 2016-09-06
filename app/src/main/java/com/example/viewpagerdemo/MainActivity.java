package com.example.viewpagerdemo;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.viewpagerdemo.view.TakeTurnsView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private TakeTurnsView takeTurnsView;
    private Button add_btn, remove_btn, update_btn;
    private int number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takeTurnsView = (TakeTurnsView) findViewById(R.id.default_take_turns_view);
        add_btn = (Button) findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number++;
                //addDrawables(number);
                drawables.add(getResources().getDrawable(R.drawable.i2));
                takeTurnsView.setImageUrls(drawables);
            }
        });
        remove_btn = (Button) findViewById(R.id.remove_btn);
        remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeDrawables();
                takeTurnsView.setImageUrls(drawables);
            }
        });
        update_btn = (Button) findViewById(R.id.update_btn);
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDrawables();
                takeTurnsView.setImageUrls(drawables);
            }
        });
        takeTurnsView.setSleepTime(4000);
        takeTurnsView.setViewpagerScrollTime(200);
        takeTurnsView.setTakeTurnsHeight(300);
        drawables.add(getResources().getDrawable(R.drawable.i1));
        drawables.add(getResources().getDrawable(R.drawable.i2));
        takeTurnsView.setImageUrls(drawables);
        takeTurnsView.setUpdateUI(new TakeTurnsView.UpdateUI() {
            @Override
            public void onUpdateUI(int position) {
                Log.i("test", "当前图片位置：" + position);
            }

            @Override
            public void onItemClick(int position, ImageView imageView) {
                Toast.makeText(MainActivity.this, "当前图片位置：" + position + "  id:" + imageView.getId(), Toast.LENGTH_LONG).show();
            }
        });
    }

    List<Drawable> drawables = new ArrayList<>();

    private void removeDrawables() {
        drawables.remove(0);
    }

    private void updateDrawables() {
        drawables.set(0, getResources().getDrawable(R.drawable.i4));
    }


    @Override
    protected void onPause() {
        super.onPause();
        takeTurnsView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //takeTurnsView.onResume();
    }
}
