package com.ionutv.classroomplus.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionutv.classroomplus.models.TimeTableEntry
import com.ionutv.classroomplus.services.RealtimeDatabaseService
import com.ionutv.classroomplus.repositories.TimeTableRepository
import com.ionutv.classroomplus.utils.observeOnce
import com.ionutv.classroomplus.utils.toEntry
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class ClassesViewModel(private val repository: TimeTableRepository) : ViewModel() {

    private val _onIsBusy = MutableLiveData<Boolean>()
    val onIsBusy : LiveData<Boolean>
    get() = _onIsBusy

    fun addClass(timeTableClass: TimeTableEntry) {
        viewModelScope.launch {
            kotlin.runCatching {
                repository.addClass(timeTableClass)
            }.onFailure {
                when (it) {
                    is CancellationException -> throw it
                    else -> Log.d("ClassesViewModel Fail to add", it.message.toString())
                }
            }
        }
    }

    fun restoreFromFirebase(){
        _onIsBusy.value = true
        RealtimeDatabaseService.getTimeTablesFromFirebase().observeOnce{ timeTablesList ->
            viewModelScope.launch {
                kotlin.runCatching {
                    repository.deleteAll()
                    timeTablesList.forEach {
                        repository.addClass(it.toEntry())
                    }
                }.onSuccess {
                    _onIsBusy.value = false
                }.onFailure {
                    when (it) {
                        is CancellationException -> throw it
                        else -> Log.d("ClassesViewModel Fail to add", it.message.toString())
                    }
                    _onIsBusy.value = false
                }
            }
        }
    }

    fun saveToFirebase(){
        _onIsBusy.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                val allTimeTables = repository.getAllClasses()
                RealtimeDatabaseService.saveTimeTablesToFirebase(allTimeTables)
            }.onSuccess {
                _onIsBusy.value = false
            }.onFailure {
                when (it) {
                    is CancellationException -> throw it
                    else -> Log.d("ClassesViewModel Fail to add", it.message.toString())
                }
                _onIsBusy.value = false
            }
        }
    }



}