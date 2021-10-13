package com.ionutv.classroomplus.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ionutv.classroomplus.databinding.ListItemIndividualAttendanceBinding
import com.ionutv.classroomplus.models.AttendanceEntry

class IndividualClassAttendanceListRecyclerViewAdapter : RecyclerView.Adapter<IndividualClassAttendanceListRecyclerViewAdapter.ViewHolder>() {

    private var items: MutableList<AttendanceEntry> = ArrayList()

    fun setItems(newList : List<AttendanceEntry>){
        items = newList.toMutableList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemIndividualAttendanceBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ListItemIndividualAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AttendanceEntry) {
            binding.tvName.text = item.name
            binding.tvEmail.text = item.email
        }
    }
}