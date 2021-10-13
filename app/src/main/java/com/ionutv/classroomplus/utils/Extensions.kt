package com.ionutv.classroomplus.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.api.services.classroom.model.Course
import com.ionutv.classroomplus.models.CourseEntry
import com.ionutv.classroomplus.models.Role
import com.ionutv.classroomplus.models.TimeTableDTO
import com.ionutv.classroomplus.models.TimeTableEntry

// LiveData extension to observe LiveData from ViewModel without leaking observations
fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            removeObserver(this)
            observer.onChanged(t)
        }
    })
}

fun Course.toEntry(role: Role): CourseEntry {
    return CourseEntry(
        this.id,
        this.name,
        this.section ?: "",
        this.descriptionHeading ?: "",
        this.description ?: "",
        this.room ?: "",
        this.ownerId,
        this.creationTime,
        this.updateTime,
        this.courseState,
        role
    )
}

fun TimeTableDTO.toEntry(): TimeTableEntry {
    with(this) {
        return TimeTableEntry(
            id,
            week,
            teacher,
            name,
            type,
            time,
            day,
            room
        )
    }

}
