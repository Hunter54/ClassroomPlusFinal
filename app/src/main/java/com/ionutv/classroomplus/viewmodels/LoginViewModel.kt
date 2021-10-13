package com.ionutv.classroomplus.viewmodels

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.ionutv.classroomplus.models.User
import com.ionutv.classroomplus.utils.observeOnce
import com.ionutv.classroomplus.services.GoogleSignInService
import com.ionutv.classroomplus.services.RealtimeDatabaseService

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val isUserSignedIn: Boolean
        get() = GoogleSignInService.isUserSignedIn()
    val showGoogleSignInActivity = SingleLiveEvent<GoogleSignInOptions>()
    private val navigateToMainActivity = SingleLiveEvent<User>()
    val onNavigateToMainActivity: LiveData<User>
        get() = navigateToMainActivity
    private val showSnackBar = SingleLiveEvent<String>()
    val onShowSnackBar: LiveData<String>
        get() = showSnackBar


    fun signIn() {
        GoogleSignInService.returnSignedIn().observeOnce {
            it?.also { user ->
                user.uid?.let { uid ->
                    RealtimeDatabaseService.checkIfUsersExists(uid).observeOnce {
                        Log.d("===========Signing IN", "with this message ===========")
                        if(!it){
                            RealtimeDatabaseService.createUser(user)
                        }
                        navigateToMainActivity.value = user
                    }
                }
//                navigateToMainActivity.value = it
            } ?: run {
                showSnackBar.value = "Authentication failed! Try again"
            }
        }
        showGoogleSignInActivity.value = GoogleSignInService.googleSignInOptions
    }

    fun navigateToNextScreenIfUserIsSignedIn() {
        if (isUserSignedIn) {
            GoogleSignInService.getUser()?.let {
                GoogleSignInService.account = GoogleSignIn.getLastSignedInAccount(getApplication())
                navigateToMainActivity.value = it
            }
        }
    }

    fun handleSignInActivityResult(requestCode: Int, data: Intent?) {
        GoogleSignInService.handleSignInActivityResult(requestCode, data)
    }

}