package com.example.viewpagerdemo.view;


import android.content.Context;
import android.graphics.drawable.Drawable;
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

    private Handler mHandler;//消息队列，用于自动轮播

    private int sleepTime = 3000;//默认轮番时间为3s一轮播

    public int getSleepTime() {//设置轮播时间
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    /**
     * 资源集合
     */
    private List<Drawable> imageDataUrls;

    /**
     * 控制viewpager的滑动速度
     */
    private FixedSpeedScroller scroller;

    private int fixedTime = 200;//设置viewpager的滑动速度为200ms

    private UpdateUI updateUI;//viewpager中ui的更新回调接口

    public UpdateUI getUpdateUI() {
        return updateUI;
    }

    public void setUpdateUI(UpdateUI updateUI) {
        this.updateUI = updateUI;
        if (getUpdateUI() != null && !imageViews.isEmpty())
            getUpdateUI().onUpdateUI(0);
    }

    public List<Drawable> getImageUrls() {
        return imageDataUrls;
    }

    /**
     * 传入数据，真正需要显示的数据都在这个方法中处理
     *
     * @param imageUrls
     */
    public void setImageUrls(List<Drawable> imageUrls) {
        setImageUrls(imageUrls, 0);
    }

    public void setImageUrls(List<Drawable> imageUrls, int drawableId) {
        //为null则不赋值
        if (imageUrls == null || imageUrls.isEmpty()) return;
        //若新传递进来的数据和原来的数据一样，则不处理
        //if (imageDataUrls != null && imageDataUrls.equals(imageUrls)) return;
        //更新数据
        imageDataUrls = imageUrls;
        //若数据长度一样，则不变，若长度不一样，重新赋值
        if (tips == null || tips.length != imageUrls.size()) {
            // 将点点加入到ViewGroup中
            tips = new RadioButton[imageUrls.size()];
            getRadioButton(drawableId);
        }
        //检测数据需不需要更新，只关心数据长度，若长度一样则只更新内容
        if (!checkData())
            //若数据不一样，则重新赋值
            getImageViews();

        //适配器在有了数据以后才创建
        if (pagerAdapter == null) {
            pagerAdapter = new MyAdapter();
            take_turns_view_pager.setInfinateAdapter(mHandler, pagerAdapter);
            take_turns_view_pager.setCurrentItem(imageViews.size() * 100);
        }

        //更新适配器数据
        pagerAdapter.setmImageViews(imageViews);
        //更新，这里不需要更新适配器
        //pagerAdapter.notifyDataSetChanged();
        //设置当前点点的位置
        tips[take_turns_view_pager.getCurrentItem() < imageDataUrls.size() ? take_turns_view_pager.getCurrentItem() : take_turns_view_pager.getCurrentItem() % imageDataUrls.size()].setChecked(true);
        //轮番开始
        if (mHandler != null) {
            Contant.isRun = true;
            mHandler.removeCallbacksAndMessages(null);
            mHandler.sendEmptyMessage(1);
        }
    }

    /**
     * 显示点点的布局
     *
     * @param drawableId
     */
    private void getRadioButton(int drawableId) {
        //添加之前清除点点
        take_turns_radio_group.removeAllViews();
        //radiobutton的布局
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //设置边距
        int margs = DisplayUtil.dip2px(getContext(), 3);
        layoutParams.setMargins(margs, margs, margs, margs);

        //radiobutton的样式
        drawableId = drawableId == 0 ? R.drawable.bg_page_item_tag : drawableId;

        //将点点添加到radiogroup中
        for (int i = 0; i < tips.length; i++) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setButtonDrawable(drawableId);
            radioButton.setLayoutParams(layoutParams);
            tips[i] = radioButton;
            take_turns_radio_group.addView(radioButton);
        }
    }

    /**
     * 将数据添加进inmageview中
     */
    private void getImageViews() {
        //清理数据
        imageViews.clear();

        //图片布局
        ViewGroup.LayoutParams imageLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        take_turns_view_pager.setNoScroll(false);
        // 将图片装载到数组中，这里集中处理，数据为1和数据为2/3的情况
        if (imageDataUrls.size() == 1) {
            for (int i = 0; i < 2; i++) {
                ImageView imageView = new ImageView(getContext());
                imageView.setId(0);
                imageView.setOnClickListener(new OnItemClickListener(0));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(imageLayoutParams);
                imageView.setImageDrawable(imageDataUrls.get(0));
                imageViews.add(imageView);
            }
            take_turns_view_pager.setNoScroll(true);
        } else if (imageDataUrls.size() == 2 || imageDataUrls.size() == 3) {
            for (int i = 0; i < imageDataUrls.size() * 2; i++) {
                ImageView imageView = new ImageView(getContext());
                int j;
                if (i > (imageDataUrls.size() - 1)) {
                    j = i - imageDataUrls.size();
                } else {
                    j = i;
                }
                imageView.setId(j);
                imageView.setOnClickListener(new OnItemClickListener(j));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(imageLayoutParams);
                imageView.setImageDrawable(imageDataUrls.get(j));
                imageViews.add(imageView);
            }
        } else {
            for (int i = 0; i < imageDataUrls.size(); i++) {
                ImageView imageView = new ImageView(getContext());
                imageView.setId(i);
                imageView.setOnClickListener(new OnItemClickListener(i));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(imageLayoutParams);
                imageView.setImageDrawable(imageDataUrls.get(i));
                imageViews.add(imageView);
            }
        }
    }

    //每一个imageviewd点击事件
    private class OnItemClickListener implements OnClickListener {
        private int viewId;

        OnItemClickListener(int viewId) {
            this.viewId = viewId;
        }

        @Override
        public void onClick(View v) {
            if (getUpdateUI() != null) {
                getUpdateUI().onItemClick(viewId, (ImageView) v);
            }
        }
    }

    //判断是否需要重新设置数据
    private boolean checkData() {
        if (imageViews == null || imageViews.isEmpty()) return false;
        // 将图片装载到数组中
        if (imageDataUrls.size() == 1) {
            if (imageViews.size() == 2) {
                for (ImageView imageView : imageViews) {
                    imageView.setImageDrawable(imageDataUrls.get(0));
                }
                return true;
            }
            return false;
        } else if (imageDataUrls.size() == 2 || imageDataUrls.size() == 3) {
            if (imageViews.size() == imageDataUrls.size() * 2) {
                for (int i = 0; i < imageViews.size(); i++) {
                    imageViews.get(i).setImageDrawable(imageDataUrls.get((i > (imageDataUrls.size() - 1)) ? i - imageDataUrls.size() : i));
                }
                return true;
            }
            return false;
        } else {
            if (imageViews.size() == imageDataUrls.size()) {
                for (int i = 0; i < imageViews.size(); i++) {
                    imageViews.get(i).setImageDrawable(imageDataUrls.get(i));
                }
                return true;
            }
            return false;
        }
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

        //消息队列接收消息后来通过代码使viewpager进行轮番
        //具体逻辑看代码
        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        if (imageDataUrls == null || imageDataUrls.size() <= 1) return;
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
     * 通过反射来重新设置viewpager的滑动速度
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
     * 外界调用，赋值viewpager的滑动速度
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


    /**
     * viewpager的滑动监听事件
     */
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
            //当viewpager显示出来的时候，将position传递给接口供外部调用
            if (imageViews == null || imageViews.size() < 1) return;
            int j = setImageBackground(position % imageViews.size());
            if (getUpdateUI() != null)
                getUpdateUI().onUpdateUI(j);
        }

        /**
         * 设置选中的tip的背景
         *
         * @param selectItems
         */
        private int setImageBackground(int selectItems) {
            int j = 0;
            if (imageDataUrls.size() == 1) {// 说明只有一个图片.默认全部选中
                for (int i = 0; i < tips.length; i++) {
                    tips[i].setChecked(true);
                    j = i;
                }
            } else if (imageDataUrls.size() == 2 || imageDataUrls.size() == 3) {
                if (selectItems < imageViews.size() / 2) {
                    for (int i = 0; i < tips.length; i++) {
                        if (i == selectItems) {
                            tips[i].setChecked(true);
                            j = i;
                        }
                    }
                } else {
                    for (int i = 0; i < tips.length; i++) {
                        if (i == selectItems % imageDataUrls.size()) {
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

    /**
     * 设置viewpager的高度
     *
     * @param height
     */
    public void setTakeTurnsHeight(int height) {
        root.setMinimumHeight(height);
    }

    /**
     * 轮番图当前显示的图片接口
     */
    public interface UpdateUI {
        //当前ui显示出来的位置
        void onUpdateUI(int position);

        //点击了当前页面
        void onItemClick(int position, ImageView imageView);
    }


    /**设置当前viewpager的ontouch事件，用于解决和外部ontouch的冲突
     * @param touchListener
     */
    public void setTouchListener(OnTouchListener touchListener) {
        if (take_turns_view_pager == null) return;
        take_turns_view_pager.setOnTouchListener(touchListener);
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
