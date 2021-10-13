package com.ionutv.classroomplus.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionutv.classroomplus.ui.ClassroomPlusSharedPreferences
import com.ionutv.classroomplus.repositories.ClassroomRepository
import com.ionutv.classroomplus.repositories.TimeTableRepository
import com.ionutv.classroomplus.services.GoogleSignInService
import com.ionutv.classroomplus.utils.observeOnce
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AccountViewModel(
    private val classroomRepository: ClassroomRepository,
    private val timeTableRepository: TimeTableRepository
) : ViewModel() {
    private val signedInUser = GoogleSignInService.signedInUser
    private val _onAccountName = MutableLiveData<String>()
    val onAccountName: LiveData<String>
        get() = _onAccountName
    private val _onAccountEmail = MutableLiveData<String>()
    val onAccountEmail: LiveData<String>
        get() = _onAccountEmail

    init {
        _onAccountEmail.value = signedInUser.email.toString()
        _onAccountName.value = signedInUser.name.toString()
    }

    private val _onLogOut = SingleLiveEvent<Void>()
    val onLogOut: LiveData<Void>
        get() = _onLogOut

    fun signOut(){
        GoogleSignInService.signOutFirebase()
    }

    fun completeLogOut() {
        GoogleSignInService.signOutGoogle().observeOnce{
            viewModelScope.launch {
                kotlin.runCatching {
                    classroomRepository.deleteAll()
                    timeTableRepository.deleteAll()
                    ClassroomPlusSharedPreferences.clear()
                    delay(300)
                }.onSuccess {
                    _onLogOut.call()
                }.onFailure {
                    when (it) {
                        is CancellationException -> throw it
                        else -> Log.d("AccountViewModel Fail to delete", it.message.toString())
                    }
                }
            }
        }

    }

}