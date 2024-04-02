package com.bitronics.bitronicsmobilebiosignals.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Context
import android.util.Log
import com.bitronics.bitronicsmobilebiosignals.MainActivity.Companion.deviceC
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class MainRepository @Inject constructor(val context: Context, val parser: Parser) {

    val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    var dataSensor = doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0)

    var sensorType = 0

    var statusConnected = false

    var connected = false

    init {
        BleManager.getInstance().init(getApplication(context))
        BleManager.getInstance()
            .enableLog(true)
            .setReConnectCount(1, 5000)
            .setSplitWriteNum(20)
            .setConnectOverTime(10000).operateTimeout = 5000
    }


    @SuppressLint("MissingPermission")
    fun startScan(): Flow<ArrayList<BluetoothDevice>> = flow {
        val devices = ArrayList<BluetoothDevice>()
        bluetoothAdapter.startDiscovery()
        while (true) {
            val bondedDevices = bluetoothAdapter.bondedDevices
            bondedDevices?.forEach { device ->
                if (device.name == "YourDeviceName") {
                    devices.add(device)
                }
            }
            if (!devices.isEmpty()) {
                emit(devices)
                break
            }
        }
    }.flowOn(Dispatchers.IO)


    fun connect(bleDevice: BleDevice): Flow<Boolean> = flow {
        var state = 0
        BleManager.getInstance().connect(bleDevice, object : BleGattCallback() {
            override fun onStartConnect() {
                Log.e("Start connect", "connect start")
            }

            override fun onConnectFail(bleDevice: BleDevice, exception: BleException) {
                Log.e("Fail connect", exception.toString())
                connected = false
                state = 1
            }

            override fun onConnectSuccess(bleDevice: BleDevice, gatt: BluetoothGatt, status: Int) {
                Log.e("Connect", "connect finished")
                startNotify(bleDevice)
                connected = true
                statusConnected = true
                deviceC = bleDevice
                state = 1
            }

            override fun onDisConnected(
                isActiveDisConnected: Boolean,
                bleDevice: BleDevice,
                gatt: BluetoothGatt,
                status: Int
            ) {
                statusConnected = if (isActiveDisConnected) false
                else false
            }
        })
        while (true) {
            Log.d("Status", statusConnected.toString())
            if (state == 1) {
                emit(connected)
                break
            }
        }
    }.flowOn(Dispatchers.IO)

    fun disconnect() {
        BleManager.getInstance().disconnectAllDevice()
        connected = false
        statusConnected = false
    }

    fun startNotify(bleDevice: BleDevice) {
        BleManager.getInstance().notify(
            bleDevice,
            uuid_service,
            uuid_characteristic_notify,
            object : BleNotifyCallback() {
                override fun onNotifySuccess() {
                    Log.d("Status", "Start notify")
                }

                override fun onNotifyFailure(exception: BleException) {}
                override fun onCharacteristicChanged(data: ByteArray) {
                    if (data.size == 20) {
                        if (data[3].toInt() == 127) {
                            sensorType = parser.getType(data[2])
                            dataSensor[0] = parser.parse(data[0], data[1]).toDouble()
                            dataSensor[1] = parser.parse(data[4], data[5]).toDouble()
                            dataSensor[2] = parser.parse(data[8], data[9]).toDouble()
                            dataSensor[3] = parser.parse(data[12], data[13]).toDouble()
                            dataSensor[4] = parser.parse(data[16], data[17]).toDouble()

                            //Log.e("Data", dataSensor.toString())
                        } else Log.e("Data", "Kukan")
                    } else Log.e("Data", "Kukan")

                }
            })
    }

    fun fetchSensorType() = sensorType


    fun fetchDataSensor() = dataSensor


    fun isConnected() = statusConnected


}