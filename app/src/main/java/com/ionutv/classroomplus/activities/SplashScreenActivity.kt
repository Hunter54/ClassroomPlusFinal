package com.ionutv.classroomplus.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.ionutv.classroomplus.Constants
import com.ionutv.classroomplus.R
import com.ionutv.classroomplus.databinding.ActivitySplashScreenBinding
import com.ionutv.classroomplus.services.GoogleSignInService
import com.ionutv.classroomplus.services.MyFirebaseMessagingService
import com.ionutv.classroomplus.utils.observeOnce

class SplashScreenActivity : BaseActivity() {

    private lateinit var binding : ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        createNotificationChannel()
        MyFirebaseMessagingService().getToken().observeOnce{
            Log.d("=====> Firebase Token ====>>", it.toString())
            GoogleSignInService.notificationToken = it.toString()
            startLoginActivity()
        }

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.CHANNEL_ID), name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun connectViewModelEvents() {
        TODO("Not yet implemented")
    }

    override fun disconnectViewModelEvents() {
        TODO("Not yet implemented")
    }

    private fun startLoginActivity(){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}