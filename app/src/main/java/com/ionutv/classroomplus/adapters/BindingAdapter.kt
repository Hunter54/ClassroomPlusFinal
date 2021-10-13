package com.ionutv.classroomplus.adapters

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.google.android.material.chip.ChipGroup
import com.ionutv.classroomplus.R
import com.ionutv.classroomplus.models.ClassType
import com.ionutv.classroomplus.models.ClassWeek
import com.ionutv.classroomplus.models.WeekDays

@BindingAdapter("checkedTypeConverter")
fun setCheckedTypeChip(view:ChipGroup, type : LiveData<ClassType>){
    when(type.value){
        ClassType.COURSE ->view.check(R.id.choice_chip1)
        ClassType.LAB -> view.check(R.id.choice_chip2)
    }
}

@BindingAdapter("checkedDayConverter")
fun setCheckedDayChip(view: ChipGroup, day: LiveData<WeekDays>){
    when(day.value){
        WeekDays.MONDAY -> view.check(R.id.day_choice_chip1)
        WeekDays.TUESDAY -> view.check(R.id.day_choice_chip2)
        WeekDays.WEDNESDAY -> view.check(R.id.day_choice_chip3)
        WeekDays.THURSDAY -> view.check(R.id.day_choice_chip4)
        WeekDays.FRIDAY -> view.check(R.id.day_choice_chip5)
    }
}

@BindingAdapter("checkedWeekConverter")
fun setCheckedWeekChip(view:ChipGroup, type : LiveData<ClassWeek>){
    when(type.value){
        ClassWeek.ODD ->view.check(R.id.week_choice_chip1)
        ClassWeek.EVEN -> view.check(R.id.week_choice_chip2)
        ClassWeek.BOTH -> view.check(R.id.week_choice_chip3)

    }
}