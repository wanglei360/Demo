package com.ntrade.demo.view.m_view_page

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration

/** * 创建者：wanglei
 * <p>时间：2022/1/27 10:59
 * <p>类描述：
 * <p>修改人：
 * <p>修改时间：
 * <p>修改备注：
 */
class MViewPage : View {
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        attrs?.initAttrs()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        attrs?.initAttrs()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w;
        mHeight = h;
    }

    private fun AttributeSet.initAttrs(){
        ViewConfiguration.get(context).apply {
            mMinimumVelocity = scaledMinimumFlingVelocity.toFloat()
            mMaximumVelocity = scaledMaximumFlingVelocity.toFloat()
        }
    }

    private var mWidth = 0//控件的宽
    private var mHeight = 0//控件的高
    var downX = 0f
    var downY = 0f
    private val flag by lazy { mWidth * 0.04f }
    var isIntercept: Boolean = true

    //    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
//        event?.apply {
//            when (action) {
//                MotionEvent.ACTION_DOWN -> {
//                    downX = x
//                    downY = y
//                    isIntercept = true
//                }
//                MotionEvent.ACTION_MOVE -> {
//
//                    val moveX = Math.abs(x - downX)
//                    val moveY = Math.abs(y - downY)
//                    isIntercept =
//                        when {
//                            !isIntercept -> false
//                            moveY > flag && moveX < flag -> false
//                            moveX > flag && moveY < flag -> true
//                            else -> true
//                        }
//                }
//            }
//        }
//        log("isIntercept = $isIntercept")
//        return isIntercept
//    }
//
//

    private var mTracker: VelocityTracker? = null
    private var mMinimumVelocity = 50f
    private var mMaximumVelocity = 8000f
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.apply {
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    if (null == mTracker)
                        mTracker = VelocityTracker.obtain();
                    else
                        mTracker!!.clear();
                    mTracker?.addMovement(event);
                }
                MotionEvent.ACTION_MOVE -> {
                    mTracker?.addMovement(event);
                }
                MotionEvent.ACTION_UP -> {
                    mTracker?.addMovement(event);
                    mTracker?.computeCurrentVelocity(1000, mMaximumVelocity);


                    // 根据速度判断是该滚动还是滑动
                    mTracker?.xVelocity?.apply {
                        if (Math.abs(this) > mMinimumVelocity) {
//                            mScroller.fling(0, mScrollOffsetY, 0, velocity, 0, 0, mMinFlingY, mMaxFlingY);
//                            mScroller.setFinalY(mScroller.getFinalY() +
//                                    computeDistanceToEndPoint(mScroller.getFinalY() % mItemHeight));
                        } else {
//                            mScroller.startScroll(0, mScrollOffsetY, 0,
//                                computeDistanceToEndPoint(mScrollOffsetY % mItemHeight));
                        }
                        log(",,,,  ${Math.abs(this) > mMinimumVelocity}     ${Math.abs(this)}    mMinimumVelocity = $mMinimumVelocity")
                    }
                    if (null != mTracker) {
                        mTracker?.recycle();
                        mTracker = null;
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (null != mTracker) {
                        mTracker?.recycle();
                        mTracker = null;
                    }
                }
            }
        }
        return true
    }

    private fun log(str: String) {
        Log.d("MViewPage", str)
    }

    /*
    ACTION_DOWN
                if (null == mTracker)
                    mTracker = VelocityTracker.obtain();
                else
                    mTracker.clear();
                mTracker.addMovement(event);

    ACTION_MOVE
          mTracker.addMovement(event);

    ACTION_UP
           mTracker.addMovement(event);
           mTracker.computeCurrentVelocity(1000, mMaximumVelocity);

            // 根据速度判断是该滚动还是滑动
            int velocity = (int) mTracker.getYVelocity();
            if (Math.abs(velocity) > mMinimumVelocity) {
                    mScroller.fling(0, mScrollOffsetY, 0, velocity, 0, 0, mMinFlingY, mMaxFlingY);
                    mScroller.setFinalY(mScroller.getFinalY() +
                            computeDistanceToEndPoint(mScroller.getFinalY() % mItemHeight));
                } else {
                    mScroller.startScroll(0, mScrollOffsetY, 0,
                            computeDistanceToEndPoint(mScrollOffsetY % mItemHeight));
                }
            if (null != mTracker) {
                    mTracker.recycle();
                    mTracker = null;
                }

    ACTION_CANCEL
        if (null != mTracker) {
                    mTracker.recycle();
                    mTracker = null;
                }

     */


}