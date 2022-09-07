package com.bitronics.bitronicsmobilebiosignals.data

import android.util.Log

interface Parser {

    fun parse(msb: Byte, lsb: Byte): Double

    fun getType(value: Byte): Int

    class Base() : Parser {
        override fun parse(lsb: Byte, msb: Byte): Double {
            var x = msb.toInt() shl 8
            val y = lsb.toUByte()
            x += y.toInt()
            val a  = 3.0 / 1023.0
            val res =  a * x.toDouble()
            Log.d("Data", res.toString())
            return res
        }

        override fun getType(value: Byte): Int {
            val v = value.toInt()
            return v and 0b00001111
        }
    }
}