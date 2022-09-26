package com.ntrade.demo.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ntrade.demo.bean.HandicapData
import com.ntrade.demo.databinding.ActivityHandicapBinding
import java.util.*
import kotlin.collections.ArrayList

/** * 创建者：leiwu
 * * 时间：2022/9/26 09:47
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：activity_handicap
 */
class HandicapActivity : AppCompatActivity() {

    val binding by lazy { ActivityHandicapBinding.inflate(layoutInflater) }

    private var b = false
    val list1 by lazy { ArrayList<HandicapData>() }
    val list2 by lazy { ArrayList<HandicapData>() }
    val handler = Handler(Looper.getMainLooper()) {
        if (b) setData()
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        b = false
        binding.handicap1.viewDestroy()
        binding.handicap2.viewDestroy()
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    fun btnClick(v: View) {
        if (!b) {
            b = !b
            setData()
        }
    }

    fun btnClick1(v: View) {
        if (b) {
            b = !b
            binding.handicap1.setDefaultDatas()
            binding.handicap2.setDefaultDatas()
        }
    }

    private fun setData() {
        handler.sendMessageDelayed(Message.obtain(), 100L)
        list1.clear()
        list2.clear()
        for (x in 0..19) {
            val num = getRanNumber1(10, 100)
            list1.add(HandicapData(num / 100f, "$num", "$num"))
        }
        for (x in 0..19) {
            val num = getRanNumber1(10, 100)
            list2.add(HandicapData(num / 100f, "$num", "$num"))
        }
        list1.sort()
        list2.sort()
        binding.handicap1.setDatas(list1)
        binding.handicap2.setDatas(list2)
    }

    /**
     * 获取从 startNum 到 endNum 指定范围的int类型随机整数
     * @return int 类型的数字
     */
    fun getRanNumber1(startNum: Int, endNum: Int): Int {
        return (startNum + (Math.random() * (endNum - startNum))).toInt()
    }
}