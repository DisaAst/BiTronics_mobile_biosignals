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
    var eegValue = MutableLiveData<Double>()
    var trigger = MutableLiveData<Double>(10.0)
    var seriesEEG = DoubleArray(32)
    var counter = 0
    var time = 0.0
    var alpha = MutableLiveData<Double>(0.0)
    var melody = MutableLiveData<Int>()

    fun runReading(){
        viewModelScope.launch {
            mainRepository.runReading().collect {
                var value = (it[0].toUByte().toDouble() / 255.0) * 5.0
                eegValue.value = value
                if(counter == 32){
                    var newSpectr = bioSignalProcessor.fftTransform(seriesEEG)
                    spectr.value = newSpectr
                    alpha.value = newSpectr[8] + newSpectr[9] + newSpectr[10] + newSpectr[11]
                    counter = 0
                }
                seriesEEG[counter] = value
                time += 0.03
                counter ++
            }
        }
    }
    fun setTrigger(value: Double){
        trigger.value = value
    }

}