package com.ntrade.demo.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ntrade.demo.R
import com.ntrade.demo.databinding.ActivityColorGradientBinding
import com.ntrade.demo.tool.ColorUtils

/** * 创建者：leiwu
 * * 时间：2023/2/1 09:39
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：
 */
class ColorGradientActivity : AppCompatActivity() {

    val binding by lazy { ActivityColorGradientBinding.inflate(layoutInflater) }
    private val startColor by lazy { getColor(R.color.depth_sell_fill_color) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.acgTv.setBackgroundColor(startColor)
        binding.acgTv.setOnClickListener {
            ColorUtils.colorAnimator(
                3000L,
                startColor,
                getColor(R.color.text_cc0000)
            ) {
                binding.acgTv.setBackgroundColor(it)
            }
        }
    }
}