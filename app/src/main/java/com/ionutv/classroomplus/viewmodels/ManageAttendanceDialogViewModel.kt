package com.ionutv.classroomplus.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ionutv.classroomplus.R
import com.ionutv.classroomplus.models.Attendance
import com.ionutv.classroomplus.repositories.ClassroomRepository

class ManageAttendanceDialogViewModel(classroomRepository: ClassroomRepository) : ViewModel() {
    var onCoursesWhereTeacher = classroomRepository.onCoursesWhereTeacher
    private val _currentClassName = MutableLiveData("")
    val currentClassName: LiveData<String>
        get() = _currentClassName
    private val _currentDuration = MutableLiveData("")
    val currentDuration: LiveData<String>
        get() = _currentDuration

    private val _onError = SingleLiveEvent<Int>()
    val onError: LiveData<Int>
        get() = _onError

    private val _onStart = SingleLiveEvent<Void>()
    val onStart: LiveData<Void>
        get() = _onStart

    private val _onCancel = SingleLiveEvent<Void>()
    val onCancel: LiveData<Void>
        get() = _onCancel

    fun onCurrentClassNameChanged(value: CharSequence) {
        if (value.isNotBlank() && _currentClassName.value != value) {
            _currentClassName.value = value.toString()
        } else if (value.isBlank()) {
            _currentClassName.value = ""
        }
    }

    fun onCurrentDurationChanged(value: CharSequence) {
        if (value.isNotBlank() && _currentDuration.value != value) {
            _currentDuration.value = value.toString()
        } else if (value.isBlank()) {
            _currentDuration.value = ""
        }
    }

    fun validateData(): Boolean {
        val regex = "^[0-9]+$".toRegex()
        return when {
            _currentClassName.value.isNullOrEmpty() -> {
                _onError.value = R.string.no_valid_class_name_error
                false
            }
            !(_currentDuration.value?.matches(regex) ?: false) -> {
                _onError.value = R.string.no_valid_duration
                false
            }
            else -> true
        }
    }

    fun createAttendance(): Attendance {
        val currentTime = System.currentTimeMillis()
        return _currentClassName.value?.let { className ->
            val courseId = onCoursesWhereTeacher.value?.first { it.name == className }?.id
            courseId?.let {
                Attendance(courseId, className, currentTime, currentDuration.value?.toInt() ?: 5)
            }
        } ?: run {
            Attendance()
        }
    }

    fun onStartPressed() {
        _onStart.call()
    }

    fun onCancelPressed() {
        _onCancel.call()
    }
}