package com.bitronics.bitronicsmobilebiosignals.presentation.featureEMG


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitronics.bitronicsmobilebiosignals.data.MainRepository
import com.bitronics.bitronicsmobilebiosignals.data.biosignals.BioSignalProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmgViewModel @Inject constructor(
    val mainRepository: MainRepository,
    private val bioSignalProcessor: BioSignalProcessor
) : ViewModel() {

    var ampl = MutableLiveData<Double>()

    var trigger = MutableLiveData<Int>()

    var sokr = MutableLiveData<Int>()

    var kolvo = 0

    fun setTrigger(value:Int){
        trigger.value = value
    }

    fun plusEmg(){
        kolvo += 1
        sokr.value = kolvo
    }

    fun minusEmg(){
        kolvo = 0
        sokr.value = kolvo
    }
    fun fetchData() = mainRepository.fetchDataSensor()

    fun fetchModuleType() = mainRepository.fetchSensorType()

    fun isConnected() = mainRepository.isConnected()

    fun fetchAmplitudeEMG(arr: DoubleArray) {
        viewModelScope.launch {
            ampl.value = bioSignalProcessor.getAmpl(arr)
        }
    }
}