package com.ntrade.map

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer
import com.ntrade.demo.databinding.ActivityMapBinding
import com.yanzhenjie.permission.AndPermission

/** * 创建者：leiwu
 * * 时间：2022/9/8 10:54
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 控制台：https://lbsyun.baidu.com/apiconsole/key#/home
 * * 鹰眼服务(serivce_id)：https://lbsyun.baidu.com/trace/admin/service
 * * 相关下载：https://lbsyun.baidu.com/index.php?title=android-yingyan/sdkandev-download
 * * 鹰眼轨迹SDK：https://lbsyun.baidu.com/index.php?title=android-yingyan/guide/introduction
 * * 基本概念（一些限制这里有些）：https://lbsyun.baidu.com/index.php?title=android-yingyan/guide/concept
 * * TODO API_KEY使用的值，百度的鉴权有的时候使用SHA1，有的时候使用MD5，真的傻逼，相同的两个demo，就出现了这个情况
 */
class MapActivity : AppCompatActivity() {

    val binding by lazy { ActivityMapBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        binding.amTv.text = sb
        binding.amTv1.text = "https://lbsyun.baidu.com/cashier/auth"
        binding.amTv2.text = sb2
        binding.amTv3.text = "https://lbs.amap.com/upgrade#business"
        binding.amTv4.text =
            "\n\n\nGoogle地图需要手机必须有Google play服务才能使用,否则无法使用(国产手机大多数Google play服务被阉割)。\nGoogle地图需要申请Google key,但在申请列表中没有中国和俄罗斯，所以如使用此方式，后续可能会出现不可控的问题"

        binding.amTv1.setOnClickListener {
            val uri: Uri = Uri.parse("https://lbsyun.baidu.com/cashier/auth")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        binding.amTv3.setOnClickListener {

            val uri: Uri = Uri.parse("https://lbs.amap.com/upgrade#business")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        applyPerission()
    }

    @SuppressLint("WrongConstant")
    private fun applyPerission() {
        AndPermission.with(this)
            .runtime()
            .permission(
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
            )
            .onGranted {
            }.onDenied {
            }.start()
    }

    fun BtnClick1(v: View) {
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL)
        Intent(this, BaiduActivity::class.java).apply {
            startActivity(this)
        }
    }

    fun BtnClick2(v: View) {
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.GCJ02)
        Intent(this, SystemServiceLocationActivity::class.java).apply {
            startActivity(this)
        }
    }

    fun BtnClick3(v: View) {
        Intent(this, SystemServiceLocationTextShowActivity::class.java).apply {
            startActivity(this)
        }
    }

    private val sb =
        StringBuilder("百度地图说明\n如您以商业目的（包括但不限于对第三方用户收费、项目投标，以及其他直接或间接获取收益或利益）使用本平台服务，则用户须要事先获得本平台\"商用授权\"许可\n\n")
            .append("授权类型：\n")
            .append("• 企业自用：用于开发者自主开发或运营的产品或服务\n")
            .append("• 项目专用：用于开发者向第三方交付的指定项目\n\n")
            .append("费用说明地址：")
            .toString()
    private val sb2 =
        StringBuilder("\n\n\n高德地图说明\n")
            .append("如果您需将高德地图开放平台服务用以商业目的（包括但不限于向第三方或公众用户收费、参与第三方项目投标、后台管理系统、以及任何其他直接或间接获取收益或利益的目的等），您需事先从高德获取商用授权。\n")
            .append("\n费用说明地址：")
            .toString()

}