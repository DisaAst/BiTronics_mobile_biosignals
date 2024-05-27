package com.bitronics.bitronicsmobilebiosignals.presentation.featurePPG

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitronics.bitronicsmobilebiosignals.data.MainRepository
import com.bitronics.bitronicsmobilebiosignals.data.biosignals.BioSignalProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PpgViewModel @Inject constructor(
    val mainRepository: MainRepository,
    val bioSignalProcessor: BioSignalProcessor
) : ViewModel() {

    var ppgValue = MutableLiveData<Double>()
    var pulse = MutableLiveData<Double>()
    var ampl = MutableLiveData<Double>()
    var time = 0.0
    var ppgSeries = DoubleArray(99)
    var counter = 0

    @OptIn(ExperimentalUnsignedTypes::class)
    fun runReading() {
        viewModelScope.launch {
            mainRepository.runReading().collect {
                time += 0.03
                ppgValue.value = (it[0].toUByte().toDouble() / 255.0) * 5.0
                if(counter == 99){
                    ampl.value = ppgSeries.max() - ppgSeries.min()
                    counter = 0
                    pulse.value = bioSignalProcessor.getPulseWithPPG(ppgSeries)
                }
                ppgSeries[counter] = (it[0].toUByte().toDouble() / 255.0) * 5.0
                counter++
            }
        }
    }


}