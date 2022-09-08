package com.ntrade.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.ntrade.demo.R
import com.ntrade.demo.databinding.ActivitySystemServiceLocationBinding
import java.util.*

/** * 创建者：leiwu
 * * 时间：2022/9/7 13:02
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：activity_system_service_location
 */
class SystemServiceLocationActivity : AppCompatActivity() {

    val binding by lazy { ActivitySystemServiceLocationBinding.inflate(layoutInflater) }
    private var locationManager: LocationManager? = null
    private val list = ArrayList<LatLng>()
    private var dataSize = 0
    private var num = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initSystemService()
    }

    override fun onResume() {
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        binding.bmapView.onResume()
        super.onResume()
    }

    override fun onPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        binding.bmapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        binding.bmapView.onDestroy()
        locationManager?.removeUpdates(listener)
        super.onDestroy()
    }

    private fun initSystemService() {
        (getSystemService(Context.LOCATION_SERVICE) as LocationManager).apply {
            locationManager = this
            setRequestLocationUpdates()
        }
    }

    /**
     * 因为 requestLocationUpdates 方法的第一个参数，只使用GPS的有时会没有回调，所以 GPS WiFi 同时使用
     */
    @SuppressLint("MissingPermission")
    private fun LocationManager.setRequestLocationUpdates() {
        // 产生位置改变事件的条件设定为距离改变10米，时间间隔为2秒，设定监听位置变化
        requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 10f, listener)
        requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000L, 10f, listener)
    }

    private val listener by lazy {
        LocationListener {
            getLocationAddress(this@SystemServiceLocationActivity, it).apply {
                binding.pb.visibility = View.GONE
                if (list.isNotEmpty()) {
                    list[list.size - 1].also { ll ->
                        if (ll.latitude != latitude && ll.longitude != longitude) {
                            list.add(this)
                        }
                    }
                } else list.add(this)
                if (list.size != dataSize) {
                    setLocation(list[list.size - 1])
                    zoom(18f)
                    if (list.size > 2)
                        drawLine(list)

                    list[list.size - 1].also { address ->
                        log("纬度 = ${address.latitude}   精度 = ${address.longitude}")
                    }

                    setBitMap(list[list.size - 1])
                    dataSize = list.size
                    binding.btn.text =
                        "${list[list.size - 1].latitude}:${list[list.size - 1].longitude}"
                }
            }
        }
    }

    private fun log(msg: String) {
        Log.d("asdfrwe", msg)
        Toast.makeText(
            this@SystemServiceLocationActivity, msg, Toast.LENGTH_LONG
        ).show()
    }

    private fun setLocation(latLng: LatLng) {
        val builder = MapStatus.Builder()
        builder.target(latLng)
        binding.bmapView.map.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
    }

    private fun zoom(zoom: Float) {
        binding.bmapView.map.setMapStatus(MapStatusUpdateFactory.zoomTo(zoom))
    }

    private fun setBitMap(point: LatLng) {
        //定义Maker坐标点
//        val point = LatLng(39.963175, 116.400244)
        //构建Marker图标
        val bitmap = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_gcoding)
        //构建MarkerOption，用于在地图上添加Marker
        val option: OverlayOptions = MarkerOptions()
            .position(point)
            .icon(bitmap)
        //在地图上添加Marker，并显示
        binding.bmapView.map.addOverlay(option)
    }

    private fun drawLine(list: ArrayList<LatLng>) {
        binding.bmapView.map.addOverlay(
            PolylineOptions()
                .width(10)
                .color(-0x55010000)
                .points(list)
        )
    }

    private fun getLocationAddress(context: Context, location: Location): LatLng {
        return Geocoder(context, Locale.CHINESE).getFromLocation(
            location.latitude, location.longitude,
            1
        ).let {
            LatLng(it[0].latitude, it[0].longitude)
        }
    }
}