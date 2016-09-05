package com.example.viewpagerdemo.viewpager;


import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.viewpagerdemo.adapter.MyAdapter;
import com.example.viewpagerdemo.contants.Contant;

/**
 * 传入onScroll kongzhi dangqian viewpager
 * shi
 */
public class NoScrollViewPager extends ViewPager {
    private boolean noScroll = false;
    private Handler handler;


    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public void setNoScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (noScroll)
            return false;
        else
            return super.onTouchEvent(arg0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (noScroll)
            return false;
        else
            return super.onInterceptTouchEvent(arg0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (noScroll)
            return super.dispatchTouchEvent(ev);
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            Contant.isRun = false;
            Contant.isDown = true;
            handler.removeCallbacksAndMessages(null);
        } else if (action == MotionEvent.ACTION_UP) {
            Contant.isRun = true;
            Contant.isDown = false;
            handler.removeCallbacksAndMessages(null);
            handler.sendEmptyMessage(1);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setInfinateAdapter(Handler handler, MyAdapter adapter) {
        this.handler = handler;
        setAdapter(adapter);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }

}
