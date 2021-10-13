package com.ionutv.classroomplus.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionutv.classroomplus.models.App
import kotlinx.coroutines.launch

class SelectedTimeTableDialogViewModel:ViewModel() {

    private val _onEditTapped = SingleLiveEvent<Void>()
    val onEditTapped : LiveData<Void>
    get () = _onEditTapped
    private val _onDeleteTapped = SingleLiveEvent<Void>()
    val onDeleteTapped : LiveData<Void>
        get () = _onDeleteTapped

    fun onDelete(){
        _onDeleteTapped.call()
    }
    fun onEdit(){
        _onEditTapped.call()
    }
    private val repository = App.appContainer.timeTableRepository
    fun deleteClass(id : Int){
        viewModelScope.launch {
            repository.deleteId(id)
        }
    }
}