package com.ionutv.classroomplus.services

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.os.ParcelUuid
import com.ionutv.classroomplus.Constants
import java.nio.charset.Charset
import java.util.*

class BLEScanService(
    private val bluetoothAdapter: BluetoothAdapter,
    bleCallbacks: BLEScanCallbacks,
    private val bleDeviceName: String
    ) {



    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    var isScanning = false
        private set

    private val scanCallback = bleCallbacks.scanCallback

    fun isBluetoothEnabled() : Boolean = bluetoothAdapter.isEnabled

    fun startBLEScan() {
        val filter = ScanFilter.Builder().setDeviceName(bleDeviceName)
            .build()
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        bleScanner.startScan(mutableListOf(filter), scanSettings, scanCallback)
        isScanning = true
    }

    fun stopScan() {
        bleScanner.stopScan(scanCallback)
        isScanning = false
    }

    fun decodeScanResult(result: ScanResult): String {
        val data = result.scanRecord?.let {
            val byteData = it.serviceData
            var data = ""
            val pUuid = ParcelUuid(UUID.fromString(Constants.BLE_UID))
            byteData[pUuid]?.let { byteArray ->
                data += String(byteArray, Charset.defaultCharset())
            }
            data
        } ?: "EMPTY DATA"

        return data
    }

    interface BLEScanCallbacks {
        val scanCallback: ScanCallback
    }
}