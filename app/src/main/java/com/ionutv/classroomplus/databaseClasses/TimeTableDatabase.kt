package com.ionutv.classroomplus.databaseClasses

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ionutv.classroomplus.Constants
import com.ionutv.classroomplus.models.TimeTableEntry
import com.ionutv.classroomplus.utils.Converter

@Database(entities = [TimeTableEntry::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class TimeTableDatabase : RoomDatabase() {
    abstract val timeTableDatabaseDao: TimeTableDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: TimeTableDatabase? = null

        fun getInstance(context: Context): TimeTableDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                TimeTableDatabase::class.java, Constants.TIMETABLE_ROOM_TABLE)
                .fallbackToDestructiveMigration()
                .build()
    }
}