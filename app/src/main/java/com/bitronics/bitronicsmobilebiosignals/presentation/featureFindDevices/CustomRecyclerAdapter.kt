package com.bitronics.bitronicsmobilebiosignals.presentation.featureFindDevices

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bitronics.bitronicsmobilebiosignals.R

interface DeviceActionListener {
    fun fetchDevice(device: BluetoothDevice)
}

class CustomRecyclerAdapter(private val devices: ArrayList<BluetoothDevice>, private val actionListener: DeviceActionListener) : RecyclerView
.Adapter<CustomRecyclerAdapter.MyViewHolder>(), View.OnClickListener{

    var context = this
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textName)
        val mac: TextView = itemView.findViewById(R.id.textAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        itemView.rootView.setOnClickListener(this)
        return MyViewHolder(itemView)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.text = devices[position].name
        holder.mac.text = devices[position].address
        holder.itemView.tag = devices[position]
    }

    override fun getItemCount() = devices.size

    override fun onClick(p0: View?) {
        val device: BluetoothDevice = p0?.tag as BluetoothDevice
        actionListener.fetchDevice(device)
    }
}