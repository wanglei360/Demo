package com.ntrade.demo.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ntrade.demo.databinding.ActivityHorizontalScrollListBinding

/** * 创建者：wanglei
 * <p>时间：2022/1/27 11:01
 * <p>类描述：
 * <p>修改人：
 * <p>修改时间：
 * <p>修改备注：
 */
class HorizontalScrollListActivity : AppCompatActivity() {

    val binding by lazy { ActivityHorizontalScrollListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}