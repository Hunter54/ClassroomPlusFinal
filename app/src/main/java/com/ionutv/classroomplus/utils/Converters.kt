package com.ionutv.classroomplus.utils

import androidx.room.TypeConverter
import com.ionutv.classroomplus.models.WeekDays

class Converter{
    @TypeConverter
    fun fromWeekDay(value : WeekDays) : Int {
        return value.ordinal
    }
    @TypeConverter
    fun fromIntToWeekDay(value : Int) : WeekDays{
        return WeekDays.values()[value]
    }
}