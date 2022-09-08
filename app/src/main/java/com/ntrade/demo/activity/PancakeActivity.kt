package com.ntrade.demo.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ntrade.demo.databinding.ActivityPancakeLayoutBinding
import com.ntrade.demo.tool.ToastUtil

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
        binding.mPancakeView.setItemClickListener { isSel, position ->
            if (isSel) {
                data?.apply {
                    Toast.makeText(this@PancakeActivity, "${data!![position]}", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    fun btnClick1(v: View) {
    }

    var data: ArrayList<Int>? = null
    fun btnClick(v: View) {
        ArrayList<Int>().apply {
            val num = getRanNumber1(4, 12)
            for (x in 0..num) {
                add(getRanNumber1(30, 80))
            }
        }.apply {
            data = this
            binding.mPancakeView.setDatas(this)
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