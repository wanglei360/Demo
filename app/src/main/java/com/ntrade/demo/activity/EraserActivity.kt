package com.ntrade.demo.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ntrade.demo.databinding.ActivityTestBinding

/** * 创建者：leiwu
 * * 时间：2022/10/8 11:13
 * * 类描述：橡皮擦
 * * 修改人：
 * * 修改时间：
 * * 修改备注：
 */
class EraserActivity : AppCompatActivity() {

    val binding by lazy { ActivityTestBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }
    fun testBtn(v: View){
        binding.tsetView.restore()
    }
}