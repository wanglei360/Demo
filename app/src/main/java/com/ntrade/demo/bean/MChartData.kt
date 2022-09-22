package com.ntrade.demo.bean

/** * 创建者：leiwu
 * * 时间：2022/9/20 08:41
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：
 */
data class MChartData(
    val maxNum: Int,//图的刻度要根据这个值
    val point: Int,//每个点的值，最大不能超过maxNum
    val bottomStr: String,//每个点下面显示的文本
)