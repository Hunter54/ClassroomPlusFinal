package com.ionutv.classroomplus.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionutv.classroomplus.R
import com.ionutv.classroomplus.models.App
import com.ionutv.classroomplus.models.TimeTableEntry
import com.ionutv.classroomplus.repositories.TimeTableRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class ClassesTabViewModel(repository: TimeTableRepository, week: String) : ViewModel() {
    val onClassesList : LiveData<List<TimeTableEntry>> = when (week) {
        "Odd" -> repository.classesOdd
        else -> repository.classesEven
    }

    var selectedClass : TimeTableEntry? = null

    private val _onError = SingleLiveEvent<Int>()
    val onError: LiveData<Int>
        get() = _onError
    private val repository = App.appContainer.timeTableRepository

    fun getClass(itemId : Int){
        viewModelScope.launch {
            selectedClass = repository.getClass(itemId)
        }
    }

    fun editClass(timeTableEntry: TimeTableEntry){
        viewModelScope.launch {
            kotlin.runCatching {
                repository.updateClass(timeTableEntry)
            }.onFailure {
                when(it){
                    is CancellationException -> throw it
                    else -> _onError.value =
                        R.string.unable_to_edit_message
                }
            }
        }
    }

    fun undoDelete() {
        viewModelScope.launch {
            kotlin.runCatching {
                repository.lastDeletedClass?.let {
                    repository.addClass(it)
                } ?: run {
                    throw NullPointerException("The object is null")
                }
            }.onFailure {
                when (it) {
                    is CancellationException -> throw it
                    else -> _onError.value =
                        R.string.unable_to_undo_message
                }
            }
        }

    }
}