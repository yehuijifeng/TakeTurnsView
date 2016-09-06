package com.example.viewpagerdemo.adapter;


import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by 浩 on 2016/8/30.
 * 处理viewpager的轮番适配器
 */
public class MyAdapter extends PagerAdapter {

    private List<ImageView> mImageViews;

    public void setmImageViews(List<ImageView> mImageViews) {
        this.mImageViews = mImageViews;
    }

    public void closeData() {
        mImageViews.clear();
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    /**
     * 移除未显示出来的item
     *
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mImageViews==null||mImageViews.size() < 1) return;
        container.removeView(mImageViews.get(position % mImageViews.size()));

    }

    /**
     * 添加每一个item
     * 最重要的算法自行理解，比较简单
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        if (mImageViews==null||mImageViews.size() < 1) return null;
        try {
            //有时候添加的视图会未没上个父view移除，所以这里抛出异常
            container.addView(mImageViews.get(position % mImageViews.size()), 0);
        } catch (Exception e) {
            // handler something
        }
        return mImageViews.get(position % mImageViews.size());
    }
}
