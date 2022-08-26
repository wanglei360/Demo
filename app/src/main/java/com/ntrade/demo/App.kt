package com.ntrade.demo

import android.app.Application

/** * 创建者：wanglei
 * <p>时间：2022/1/27 10:46
 * <p>类描述：
 * <p>修改人：
 * <p>修改时间：
 * <p>修改备注：
 */
class App : Application() {
    companion object {
        lateinit var context: App
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }

}