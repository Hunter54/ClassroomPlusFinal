package com.ionutv.classroomplus.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.ionutv.classroomplus.models.Attendance
import com.ionutv.classroomplus.models.FirebaseEvent
import com.ionutv.classroomplus.repositories.ClassroomRepository
import com.ionutv.classroomplus.services.RealtimeDatabaseService
import com.ionutv.classroomplus.utils.observeOnce

class ManageAttendancesViewModel(classroomRepository: ClassroomRepository) : ViewModel() {
    var onCoursesWhereTeacher = classroomRepository.onCoursesWhereTeacher
    private val _onAdvertiseClicked = SingleLiveEvent<Void>()
    val onAdvertiseClicked: LiveData<Void>
        get() = _onAdvertiseClicked
    private val _onActiveAttendancesChanged = MutableLiveData<List<Attendance>>()
    val onActiveAttendancesChanged: LiveData<List<Attendance>>
        get() = _onActiveAttendancesChanged
    private val _onAttendanceDeleteSuccess = SingleLiveEvent<String?>()
    val onAttendanceDelete: LiveData<String?>
        get() = _onAttendanceDeleteSuccess
    private var attendanceLiveData: LiveData<Pair<String, FirebaseEvent>>
    private var childEventListener: ChildEventListener
    private val attendanceObserver = Observer<Pair<String, FirebaseEvent>> { pair ->
        when (pair.second) {
            FirebaseEvent.Added -> {
                RealtimeDatabaseService.getActiveAttendance(pair.first).observeOnce { attendance ->
                    _onActiveAttendancesChanged.value?.let { oldList ->
                        val newList = oldList.toMutableList()
                        newList.add(attendance)
                        _onActiveAttendancesChanged.value = newList
                    } ?: run {
                        _onActiveAttendancesChanged.value = mutableListOf(attendance)
                    }
                }
            }
            FirebaseEvent.Changed -> {
            }
            FirebaseEvent.Removed -> {
                _onActiveAttendancesChanged.value?.let { oldList ->
                    val newList = oldList.toMutableList()
                    newList.removeIf { course -> course.courseId == pair.first }
                    _onActiveAttendancesChanged.value = newList
                } ?: run {
                    _onActiveAttendancesChanged.value = mutableListOf()
                }
            }
        }

    }

    init {
        val pair = addCreatedAttendanceListener()
        attendanceLiveData = pair.first
        childEventListener = pair.second
    }

    private fun addCreatedAttendanceListener(): Pair<LiveData<Pair<String, FirebaseEvent>>, ChildEventListener> {
        val pair = RealtimeDatabaseService.addCreatedAttendancesListener()
        pair.first.observeForever(attendanceObserver)
        return pair
    }

    fun onAdvertiseClicked() {
        _onAdvertiseClicked.call()
    }

    fun uploadAttendance(attendance: Attendance) {
        RealtimeDatabaseService.addAttendanceRequest(attendance)
        val list = mutableListOf(attendance)
//        _onActiveAttendancesChanged.value = list
    }

    fun getActiveAttendances(){
        RealtimeDatabaseService.getActiveTeacherAttendances().observeOnce {
            _onActiveAttendancesChanged.value = mutableListOf(it)
        }
    }

    private fun removeAttendanceListener() {
        attendanceLiveData.removeObserver(attendanceObserver)
        RealtimeDatabaseService.removeAttendancesListener(childEventListener)
    }

    fun deleteActiveAttendance(classroomId : String){
        RealtimeDatabaseService.deleteActiveAttendance(classroomId).observeOnce{
            _onAttendanceDeleteSuccess.value = it
        }
    }

    override fun onCleared() {
        super.onCleared()
        removeAttendanceListener()
    }
}