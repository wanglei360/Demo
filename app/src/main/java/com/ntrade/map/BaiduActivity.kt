package com.ntrade.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.ntrade.demo.R
import com.ntrade.demo.databinding.ActivityBaiduTestBinding
import java.util.ArrayList

/** * 创建者：leiwu
 * * 时间：2022/9/7 08:25
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：
 */
class BaiduActivity : AppCompatActivity() {

    private val binding by lazy { ActivityBaiduTestBinding.inflate(layoutInflater) }
    private var mLocationClient: LocationClient? = null
    private val list by lazy { ArrayList<LatLng>() }
    private var dataSize = 0
    private var num = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        LocationClient.setAgreePrivacy(true)

        //开启地图的定位图层
        binding.bmapView.map.isMyLocationEnabled = true
        mLocationClient = LocationClient(applicationContext).apply {
            binding.bmapView.map.setMyLocationConfiguration(getMyLocationConfiguration())
            setMLocOption()
            //注册LocationListener监听器
            registerLocationListener(MyLocationListener(foo = ::locationListener))
            //开启地图定位图层
            start()
        }
    }

    private fun getMyLocationConfiguration(): MyLocationConfiguration {
//        val mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;//定位跟随态
        val mCurrentMode =
            MyLocationConfiguration.LocationMode.NORMAL   //默认为 LocationMode.NORMAL 普通态
//        val mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;  //定位罗盘态
        return MyLocationConfiguration(mCurrentMode, true, null)
    }

    private fun locationListener(location: BDLocation?) {
        location?.apply {
            val locData: MyLocationData = MyLocationData.Builder()
                .accuracy(radius)
                .direction(direction)// 此处设置开发者获取到的方向信息，顺时针0-360
                .latitude(latitude)
                .longitude(longitude).build()
            binding.bmapView.map.setMyLocationData(locData)

            val llng = LatLng(latitude, longitude)
            if (list.isNotEmpty()) {
                list[list.size - 1].also { ll ->
                    if (ll.latitude != latitude && ll.longitude != longitude) {
                        list.add(llng)
                    }
                }
            } else list.add(llng)

            if (list.size != dataSize) {
                if (num < 2) {
                    setLocation(llng)
                    zoom(20f)
                }

                if (list.size > 2)
                    drawLine(list)

                dataSize = list.size
            }
            binding.btn.text =
                "${list[list.size - 1].latitude}:${list[list.size - 1].longitude}  方向：$direction  更新次数=${num++}"
        }
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
        binding.bmapView.map.clear()
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
        mLocationClient?.stop()
        binding.bmapView.map.isMyLocationEnabled = false;
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        binding.bmapView.onDestroy()
        super.onDestroy()
    }

    private fun LocationClient.setMLocOption() {
        LocationClientOption().apply {
            //可选，设置定位模式，默认高精度
            //LocationMode.Hight_Accuracy：高精度；
            //LocationMode.Battery_Saving：低功耗；
            //LocationMode.Device_Sensors：仅使用设备；
            //LocationMode.Fuzzy_Locating, 模糊定位模式；v9.2.8版本开始支持，可以降低API的调用频率，但同时也会降低定位精度；
            locationMode = LocationClientOption.LocationMode.Hight_Accuracy

            //可选，设置返回经纬度坐标类型，默认GCJ02
            //GCJ02：国测局坐标；
            //BD09ll：百度经纬度坐标；
            //BD09：百度墨卡托坐标；
            //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标
            setCoorType("bd09ll");

            //可选，首次定位时可以选择定位的返回是准确性优先还是速度优先，默认为速度优先
            //可以搭配setOnceLocation(Boolean isOnceLocation)单次定位接口使用，当设置为单次定位时，setFirstLocType接口中设置的类型即为单次定位使用的类型
            //FirstLocType.SPEED_IN_FIRST_LOC:速度优先，首次定位时会降低定位准确性，提升定位速度；
            //FirstLocType.ACCUARACY_IN_FIRST_LOC:准确性优先，首次定位时会降低速度，提升定位准确性；
            setFirstLocType(LocationClientOption.FirstLocType.SPEED_IN_FIRST_LOC)

            //可选，设置发起定位请求的间隔，int类型，单位ms
            //如果设置为0，则代表单次定位，即仅定位一次，默认为0
            //如果设置非0，需设置1000ms以上才有效
            setScanSpan(1000);

            //可选，设置是否使用gps，默认false
            //使用高精度和仅用设备两种定位模式的，参数必须设置为true
            isOpenGps = true;

            //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
            isLocationNotify = true

            //可选，定位SDK内部是一个service，并放到了独立进程。
            //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
            setIgnoreKillProcess(false)

            //可选，设置是否收集Crash信息，默认收集，即参数为false
            SetIgnoreCacheException(false)

            //可选，V7.2版本新增能力
            //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位
            setWifiCacheTimeOut(5 * 60 * 1000)

            //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
            setEnableSimulateGps(false)

            //可选，设置是否需要最新版本的地址信息。默认需要，即参数为true
            setNeedNewVersionRgc(true)

            // 可选，不设置，没有方向，一直返回-1
            setNeedDeviceDirect(true)

            //mLocationClient为第二步初始化过的LocationClient对象
            //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
            //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
            locOption = this
        }
    }

    class MyLocationListener(private val foo: (BDLocation?) -> Unit) :
        BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation?) {
            foo.invoke(location)
        }
    }
}