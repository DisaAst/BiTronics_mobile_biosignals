package com.bitronics.bitronicsmobilebiosignals.presentation.featureControl

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitronics.bitronicsmobilebiosignals.MainActivity.Companion.deviceC
import com.bitronics.bitronicsmobilebiosignals.R
import com.bitronics.bitronicsmobilebiosignals.databinding.FragmentControlBinding
import com.bitronics.bitronicsmobilebiosignals.databinding.FragmentFindDevicesBinding
import com.bitronics.bitronicsmobilebiosignals.presentation.featureFindDevices.DevicesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ControlFragment : Fragment() {
    private var _binding: FragmentControlBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val vm: ControlViewModel by viewModels()

    @SuppressLint("FragmentLiveDataObserve")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentControlBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.txtDevice.text = deviceC.name.toString()

        if(!vm.isConnected()) {
            view?.findNavController()?.navigate(R.id.action_nav_control_device_to_nav_home)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnDisconnect.setOnClickListener{
            vm.disconnect()
            view.findNavController().navigate(R.id.action_nav_control_device_to_nav_home)
        }

        binding.buttonBrain.setOnClickListener {
            view.findNavController().navigate(R.id.action_nav_control_device_to_nav_eeg)
        }

        binding.buttonECG.setOnClickListener {
            view.findNavController().navigate(R.id.action_nav_control_device_to_nav_ecg)
        }

        binding.buttonPulse.setOnClickListener {
            view.findNavController().navigate(R.id.action_nav_control_device_to_nav_ppg)
        }

        binding.buttonKGR.setOnClickListener {
            view.findNavController().navigate(R.id.action_nav_control_device_to_nav_gsr)
        }

        binding.buttonMuscle.setOnClickListener {
            view.findNavController().navigate(R.id.action_nav_control_device_to_nav_emg)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
    }

}