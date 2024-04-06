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

    var emgValue = MutableLiveData<Double>()

    var timeValue = MutableLiveData<Double>(0.0)

    var time = 0.0

    var trigger = MutableLiveData<Double>()

    var sokr = MutableLiveData<Int>()

    var kolvo = 0

    fun setTrigger(value:Double){
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

    fun runReading(){
        viewModelScope.launch {
            mainRepository.runReading().collect {
                time += 0.03
                var sens1 = it[0].toUByte().toDouble()
                timeValue.value = time
                var koef = 5.0 / 255.0
                emgValue.value = sens1 * koef
            }
        }
    }

    fun closeReading(){
        time = 0.0
        timeValue.value = time
    }

    fun fetchModuleType() = mainRepository.fetchSensorType()

    fun isConnected() = mainRepository.isConnected()

    fun fetchAmplitudeEMG(arr: DoubleArray) {
        viewModelScope.launch {
            ampl.value = bioSignalProcessor.getAmpl(arr)
        }
    }
}