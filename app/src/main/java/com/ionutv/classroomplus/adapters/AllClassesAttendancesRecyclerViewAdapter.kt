package com.ionutv.classroomplus.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ionutv.classroomplus.databinding.ListItemManageAttendanceBinding
import com.ionutv.classroomplus.models.CourseEntry

class AllClassesAttendancesRecyclerViewAdapter(private val clickListener: IOnClassItemClickListener) :
    RecyclerView.Adapter<AllClassesAttendancesRecyclerViewAdapter.ViewHolder>() {

    private var items: MutableList<CourseEntry> = ArrayList()

    fun setItems(newList : List<CourseEntry>){
        items = newList.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemManageAttendanceBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ListItemManageAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CourseEntry) {
            binding.tvClassroomName.text = item.name
            binding.root.setOnClickListener {
                clickListener.onClassClicked(item)
            }
        }
    }

    interface IOnClassItemClickListener {
        fun onClassClicked(item: CourseEntry)
    }
}