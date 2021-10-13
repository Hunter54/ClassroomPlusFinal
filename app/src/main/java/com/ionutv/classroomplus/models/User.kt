package com.ionutv.classroomplus.models
import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class User(
    val uid : String? = null,
    val name : String? = null,
    val email : String? = null,
    val notificationToken : String?= null,
    @get:Exclude var isNew: Boolean? = null) : Serializable, Parcelable
