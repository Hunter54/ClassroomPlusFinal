package com.ionutv.classroomplus.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ionutv.classroomplus.repositories.ClassroomRepository
import com.ionutv.classroomplus.repositories.GoogleSheetsRepository
import com.ionutv.classroomplus.viewmodels.MainActivityViewModel

class MainActivityViewModelFactory(
    private val classroomRepository: ClassroomRepository,
    private val googleSheetsRepository: GoogleSheetsRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(classroomRepository, googleSheetsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}