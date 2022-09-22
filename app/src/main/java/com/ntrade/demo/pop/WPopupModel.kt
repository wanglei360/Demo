package com.ntrade.demo.pop

data class WPopupModel(
    var text: String ,//必填参数，显示的文字
    var imgRes: Int = -1,//非必填参数，显示的图片
    var switchText: String = "",//非必填参数，点击之后切换的文字
    var switchImgRes: Int = -1,//非必填参数，点击之后切换的图片
    var tag: Any? = null//后加的tag字段
)