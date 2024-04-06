package com.bitronics.bitronicsmobilebiosignals.presentation.featureControl

import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitronics.bitronicsmobilebiosignals.data.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControlViewModel @Inject constructor(val mainRepository: MainRepository) : ViewModel() {

    var value = MutableLiveData<String>()


    fun disconnect(){
        mainRepository.disconnect()
    }

    fun startScan(){
        viewModelScope.launch {
            mainRepository.runReading().collect {
                value.value = it[1].toString()
            }
        }
    }


    fun isConnected() = mainRepository.isConnected()

}