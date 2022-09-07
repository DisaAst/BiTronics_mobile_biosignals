package com.bitronics.bitronicsmobilebiosignals.presentation.featureGSR

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitronics.bitronicsmobilebiosignals.data.MainRepository
import com.bitronics.bitronicsmobilebiosignals.data.biosignals.BioSignalProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GsrViewModel @Inject constructor(
    val mainRepository: MainRepository,
    val bioSignalProcessor: BioSignalProcessor
) : ViewModel() {

    var timeReaction = MutableLiveData<Double>()
    var amplitude = MutableLiveData<Double>()
    var lastValue = 0.0

    fun setTimeReaction(value: Double){
        if(value!=0.01){
             timeReaction.value = value
            lastValue = value
        }
        else{
            timeReaction.value = lastValue
        }
    }
    fun fetchData() = mainRepository.fetchDataSensor()

    fun fetchModuleType() = mainRepository.fetchSensorType()

    fun isConnected() = mainRepository.isConnected()

    fun fetchAmplitude(array: DoubleArray){
        viewModelScope.launch {
            amplitude.value = bioSignalProcessor.getAmpl(array)
        }
    }
}