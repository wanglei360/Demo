package com.ntrade.demo.activity

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import com.ntrade.demo.databinding.ActivityBesselCurveLayoutBinding
import com.ntrade.demo.pop.WPopup
import com.ntrade.demo.pop.WPopupModel
import com.ntrade.demo.view.chart.MChartData

/** * 创建者：leiwu
 * * 时间：2022/8/26 09:46
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：
 */
class BesselCurveActivity : Activity() {

    private val binding by lazy { ActivityBesselCurveLayoutBinding.inflate(layoutInflater) }
    private var scrollY = 0
    private var mChartY = 0
    private var wPopup: WPopup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.mChart.setOnItemClickListener { position, isDown, pointX, pointY ->
                if (isDown)
                    showPopupWindow(position, pointX, pointY)
                else wPopup?.dismiss()
            }
            binding.mSv.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                this@BesselCurveActivity.scrollY = scrollY
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        mChartY = binding.topView.height
    }

    private fun showPopupWindow(position: Int, pointX: Float, pointY: Float) {
        val poputData = ArrayList<WPopupModel>()
        poputData.add(WPopupModel(list[position].bottomStr))
        WPopup.Builder(this)
            .setData(poputData)    // 设置数据，数据类为WPopupModel
            .setCancelable(true) // 设置是否能点击外面dismiss
            .setIsShowTriangle(false)
            .setPopupOrientation(WPopup.Builder.VERTICAL)   // 设置item排列方向 默认为竖向排列
            .create().also {
                it.showAtLocation(
                    binding.root,
                    Gravity.NO_GRAVITY,
                    pointX.toInt(),
                    pointY.toInt() - scrollY + mChartY
                )
                wPopup = it
            }
    }

    fun btnClick1(v: View) {
    }

    private var list = ArrayList<MChartData>()
    fun btnClick(v: View) {
        val maxNum = 333
        ArrayList<MChartData>().apply {
            for (x in 0..88) {
                val num = getRanNumber1(0, maxNum)
                add(MChartData(maxNum, num, "  $num  "))
            }
        }.apply {
            add(2, MChartData(maxNum, 0, "  0  "))
            add(MChartData(maxNum, 0, "  0  "))
            binding.mChart.setDatas(this)
            var str = ""
            var ssr = ""
            forEach {
                str += "${it.point},"
            }
            var sss = " $str \n  $ssr"
            sss += sss
            sss += sss
            sss += sss
            sss += sss
            sss += sss
            binding.tv.text = sss
            list.clear()
            list.addAll(this)
        }
    }


    /**
     * 获取从 startNum 到 endNum 指定范围的int类型随机整数
     * @return int 类型的数字
     */
    fun getRanNumber1(startNum: Int, endNum: Int): Int {
        return (startNum + (Math.random() * (endNum - startNum))).toInt()
    }

    fun getRanNumber2(startNum: Int, endNum: Int): Int {
        var num = (startNum + (Math.random() * (endNum - startNum))).toInt()
        if (num > endNum * 0.6f) num /= 2
        return num
    }
}