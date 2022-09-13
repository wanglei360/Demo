package com.ntrade.demo.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ntrade.demo.databinding.ActivityBesselCurveLayoutBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.mChart.setOnItemClickListener { position, isDown ->
            val s = if (position != -1)
                list[position] else ""
            Log.i("asdf", "position = $position   content = $s    isDown = $isDown")
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