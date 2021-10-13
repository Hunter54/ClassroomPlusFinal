package com.ionutv.classroomplus.services

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.os.ParcelUuid
import com.ionutv.classroomplus.Constants
import com.ionutv.classroomplus.utils.RandomUtils
import java.nio.charset.Charset

class BLEAdvertiserService(
    private val _bluetoothAdapter: BluetoothAdapter,
    bleCallbacks: BLEAdvertiseCallbacks
) {
    val bluetoothAdapter: BluetoothAdapter
        get() = _bluetoothAdapter

    private val bleAdvertiser: BluetoothLeAdvertiser?
        get() = _bluetoothAdapter.bluetoothLeAdvertiser
    var isAdvertising = false
        private set
    private val advertiseCallback = bleCallbacks.advertiseCallback

    var advertiseData = RandomUtils.generateRandomString(8)
        private set

    fun generateNewAdvertiseData() {
        advertiseData = RandomUtils.generateRandomString(8)
    }

    fun startBLEAdvertiser() {
        if (isAdvertising) {
            stopAdvertising()
        }
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(false)
            .build()
        val pUuid = ParcelUuid.fromString(Constants.BLE_UID)
        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .addServiceUuid(pUuid)
            .addServiceData(pUuid, advertiseData.toByteArray(Charset.defaultCharset()))
            .build()
        bleAdvertiser?.startAdvertising(settings, data, advertiseCallback)
        isAdvertising = true
    }

    fun stopAdvertising() {
        bleAdvertiser?.let {
            it.stopAdvertising(advertiseCallback)
            isAdvertising = false
        }

    }

    interface BLEAdvertiseCallbacks {
        val advertiseCallback: AdvertiseCallback
    }
}