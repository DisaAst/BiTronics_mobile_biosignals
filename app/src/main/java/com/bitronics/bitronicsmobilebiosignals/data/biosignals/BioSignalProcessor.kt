package com.bitronics.bitronicsmobilebiosignals.data.biosignals

import ECG
import EEG
import EMG
import PPG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.math3.complex.Complex
import org.apache.commons.math3.transform.DftNormalization
import org.apache.commons.math3.transform.FastFourierTransformer
import org.apache.commons.math3.transform.TransformType
import kotlin.math.roundToInt
import kotlin.math.sqrt


class BioSignalProcessor {

    val emg: EMG = EMG()
    val ppg: PPG = PPG()
    val ecg: ECG = ECG()
    val eeg: EEG = EEG()

    suspend fun getAmpl(array: DoubleArray): Double = withContext(Dispatchers.IO) {
        return@withContext array.max() - array.min()
    }

    suspend fun getPulseWithPPG(arrData: DoubleArray): Double = withContext(Dispatchers.IO) {
        var frequency = 1.0 / 0.03
        return@withContext ppg.getPulse(arrData, frequency)
    }

    suspend fun getPulseWithECG(arrData: DoubleArray): Double = withContext(Dispatchers.IO) {
        var frequency = 1.0 / 0.15
        return@withContext ecg.getPulse(arrData, frequency)
    }

    fun calculateStressIndex(rrIntervals: List<Double>): Double {
        if (rrIntervals.isEmpty()) return 0.0
        val binSize = 0.05
        val bins = rrIntervals.groupBy { (it / binSize).roundToInt() * binSize }
        val mode = bins.maxByOrNull { it.value.size } ?: return 0.0
        val modeValue = mode.key
        val modeAmplitude = (mode.value.size.toDouble() / rrIntervals.size) * 100
        val maxRR = rrIntervals.maxOrNull() ?: return 0.0
        val minRR = rrIntervals.minOrNull() ?: return 0.0
        val mxDMn = maxRR - minRR
        if (mxDMn == 0.0) return Double.POSITIVE_INFINITY
        return (modeAmplitude) / (2 * modeValue * mxDMn)
    }

    suspend fun getRRINtervals(arrData: DoubleArray): DoubleArray = withContext(Dispatchers.IO) {
        var frequency = 1.0 / 0.15
        return@withContext ecg.getRRInterval(ecg.getRPeaksCoordinate(arrData, frequency))
            .toDoubleArray()
    }

    private fun filter(arr: DoubleArray, n: Int): List<Double> {
        val items = arr.map { it }
        return items.windowed(n, n) { it.average() }
    }

    suspend fun fftTransform(arr: DoubleArray): DoubleArray = withContext(Dispatchers.IO) {
        return@withContext eeg.fftTransform(arr)
    }

}
