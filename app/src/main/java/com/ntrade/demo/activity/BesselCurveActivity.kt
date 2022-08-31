package com.ntrade.demo.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.ntrade.demo.databinding.ActivityBesselCurveLayoutBinding

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
    }

    fun btnClick1(v: View) {
//        binding.waveView.startAnimation();
    }

    fun btnClick(v: View) {
        ArrayList<Int>().apply {
            for (x in 0..100) {
                add(getRanNumber1(0, 100))
            }
        }.apply {
            binding.mChart.setDatas(this)
            var str = ""
            var ssr = ""
            forEach {
                str += "${it},"
                ssr += "${360f / 100f * it}，"
            }
            binding.tv.text = " $str \n  $ssr"
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