package cn.com.uama.blemanager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity

class RequestLocationPermissionActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            AlertDialog.Builder(this)
                    .setMessage("我们需要定位权限才能进行扫描蓝牙操作")
                    .setPositiveButton("好的") { _, _ ->
                        requestPermission()
                    }
                    .setCancelable(false)
                    .show()
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_LOCATION_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                RxBus.send(LocationPermissionResultEvent(true))
            } else {
                RxBus.send(LocationPermissionResultEvent(false))
            }
            finish()
        }
    }
}
