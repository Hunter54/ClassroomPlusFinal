package com.ionutv.classroomplus.activities

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.ionutv.classroomplus.R
import com.ionutv.classroomplus.databinding.ActivityLoginBinding
import com.ionutv.classroomplus.Constants
import com.ionutv.classroomplus.models.App
import com.ionutv.classroomplus.models.User
import com.ionutv.classroomplus.services.MyFirebaseMessagingService
import com.ionutv.classroomplus.viewmodels.LoginViewModel

class LoginActivity : BaseActivity() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding
    private var activityRegisterListenerMethod = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        viewModel.handleSignInActivityResult(result.resultCode, result.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.btnNextScreen.setOnClickListener {
            navigateToMainActivity()
        }
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.navigateToNextScreenIfUserIsSignedIn()
        binding.btnGoogleSignIn.setOnClickListener {
//            Toast.makeText(this, "Pressed", Toast.LENGTH_LONG).show()
            viewModel.signIn()
        }
        MyFirebaseMessagingService().getToken()
    }

    private val showGoogleSignInObserver = Observer<GoogleSignInOptions> { googleSignInOptions ->
        val googleSignInClient = GoogleSignIn.getClient(App.instance, googleSignInOptions)
        val signInIntent = googleSignInClient.signInIntent
        activityRegisterListenerMethod.launch(signInIntent)

    }

    private val navigateToMainActivityObserver = Observer<User> {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.LOGIN_USER_INTENT, it as Parcelable)
        startActivity(intent)
        finish()
    }

    private val showSnackbar = Observer<String> {
        Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
    }

    override fun connectViewModelEvents() {
        viewModel.showGoogleSignInActivity.observe(this, showGoogleSignInObserver)
        viewModel.onShowSnackBar.observe(this, showSnackbar)
        viewModel.onNavigateToMainActivity.observe(this, navigateToMainActivityObserver)
    }

    override fun disconnectViewModelEvents() {
        viewModel.showGoogleSignInActivity.removeObserver(showGoogleSignInObserver)
        viewModel.onShowSnackBar.removeObserver(showSnackbar)
        viewModel.onNavigateToMainActivity.removeObserver(navigateToMainActivityObserver)
    }

    override fun onResume() {
        super.onResume()
        connectViewModelEvents()
    }

    override fun onPause() {
        super.onPause()
        disconnectViewModelEvents()
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}