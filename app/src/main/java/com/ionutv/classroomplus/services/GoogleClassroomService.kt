package com.ionutv.classroomplus.services

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.classroom.Classroom
import com.google.api.services.classroom.model.Course
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoogleClassroomService private constructor(mCredential: GoogleAccountCredential) {
    private var service: Classroom

    init {
        val json_factory = JacksonFactory.getDefaultInstance()
        mCredential.selectedAccountName = GoogleSignInService.account?.email
        mCredential.selectedAccount = GoogleSignInService.account?.account
        val transport = com.google.api.client.http.javanet.NetHttpTransport()
        service = Classroom.Builder(transport, json_factory, mCredential)
            .setApplicationName("ClassroomPlus")
            .build()
    }

    suspend fun getAllUserCourses(): List<Course> =
        withContext(Dispatchers.IO) {
            val coursesRequest = service.courses().list()
                .setPageSize(1000)
                .setCourseStates(listOf("Active"))
                .execute()
            coursesRequest.courses
        }

    suspend fun getCoursesWhereStudent(): List<Course> =
        withContext(Dispatchers.IO) {
            val coursesRequest = service.courses().list()
                .setStudentId("me")
                .setCourseStates(listOf("Active"))
                .setPageSize(1000)
                .execute()
            coursesRequest.courses ?: listOf()
        }

    suspend fun getCoursesWhereTeacher(): List<Course> =
        withContext(Dispatchers.IO) {
            val coursesRequest = service.courses().list()
                .setTeacherId("me")
                .setCourseStates(listOf("Active"))
                .setPageSize(1000)
                .execute()
            coursesRequest.courses ?: listOf()
        }

    suspend fun getUserID(): String =
        withContext(Dispatchers.IO) {
            val userIdRequest = service.UserProfiles().get("me")
                .execute()
            userIdRequest.id
        }

    companion object {
        @Volatile
        private var INSTANCE: GoogleClassroomService? = null

        fun getInstance(mCredential: GoogleAccountCredential): GoogleClassroomService =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: GoogleClassroomService(mCredential).also { INSTANCE = it }
            }
    }
}