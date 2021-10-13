package com.ionutv.classroomplus.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ionutv.classroomplus.databinding.ListItemSelectedClassroomAttendanceBinding
import com.ionutv.classroomplus.utils.DateUtils

class SelectedClassAttendancesRecyclerViewAdapter(private val clickListener: IOnTimeItemClickListener) : RecyclerView.Adapter<SelectedClassAttendancesRecyclerViewAdapter.ViewHolder>() {

    private var items: MutableList<String> = ArrayList()

    fun setItems(newList : List<String>){
        items = newList.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemSelectedClassroomAttendanceBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ListItemSelectedClassroomAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            val dateTime = DateUtils.getDateTimeFromTimeStamp(item.toLong())
            binding.tvTime.text = DateUtils.getFormattedDateFromDateTime(dateTime)
            binding.root.setOnClickListener {
                clickListener.onTimeClicked(item)
            }
            binding.root.setOnLongClickListener {
                clickListener.onTimeLongPressed(item)
                true
            }
        }
    }

    interface IOnTimeItemClickListener {
        fun onTimeClicked(item: String)
        fun onTimeLongPressed(item:String)
    }
}