package com.bitronics.bitronicsmobilebiosignals.presentation.featureFindDevices

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitronics.bitronicsmobilebiosignals.data.MainRepository
import com.clj.fastble.data.BleDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(val mainRepository: MainRepository) : ViewModel() {


    var devices = MutableLiveData<ArrayList<BluetoothDevice>>()

    var status = MutableLiveData<Boolean>()

    var scanStatus = MutableLiveData<Boolean>()

    init {
        scanStatus.value = false
    }

    fun startScan() {
        scanStatus.value = true
        viewModelScope.launch {
            mainRepository.startScan().collect {
                devices.value = it
                scanStatus.value = false
            }
        }
    }

    fun connect(bleDevice: BleDevice) {
        status.value = false
        viewModelScope.launch {
            mainRepository.connect(bleDevice).collect {
                status.value = it
            }
        }
    }

}