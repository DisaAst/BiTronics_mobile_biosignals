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

    var ampl = MutableLiveData<DoubleArray>(doubleArrayOf(0.0, 0.0))
    var firstEmgSeries = DoubleArray(33)
    var secondEmgSeries = DoubleArray(33)
    var contractions = MutableLiveData<IntArray>(intArrayOf(0, 0))
    var emgValue = MutableLiveData<DoubleArray>(doubleArrayOf(0.0, 0.0))
    var time = 0.0
    var trigger = MutableLiveData<Double>()
    var counter = 0

    fun setTrigger(value:Double){
        trigger.value = value
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun runReading(){
        viewModelScope.launch {
            mainRepository.runReading().collect {
                var values = doubleArrayOf((it[0].toUByte().toDouble() / 255.0) * 5.0, (it[8].toUByte().toDouble() / 255.0) * 5.0)
                emgValue.value = values
                if(counter == 10){
                    counter = 0
                    ampl.value = doubleArrayOf(bioSignalProcessor.getAmpl(firstEmgSeries), bioSignalProcessor.getAmpl(secondEmgSeries))
                }
                firstEmgSeries[counter] = values[0]
                secondEmgSeries[counter] = values[1]
                time += 0.03
                counter ++
            }
        }
    }

    fun closeReading(){
        time = 0.0
    }
}