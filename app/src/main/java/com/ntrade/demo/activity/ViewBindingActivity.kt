package com.ntrade.demo.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ntrade.demo.R
import com.ntrade.demo.databinding.ActivityViewBindingBinding
import com.ntrade.demo.fragment.RecyclerViewFragment

/** * 创建者：leiwu
 * * 时间：2022/6/10 10:12
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：
 */
class ViewBindingActivity: AppCompatActivity() {

    private val binding by lazy { ActivityViewBindingBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.avbTv.setOnClickListener {
            Toast.makeText(this, "textView被点击了", Toast.LENGTH_SHORT).show()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.avb_frame_layout, RecyclerViewFragment())
            .commitAllowingStateLoss()
    }

}