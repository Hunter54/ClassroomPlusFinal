package com.ionutv.classroomplus.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ionutv.classroomplus.repositories.ClassroomRepository
import com.ionutv.classroomplus.repositories.TimeTableRepository
import com.ionutv.classroomplus.viewmodels.TimeTableDialogViewModel

class TimeTableDialogViewModelFactory (
    private val classroomDataSource: ClassroomRepository,
    private val timeTableDataSource : TimeTableRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimeTableDialogViewModel::class.java)) {
            return TimeTableDialogViewModel(classroomDataSource, timeTableDataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}