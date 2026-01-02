// File: app/src/main/java/com/group10/carservicebook/ui/ServiceAdapter.kt
package com.group10.carservicebook.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group10.carservicebook.R
import com.group10.carservicebook.data.ServiceRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// MODIFIED: Constructor now takes a lambda function 'onItemClick'
class ServiceAdapter(private val onItemClick: (ServiceRecord) -> Unit) :
    ListAdapter<ServiceRecord, ServiceAdapter.ServiceViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service_record, parent, false)
        return ServiceViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ServiceViewHolder(itemView: View, val onItemClick: (ServiceRecord) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val tvType: TextView = itemView.findViewById(R.id.tvServiceType)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvMileage: TextView = itemView.findViewById(R.id.tvMileage)
        private val tvCost: TextView = itemView.findViewById(R.id.tvCost)

        fun bind(item: ServiceRecord) {
            tvType.text = item.serviceType
            tvMileage.text = "${item.mileageAtService} km"
            tvCost.text = "RM ${String.format("%.2f", item.cost)}"
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            tvDate.text = sdf.format(Date(item.date))

            // ADDED: Click listener
            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ServiceRecord>() {
        override fun areItemsTheSame(oldItem: ServiceRecord, newItem: ServiceRecord) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ServiceRecord, newItem: ServiceRecord) = oldItem == newItem
    }
}