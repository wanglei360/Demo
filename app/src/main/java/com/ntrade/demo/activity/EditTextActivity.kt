package com.ntrade.demo.activity

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.ntrade.demo.R
import com.ntrade.demo.databinding.ActivityEdittextLayoutBinding

/** * 创建者：leiwu
 * * 时间：2022/6/20 11:02
 * * 类描述：
 * * 修改人：
 * * 修改时间：sign up
 * * 修改备注：不好用，如果有这样的需求还是自己写一个吧
 */
class EditTextActivity : AppCompatActivity() {


    val binding by lazy { ActivityEdittextLayoutBinding.inflate(layoutInflater) }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.passwordInputEditText.addTextChangedListener {
            it.toString().apply {
                binding.passwordInputLayout.error = when {
                    isEmpty() -> null
                    isNum(this) -> null
                    else -> "显示不对"
                }
            }
        }
        binding.passwordInputLayout.boxBackgroundColor = getColor(R.color.line_1966ff)
    }

    private fun isNum(str: String): Boolean {
        return try {
            str.toLong()
            true
        } catch (e: Exception) {
            false
        }
    }
}