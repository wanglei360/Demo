package com.ntrade.demo.view.scroll_view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ScrollView

/** * 创建者：wanglei
 * <p>时间：2022/1/27 11:33
 * <p>类描述：
 * <p>修改人：
 * <p>修改时间：
 * <p>修改备注：
 */
class MScrollView : ScrollView {
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w;
        mHeight = h;
    }

    private var mWidth = 0//控件的宽
    private var mHeight = 0//控件的高
    private var downX = 0f
    private var downY = 0f
    private val flag by lazy { mWidth * 0.04f }
    private var isIntercept: Boolean = true
    private var isMove = false
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        event?.apply {
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = x
                    downY = y
                    isIntercept = true
                }
                MotionEvent.ACTION_MOVE -> {
                    val moveX = Math.abs(x - downX)
                    val moveY = Math.abs(y - downY)

                    isIntercept =
                        when {
                            isMove -> isIntercept
                            moveY > flag && moveX < flag -> {
                                isMove = true
                                true
                            }
                            moveX > flag && moveY < flag -> {
                                isMove = true
                                false
                            }
                            moveY > flag && moveX > flag -> {
                                isMove = true
                                moveY > moveX
                            }
                            else -> true
                        }
                }
                MotionEvent.ACTION_UP -> isMove = false
            }
        }
        Log.d("MViewPage", "isIntercept = $isIntercept")
        return if (isIntercept) super.dispatchTouchEvent(event)
        else isIntercept
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.d("MViewPage", "onInterceptTouchEvent   isIntercept = $isIntercept")
        return isIntercept
    }

    private fun log(str: String) {
        Log.d("MViewPage", str)
    }
}