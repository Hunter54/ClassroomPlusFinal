package com.ionutv.classroomplus.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ionutv.classroomplus.Constants

@Entity(tableName = Constants.COURSES_ROOM_TABLE)
data class CourseEntry(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "section") val section: String ="",
    @ColumnInfo(name = "description_heading") val descriptionHeading: String = "",
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "room") val room: String = "",
    @ColumnInfo(name = "owner_id") val ownerId: String,
    @ColumnInfo(name = "creation_time") val creationTime: String,
    @ColumnInfo(name = "update_time") val updateTime: String,
    @ColumnInfo(name = "course_state") val courseState: String,
    @ColumnInfo(name = "role") val role: Role? = null
    //    @ColumnInfo(name = "enrollment_code") val enrollmentCode: String,
//    @ColumnInfo(name = "alternate_link") val alternateLink: String,
//    @ColumnInfo(name = "course_group_email") val courseGroupEmail: String,
//    @ColumnInfo(name = "teacher_group_email") val teacherGroupEmail: String,
//    @ColumnInfo(name = "guardians_enabled") val guardiansEnabled: Boolean,
//    @ColumnInfo(name = "calendar_id") val calendarId: String
)
