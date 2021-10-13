package com.ionutv.classroomplus.services

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.services.classroom.ClassroomScopes
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ionutv.classroomplus.R
import com.ionutv.classroomplus.models.App
import com.ionutv.classroomplus.models.User
import com.ionutv.classroomplus.utils.observeOnce
import com.ionutv.classroomplus.viewmodels.SingleLiveEvent

object GoogleSignInService {

    var googleSignInOptions: GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(App.instance.getString(R.string.default_web_client_id))
            .requestEmail()
            .requestScopes(
                Scope(ClassroomScopes.CLASSROOM_COURSES_READONLY),
                Scope(ClassroomScopes.CLASSROOM_ROSTERS_READONLY),
                Scope(SheetsScopes.DRIVE_FILE)
            )
            .build()

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val TAG = this::class.qualifiedName
    private var returnedUser = MutableLiveData<User?>()
    lateinit var signedInUser: User

    var account: GoogleSignInAccount? = null
    lateinit var notificationToken: String
    var notificationTokenRefreshed : String? = null

    fun returnSignedIn(): MutableLiveData<User?> {
        val result = MutableLiveData<User?>()
        returnedUser.observeOnce {
            result.value = it
        }
        return result
    }

    fun handleSignInActivityResult(requestCode: Int, data: Intent?) {
        if (requestCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                this.account = task.getResult(ApiException::class.java)
                this.account?.also { account ->
                    firebaseAuthWithGoogle(account)
                } ?: run {
                    returnedUser.value = null
                    Log.w(TAG, "Google sign in failed, account was null!")
                }
            } catch (e: ApiException) {
                returnedUser.value = null
                Log.w(TAG, "Google sign in failed", e)
            }
        }

    }

    fun signOutFirebase() {
        FirebaseAuth.getInstance().signOut()
    }

    fun signOutGoogle(): SingleLiveEvent<Void> {
        val result = SingleLiveEvent<Void>()
        GoogleSignIn.getClient(App.instance, googleSignInOptions).signOut().addOnSuccessListener {
            result.call()
        }
        return result
    }

    fun isUserSignedIn(): Boolean {
        if (firebaseAuth.currentUser != null) {
            firebaseAuth.currentUser?.getIdToken(false)
            return true
        }
        return false
    }

    fun getUser(): User? {
        val firebaseUser = firebaseAuth.currentUser ?: return null
        signedInUser = User(
            firebaseUser.uid,
            firebaseUser.displayName,
            firebaseUser.email,
            null,
            null
        )
        return signedInUser
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        var user: User
        this.account = account
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser == null) {
                        returnedUser.value = null
                    } else {
                        user = User(
                            firebaseUser.uid,
                            firebaseUser.displayName,
                            firebaseUser.email,
                            notificationToken,
                            true,
                        )
                        this.signedInUser = user
                        returnedUser.value = user
                    }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    returnedUser.value = null
                }
            }
    }
}