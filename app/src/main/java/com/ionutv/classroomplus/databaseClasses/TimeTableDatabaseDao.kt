package com.ionutv.classroomplus.databaseClasses

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ionutv.classroomplus.models.ClassWeek
import com.ionutv.classroomplus.models.TimeTableEntry
import com.ionutv.classroomplus.models.WeekDays

@Dao
interface TimeTableDatabaseDao{
    @Query("SELECT * FROM user_timetable ORDER BY day ASC, time ASC")
    fun getAll(): LiveData<List<TimeTableEntry>>

    @Query("SELECT * FROM user_timetable ORDER BY day ASC, time ASC" )
    suspend fun getAllAsync() : List<TimeTableEntry>

    @Query("SELECT * FROM user_timetable WHERE week = :week OR week = :both ORDER BY day ASC, time ASC")
    fun getWeek(week : ClassWeek, both : ClassWeek = ClassWeek.BOTH) : LiveData<List<TimeTableEntry>>

    @Query("SELECT * FROM user_timetable WHERE day = :day")
    fun getDay(day : WeekDays) : LiveData<List<TimeTableEntry>>

    @Query("SELECT * FROM user_timetable WHERE id = :id")
    suspend fun getId(id : Int) : TimeTableEntry

    @Insert
    suspend fun insert(timeTable : TimeTableEntry)

    @Update
    suspend fun update(timeTable: TimeTableEntry)

    @Query("DELETE FROM user_timetable")
    suspend fun deleteAll() : Int

    @Query("DELETE FROM user_timetable WHERE id = :id")
    suspend fun deleteId(id : Int) : Int
}