package com.ntrade.demo.activity

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import com.ntrade.demo.bean.MChartData
import com.ntrade.demo.databinding.ActivityBesselCurveLayoutBinding
import com.ntrade.demo.pop.WPopup
import com.ntrade.demo.pop.WPopupModel

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
    private var list = ArrayList<MChartData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.mChart1.setOnItemClickListener { position, isSel, pointX, pointY ->
                if (isSel)
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
        Log.d("showPopupWindow", "asdf = ${pointY.toInt() - scrollY + mChartY}")
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

    fun btnClick(v: View) {
        val maxNum = 100
        var str = ""
        list = ArrayList<MChartData>().apply {
            for (x in 0..20000) {
                val num = getRanNumber1(0, maxNum)
                add(MChartData(maxNum, num, "  $num  "))
                if (x < 200) str += "$num,"
            }
            binding.tv.text = "$str$str"
            binding.mChart1.setDatas(this)
        }
    }

    /**
     * 获取从 startNum 到 endNum 指定范围的int类型随机整数
     * @return int 类型的数字
     */
    fun getRanNumber1(startNum: Int, endNum: Int): Int {
        return (startNum + (Math.random() * (endNum - startNum))).toInt()
    }
}