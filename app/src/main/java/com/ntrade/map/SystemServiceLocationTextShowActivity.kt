package com.ntrade.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ntrade.demo.databinding.ActivitySystemServiceLocationTextShowBinding
import java.util.*


/** * 创建者：leiwu
 * * 时间：2022/9/7 13:18
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：activity_system_service_location_text_show
 */
class SystemServiceLocationTextShowActivity : AppCompatActivity() {

    val binding by lazy { ActivitySystemServiceLocationTextShowBinding.inflate(layoutInflater) }
    private var locationManager: LocationManager? = null
    private val listener by lazy {
        LocationListener {
            getLocationAddress(this@SystemServiceLocationTextShowActivity, it).apply {
                binding.pb.visibility = View.GONE
                binding.assltsTv.text =
                    "$countryName $locality $subLocality $subAdminArea $subThoroughfare $featureName "
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initSystemService()
    }

    override fun onDestroy() {
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
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

    private fun log(msg: String) {
        Log.d("asdfrwe", msg)
        Toast.makeText(
            this@SystemServiceLocationTextShowActivity, msg, Toast.LENGTH_LONG
        ).show()
    }

    private fun getLocationAddress(context: Context, location: Location): Address {
        return Geocoder(context, Locale.CHINESE).getFromLocation(
            location.latitude, location.longitude,
            1
        )[0]
    }
}