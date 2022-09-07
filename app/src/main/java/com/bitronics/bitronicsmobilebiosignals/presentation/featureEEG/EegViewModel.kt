package com.bitronics.bitronicsmobilebiosignals.presentation.featureEEG

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitronics.bitronicsmobilebiosignals.data.MainRepository
import com.bitronics.bitronicsmobilebiosignals.data.biosignals.BioSignalProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EegViewModel @Inject constructor(val mainRepository: MainRepository, val bioSignalProcessor: BioSignalProcessor) : ViewModel() {

    var spectr = MutableLiveData<DoubleArray>()

    var trigger = MutableLiveData<Double>()

    fun setTrigger(value: Double){
        trigger.value = value
    }

    fun fetchData() = mainRepository.fetchDataSensor()

    fun fetchModuleType() = mainRepository.fetchSensorType()

    fun isConnected() = mainRepository.isConnected()

    fun fetchSpectr(arr: DoubleArray){
        viewModelScope.launch {
            spectr.value = bioSignalProcessor.fftTransform(arr)
        }
    }
}