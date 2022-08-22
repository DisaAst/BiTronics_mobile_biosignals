package com.bitronics.bitronicsmobilebiosignals.data

interface Parser {

    fun parse(msb: Byte, lsb: Byte): Int

    fun getType(value: Byte): Int

    class Base() : Parser {
        override fun parse(lsb: Byte, msb: Byte): Int {
            var x = msb.toInt() shl 8
            val y = lsb.toUByte()
            x += y.toInt()
            return x
        }

        override fun getType(value: Byte): Int {
            val v = value.toInt()
            return v and 0b00001111
        }
    }
}