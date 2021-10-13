package com.ionutv.classroomplus.viewmodels

import androidx.lifecycle.*
import com.google.firebase.database.ChildEventListener
import com.ionutv.classroomplus.models.Attendance
import com.ionutv.classroomplus.models.FirebaseEvent
import com.ionutv.classroomplus.repositories.ClassroomRepository
import com.ionutv.classroomplus.services.RealtimeDatabaseService
import com.ionutv.classroomplus.utils.observeOnce

class AttendanceViewModel(classroomRepository: ClassroomRepository) : ViewModel() {
    private var attendanceLiveData: LiveData<Pair<String, FirebaseEvent>>
    private var childEventListener: ChildEventListener
    private val attendanceObserver = Observer<Pair<String, FirebaseEvent>> { pair ->
        when (pair.second) {
            FirebaseEvent.Added -> {
                RealtimeDatabaseService.getActiveAttendance(pair.first).observeOnce { attendance ->
                    _currentAttendances.value?.let { oldList ->
                        val newList = oldList.toMutableList()
                        newList.add(attendance)
                        _currentAttendances.value = newList
                    } ?: run {
                        _currentAttendances.value = mutableListOf(attendance)
                    }
                }
            }
            FirebaseEvent.Changed -> {
            }
            FirebaseEvent.Removed -> {
                _currentAttendances.value?.let { oldList ->
                    val newList = oldList.toMutableList()
                    newList.removeIf { course -> course.courseId == pair.first }
                    _currentAttendances.value = newList
                } ?: run {
                    _currentAttendances.value = mutableListOf()
                }
            }
        }

    }

    init {
        val pair = addAttendanceListener()
        attendanceLiveData = pair.first
        childEventListener = pair.second
    }

    private var onCoursesWhereStudent = classroomRepository.onCoursesWhereStudent

    private val _currentAttendances = MutableLiveData<List<Attendance>>()
    val currentAttendances: LiveData<List<Attendance>>
        get() = _currentAttendances

    private fun addAttendanceListener(): Pair<LiveData<Pair<String, FirebaseEvent>>, ChildEventListener> {
        val pair = RealtimeDatabaseService.addActiveAttendancesListener()
        pair.first.observeForever(attendanceObserver)
        return pair
    }

    private fun removeAttendanceListener() {
        attendanceLiveData.removeObserver(attendanceObserver)
        RealtimeDatabaseService.removeAttendancesListener(childEventListener)
    }

    fun retrieveActiveAttendances() {
        _currentAttendances.value = mutableListOf()
        RealtimeDatabaseService.retrieveActiveUserAttendances().observeOnce {
            val classroomAttendanceList = it
            for (item in classroomAttendanceList) {
                RealtimeDatabaseService.getActiveAttendance(item).observeOnce { attendance ->
                    val currentList = _currentAttendances.value?.toMutableList() ?: mutableListOf()
                    currentList.add(attendance)
                    _currentAttendances.value = currentList
                }
            }
        }
    }

    fun registerAttendance(attendance: Attendance) {
        RealtimeDatabaseService.addAttendanceRegistration(attendance)
    }

    override fun onCleared() {
        super.onCleared()
        removeAttendanceListener()
    }
}