package com.ntrade.demo.activity

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.ntrade.demo.R
import com.ntrade.demo.databinding.ActivitySoundPoolBinding
import com.ntrade.demo.databinding.ActivityViewBindingBinding
import com.ntrade.demo.tool.SoundPoolTool

/** * 创建者：leiwu
 * * 时间：2022/8/19 16:38
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：
 */
class SoundPoolActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySoundPoolBinding.inflate(layoutInflater) }

    private var soundPool: SoundPoolTool? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btn1.setOnClickListener {
            soundPool?.playSound(R.raw.select_button)
        }

        binding.btn2.setOnClickListener {
            soundPool?.playSound(R.raw.confirm)
        }

        initSoundPool()
    }

    override fun onDestroy() {
        soundPool?.release()
        super.onDestroy()
    }

    private fun initSoundPool() {
        soundPool = SoundPoolTool(
            soundIds = listOf(
                R.raw.select_button,
                R.raw.confirm
            )
        )
    }
}