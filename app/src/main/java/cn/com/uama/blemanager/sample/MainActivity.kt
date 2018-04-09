package cn.com.uama.blemanager.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import cn.com.uama.blemanager.BleManager
import cn.com.uama.blemanager.Callback

class MainActivity : AppCompatActivity() {

    private lateinit var bleManager: BleManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bleManager = BleManager(this)
    }

    fun startScan(view: View) {
        bleManager.scan("", 0, 0, object : Callback {
            override fun connected() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun failed() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }
}
