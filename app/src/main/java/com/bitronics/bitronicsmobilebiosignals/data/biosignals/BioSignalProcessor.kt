package com.bitronics.bitronicsmobilebiosignals.data.biosignals

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BioSignalProcessor {

    fun getAmplEmg(array: DoubleArray): Double {
        var max = 0.0
        var min = 1024.0
        for (i in array.indices) {
            if (array[i] < min) min = array[i]
            if (array[i] > max) max = array[i]
        }
        return max - min
    }

    suspend fun getPulseWithPPG(arrData: DoubleArray): Int = withContext(Dispatchers.IO) {
        val data = filter(arrData, 100)
        val th = data.max() * 0.7
        var pulse = 0
        val arrTime = doubleArrayOf(
            0.00,
            0.002, 0.004, 0.006, 0.008, 0.010, 0.012, 0.014, 0.016,
            0.018, 0.020, 0.022, 0.024, 0.026, 0.028, 0.030, 0.032, 0.034, 0.036,
            0.038, 0.040, 0.042, 0.044, 0.046, 0.048, 0.050, 0.052, 0.054, 0.056, 0.058, 0.060
        )
        val newTimeArr = doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        var count = 0
        for (i in 3..data.size - 3) {
            if ((data[i] > th) &&
                (data[i] > data[i - 1]) && (data[i] > data[i + 1]) &&
                (data[i] > data[i - 2]) && (data[i] > data[i + 2])
            ) {
                pulse += 1
                newTimeArr[count] = arrTime[i]
                Log.d("Time Pics", newTimeArr[count].toString())
                count += 1
            }
        }
        return@withContext getPulse(newTimeArr, pulse).toInt()
    }

    suspend fun getPulseWithECG(arrData: DoubleArray): Int = withContext(Dispatchers.IO) {
        val data = filter(arrData, 100)
        val th = data.max() * 0.7
        var pulse = 0
        val arrTime = doubleArrayOf(
            0.00,
            0.002, 0.004, 0.006, 0.008, 0.010, 0.012, 0.014, 0.016,
            0.018, 0.020, 0.022, 0.024, 0.026, 0.028, 0.030, 0.032, 0.034, 0.036,
            0.038, 0.040, 0.042, 0.044, 0.046, 0.048, 0.050, 0.052, 0.054, 0.056, 0.058, 0.060
        )
        val newTimeArr = doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        var count = 0
        for (i in 3..data.size - 3) {
            if ((data[i] > th) &&
                (data[i] > data[i - 1]) && (data[i] > data[i + 1]) &&
                (data[i] > data[i - 2]) && (data[i] > data[i + 2])
            ) {
                pulse += 1
                newTimeArr[count] = arrTime[i]
                count += 1
            }
        }
        return@withContext getPulse(newTimeArr, pulse).toInt()
    }

    private fun filter(arr: DoubleArray, n: Int): List<Double> {
        val items = arr.map { it }
        return items.windowed(n, n) { it.average() }
    }

    private fun getPulse(arr: DoubleArray, pics: Int): Double {
        val firstPic = arr.min()
        val lastPic = arr.max()
        val rr = lastPic - firstPic
        val res = 60 / ((rr * 100) / pics)
        Log.d("Pulse", res.toString())
        return res
    }


    private fun getPulseTest(arr: DoubleArray, pics: Int): EcgData {
        val firstPic = arr.min()
        val lastPic = arr.max()
        val rr = lastPic - firstPic
        val res = 60 / ((rr * 100) / pics)
        Log.d("Pulse", res.toString())
        return EcgData(res, rr)
    }

    fun fftTransform(){

    }

}
