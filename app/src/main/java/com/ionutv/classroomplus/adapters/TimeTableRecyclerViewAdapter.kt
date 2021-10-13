package com.ionutv.classroomplus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ionutv.classroomplus.R
import com.ionutv.classroomplus.databinding.ListItemTimetableBinding
import com.ionutv.classroomplus.databinding.ListItemWeekDayBinding
import com.ionutv.classroomplus.models.*

class TimeTableRecyclerViewAdapter(private val clickListener: IOnTimeTableItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: MutableList<TimeTableListObject> = ArrayList()

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TimeTableListObject.TimeTable -> TimeTableListViewType.TYPE_TIMETABLE.type
            is TimeTableListObject.Day -> TimeTableListViewType.TYPE_DAY.type
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TimeTableListViewType.TYPE_TIMETABLE.type -> {
                val binding = ListItemTimetableBinding.inflate(inflater, parent, false)
                TimeTableViewHolder(binding)
            }
            TimeTableListViewType.TYPE_DAY.type -> {
                val binding = ListItemWeekDayBinding.inflate(inflater, parent, false)
                DayViewHolder(binding)
            }
            else -> {
                val binding = ListItemWeekDayBinding.inflate(inflater, parent, false)
                DayViewHolder(binding)

            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TimeTableViewHolder -> holder.bind(items[position] as TimeTableListObject.TimeTable)
            is DayViewHolder -> holder.bind(items[position] as TimeTableListObject.Day)
        }
    }

    fun setItems(newItems: List<TimeTableListObject>) {
        items = ArrayList(newItems)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    inner class TimeTableViewHolder(private val binding: ListItemTimetableBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TimeTableListObject.TimeTable) {
            with(item.timeTable)
            {
                binding.root.setOnClickListener {
                    clickListener.onTimeTableItemCLicked(id)
                }

                if (type == ClassType.COURSE) {
                    binding.tvType.text = App.instance.getString(R.string.list_item_course)
                    binding.tvTimeTableName.setTextColor(ContextCompat.getColor(App.instance,R.color.course_blue))
                    binding.ivLecture.setImageResource(R.drawable.lecture)
                } else {
                    binding.tvType.text = App.instance.getString(R.string.list_item_lab_or_seminar)
                    binding.tvTimeTableName.setTextColor(ContextCompat.getColor(App.instance,R.color.red))
                    binding.ivLecture.setImageResource(R.drawable.lab)
                }
                binding.tvTimeTableName.text = name
                binding.tvRoom.text = room
                binding.tvDayofWeek.text = day.toString()
                binding.tvTime.text = time
                if(teacher.isEmpty()){
                    binding.tvTeacher.visibility = View.GONE
                }
                binding.tvTeacher.text = teacher
            }
        }
    }

    inner class DayViewHolder(private val binding: ListItemWeekDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TimeTableListObject.Day) {
            binding.tvWeekDay.text = item.day.day
        }
    }


    interface IOnTimeTableItemClickListener {
        fun onTimeTableItemCLicked(itemId: Int)
    }


}