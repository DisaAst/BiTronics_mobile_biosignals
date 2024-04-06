package com.bitronics.bitronicsmobilebiosignals.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.bitronics.bitronicsmobilebiosignals.MainActivity.Companion.deviceC
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException


class ConnectThread() {

    var socket: BluetoothSocket? = null
    private var device: BluetoothDevice? = null

    @SuppressLint("MissingPermission")
    suspend fun connect(bluetoothDevice: BluetoothDevice): Flow<Boolean> = flow {
        try {
            Log.e("Device", bluetoothDevice.name.toString())
            device = bluetoothDevice
            val method =
                device!!.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
            socket = method.invoke(device, 1) as BluetoothSocket
            socket?.connect()
            deviceC = bluetoothDevice
            emit(true)
        } catch (e: IOException) {
            Log.e("ConnectThread", "Failed to connect: ${e.message}")
            emit(false)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun disconnect(): Flow<Boolean> = flow {
        try {
            socket?.close()
            device = null
            emit(false)
        } catch (e: IOException) {
            emit(true)
            Log.e("ConnectThread", "Failed to close socket: ${e.message}")
        }
    }
}