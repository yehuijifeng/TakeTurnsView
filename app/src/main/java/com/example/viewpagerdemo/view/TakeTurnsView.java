package com.example.viewpagerdemo.view;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.viewpagerdemo.R;
import com.example.viewpagerdemo.adapter.MyAdapter;
import com.example.viewpagerdemo.contants.Contant;
import com.example.viewpagerdemo.utils.DisplayUtil;
import com.example.viewpagerdemo.viewpager.NoScrollViewPager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 浩 on 2016/8/30.
 * 轮番图
 */
public class TakeTurnsView extends LinearLayout {
    private View root;//根布局
    public NoScrollViewPager take_turns_view_pager;//改造后的viewpager
    private RadioGroup take_turns_radio_group;//存放轮番图下标的radiogroup
    /**
     * viewpager的适配器
     */
    private MyAdapter pagerAdapter;

    private Handler mHandler;

    private int sleepTime = 3000;

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    /**
     * 资源id
     */
    private List<String> imageUrls;

    /**
     * 控制viewpager的滑动速度
     */
    private FixedSpeedScroller scroller;

    private int fixedTime = 200;

    private UpdateUI updateUI;

    public UpdateUI getUpdateUI() {
        return updateUI;
    }

    public void setUpdateUI(UpdateUI updateUI) {
        this.updateUI = updateUI;
        if (getUpdateUI() != null)
            getUpdateUI().onUpdateUI(0, imageViews.get(0));
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls, ImageLoader imageLoader) {
        setImageUrls(imageUrls, 0, imageLoader);
    }

    public void setImageUrls(List<String> imageUrls, int drawableId, ImageLoader imageLoader) {
        this.imageUrls = imageUrls;
        if (imageViews == null)
            imageViews = new ArrayList<>();
        if (take_turns_radio_group != null)
            take_turns_radio_group.removeAllViews();
        imageViews.clear();
        // 将点点加入到ViewGroup中
        tips = new RadioButton[imageUrls.size()];

        //radiobutton的布局
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margs = DisplayUtil.dip2px(getContext(), 3);
        layoutParams.setMargins(margs, margs, margs, margs);
        //radiobutton的样式
        drawableId = drawableId == 0 ? R.drawable.bg_page_item_tag : drawableId;
        for (int i = 0; i < tips.length; i++) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setButtonDrawable(drawableId);
            radioButton.setLayoutParams(layoutParams);
            tips[i] = radioButton;
            take_turns_radio_group.addView(radioButton);
        }
        tips[0].setChecked(true);
        ViewGroup.LayoutParams imageLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // 将图片装载到数组中
        if (imageUrls.size() == 1) {
            for (int i = 0; i < 2; i++) {
                ImageView imageView = new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(imageLayoutParams);
                imageLoader.displayImage(imageUrls.get(0), imageView, MallApplication.options);
                imageViews.add(imageView);
            }
            take_turns_view_pager.setNoScroll(true);
        } else if (imageUrls.size() == 2 || imageUrls.size() == 3) {
            for (int i = 0; i < imageUrls.size() * 2; i++) {
                ImageView imageView = new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(imageLayoutParams);
                imageLoader.displayImage(imageUrls.get((i > (imageUrls.size() - 1)) ? i - imageUrls.size() : i), imageView, MallApplication.options);
                imageViews.add(imageView);
            }
        } else {
            for (int i = 0; i < imageUrls.size(); i++) {
                ImageView imageView = new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(imageLayoutParams);
                imageLoader.displayImage(imageUrls.get(i), imageView, MallApplication.options);
                imageViews.add(imageView);
            }
        }

        if (pagerAdapter == null) {
            pagerAdapter = new MyAdapter();
            take_turns_view_pager.setInfinateAdapter(mHandler, pagerAdapter);
        }
        pagerAdapter.setmImageViews(imageViews);
        pagerAdapter.notifyDataSetChanged();
        take_turns_view_pager.setCurrentItem((imageViews.size()) * 100);
        onResume();
    }

    /**
     * view控件集合
     */
    private List<ImageView> imageViews;

    /**
     * 游标集合
     */
    private RadioButton[] tips;

    public TakeTurnsView(Context context) {
        super(context);
        initView();
    }

    public TakeTurnsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TakeTurnsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        root = LayoutInflater.from(getContext()).inflate(R.layout.base_take_truns, this);
        take_turns_view_pager = (NoScrollViewPager) root.findViewById(R.id.take_turns_view_pager);
        take_turns_radio_group = (RadioGroup) root.findViewById(R.id.take_turns_radio_group);
        imageViews = new ArrayList<>();
        take_turns_view_pager.addOnPageChangeListener(new MyOnPageChangeListener());
        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        if (imageUrls == null || imageUrls.size() <= 1) return;
                        take_turns_view_pager.setCurrentItem(take_turns_view_pager.getCurrentItem() + 1, true);
                        if (Contant.isRun && !Contant.isDown) {
                            this.sendEmptyMessageDelayed(0, sleepTime);
                        }
                        break;

                    case 1:
                        if (Contant.isRun && !Contant.isDown) {
                            this.sendEmptyMessageDelayed(0, sleepTime);
                        }
                        break;
                }
            }
        };

        getViewpagerScrollTime();
    }

    /**
     * 设置viewpager的滑动速度
     */
    private void getViewpagerScrollTime() {
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            scroller = new FixedSpeedScroller(take_turns_view_pager.getContext(),
                    new AccelerateInterpolator());
            field.set(take_turns_view_pager, scroller);
            //经测试，200ms是最佳视觉效果
            scroller.setmDuration(fixedTime);
        } catch (Exception e) {
            //LogUtils.e(TAG, "", e);
        }
    }

    /**
     * 外界调用，赋值
     *
     * @param time
     */
    public void setViewpagerScrollTime(int time) {
        try {
            fixedTime = time;
            //经测试，200ms是最佳视觉效果
            scroller.setmDuration(fixedTime);
        } catch (Exception e) {
            //LogUtils.e(TAG, "", e);
        }
    }


    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        /**
         * Indicates that the pager is in an idle, settled state. The current
         * page is fully in view and no animation is in progress.
         */
        public static final int SCROLL_STATE_IDLE = 0;

        /**
         * Indicates that the pager is currently being dragged by the user.
         */

        public static final int SCROLL_STATE_DRAGGING = 1;

        /**
         * Indicates that the pager is in the process of settling to a final
         * position.
         */
        public static final int SCROLL_STATE_SETTLING = 2;

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                case SCROLL_STATE_IDLE:
                    // System.out
                    // .println("===========>>>"
                    // + " onPageScrollStateChanged --->>> SCROLL_STATE_IDLE");
                    break;

                case SCROLL_STATE_DRAGGING:
                    // System.out
                    // .println("===========>>>"
                    // + " onPageScrollStateChanged --->>> SCROLL_STATE_DRAGGING");
                    break;
                case SCROLL_STATE_SETTLING:
                    // System.out
                    // .println("===========>>>"
                    // + " onPageScrollStateChanged --->>> SCROLL_STATE_SETTLING");
                    break;
            }

        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (imageViews.size() < 1) return;
            int j = setImageBackground(position % imageViews.size());
            if (getUpdateUI() != null)
                getUpdateUI().onUpdateUI(j, imageViews.get(j));
        }


        /**
         * 设置选中的tip的背景
         *
         * @param selectItems
         */
        private int setImageBackground(int selectItems) {
            int j = 0;
            if (imageUrls.size() == 1) {// 说明只有一个图片.默认全部选中
                for (int i = 0; i < tips.length; i++) {
                    tips[i].setChecked(true);
                    j = i;
                }
            } else if (imageUrls.size() == 2 || imageUrls.size() == 3) {
                if (selectItems < imageViews.size() / 2) {
                    for (int i = 0; i < tips.length; i++) {
                        if (i == selectItems) {
                            tips[i].setChecked(true);
                            j = i;
                        }
                    }
                } else {
                    for (int i = 0; i < tips.length; i++) {
                        if (i == selectItems % imageUrls.size()) {
                            tips[i].setChecked(true);
                            j = i;
                        }
                    }
                }
            } else {
                for (int i = 0; i < tips.length; i++) {
                    if (i == selectItems) {
                        tips[i].setChecked(true);
                        j = i;
                    }
                }
            }
            return j;
        }

    }

    public void setTakeTurnsHeight(int height) {
        root.setMinimumHeight(height);
    }

    /**
     * 轮番图当前显示的图片接口
     */
    public interface UpdateUI {
        void onUpdateUI(int position, ImageView imageView);
    }

    //跟随activity的生命周期
    public void onPause() {
        Contant.isRun = false;
        mHandler.removeCallbacksAndMessages(null);
    }

    //跟随activity的生命周期
    public void onResume() {
        Contant.isRun = true;
        mHandler.sendEmptyMessageDelayed(0, sleepTime);
    }
}
