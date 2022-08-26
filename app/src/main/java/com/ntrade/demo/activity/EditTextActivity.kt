package com.ntrade.demo.activity

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils.isEmpty
import android.text.TextWatcher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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

        binding.passwordInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s.toString().apply {
                    binding.passwordInputLayout.error = when {
                        isEmpty() -> null
                        isNum(this) -> null
                        else -> "显示不对"
                    }
                }
            }
        })
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