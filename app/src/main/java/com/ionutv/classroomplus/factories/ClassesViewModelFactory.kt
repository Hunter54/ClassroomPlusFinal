package com.ionutv.classroomplus.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ionutv.classroomplus.repositories.TimeTableRepository
import com.ionutv.classroomplus.viewmodels.ClassesViewModel

class ClassesViewModelFactory(
    private val dataSource: TimeTableRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClassesViewModel::class.java)) {
            return ClassesViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}