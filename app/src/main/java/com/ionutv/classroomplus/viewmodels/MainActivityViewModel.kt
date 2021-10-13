package com.ionutv.classroomplus.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.services.sheets.v4.model.SpreadsheetProperties
import com.ionutv.classroomplus.utils.observeOnce
import com.ionutv.classroomplus.repositories.ClassroomRepository
import com.ionutv.classroomplus.repositories.GoogleSheetsRepository
import com.ionutv.classroomplus.services.GoogleSignInService
import com.ionutv.classroomplus.services.RealtimeDatabaseService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

class MainActivityViewModel(
    private val classroomRepository: ClassroomRepository,
    private val sheetsRepository: GoogleSheetsRepository
) : ViewModel() {
    private var onCoursesWhereStudent = classroomRepository.onCoursesWhereStudent
    private var onCoursesWhereTeacher = classroomRepository.onCoursesWhereTeacher
    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean>
        get() = _isConnected
    val allCourses = classroomRepository.allCourses
//    val courses: LiveData<List<Course>>
//        get() = _courses

    fun refreshCourses() {

    }

    fun initializeCourses() {
        viewModelScope.launch {
            kotlin.runCatching {
                classroomRepository.getAllTeacherStudentCourses()
                classroomRepository.getUserID()
            }.onFailure {
                when (it) {
                    is CancellationException -> throw it
                    else -> Log.e("Error fetching classes", it.message.toString())
                }
            }
                .onSuccess {
                    initFirebase()
                }
        }
    }

    private fun createSpreadSheet(spreadsheet: Spreadsheet, courseId: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                sheetsRepository.createSpreadSheet(spreadsheet)
            }.onSuccess {
                RealtimeDatabaseService.addSpreadSheetIdToCourse(courseId, it.spreadsheetId)
            }.onFailure {
                when (it) {
                    is CancellationException -> throw it
                    else -> Log.e("Error creating spreadsheet", it.message.toString())
                }
            }
        }
    }

    private suspend fun initTeacherCourses() {
        kotlin.runCatching {
            classroomRepository.getCoursesWhereTeacher()
        }.onSuccess { coursesList ->
            coursesList.forEach { courseEntry ->
                RealtimeDatabaseService.checkIfCourseExists(courseEntry.id).observeOnce {
                    if (!it) {
                        RealtimeDatabaseService.addCourse(courseEntry.id, courseEntry.name)
                        val newSpreadsheet = Spreadsheet().also { sheet ->
                            sheet.properties = SpreadsheetProperties().apply {
                                title = courseEntry.name
                            }
                        }
                        createSpreadSheet(newSpreadsheet, courseEntry.id)
                    } else {
                        RealtimeDatabaseService.checkIfTeacherExists(courseEntry.id).observeOnce {
                            if (!it) {
                                RealtimeDatabaseService.addTeacherToCourse(courseEntry.id)
                            }
                        }
                    }
                }
            }
        }.onFailure {
            when (it) {
                is CancellationException -> throw it
                else -> Log.e("Error fetching users", it.message.toString())
            }
        }
    }

    private suspend fun initStudentCourses() {
        kotlin.runCatching {
            classroomRepository.getCoursesWhereStudent()
        }.onFailure {
            when (it) {
                is CancellationException -> throw it
                else -> Log.e("Error fetching users", it.message.toString())
            }
        }.onSuccess { courseList ->
            courseList.forEach { course ->
                RealtimeDatabaseService.checkIfStudentCourseExists(course.id).observeOnce {
                    if (it?.value != null) {
                        if (it.key?.contains(classroomRepository.userId) != true) {
                            RealtimeDatabaseService.addStudentToCourse(course.id)
                        }
                    }
                }
            }
        }
    }

    fun clearUserDatabaseCourses() {
        classroomRepository.deletedStudentCourses.forEach {
            RealtimeDatabaseService.removeStudentFromCourse(it.id)
        }
        classroomRepository.deletedTeacherCourses.forEach {
            RealtimeDatabaseService.removeTeacherFromCourse(it.id)
        }
    }

    suspend fun checkInternet() {
        withContext(Dispatchers.IO) {
            kotlin.runCatching {
                val timeout = 1500
                val sock = Socket()
                val sockAddr = InetSocketAddress("8.8.8.8", 53)
                sock.connect(sockAddr, timeout)
                sock.close()
            }.onSuccess {
                _isConnected.value = true
            }.onFailure {
                _isConnected.value = false
            }
        }
    }

    private fun initFirebase() {
        viewModelScope.launch {
            initTeacherCourses()
            GoogleSignInService.notificationTokenRefreshed?.let {
                RealtimeDatabaseService.updateFirebaseNotificationToken(it)
            }
        }
        viewModelScope.launch {
            initStudentCourses()
            clearUserDatabaseCourses()
        }
    }
}