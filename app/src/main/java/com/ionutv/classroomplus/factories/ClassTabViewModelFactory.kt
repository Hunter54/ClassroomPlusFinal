package com.ionutv.classroomplus.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ionutv.classroomplus.repositories.TimeTableRepository
import com.ionutv.classroomplus.viewmodels.ClassesTabViewModel

class ClassTabViewModelFactory (
    private val dataSource: TimeTableRepository,
    private val week: String) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ClassesTabViewModel::class.java)) {
                return ClassesTabViewModel(dataSource, week) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }