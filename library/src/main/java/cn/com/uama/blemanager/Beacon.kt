package cn.com.uama.blemanager

import android.bluetooth.BluetoothDevice

/**
 * Created by liwei on 2018/4/10 16:07
 * Email: liwei@uama.com.cn
 * Description: Beancon 表示类及其工厂
 */
class Beacon {
    var name: String? = null
    var address: String? = null
    var uuid: String? = null
    var major: Int? = null
    var minor: Int? = null
    var power: Int? = null
    var rssi: Int? = null
}

object BeaconFactory {
    fun createBeacon(device: BluetoothDevice, rssi: Int, scanData: ByteArray): Beacon {
        var startByte = 2
        var patternFound = false
        while (startByte <= 5) {
            if ((scanData[startByte + 2].toInt() and 0xff) == 0x02
                    && (scanData[startByte + 3].toInt() and 0xff) == 0x15) {
                patternFound = true
                break
            } else if (scanData[startByte].toInt() and 0xff == 0x2d
                    && scanData[startByte + 1].toInt() and 0xff == 0x24
                    && scanData[startByte + 2].toInt() and 0xff == 0xbf
                    && scanData[startByte + 3].toInt() and 0xff == 0x16) {
                return defaultBeacon()
            } else if (scanData[startByte].toInt() and 0xff == 0xad
                    && scanData[startByte + 1].toInt() and 0xff == 0x77
                    && scanData[startByte + 2].toInt() and 0xff == 0x00
                    && scanData[startByte + 3].toInt() and 0xff == 0xc6) {
                return defaultBeacon()
            }

            startByte++
        }

        if (!patternFound) return Beacon()

        val beacon = Beacon()
        beacon.name = device.name
        beacon.address = device.address
        beacon.rssi = rssi
        beacon.major = (scanData[startByte + 20].toInt() and 0xff) * 0x100 +
                (scanData[startByte + 21].toInt() and 0xff)
        beacon.minor = (scanData[startByte + 22].toInt() and 0xff) * 0x100 +
                (scanData[startByte + 23].toInt() and 0xff)
        beacon.power = scanData[startByte + 24].toInt()

        // 表示 uuid 的字节数组
        val uuidBytes = scanData.copyOfRange(startByte + 4, startByte + 20)
        // 将 uuid 的所有字节以两位十六进制（不足两位高位补零）的形式表示并拼接成一个字符串
        val uuidHexStr = uuidBytes.joinToString("") { "%02x".format(it.toInt() and 0xff)}
        // 转换成标准的 uuid 形式并赋值
        beacon.uuid = StringBuilder()
                .append(uuidHexStr.substring(0, 8))
                .append("-")
                .append(uuidHexStr.substring(8, 12))
                .append("-")
                .append(uuidHexStr.substring(12, 16))
                .append("-")
                .append(uuidHexStr.substring(16, 20))
                .append("-")
                .append(uuidHexStr.substring(20, 32))
                .toString()

        return beacon
    }

    private fun defaultBeacon(): Beacon {
        val beacon = Beacon()
        beacon.major = 0
        beacon.minor = 0
        beacon.uuid = "00000000-0000-0000-0000-000000000000"
        beacon.power = -55
        return beacon
    }
}
