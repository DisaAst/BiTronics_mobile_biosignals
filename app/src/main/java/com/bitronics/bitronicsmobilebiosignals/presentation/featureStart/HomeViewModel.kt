package com.bitronics.bitronicsmobilebiosignals.presentation.featureStart


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitronics.bluetooth.EnableBluetooth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val enableBluetooth: EnableBluetooth) : ViewModel() {


    fun enableBluetooth() {
        enableBluetooth.onBluetooth()
    }
}