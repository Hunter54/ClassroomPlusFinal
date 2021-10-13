package com.ionutv.classroomplus.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ionutv.classroomplus.R
import com.ionutv.classroomplus.models.*
import com.ionutv.classroomplus.repositories.ClassroomRepository
import com.ionutv.classroomplus.repositories.TimeTableRepository

class TimeTableDialogViewModel(private val classroomRepository: ClassroomRepository, private val timeTableRepository: TimeTableRepository) : ViewModel() {
    val onClassroomCourses = classroomRepository.allCourses

    private val _currentClassName = MutableLiveData("")
    val currentClassName: LiveData<String>
        get() = _currentClassName
    private val _currentTeacherName = MutableLiveData("")
    val currentTeacherName: LiveData<String>
        get() = _currentTeacherName
    private val _currentRoomName = MutableLiveData("")
    val currentRoomName: LiveData<String>
        get() = _currentRoomName
    private val _currentTime = MutableLiveData("")
    val currentTime: LiveData<String>
        get() = _currentTime

    private val _onError = SingleLiveEvent<Int>()
    val onError
    get() = _onError

    private val _currentClassType = MutableLiveData(ClassType.COURSE)
    val currentSelectedClassType: LiveData<ClassType>
        get() = _currentClassType
    private val _currentClassDay = MutableLiveData(WeekDays.MONDAY)
    val currentSelectedClassDay: LiveData<WeekDays>
        get() = _currentClassDay
    private val _currentClassWeek = MutableLiveData(ClassWeek.BOTH)
    val currentClassWeek: LiveData<ClassWeek>
        get() = _currentClassWeek

    private val _onCancelTapped = SingleLiveEvent<Void>()
    val onCancelTapped: LiveData<Void>
        get() = _onCancelTapped

    val classroomCourses : Array<String>
    get(){
        val courseList = classroomRepository.allCourses.value
        return courseList?.let { courses ->
            Array(courses.size){ courses[it].name}
        } ?: arrayOf()
    }

    var selectedId = 0
    private set

    fun onCurrentClassNameChanged(value: CharSequence) {
        if (value.isNotBlank() && _currentClassName.value != value) {
            _currentClassName.value = value.toString()
        } else if (value.isBlank()) {
            _currentClassName.value = ""
        }
    }

    fun onCurrentTeacherNameChanged(value: CharSequence) {
        if (value.isNotBlank() && _currentTeacherName.value != value) {
            _currentTeacherName.value = value.toString()
        } else if (value.isBlank()) {
            _currentTeacherName.value = ""
        }
    }

    fun onCurrentRoomNameChanged(value: CharSequence) {
        if (value.isNotBlank() && _currentRoomName.value != value) {
            _currentRoomName.value = value.toString()
        } else if (value.isBlank()) {
            _currentRoomName.value = ""
        }
    }

    fun onCurrentTimeChanged(value: CharSequence) {
        if (value.isNotBlank() && _currentTime.value != value) {
            _currentTime.value = value.toString()
        } else if (value.isBlank()) {
            _currentTime.value = ""
        }
    }

    fun onCurrentClassTypeChanged(view: Int) {
        when (view) {
            R.id.choice_chip1 -> _currentClassType.value = ClassType.COURSE
            R.id.choice_chip2 -> _currentClassType.value = ClassType.LAB
        }
    }

    fun onCurrentClassDayChanged(view: Int) {
        when (view) {
            R.id.day_choice_chip1 -> _currentClassDay.value = WeekDays.MONDAY
            R.id.day_choice_chip2 -> _currentClassDay.value = WeekDays.TUESDAY
            R.id.day_choice_chip3 -> _currentClassDay.value = WeekDays.WEDNESDAY
            R.id.day_choice_chip4 -> _currentClassDay.value = WeekDays.THURSDAY
            R.id.day_choice_chip5 -> _currentClassDay.value = WeekDays.FRIDAY
        }
    }

    fun validateData(): Boolean{
        val regex = "^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$".toRegex()
        return when {
            _currentClassName.value.isNullOrEmpty() -> {
                _onError.value = R.string.no_class_name_error
                false
            }
            _currentRoomName.value.isNullOrEmpty() -> {
                _onError.value = R.string.no_room_error
                false
            }
            _currentTime.value.isNullOrEmpty() || !(_currentTime.value?.matches(regex) ?: false) -> {
                _onError.value = R.string.no_time_error
                false
            }
            else -> true
        }
    }

    fun onCurrentClassWeekChanged(view: Int) {
        when (view) {
            R.id.week_choice_chip1 -> _currentClassWeek.value = ClassWeek.ODD
            R.id.week_choice_chip2 -> _currentClassWeek.value = ClassWeek.EVEN
            R.id.week_choice_chip3 -> _currentClassWeek.value = ClassWeek.BOTH
        }
    }

    fun createTimeTableEntry(): TimeTableEntry {
        var string = _currentTime.value!!.toString()
        val split = string.split(':')
        if(split[0].count()==1 && split.size>1){
            string = "0$string"
            _currentTime.value = string
        }
        return TimeTableEntry(
            selectedId,
            currentClassWeek.value!!,
            currentTeacherName.value!!,
            currentClassName.value!!,
            _currentClassType.value!!,
            _currentTime.value!!,
            _currentClassDay.value!!,
            currentRoomName.value!!
        )
    }
    fun setupSelectedItem(itemId : Int){
        val selectedTimeTable = timeTableRepository.lastRetrievedClass
        if(itemId == selectedTimeTable?.id){
            selectedTimeTable.also {
                selectedId = it.id
                _currentClassName.value = it.name
                _currentClassWeek.value = it.week
                _currentClassType.value = it.type
                _currentClassDay.value = it.day
                _currentRoomName.value = it.room
                _currentTeacherName.value = it.teacher
                _currentTime.value = it.time
            }

        }
    }

    fun onCancelClicked() {
        _onCancelTapped.call()
    }

}