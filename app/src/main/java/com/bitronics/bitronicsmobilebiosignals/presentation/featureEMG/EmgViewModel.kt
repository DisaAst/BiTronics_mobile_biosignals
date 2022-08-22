package com.bitronics.bitronicsmobilebiosignals.presentation.featureEMG


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitronics.bitronicsmobilebiosignals.data.MainRepository
import com.bitronics.bitronicsmobilebiosignals.data.biosignals.BioSignalProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EmgViewModel @Inject constructor(
    val mainRepository: MainRepository,
    private val bioSignalProcessor: BioSignalProcessor
) : ViewModel() {

    fun fetchData() = mainRepository.fetchDataSensor()

    fun fetchModuleType() = mainRepository.fetchSensorType()

    fun isConnected() = mainRepository.isConnected()

    fun fetchAmplitudeEMG(arr: DoubleArray): Double {
        var ampl = 0.0
        ampl = bioSignalProcessor.getAmplEmg(arr)
        return ampl
    }

}