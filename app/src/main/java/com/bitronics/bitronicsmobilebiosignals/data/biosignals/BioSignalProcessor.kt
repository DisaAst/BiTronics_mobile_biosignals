package com.bitronics.bitronicsmobilebiosignals.data.biosignals

import ECG
import EEG
import EMG
import PPG
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.math3.complex.Complex
import org.apache.commons.math3.transform.DftNormalization
import org.apache.commons.math3.transform.FastFourierTransformer
import org.apache.commons.math3.transform.TransformType
import kotlin.math.sqrt


class BioSignalProcessor {

    val emg: EMG = EMG()
    val ppg: PPG = PPG()
    val ecg: ECG = ECG()
    val eeg: EEG = EEG()

    suspend fun getAmpl(array: DoubleArray): Double = withContext(Dispatchers.IO) {
        return@withContext emg.getAmpl(array)
    }

    suspend fun getPulseWithPPG(arrData: DoubleArray): Double = withContext(Dispatchers.IO) {
        var frequency = 1.0 / 0.03
        return@withContext ppg.getPulse(arrData, frequency)
    }

    suspend fun getPulseWithECG(arrData: DoubleArray): Double = withContext(Dispatchers.IO) {
        var frequency = 1.0 / 0.03
        return@withContext ecg.getPulse(arrData, frequency)
    }

    private fun filter(arr: DoubleArray, n: Int): List<Double> {
        val items = arr.map { it }
        return items.windowed(n, n) { it.average() }
    }

    suspend fun fftTransform(arr: DoubleArray): DoubleArray  = withContext(Dispatchers.IO){
        return@withContext eeg.fftTransform(arr)
    }

}
