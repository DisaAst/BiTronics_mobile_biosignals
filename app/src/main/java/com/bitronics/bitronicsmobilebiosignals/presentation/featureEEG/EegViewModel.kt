package com.bitronics.bitronicsmobilebiosignals.presentation.featureEEG

import androidx.lifecycle.ViewModel
import com.bitronics.bitronicsmobilebiosignals.data.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EegViewModel @Inject constructor(val mainRepository: MainRepository) : ViewModel() {

    fun fetchData() = mainRepository.fetchDataSensor()

    fun fetchModuleType() = mainRepository.fetchSensorType()

    fun isConnected() = mainRepository.isConnected()
}