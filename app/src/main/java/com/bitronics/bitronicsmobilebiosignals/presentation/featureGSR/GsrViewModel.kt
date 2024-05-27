package com.bitronics.bitronicsmobilebiosignals.presentation.featureGSR

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitronics.bitronicsmobilebiosignals.data.MainRepository
import com.bitronics.bitronicsmobilebiosignals.data.biosignals.BioSignalProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class GsrViewModel @Inject constructor(
    val mainRepository: MainRepository,
    val bioSignalProcessor: BioSignalProcessor
) : ViewModel() {

    var amplitude = MutableLiveData<Double>()
    var time = 0.0
    var gsr_value = MutableLiveData<Double>(0.0)
    var data = DoubleArray(33)
    var count = 0
    var timeReaction = MutableLiveData(0.0)
    var treshold = 0.0
    var runCase = false

    fun startCase() {
        treshold = gsr_value.value ?: (0.0 + 0.01)
        runCase = true
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun runReading() {
        viewModelScope.launch {
            mainRepository.runReading().collect {
                time += 0.03
                var value = (it[0].toUByte().toDouble() / 255.0) * 5.0
                gsr_value.value = value
                if (count == 33) {
                    amplitude.value = (data.max() - data.min())
                    count = 0
                }
                if(runCase && (value >= treshold + 0.025 || value <= treshold - 0.025)){
                    timeReaction.value = timeReaction.value?.plus(0.03)?.toBigDecimal()?.setScale(1, RoundingMode.UP)?.toDouble()
                }
                else {
                    timeReaction.value = 0.0
                }
                data[count] = value
                count++
            }
        }
    }

}