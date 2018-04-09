package cn.com.uama.blemanager

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.widget.Toast

/**
 * Created by liwei on 2018/4/9 20:40
 * Email: liwei@uama.com.cn
 * Description:
 */

interface Callback {
    fun connected()
    fun failed()
}

class BleManager(private val context: Context) {
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter

    fun scan(uuid: String, major: Int, minor: Int, callback: Callback) {
        // 判断设备是否支持 BLE
        if (!hasBLEFeature()) {
            Toast.makeText(context, "设备不支持 BLE", Toast.LENGTH_SHORT).show()
            return
        }

        // 判断蓝牙是否已经开启
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            // 启动一个透明的 activity 来启动蓝牙
            context.startActivity(Intent(context, EnableBluetoothActivity::class.java))
            return
        }

        // 判断是否有定位权限
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: 启动一个透明的 activity 来申请权限
            return
        }
    }

    private fun hasBLEFeature(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
    }
}