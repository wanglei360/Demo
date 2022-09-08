package com.ntrade.demo

import android.app.Application
import com.baidu.mapapi.SDKInitializer

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

        SDKInitializer.setAgreePrivacy(applicationContext, true)
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(applicationContext)
    }

}