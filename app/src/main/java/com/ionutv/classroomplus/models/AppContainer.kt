package com.ionutv.classroomplus.models

import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.classroom.ClassroomScopes
import com.google.api.services.sheets.v4.SheetsScopes
import com.ionutv.classroomplus.repositories.ClassroomRepository
import com.ionutv.classroomplus.repositories.GoogleSheetsRepository
import com.ionutv.classroomplus.repositories.TimeTableRepository
import com.ionutv.classroomplus.databaseClasses.ClassroomCoursesDatabase
import com.ionutv.classroomplus.databaseClasses.TimeTableDatabase
import com.ionutv.classroomplus.services.*


class AppContainer(context: Context) {

    private val timeTableDatabase = TimeTableDatabase.getInstance(context)
    private val classroomCoursesDatabase = ClassroomCoursesDatabase.getInstance(context)

    val timeTableRepository = TimeTableRepository(timeTableDatabase.timeTableDatabaseDao)

    private val mCredential = GoogleAccountCredential.usingOAuth2(
        context, listOf(ClassroomScopes.CLASSROOM_COURSES_READONLY, ClassroomScopes.CLASSROOM_ROSTERS_READONLY, SheetsScopes.DRIVE_FILE) )
        .setBackOff(ExponentialBackOff())

    val classRoomRepository = ClassroomRepository(GoogleClassroomService.getInstance(mCredential), classroomCoursesDatabase.courseDatabaseDao)

    val sheetsRepository = GoogleSheetsRepository(GoogleSheetsService.getInstance(mCredential))

}