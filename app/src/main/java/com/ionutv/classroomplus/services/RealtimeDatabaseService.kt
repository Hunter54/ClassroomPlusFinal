package com.ionutv.classroomplus.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ionutv.classroomplus.models.*

object RealtimeDatabaseService {
    private val database = Firebase.database.reference

    fun createUser(user: User) {
        user.uid.let {
            if (it != null) {
                database.child("users").child(it).child("user_details").setValue(user)
            }
        }
    }


    fun checkIfUsersExists(uid: String): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        database.child("users").child(uid).child("user_details").get()
            .addOnSuccessListener { snapshot ->
                snapshot.value?.let {
                    GoogleSignInService.signedInUser = snapshot.getValue(User::class.java)!!
                }
                result.value = snapshot.value != null
            }.addOnFailureListener {
                result.value = false
            }

        return result
    }

    fun checkIfTeacherExists(courseUid: String): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        GoogleSignInService.signedInUser.uid?.let {
            database.child("courses").child(courseUid).child("teachers").child(it).get()
                .addOnSuccessListener {
                    result.value = it.value != null
                }.addOnFailureListener {
                    result.value = false
                }
        }
        return result
    }

    fun addCourse(courseUid: String, courseName: String) {
        GoogleSignInService.signedInUser.uid?.let {
            database.child("courses").child(courseUid).child("teachers")
                .child(it).setValue(true)
            database.child("courses").child(courseUid).child("name").setValue(courseName)
            database.child("users").child(it).child("teacher_courses").child(courseUid)
                .setValue(true)
        }
    }

    fun addTeacherToCourse(courseUid: String){
        GoogleSignInService.signedInUser.uid?.let {
            database.child("courses").child(courseUid).child("teachers")
                .child(it).setValue(true)
            database.child("users").child(it).child("teacher_courses").child(courseUid)
                .setValue(true)
        }
    }

    fun checkIfCourseExists(courseUid: String): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        database.child("courses").child(courseUid).get().addOnSuccessListener {
            result.value = it.value != null
        }.addOnFailureListener {
            result.value = false
        }
        return result
    }

    fun checkIfStudentCourseExists(courseUid: String): MutableLiveData<DataSnapshot?> {
        val result = MutableLiveData<DataSnapshot?>()
        GoogleSignInService.signedInUser.uid?.let { uid ->
            database.child("courses").child(courseUid).get()
                .addOnSuccessListener {
                    result.value = it
                }.addOnFailureListener {
                    result.value = null
                }
        }
        return result
    }

    fun addActiveAttendancesListener(): Pair<LiveData<Pair<String, FirebaseEvent>>, ChildEventListener> {
        val result = MutableLiveData<Pair<String, FirebaseEvent>>()
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val attendance = snapshot.key
                attendance?.apply {
                    result.value = Pair(this, FirebaseEvent.Added)
                }
            }

            override fun onChildChanged(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) {
                val attendance = snapshot.key
                attendance?.apply {
                    Pair(this, FirebaseEvent.Changed)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val attendance = snapshot.key
                attendance?.apply {
                    result.value = Pair(this, FirebaseEvent.Removed)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }
        GoogleSignInService.signedInUser.uid?.let {
            database.child("users").child(it).child("active_attendances")
                .addChildEventListener(childEventListener)
        }
        return Pair(result, childEventListener)
    }

    fun addCreatedAttendancesListener(): Pair<LiveData<Pair<String, FirebaseEvent>>, ChildEventListener> {
        val result = MutableLiveData<Pair<String, FirebaseEvent>>()
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val attendance = snapshot.key
                attendance?.apply {
                    result.value = Pair(this, FirebaseEvent.Added)
                }
            }

            override fun onChildChanged(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) {
                val attendance = snapshot.key
                attendance?.apply {
                    Pair(this, FirebaseEvent.Changed)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val attendance = snapshot.key
                attendance?.apply {
                    result.value = Pair(this, FirebaseEvent.Removed)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }
        GoogleSignInService.signedInUser.uid?.let {
            database.child("users").child(it).child("created_attendances")
                .addChildEventListener(childEventListener)
        }
        return Pair(result, childEventListener)
    }

    fun removeAttendancesListener(childEventListener: ChildEventListener) {
        GoogleSignInService.signedInUser.uid?.let {
            database.child("users").child(it).child("active_attendances")
                .removeEventListener(childEventListener)
        }
    }

    fun retrieveActiveUserAttendances(): MutableLiveData<List<String>> {
        val result = MutableLiveData<List<String>>()
        GoogleSignInService.signedInUser.uid?.let {
            database.child("users").child(it).child("active_attendances").get()
                .addOnSuccessListener { snapshot ->
                    val activeAttendances = mutableListOf<String>()
                    snapshot.children.forEach { course ->
                        course.key?.let { it1 -> activeAttendances.add(it1) }
                    }
                    result.value = activeAttendances
                }
        }
        return result
    }

    fun getActiveAttendance(classroomID: String): MutableLiveData<Attendance> {
        val result = MutableLiveData<Attendance>()
        database.child("active_attendances").child(classroomID).get()
            .addOnSuccessListener { snapshot ->
                val attendance = snapshot.getValue(Attendance::class.java)
                attendance?.apply {
                    courseId = classroomID
                    result.value = this
                }
            }
        return result
    }

    fun getAttendanceEntries(classroomID: String): MutableLiveData<Map<String, List<AttendanceEntry>>> {
        val result = MutableLiveData<Map<String, List<AttendanceEntry>>>()
        database.child("courses").child(classroomID).child("attendances").get()
            .addOnSuccessListener { snapshot ->
                val attendancesMap = mutableMapOf<String, List<AttendanceEntry>>()
                snapshot.children.forEach { attendance ->
                    attendance.key?.let { timeStamp ->
                        val attendanceList = mutableListOf<AttendanceEntry>()
                        attendance.children.forEach { snapshot ->
                            snapshot.key?.let { name ->
                                snapshot.getValue(String::class.java)?.let { email ->
                                    attendanceList.add(AttendanceEntry(name, email))
                                }
                            }
                        }
                        attendancesMap[timeStamp] = attendanceList
                    }
                }
                result.value = attendancesMap
            }
        return result
    }

    fun addAttendanceRegistration(attendance: Attendance) {
        GoogleSignInService.signedInUser.let {
            database.child("courses").child(attendance.courseId).child("attendances")
                .child(attendance.startTime.toString()).child(it.name.toString())
                .setValue(it.email.toString())
        }
    }

    fun addAttendanceRequest(attendance: Attendance) {
        database.child("active_attendances").child(attendance.courseId).setValue(attendance)
        GoogleSignInService.signedInUser.uid?.let {
            database.child("users").child(it).child("created_attendances")
                .child(attendance.courseId).setValue(true)
        }
    }

    fun addStudentToCourse(courseUid: String) {
        GoogleSignInService.signedInUser.uid?.let {
            database.child("courses").child(courseUid).child("students").child(it).setValue(true)
            database.child("users").child(it).child("student_courses").child(courseUid)
                .setValue(true)
        }
    }

    fun removeStudentFromCourse(courseUid: String) {
        GoogleSignInService.signedInUser.uid?.let {
            database.child("courses").child(courseUid).child("students").child(it).removeValue()
            database.child("users").child(it).child("student_courses").child(courseUid)
                .removeValue()
        }
    }

    fun removeTeacherFromCourse(courseUid: String) {
        GoogleSignInService.signedInUser.uid?.let {
            database.child("courses").child(courseUid).child("teachers").child(it).removeValue()
            database.child("users").child(it).child("teacher_courses").child(courseUid)
                .removeValue()
        }
    }

    fun saveTimeTablesToFirebase(timeTableList: List<TimeTableEntry>) {
        GoogleSignInService.signedInUser.uid?.let { user ->
            timeTableList.forEach { timeTable ->
                database.child("users").child(user).child("timetables").removeValue()
                    .addOnSuccessListener {
                        val newReference =
                            database.child("users").child(user).child("timetables").push()
                        newReference.setValue(timeTable)
                    }
            }
        }
    }

    fun getTimeTablesFromFirebase(): LiveData<List<TimeTableDTO>> {
        val result = MutableLiveData<List<TimeTableDTO>>()
        GoogleSignInService.signedInUser.uid?.let { user ->
            database.child("users").child(user).child("timetables").get()
                .addOnSuccessListener { snapshot ->
                    val timeTableList = mutableListOf<TimeTableDTO>()
                    snapshot.children.forEach { data ->
                        data.getValue(TimeTableDTO::class.java)?.let {
                            timeTableList.add(it)
                        }
                    }
                    result.value = timeTableList
                }
        }
        return result
    }

    fun deleteAttendanceTimeStamp(classroomId: String, timeStamp: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        database.child("courses").child(classroomId).child("attendances").child(timeStamp)
            .removeValue().addOnCompleteListener {
                result.value = it.isSuccessful
            }
        return result
    }

    fun deleteActiveAttendance(classroomID: String): LiveData<String?> {
        val result = MutableLiveData<String?>()
        GoogleSignInService.signedInUser.uid?.let { userId ->
            database.child("users").child(userId).child("created_attendances").child(classroomID)
                .removeValue().addOnCompleteListener {
                    if(it.isSuccessful){
                        result.value = classroomID
                    }else{
                        result.value = null
                    }
            }
        }
        return result
    }

    fun getActiveTeacherAttendances(): LiveData<Attendance> {
        val result = MutableLiveData<Attendance>()
        GoogleSignInService.signedInUser.uid?.let { userId ->
            database.child("users").child(userId).child("created_attendances").get()
                .addOnSuccessListener { snapshot ->
                    snapshot.children.firstOrNull()?.key?.let { classroomId ->
                        database.child("active_attendances").child(classroomId).get()
                            .addOnSuccessListener { attendanceSnapshot ->
                                val attendance = attendanceSnapshot.getValue(Attendance::class.java)
                                attendance?.let {
                                    attendance.courseId = classroomId
                                    result.value = it
                                }
                            }
                    }
                }
        }
        return result
    }

    fun addSpreadSheetIdToCourse(courseId: String, spreadsheetId: String) {
        database.child("courses").child(courseId).child("spreadsheetId").setValue(spreadsheetId)
    }

    fun updateFirebaseNotificationToken(token:String){
        GoogleSignInService.signedInUser.uid?.let { userId ->
            database.child("users").child(userId).child("user_details").child("notificationToken").setValue(token)
        }
    }

    fun getCourseSpreadSheetId(courseId: String): LiveData<String?> {
        val result = MutableLiveData<String?>()
        database.child("courses").child(courseId).child("spreadsheetId").get()
            .addOnSuccessListener {
                val sheetId = it.getValue(String::class.java)
                result.value = sheetId
            }.addOnFailureListener {
            result.value = null
        }
        return result
    }

}