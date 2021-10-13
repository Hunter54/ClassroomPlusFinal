package com.ionutv.classroomplus.repositories

import androidx.lifecycle.LiveData
import com.ionutv.classroomplus.models.ClassWeek
import com.ionutv.classroomplus.models.TimeTableEntry
import com.ionutv.classroomplus.databaseClasses.TimeTableDatabaseDao

class TimeTableRepository(private val timeTableDao: TimeTableDatabaseDao) {

    val classesOdd : LiveData<List<TimeTableEntry>> = timeTableDao.getWeek(ClassWeek.ODD)
    val classesEven : LiveData<List<TimeTableEntry>> = timeTableDao.getWeek(ClassWeek.EVEN)
    val allClasses : LiveData<List<TimeTableEntry>> = timeTableDao.getAll()
    var lastDeletedClass : TimeTableEntry? = null
    var lastRetrievedClass : TimeTableEntry? = null

    suspend fun addClass(timeTableClass : TimeTableEntry){
        timeTableDao.insert(timeTableClass)
    }

    suspend fun getAllClasses() : List<TimeTableEntry>{
        return timeTableDao.getAllAsync()
    }

    suspend fun getClass(id : Int) : TimeTableEntry{
        val retrievedClass = timeTableDao.getId(id)
        lastRetrievedClass = retrievedClass
        return retrievedClass
    }

    suspend fun deleteId(id: Int) : Int{
        lastDeletedClass = timeTableDao.getId(id)
        return timeTableDao.deleteId(id)
    }

    suspend fun updateClass(timeTableEntry: TimeTableEntry){
        timeTableDao.update(timeTableEntry)
    }

    suspend fun deleteAll() : Int{
        return timeTableDao.deleteAll()
    }

}