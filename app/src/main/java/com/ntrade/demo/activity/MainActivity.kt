package com.ntrade.demo.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.ntrade.demo.R
import com.ntrade.demo.adapter.MListAdapter
import com.ntrade.demo.databinding.ActivityMainBinding
import com.ntrade.demo.view.chart.MyChartView

class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ArrayList<String>().apply {
            add("view_binding_demo")// 0
            add("进度的按钮")// 1
            add("横着转圈滚动的List")// 2
            add("输入框")// 3
            add("按钮音效")// 4
            add("贝瑟尔曲线的图")// 5
            add("饼图")// 6
            binding.lv.adapter = MListAdapter(this) { position ->
                when (position) {
                    0 -> goToActivity(ViewBindingActivity::class.java)
                    1 -> goToActivity(ProgressButtonActivity::class.java)
                    2 -> goToActivity(HorizontalScrollListActivity::class.java)
                    3 -> goToActivity(EditTextActivity::class.java)
                    4 -> goToActivity(SoundPoolActivity::class.java)
                    5 -> goToActivity(BesselCurveActivity::class.java)
                    6 -> goToActivity(PancakeActivity::class.java)
                }
            }
        }
    }

    fun goToActivity(cls: Class<*>, bundle: Bundle? = null) {
        Intent(this, cls).apply {
            bundle?.let {
                putExtras(it)
            }
            startActivity(this)
        }
    }
}


