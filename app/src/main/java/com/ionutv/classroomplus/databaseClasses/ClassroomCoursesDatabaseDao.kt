package com.ionutv.classroomplus.databaseClasses

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ionutv.classroomplus.models.CourseEntry
import com.ionutv.classroomplus.models.Role

@Dao
interface ClassroomCoursesDatabaseDao{

    @Query("SELECT * FROM courses_timetable")
    fun getAll(): LiveData<List<CourseEntry>>

    @Query("SELECT * FROM courses_timetable WHERE role = :role ")
    fun getAllWhereStudent(role : Role = Role.STUDENT) : LiveData<List<CourseEntry>>

    @Query("SELECT * FROM courses_timetable WHERE role = :role ")
    fun getAllWhereTeacher(role : Role = Role.TEACHER) : LiveData<List<CourseEntry>>

    @Query("SELECT * FROM courses_timetable WHERE role = :role ")
    suspend fun getAllWhereStudentAsync(role : Role = Role.STUDENT) : List<CourseEntry>

    @Query("SELECT * FROM courses_timetable WHERE role = :role ")
    suspend fun getAllWhereTeacherAsync(role : Role = Role.TEACHER) : List<CourseEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(course : CourseEntry)

    @Query("DELETE FROM courses_timetable WHERE id = :courseId")
    suspend fun deleteCourse(courseId : String) : Int

    @Query("DELETE FROM courses_timetable")
    suspend fun deleteAll() : Int
}