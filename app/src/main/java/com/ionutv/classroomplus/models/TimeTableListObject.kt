package com.ionutv.classroomplus.models

sealed class TimeTableListObject{
    data class TimeTable(val timeTable : TimeTableEntry) : TimeTableListObject()
    data class Day(val day : WeekDays) : TimeTableListObject()
}
