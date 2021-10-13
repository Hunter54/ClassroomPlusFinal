package com.ionutv.classroomplus.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimeTableDTO(
    val id: Int = 0,
    val week: ClassWeek = ClassWeek.BOTH,
    val teacher: String = "",
    val name: String = "",
    val type: ClassType = ClassType.COURSE,
    val time: String = "",
    val day: WeekDays = WeekDays.MONDAY,
    val room: String = ""
) : Parcelable