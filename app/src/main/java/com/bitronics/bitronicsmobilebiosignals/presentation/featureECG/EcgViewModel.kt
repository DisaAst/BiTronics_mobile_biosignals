package com.bitronics.bitronicsmobilebiosignals.presentation.featureECG

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitronics.bitronicsmobilebiosignals.data.MainRepository
import com.bitronics.bitronicsmobilebiosignals.data.biosignals.BioSignalProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class EcgViewModel @Inject constructor(
    val mainRepository: MainRepository,
    val bioSignalProcessor: BioSignalProcessor
) : ViewModel() {

    var ecgValue = MutableLiveData<Double>()
    var pulse = MutableLiveData<Double>()
    var ampl = MutableLiveData<Double>()
    var time = 0.0
    var ecgSeries = DoubleArray(99)
    var counter = 0
    var stress = MutableLiveData<Double>()

    @OptIn(ExperimentalUnsignedTypes::class)
    fun runReading() {
        viewModelScope.launch {
            mainRepository.runReading().collect {
                time += 0.03
                time = time
                ecgValue.value = (it[0].toUByte().toDouble() / 255.0) * 5.0
                if(counter == 99){
                    ampl.value = ecgSeries.max() - ecgSeries.min()
                    pulse.value = bioSignalProcessor.getPulseWithECG(ecgSeries.toList().windowed(5, 5){ it.average() }.toDoubleArray())
                    stress.value = bioSignalProcessor.calculateStressIndex(bioSignalProcessor.getRRINtervals(ecgSeries.toList().windowed(5, 5){ it.average() }.toDoubleArray()).toList())
                    counter = 0
                }
                ecgSeries[counter] = (it[0].toUByte().toDouble() / 255.0) * 5.0
                counter++
            }
        }
    }
}