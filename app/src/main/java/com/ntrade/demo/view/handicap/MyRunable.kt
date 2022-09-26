package com.ntrade.demo.view.handicap

import java.lang.ref.WeakReference

/** * 创建者：leiwu
 * * 时间：2022/9/26 10:05
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：
 */
class MyRunable : Runnable {
    private var mInterface: WeakReference<MyInterface>? = null

    constructor(mInterface: MyInterface) {
        this.mInterface = WeakReference(mInterface)
    }

    override fun run() {
        mInterface?.apply {
            get()?.doSomething()
        }
    }
}

interface MyInterface {
    fun doSomething()
}