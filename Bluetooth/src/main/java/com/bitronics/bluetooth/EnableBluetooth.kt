package com.bitronics.bluetooth

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent

interface EnableBluetooth {

    suspend fun onBluetooth() : Int

    class Base (val activity: Activity) : EnableBluetooth{

        companion object {
            val REQ_ENABLE_BT = 10
        }

        @SuppressLint("MissingPermission")
        override suspend fun onBluetooth() : Int{
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(intent, REQ_ENABLE_BT)
            return REQ_ENABLE_BT
        }
    }
}