package com.ntrade.demo.view.chart

import android.content.Context
import android.util.AttributeSet
import android.view.View

/** * 创建者：leiwu
 * * 时间：2022/8/29 15:03
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：
 */
class PancakeView : View {


    private var mHeight = 0f//View的高
    private var mWidth = 0f//View的宽

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
//        initAttrs(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
//        initAttrs(attrs)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()//控件的宽
        mHeight = h.toFloat()//控件的高  TSW19740324
    }





}