package com.bitronics.bitronicsmobilebiosignals.data

import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class ConnectedThread(socket: BluetoothSocket?) {


    private val mmInStream: InputStream? = socket?.inputStream
    private val mmOutStream: OutputStream? = socket?.outputStream
    private val mmBuffer: ByteArray = ByteArray(16)

    fun run() = callbackFlow<ByteArray> {
        val bis: BufferedInputStream = BufferedInputStream(mmInStream)
        while (mmInStream !== null){
            try{
                send("A")
                val buf = ByteArray(16)
                bis.read(buf)
                Log.e("Data", buf[0].toString())
                trySendBlocking(buf)
                delay(10)
            } catch (e: IOException) {
               cancel()
               break
            }
        }
    }.flowOn(Dispatchers.IO)

    fun send(bytes: ByteArray){
        mmOutStream?.write(bytes)
        mmOutStream?.flush()
    }

    fun send(text: String){
        val bytes = text.toByteArray()
        mmOutStream?.write(bytes)
        mmOutStream?.flush()
    }

    fun closeConnections(){
        mmOutStream?.close()
        mmInStream?.close()
    }

}