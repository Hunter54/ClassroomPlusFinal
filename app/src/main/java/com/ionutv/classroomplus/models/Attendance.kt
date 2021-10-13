package com.ionutv.classroomplus.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attendance(
    @get:Exclude var courseId: String = "",
    var courseName: String = "",
    val startTime: Long = 0,
    val duration:Int=0,
    var bleId: String = "",
    var bleName : String =""
): Parcelable