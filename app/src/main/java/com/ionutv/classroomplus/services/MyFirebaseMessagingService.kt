package com.ionutv.classroomplus.services

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ionutv.classroomplus.R
import com.ionutv.classroomplus.activities.MainActivity
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
         GoogleSignInService.notificationTokenRefreshed = token
    }

    fun getToken(): LiveData<String?> {
        val result = MutableLiveData<String?>()
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Get new FCM registration token
                result.value = task.result
            } else {
                result.value = null
            }
        }
        return result
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        val intent = Intent(this,MainActivity::class.java)

        val pendingIntent = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(intent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            val builder = NotificationCompat.Builder(this, getString(R.string.CHANNEL_ID))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(it.title)
                .setContentText(it.body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            val notificationId = Random.nextInt()
            with(NotificationManagerCompat.from(this)) {
                notify(notificationId, builder.build())
            }
        }
    }
}