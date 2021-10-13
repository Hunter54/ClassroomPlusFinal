package com.ionutv.classroomplus.databaseClasses

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ionutv.classroomplus.Constants
import com.ionutv.classroomplus.models.CourseEntry

@Database(entities = [CourseEntry::class], version = 1, exportSchema = false)
abstract class ClassroomCoursesDatabase : RoomDatabase() {
    abstract val courseDatabaseDao: ClassroomCoursesDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: ClassroomCoursesDatabase? = null

        fun getInstance(context: Context): ClassroomCoursesDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ClassroomCoursesDatabase::class.java, Constants.COURSES_ROOM_TABLE
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}