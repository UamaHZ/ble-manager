package cn.com.uama.blemanager

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import io.reactivex.disposables.Disposable

/**
 * Created by liwei on 2018/4/9 20:40
 * Email: liwei@uama.com.cn
 * Description:
 */
/**
 * 启动蓝牙结果事件
 */
class EnableBTResultEvent(val enabled: Boolean)

/**
 * 请求定位权限结果事件
 */
class LocationPermissionResultEvent(val granted: Boolean)

interface Callback {
    fun connected()
    fun failed()
}

class BleManager(private val context: Context) {
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val handler = Handler()

    companion object {
        const val SCAN_PERIOD = 3000L
    }

    fun scan(uuid: String, major: Int, minor: Int, callback: Callback) {
        // 判断设备是否支持 BLE
        if (!hasBLEFeature()) {
            Toast.makeText(context, "设备不支持 BLE", Toast.LENGTH_SHORT).show()
            return
        }

        // 判断蓝牙是否已经开启
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            lateinit var disposable: Disposable
            disposable = RxBus.toFlowable(EnableBTResultEvent::class.java)
                    .map { it.enabled }
                    .subscribe {
                        if (it) scan(uuid, major, minor, callback)
                        disposable.dispose()
                    }

            // 启动一个透明的 activity 来启动蓝牙
            val intent = Intent(context, EnableBluetoothActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            return
        }

        // 判断是否有定位权限
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            lateinit var disposable: Disposable
            disposable = RxBus.toObservable(LocationPermissionResultEvent::class.java)
                    .map { it.granted }
                    .subscribe {
                        if (it) {
                            scan(uuid, major, minor, callback)
                        } else {
                            Toast.makeText(context, "没有定位权限无法进行蓝牙扫描操作", Toast.LENGTH_SHORT).show()
                        }
                        disposable.dispose()
                    }

            // 启动一个透明的 activity 来申请权限
            val intent = Intent(context, RequestLocationPermissionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            return
        }

        // 开始执行扫描逻辑
        val leScanCallback = BluetoothAdapter.LeScanCallback {device, rssi, scanRecord ->
            // TODO: 对比看是否有要扫描的设备
            Log.d("BleManager", "扫描到设备：${device.address}")
        }
        // SCAN_PERIOD 时间之后如果仍未搜索到，停止搜索
        handler.postDelayed({
            bluetoothAdapter.stopLeScan(leScanCallback)
        }, SCAN_PERIOD)
        bluetoothAdapter.startLeScan(leScanCallback)
    }

    private fun hasBLEFeature(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
    }
}