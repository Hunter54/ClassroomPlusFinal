package com.ionutv.classroomplus.fragments

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    abstract fun connectViewModelEvents()

    abstract fun disconnectViewModelEvents()

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}