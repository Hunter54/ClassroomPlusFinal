package com.ionutv.classroomplus.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ionutv.classroomplus.repositories.ClassroomRepository
import com.ionutv.classroomplus.viewmodels.ManageAttendancesViewModel

class ManageAttendancesViewModelFactory(
    private val dataSource: ClassroomRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ManageAttendancesViewModel::class.java)) {
            return ManageAttendancesViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}