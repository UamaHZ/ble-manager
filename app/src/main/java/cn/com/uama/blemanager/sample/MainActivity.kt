package cn.com.uama.blemanager.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import cn.com.uama.blemanager.BleManager

class MainActivity : AppCompatActivity() {

    private lateinit var bleManager: BleManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bleManager = BleManager(this)
    }

    fun startScan(view: View) {
        bleManager.scan("FDA50693-A4E2-4FB1-AFCF-C6EB07647907", 1234, 1234) {
            if (it) {
                Toast.makeText(this, "搜索到目标设备", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "没有搜索到目标设备", Toast.LENGTH_LONG).show()
            }
        }
    }
}
