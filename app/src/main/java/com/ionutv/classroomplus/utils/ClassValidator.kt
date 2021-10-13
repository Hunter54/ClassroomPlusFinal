package com.ionutv.classroomplus.utils

import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView

class ClassValidator(private val arrayAdapter: ArrayAdapter<String>) : AutoCompleteTextView.Validator {

    override fun isValid(text: CharSequence?): Boolean {
        for (i in 0 until arrayAdapter.count) {
            val temp = arrayAdapter.getItem(i)
            if (text.toString() == temp) {
                return true
            }
        }
        return false
    }

    override fun fixText(invalidText: CharSequence?): CharSequence {
        Log.d("Wrong text", "Returning null text")
        return ""
    }
}