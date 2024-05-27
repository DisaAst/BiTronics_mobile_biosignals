package com.bitronics.bitronicsmobilebiosignals.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothSocket
import android.bluetooth.le.ScanCallback
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.util.Log
import android.widget.Toast
import com.bitronics.bitronicsmobilebiosignals.MainActivity.Companion.deviceC
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject


class MainRepository @Inject constructor(val context: Context) {

    val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    var connectThread = ConnectThread()
    var connectedThread: ConnectedThread? = null
    var sensorType = 0
    var statusConnected = false
    var connected = false

    @SuppressLint("MissingPermission")
    fun startScan(): Flow<ArrayList<BluetoothDevice>> = callbackFlow<ArrayList<BluetoothDevice>> {
        val devices = ArrayList<BluetoothDevice>()
        bluetoothAdapter.startDiscovery()
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.action
                when (action) {
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        trySendBlocking(devices)
                        cancel()
                    }
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        device?.let {
                            if (!devices.contains(device)) {
                                devices.add(device)
                            }
                        }
                    }
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        }
        context.registerReceiver(receiver, filter)
        awaitClose{
            context.unregisterReceiver(receiver)
        }
    }.flowOn(Dispatchers.IO)

    @SuppressLint("MissingPermission")
    fun connect(bluetoothDevice: BluetoothDevice): Flow<Boolean> = flow {
        connectThread.connect(bluetoothDevice).collect { it ->
            emit(it)
            connected = it
            connectedThread = ConnectedThread(connectThread.socket)
        }
    }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalUnsignedTypes::class)
    fun runReading() = flow<ByteArray>{
        if(connectedThread !== null){
            connectedThread?.run()?.collect {
                emit(it)
            }
        }
    }.flowOn(Dispatchers.IO)


    fun disconnect(): Flow<Boolean> = flow {
        connectedThread?.closeConnections()
        connectThread.disconnect().collect { it ->
            connected = it
        }
    }

    fun isConnected() = statusConnected

}