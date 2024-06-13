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
    var firstEmgSeries = DoubleArray(100)
    var secondEmgSeries = DoubleArray(100)
    var contractions1 = MutableLiveData<Int>(0)
    var contractions2 = MutableLiveData<Int>(0)
    var emgValue = MutableLiveData<DoubleArray>(doubleArrayOf(0.0, 0.0))
    var time = 0.0
    var trigger = MutableLiveData<Double>()
    var counter = 0
    var b_count1 = false
    var b_count1_1 = false
    var b_count2 = false
    var b_count2_2 = false

    fun setTrigger(value:Double){
        trigger.value = value
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun runReading(){
        viewModelScope.launch {
            mainRepository.runReading().collect {
                var values = doubleArrayOf((it[0].toUByte().toDouble() / 255.0) * 5.0, (it[8].toUByte().toDouble() / 255.0) * 5.0)
                emgValue.value = values
                if(counter == 100){
                    counter = 0
                    ampl.value = doubleArrayOf(bioSignalProcessor.getAmpl(firstEmgSeries), bioSignalProcessor.getAmpl(secondEmgSeries))
                    contractionCounter(bioSignalProcessor.getAmpl(firstEmgSeries), bioSignalProcessor.getAmpl(secondEmgSeries))
                }
                firstEmgSeries[counter] = values[0]
                secondEmgSeries[counter] = values[1]
                time += 0.03
                counter ++
            }
        }
    }

    fun contractionCounter(value1: Double, value2: Double){
        if (value1 >= (trigger.value ?: 0.0)) {
            b_count1_1 = true
        }
        if (value1 < (trigger.value ?: 0.0)) {
            b_count1_1 = false
            b_count1 = false
        }
        if (!b_count1 && b_count1_1) {
            contractions1.value = (contractions1.value ?: 0) + 1
            b_count1 = true
        }
        if (value2 >= (trigger.value ?: 0.0)) {
            b_count2_2 = true
        }
        if (value2 < (trigger.value ?: 0.0)) {
            b_count2_2 = false
            b_count2 = false
        }
        if (!b_count2 && b_count2_2) {
            contractions2.value = (contractions2.value ?: 0) + 1
            b_count2 = true
        }
    }

    fun resetContractionsCount(){
        contractions1.value = 0
        contractions2.value = 0
    }

    fun closeReading(){
        time = 0.0
    }
}