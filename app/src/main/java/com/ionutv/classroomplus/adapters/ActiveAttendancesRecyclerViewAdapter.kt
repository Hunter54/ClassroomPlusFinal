package com.ionutv.classroomplus.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ionutv.classroomplus.databinding.ListItemAttendancesBinding
import com.ionutv.classroomplus.models.Attendance
import com.ionutv.classroomplus.utils.DateUtils
import kotlin.collections.ArrayList

class ActiveAttendancesRecyclerViewAdapter(private val clickListener: IOnAttendanceItemClickListener) :
    RecyclerView.Adapter<ActiveAttendancesRecyclerViewAdapter.ViewHolder>() {

    var items: MutableList<Attendance> = ArrayList()
    private set

    fun setItems(newItems: List<Attendance>) {
        items = ArrayList(newItems)
        notifyDataSetChanged()
    }

    fun removeClass(classroomId : String){
        items.removeIf { it.courseId == classroomId }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemAttendancesBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ListItemAttendancesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Attendance) {
            binding.tvClassName.text = item.courseName
            val dateTime = DateUtils.getDateTimeFromTimeStamp(item.startTime)
            val formatStartTime = DateUtils.getFormattedTimeFromDateTime(dateTime)
            val endDateTime = dateTime.plusMinutes(item.duration.toLong())
            val formatEndTime = DateUtils.getFormattedTimeFromDateTime(endDateTime)
            binding.tvStartTime.text = formatStartTime
            binding.tvEndTime.text = formatEndTime
            binding.root.setOnClickListener {
                clickListener.onAttendanceClicked(item)
            }
            binding.root.setOnLongClickListener {
                clickListener.onAttendanceLongPressed(item)
            }
        }
    }

    interface IOnAttendanceItemClickListener {
        fun onAttendanceClicked(item: Attendance)
        fun onAttendanceLongPressed(item:Attendance) : Boolean = false
    }
}