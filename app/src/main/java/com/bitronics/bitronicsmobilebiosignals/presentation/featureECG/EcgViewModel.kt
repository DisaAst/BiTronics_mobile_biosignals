package com.bitronics.bitronicsmobilebiosignals.presentation.featureECG

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitronics.bitronicsmobilebiosignals.data.MainRepository
import com.bitronics.bitronicsmobilebiosignals.data.biosignals.BioSignalProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EcgViewModel @Inject constructor(
    val mainRepository: MainRepository,
    val bioSignalProcessor: BioSignalProcessor
) : ViewModel() {

    var pulse = MutableLiveData<Double>()
    var ampl = MutableLiveData<Double>()

    fun fetchData() = mainRepository.fetchDataSensor()

    fun fetchModuleType() = mainRepository.fetchSensorType()

    fun isConnected() = mainRepository.isConnected()

    fun fetchPulse(array: DoubleArray) {
        viewModelScope.launch {
            pulse.value = bioSignalProcessor.getPulseWithECG(array).toDouble()
        }
    }

    fun fetchAmpl(arr: DoubleArray) {
        viewModelScope.launch {
            ampl.value = bioSignalProcessor.getAmpl(arr)
        }
    }
}