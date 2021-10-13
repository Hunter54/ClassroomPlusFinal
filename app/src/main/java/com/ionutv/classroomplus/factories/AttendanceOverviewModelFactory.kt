package com.ionutv.classroomplus.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ionutv.classroomplus.repositories.GoogleSheetsRepository
import com.ionutv.classroomplus.viewmodels.AttendanceOverviewViewModel

class AttendanceOverviewModelFactory(
    private val dataSource: GoogleSheetsRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttendanceOverviewViewModel::class.java)) {
            return AttendanceOverviewViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}