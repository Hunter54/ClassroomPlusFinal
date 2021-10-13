package com.ionutv.classroomplus.models;


import android.os.Parcelable
import androidx.room.*
import com.ionutv.classroomplus.Constants
import kotlinx.parcelize.Parcelize

@Entity(tableName = Constants.TIMETABLE_ROOM_TABLE)
@Parcelize
data class TimeTableEntry(
    @PrimaryKey(autoGenerate = true) val id:Int,
    @ColumnInfo(name = "week") val week:ClassWeek,
    @ColumnInfo(name = "teacher") val teacher:String,
    @ColumnInfo(name = "name") val name:String,
    @ColumnInfo(name = "type") val type:ClassType,
    @ColumnInfo(name = "time") val time:String,
    @ColumnInfo(name = "day") val day:WeekDays,
    @ColumnInfo(name = "room") val room:String
    ) : Parcelable