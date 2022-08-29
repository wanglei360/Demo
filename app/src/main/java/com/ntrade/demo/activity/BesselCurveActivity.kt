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
class BesselCurveActivity  : Activity() {

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
//            for (x in 0..getRanNumber(8, 20)) {
            for (x in 0..8 ) {
                add(getRanNumber(0, 100))
            }
//            add(100)
//            add(20)
//            add(30)
//            add(40)
//            add(0)
//            add(70)
//            add(50)
//            add(56)
//            add(100)
//            add(80)
//            add(100)
        }.apply {
            binding.mChart.setData(this)

            var str = ""
            forEach {
                str += "${it},"
            }
            binding.tv.text = str
        }
    }


    /**
     * 获取从 startNum 到 endNum 指定范围的int类型随机整数
     * @return int 类型的数字
     */
    fun getRanNumber(startNum: Int, endNum: Int): Int {
        return (startNum + (Math.random() * (endNum - startNum))).toInt()
    }
}