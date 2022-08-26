package com.ntrade.demo.tool

import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.ntrade.demo.App
import com.ntrade.demo.R

/** * 创建者：wanglei
 * * 时间：2021/6/21 13:29
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：
 */
object ToastUtil {

    private val handler = Handler(Looper.getMainLooper())
    private var toast: Toast? = null

    fun showMessage(msg: String, len: Int = Toast.LENGTH_SHORT) {
        showMessage(null, msg, len)
    }

    fun showMessage(msg: Int, len: Int = Toast.LENGTH_SHORT) {
        showMessage(msg, null, len)
    }

    private fun setTestSize(mToast: Toast) {
        try {
            (mToast.view as ViewGroup).apply {
                (getChildAt(0) as TextView).apply {
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                }
            }
        } catch (e: Exception) {
        }
    }

    @Synchronized
    fun showMessage(msgInt: Int?, msgStr: String?, len: Int) {
        handler.post {
            synchronized(this.javaClass) {
                try {
                    toast?.apply { cancel() }

                    when {
                        msgInt != null -> Toast.makeText(App.context.applicationContext, msgInt, len)
                        msgStr != null -> Toast.makeText(App.context.applicationContext, msgStr, len)
                        else -> Toast.makeText(App.context.applicationContext, "", len)
                    }?.apply {
                        toast = this
//                        setTestSize(this)
//                        setGravity(Gravity.CENTER, 0, 0)
                        duration = len
                        show()
                    }
                } catch (e: Exception) {
                    try {
                        when {
                            msgInt != null -> {
                                Toast.makeText(
                                    App.context.applicationContext,
                                    msgInt,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            msgStr != null -> {
                                Toast.makeText(
                                    App.context.applicationContext,
                                    msgStr,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {
                                Toast.makeText(
                                    App.context.applicationContext,
                                    App.context.applicationContext.resources.getString(
                                        R.string.net_error_message_something_wrong
                                    ),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            App.context.applicationContext,
                            App.context.applicationContext.resources.getString(R.string.net_error_message_something_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}