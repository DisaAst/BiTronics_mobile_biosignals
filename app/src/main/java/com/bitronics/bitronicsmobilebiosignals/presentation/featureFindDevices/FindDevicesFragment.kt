package com.bitronics.bitronicsmobilebiosignals.presentation.featureFindDevices

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitronics.bitronicsmobilebiosignals.MainActivity
import com.bitronics.bitronicsmobilebiosignals.R
import com.bitronics.bitronicsmobilebiosignals.databinding.FragmentFindDevicesBinding
import com.clj.fastble.data.BleDevice
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FindDevicesFragment : Fragment() {

    private var _binding: FragmentFindDevicesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val vm: DevicesViewModel by viewModels()

    var permissionGranted = false

    @SuppressLint("FragmentLiveDataObserve")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFindDevicesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    @SuppressLint("UseRequireInsteadOfGet", "FragmentLiveDataObserve")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = binding.devicesRecycler
        recyclerView.layoutManager = LinearLayoutManager(activity)

        checkPermissions()


        vm.devices.observe(viewLifecycleOwner, {
            recyclerView.adapter = CustomRecyclerAdapter(it, object : DeviceActionListener {
                override fun fetchDevice(device: BleDevice) {
                    Log.e("Device:", device.toString())
                    vm.connect(device)
                }
            })
        })

        vm.scanStatus.observe(viewLifecycleOwner, {
            if (it == true) binding.pbProgress.visibility = View.VISIBLE
            else binding.pbProgress.visibility = View.GONE


        })

        vm.status.observe(viewLifecycleOwner, {
            if (it == true) view.findNavController()
                .navigate(R.id.action_nav_find_devices_to_nav_control_device)
        })
        binding.btnEnableSearch.setOnClickListener {
            vm.startScan()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onStart() {
        super.onStart()
        subscribeOnViewModel()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun subscribeOnViewModel() {

    }

    @SuppressLint("UseRequireInsteadOfGet")
    fun checkPermissions() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val permissionDeniedList: MutableList<String> = ArrayList()
        for (permission in permissions) {
            val permissionCheck = ContextCompat.checkSelfPermission(activity!!, permission)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission)
            } else {
                permissionDeniedList.add(permission)
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            val deniedPermissions = permissionDeniedList.toTypedArray()
            ActivityCompat.requestPermissions(
                activity!!,
                deniedPermissions,
                MainActivity.REQUEST_CODE_PERMISSION_LOCATION
            )
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun onPermissionGranted(permission: String?) {
        when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                AlertDialog.Builder(activity)
                    .setTitle(R.string.notifyTitle)
                    .setMessage(R.string.gpsNotifyMsg)
                    .setNegativeButton(R.string.cancel,
                        DialogInterface.OnClickListener { dialog, which ->
                            permissionGranted = false
                        })
                    .setPositiveButton(R.string.setting,
                        DialogInterface.OnClickListener { dialog, which ->
                            permissionGranted = true
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivityForResult(intent, MainActivity.REQUEST_CODE_OPEN_GPS)
                        })
                    .setCancelable(false)
                    .show()
            } else {

            }
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun checkGPSIsOpen(): Boolean {
        val locationManager =
            activity!!.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
                ?: return false
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MainActivity.REQUEST_CODE_PERMISSION_LOCATION -> if (grantResults.size > 0) {
                var i = 0
                while (i < grantResults.size) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        onPermissionGranted(permissions[i])
                    }
                    i++
                }
            }
        }
    }
}