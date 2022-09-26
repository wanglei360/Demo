package com.ntrade.demo.bean

/**  创建者：leiwu
 * <p> 时间：2022/9/26 10:19
 * <p> 类描述：
 * <p> 修改人：
 * <p> 修改时间：
 * <p> 修改备注：
 */
class HandicapData(
    val num: Float,
    val leftStr: String,
    val rightStr: String,
) : Comparable<HandicapData> {
    override fun compareTo(bean: HandicapData): Int {
        return ((num - bean.num) * 100).toInt()
    }
}
