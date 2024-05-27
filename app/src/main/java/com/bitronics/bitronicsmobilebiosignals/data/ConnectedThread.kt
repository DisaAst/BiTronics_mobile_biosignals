package com.bitronics.bitronicsmobilebiosignals.data

import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeout
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Timer

class ConnectedThread(socket: BluetoothSocket?) {


    private val mmInStream: InputStream? = socket?.inputStream
    private val mmOutStream: OutputStream? = socket?.outputStream
    private val mmBuffer: ByteArray = ByteArray(16)

    @OptIn(ExperimentalUnsignedTypes::class)
    fun run() = flow<ByteArray> {
        val bis: BufferedInputStream = BufferedInputStream(mmInStream)
        while (true) {
            try {
                val buf = ByteArray(16)
                bis.read(buf)
                emit(buf)
            } catch (e: IOException) {
                //cancel()
                closeConnections()
            }
        }
    }.flowOn(Dispatchers.IO)

    fun send(text: String) {
        val bytes = text.toByteArray()
        mmOutStream?.write(bytes)
        mmOutStream?.flush()
    }

    fun closeConnections() {
        mmOutStream?.close()
        mmInStream?.close()
    }

}