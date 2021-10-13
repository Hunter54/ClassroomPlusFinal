package com.ionutv.classroomplus.repositories

import androidx.lifecycle.LiveData
import com.ionutv.classroomplus.models.CourseEntry
import com.ionutv.classroomplus.models.Role
import com.ionutv.classroomplus.databaseClasses.ClassroomCoursesDatabaseDao
import com.ionutv.classroomplus.services.GoogleClassroomService
import com.ionutv.classroomplus.services.RealtimeDatabaseService
import com.ionutv.classroomplus.utils.toEntry

class ClassroomRepository(
    private val classroomService: GoogleClassroomService,
    private val classroomCoursesDao: ClassroomCoursesDatabaseDao
) {
    var userId: String = ""
    val allCourses: LiveData<List<CourseEntry>> = classroomCoursesDao.getAll()
    val onCoursesWhereStudent: LiveData<List<CourseEntry>> =
        classroomCoursesDao.getAllWhereStudent()
    val onCoursesWhereTeacher: LiveData<List<CourseEntry>> =
        classroomCoursesDao.getAllWhereTeacher()
    val deletedStudentCourses = mutableListOf<CourseEntry>()
    val deletedTeacherCourses = mutableListOf<CourseEntry>()

    suspend fun getAllTeacherStudentCourses() {
        val coursesStudent = classroomService.getCoursesWhereStudent().map { it.toEntry(Role.STUDENT) }
        val coursesTeacher = classroomService.getCoursesWhereTeacher().map { it.toEntry(Role.TEACHER) }
        coursesStudent.forEach {
            classroomCoursesDao.insert(it)
        }
        coursesTeacher.forEach {
            classroomCoursesDao.insert(it)
        }
        val storedStudentCourses = getCoursesWhereStudent()
        storedStudentCourses.forEach {
            if(it !in coursesStudent){
                deletedStudentCourses.add(it)
                classroomCoursesDao.deleteCourse(it.id)
            }
        }
        val storedTeacherCourses = getCoursesWhereTeacher()
        storedTeacherCourses.forEach {
            if(it !in coursesTeacher){
                deletedTeacherCourses.add(it)
                classroomCoursesDao.deleteCourse(it.id)
            }
        }
    }

    suspend fun getCoursesWhereStudent(): List<CourseEntry> =
        classroomCoursesDao.getAllWhereStudentAsync()

    suspend fun getCoursesWhereTeacher(): List<CourseEntry> =
        classroomCoursesDao.getAllWhereTeacherAsync()

    suspend fun getUserID(): String {
        userId = classroomService.getUserID()
        return userId
    }

    suspend fun deleteAll() : Int{
        return classroomCoursesDao.deleteAll()
    }

}