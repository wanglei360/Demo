package com.ntrade.demo.view.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

/**
 * 创建者：leiwu
 * <p> 时间：2022/8/26 17:09
 * <p> 类描述：
 * <p> 修改人：
 * <p> 修改时间：
 * <p> 修改备注：
 */
public class WaveView extends View {

    private Paint paint;
    private Path path;

    //波长
    private int waveLength = 800;
    private int dx;
    private int dy;

    public WaveView(Context context) {
        super(context);

    }

    public WaveView(Context context,
                    @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        path = new Path();
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        path.reset();
        int originY = 500;
//        if(dy<originY + 150){
//            dy += 30;
//        }
        int halfWaveLength = waveLength / 2;
        path.moveTo(-waveLength + dx, originY - dy);

        //屏幕宽度里面画多少哥波长
        for (int i = -waveLength; i < getWidth() + waveLength; i += waveLength) {

            //二阶贝塞尔曲线1
            /**
             * 相对于起始点的增量
             */
            path.rQuadTo(halfWaveLength / 2, -150, halfWaveLength, 0);
            path.rQuadTo(halfWaveLength / 2, 150, halfWaveLength, 0);

        }
        //颜色填充
        //画一个封闭的空间
        path.lineTo(getWidth(), getHeight());
        path.lineTo(0, getHeight());
        path.close();
        canvas.drawPath(path, paint);

    }

    public void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofInt(0, waveLength);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        //无限循环
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dx = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.start();
    }

}