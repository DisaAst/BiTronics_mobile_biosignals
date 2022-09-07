package com.bitronics.bitronicsmobilebiosignals.data

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


    fun startScan(): Flow<ArrayList<BleDevice>> = flow {
        var state = 0
        val devices = ArrayList<BleDevice>()

        BleManager.getInstance().scan(object : BleScanCallback() {
            override fun onScanStarted(success: Boolean) {
                Log.e("Scan", "Start")
            }

            override fun onLeScan(bleDevice: BleDevice) {
                super.onLeScan(bleDevice)
            }

            override fun onScanning(bleDevice: BleDevice) {
                if(bleDevice.name == "BiTronicsLU") devices.add(bleDevice)
            }

            override fun onScanFinished(scanResultList: List<BleDevice>) {
                Log.e("Scan result", scanResultList.toString())
                state = 1
                Log.e("St", state.toString())
            }
        })
        while (true) {
            Log.d("W", "Work")
            if (state == 1) {
                Log.e("Status", "Data received")
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