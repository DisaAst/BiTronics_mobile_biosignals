package com.bitronics.bitronicsmobilebiosignals.data

import android.bluetooth.BluetoothDevice

data class ScanDevicesData(
    val devices: ArrayList<BluetoothDevice>,
    val scanStatus: Boolean
)