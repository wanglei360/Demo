package com.ntrade.demo.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import com.ntrade.demo.R
import com.ntrade.demo.databinding.ActivityProgressButtonBinding
import com.ntrade.demo.tool.ToastUtil

/** * 创建者：wanglei
 * <p>时间：2022/1/27 10:33
 * <p>类描述：
 * <p>修改人：
 * <p>修改时间：
 * <p>修改备注：
 */
class ProgressButtonActivity : AppCompatActivity() {

    private var mProgress: Int = 0
    private var isContinue = false

    val handler by lazy {
        Handler(Looper.getMainLooper()) {
            when (it.what) {
                1 -> sendMsg()
                2 -> if (isContinue && mProgress < 100) {
                    binding.testView.setProgress(++mProgress)
                    binding.testTv.text = "$mProgress"
                    sendMsg()
                }
            }
            true
        }
    }

    val binding by lazy { ActivityProgressButtonBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.testBtn.setOnClickListener {
            mProgress = 0
            isContinue = true
            sendMsg()
        }

        binding.testBtnStop.setOnClickListener {
            isContinue = false
            binding.testView.setText("暂停")
        }
        binding.testBtnBackground.setOnClickListener {
            isContinue = false
            binding.testView.setBackgroundResId(R.color.color_grey)
        }
        binding.testView.setOnClickListener {
            ToastUtil.showMessage("asdfrwt")
        }
    }

    private fun sendMsg() {
        Message.obtain().apply {
            what = 2
            handler.sendMessageDelayed(this, 100)
        }
    }
}