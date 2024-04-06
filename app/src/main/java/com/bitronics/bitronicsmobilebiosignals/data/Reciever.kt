package com.bitronics.bitronicsmobilebiosignals.data

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class Receiver(val onReceiveCallback: OnReceive) : BroadcastReceiver() {

    fun interface OnReceive {
        fun onReceive(devices: ArrayList<BluetoothDevice>)
    }

    val devices = ArrayList<BluetoothDevice>()
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        when (action) {
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                onReceiveCallback.onReceive(ArrayList<BluetoothDevice>(devices))
                devices.clear()
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
