package com.ntrade.demo.pop

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import java.lang.ref.WeakReference

data class WPopParams(
        val layoutRes: Int, // 布局
        val activity: Context, // activity
        var isDim: Boolean = false,  // 是否半透明
        var isShowTriangle: Boolean = true,//如果是竖着排列，是否显示三角型号
        var dimValue: Float = 0.4f, // 半透明属性
        var cancelable: Boolean = true, // 点击外部可以dismiss
        var width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
        var height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
) {
    var mCommonData: List<WPopupModel>? = null    // common的数据
    var mWItemClickListener: WPopup.Builder.OnItemClickListener? = null
    var commonPopupOrientation: Int = LinearLayoutManager.VERTICAL // pop方向
    var commonPopupDividerColor = Color.WHITE   // 分割线的颜色
    var commonPopupDividerSize = 1  // 分割线的粗细
    var commonPopupDividerMargin = 10   // 分割线的margin
    var commonPopupBgColor = Color.parseColor("#A5000000")
    var commonItemTextColor = Color.parseColor("#ffffff")
    var commonItemTextSize = 14
    var commonPopMargin = 1
    var commonIconDirection = WPopupDirection.LEFT   // 传入的图片的位置
    var commonDrawablePadding = 5
    var commonIsEnableChangeAnim = true    // 切换时是否启用动画
    var longClickView: WeakReference<View>? = null   //长按点击事件的View
    var animRes = WPopupAnim.ANIM_ALPHA    // 动画
}