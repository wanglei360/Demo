package com.ntrade.demo.view.bessel_curve;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;


/**
 * 创建者：leiwu
 * * 时间：2022/8/26 08:50
 * * 类描述：不拦截横向滑动的ScrollView
 * * 修改人：
 * * 修改时间：
 * * 修改备注：
 */
public class MScrollView extends NestedScrollView {

    private boolean isOldEvent;

    public MScrollView(@NonNull Context context) {
        super(context);
    }

    public MScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float downX;
    private float downY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isOldEvent) {
            return false;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = ev.getX();
                float moveY = ev.getY();
                float difX = Math.abs(moveX - downX);
                float difY = Math.abs(moveY - downY);
                if (difX > difY) {
                    downX = moveX;
                    downY = moveY;
                    isOldEvent = true;
                    return false;
                }
                downX = moveX;
                downY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                isOldEvent = false;
                break;
        }

        return super.dispatchTouchEvent(ev);
    }


}

