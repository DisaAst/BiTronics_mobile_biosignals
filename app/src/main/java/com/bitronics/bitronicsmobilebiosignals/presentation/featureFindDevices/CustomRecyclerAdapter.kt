package com.bitronics.bitronicsmobilebiosignals.presentation.featureFindDevices

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bitronics.bitronicsmobilebiosignals.R
import com.clj.fastble.data.BleDevice

interface DeviceActionListener {

    fun fetchDevice(device: BleDevice)
}

class CustomRecyclerAdapter(private val devices: ArrayList<BleDevice>, private val actionListener: DeviceActionListener) : RecyclerView
.Adapter<CustomRecyclerAdapter.MyViewHolder>(), View.OnClickListener{

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

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.text = devices[position].name
        holder.mac.text = devices[position].mac
        holder.itemView.tag = devices[position]
    }

    override fun getItemCount() = devices.size

    override fun onClick(p0: View?) {
        val device: BleDevice = p0?.tag as BleDevice
        actionListener.fetchDevice(device)
    }
}