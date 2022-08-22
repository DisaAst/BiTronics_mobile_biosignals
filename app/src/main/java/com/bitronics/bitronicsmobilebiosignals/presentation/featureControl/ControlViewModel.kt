package com.bitronics.bitronicsmobilebiosignals.presentation.featureControl

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import com.bitronics.bitronicsmobilebiosignals.data.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ControlViewModel @Inject constructor(val mainRepository: MainRepository) : ViewModel() {

    fun disconnect(){
        mainRepository.disconnect()
    }


    fun isConnected() = mainRepository.isConnected()

}