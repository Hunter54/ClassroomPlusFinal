package com.ionutv.classroomplus.models

import android.app.Application
import android.content.ContextWrapper
import com.google.firebase.FirebaseApp
import com.ionutv.classroomplus.ui.ClassroomPlusSharedPreferences
import com.vladan.networkchecker.InternetManager

class App : Application() {
    companion object {
        lateinit var instance: App private set
        lateinit var appContainer : AppContainer private set
    }

    lateinit var internetManager: InternetManager

    fun initAppContainer(){
        appContainer = AppContainer(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        instance = this
        internetManager = InternetManager.getInternetManager(this)
        internetManager.registerInternetMonitor()
        ClassroomPlusSharedPreferences.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()
    }
}