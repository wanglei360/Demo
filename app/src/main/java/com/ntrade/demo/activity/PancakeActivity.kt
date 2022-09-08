package com.ntrade.demo.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.ntrade.demo.databinding.ActivityPancakeLayoutBinding

/** * 创建者：leiwu
 * * 时间：2022/8/31 09:41
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：
 */
class PancakeActivity : Activity() {

    private val binding by lazy { ActivityPancakeLayoutBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    fun btnClick1(v: View) {
//        binding.waveView.startAnimation();
    }

    fun btnClick(v: View) {
        ArrayList<Int>().apply {
            var x = 0
            var b = true
            while (b) {
                val y = getRanNumber1(4, 20)
                if (x + y > 100) {
                    b = false
                    val num = get(size - 1) + (100 - x)
                    removeAt(size - 1)
                    add(num)
                } else {
                    x += y
                    add(y)
                }
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