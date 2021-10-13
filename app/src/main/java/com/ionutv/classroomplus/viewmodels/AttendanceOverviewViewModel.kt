package com.ionutv.classroomplus.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.services.sheets.v4.model.SpreadsheetProperties
import com.ionutv.classroomplus.models.AttendanceEntry
import com.ionutv.classroomplus.repositories.GoogleSheetsRepository
import com.ionutv.classroomplus.services.RealtimeDatabaseService
import com.ionutv.classroomplus.utils.DateUtils
import com.ionutv.classroomplus.utils.observeOnce
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AttendanceOverviewViewModel(private val sheetsRepository: GoogleSheetsRepository) :
    ViewModel() {
    private val _onAttendancesListChanged = MutableLiveData<Map<String, List<AttendanceEntry>>>()
    val onAttendancesListChanged: LiveData<Map<String, List<AttendanceEntry>>>
        get() = _onAttendancesListChanged
    private val _onTimeStampList = MutableLiveData<List<String>>()
    val onTimeStampList: LiveData<List<String>>
        get() = _onTimeStampList
    var classroomSpreadsheet: Spreadsheet? = null

    private val _onSuccessUpload = SingleLiveEvent<Void>()
    val onSuccessUpload: LiveData<Void>
        get() = _onSuccessUpload

    private val _onSpreadSheetError = SingleLiveEvent<GoogleJsonResponseException>()
    val onSpreadSheetError: LiveData<GoogleJsonResponseException>
        get() = _onSpreadSheetError

    private val _onSheetAlreadyExists = SingleLiveEvent<Int>()
    val onSheetAlreadyExists: LiveData<Int>
        get() = _onSheetAlreadyExists

    private val _wasDeleted = SingleLiveEvent<Boolean>()
    val wasDeleted: LiveData<Boolean>
        get() = _wasDeleted

    private val _onIsBusy = MutableLiveData<Boolean>()
    val onIsBusy: LiveData<Boolean>
        get() = _onIsBusy

    fun getAttendanceForClassroomId(classroomId: String) {
        RealtimeDatabaseService.getAttendanceEntries(classroomId).observeOnce {
            _onAttendancesListChanged.value = it
            val timeStampList = mutableListOf<String>()
            it.keys.forEach { timestamp ->
                timeStampList.add(0, timestamp)
            }
            _onTimeStampList.value = timeStampList
        }
    }

    fun getSpreadSheetId(classroomId: String, classroomName: String) {
        RealtimeDatabaseService.getCourseSpreadSheetId(classroomId).observeOnce { spreadsheetId ->
            spreadsheetId?.let {
                getSpreadsheet(it)
            } ?: run {
                viewModelScope.launch {
                    createSpreadsheetAsync(classroomName, classroomId)
                }
            }
        }
    }

    fun createSpreadSheet(classroomName: String, classroomId: String) {
        viewModelScope.launch {
            createSpreadsheetAsync(classroomName, classroomId)
        }
    }

    private suspend fun createSpreadsheetAsync(
        classroomName: String,
        classroomId: String
    ) {
        kotlin.runCatching {
            val newSpreadsheet = Spreadsheet().also { sheet ->
                sheet.properties = SpreadsheetProperties().also {
                    it.title = classroomName
                }
            }
            sheetsRepository.createSpreadSheet(newSpreadsheet)
        }.onSuccess {
            RealtimeDatabaseService.addSpreadSheetIdToCourse(
                classroomId,
                it.spreadsheetId
            )
            getSpreadsheet(it.spreadsheetId)
        }.onFailure {
            when (it) {
                is CancellationException -> throw it
                else -> Log.e("Error creating spreadsheet", it.message.toString())
            }
        }

    }

    fun deleteAttendanceTimeStamp(classroomId: String, timeStamp: String) {
        RealtimeDatabaseService.deleteAttendanceTimeStamp(classroomId, timeStamp).observeOnce {
            _wasDeleted.value = it
        }
    }

    fun updateSheet(spreadsheet: Spreadsheet, sheetId: Int, timeStamp: String) {
        _onIsBusy.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                _onAttendancesListChanged.value?.get(timeStamp)?.let { attendaceList ->
                    sheetsRepository.writeToSheet(
                        sheetId,
                        spreadsheet.spreadsheetId,
                        attendaceList
                    )
                }
            }.onSuccess {
                _onIsBusy.value = false
                _onSuccessUpload.call()
            }.onFailure {
                _onIsBusy.value = false
                when (it) {
                    is CancellationException -> throw it
                    else -> {
                        Log.e("Error writing spreadsheet", it.message.toString())
                    }
                }
            }
        }
    }

    fun addSheetAndUpdateSpreadSheet(spreadsheet: Spreadsheet, timeStamp: String) {
        _onIsBusy.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                val sheetName = DateUtils.getFormattedDateFromDateTime(
                    DateUtils.getDateTimeFromTimeStamp(
                        timeStamp.toLong()
                    )
                )
                spreadsheet.sheets.firstOrNull { it.properties.title == sheetName }?.let {
                    _onSheetAlreadyExists.value = it.properties.sheetId
                    coroutineContext.cancel()
                }

                val data = sheetsRepository.addSheet(
                    spreadsheet.spreadsheetId,
                    sheetName
                )
                val sheetId: Int? = data?.replies?.first()?.addSheet?.properties?.sheetId

                sheetId?.let {
                    _onAttendancesListChanged.value?.get(timeStamp)?.let { attendaceList ->
                        sheetsRepository.writeToSheet(
                            it,
                            spreadsheet.spreadsheetId,
                            attendaceList
                        )
                    }
                }
            }.onFailure {
                _onIsBusy.value = false
                when (it) {
                    is CancellationException -> throw it
                    is GoogleJsonResponseException -> {
                        _onSpreadSheetError.value = it
                    }
                    else -> Log.d(
                        "AttendanceOverviewViewModel Failed to add to spreadSheet",
                        it.message.toString()
                    )
                }
            }.onSuccess {
                _onIsBusy.value = false
                getSpreadsheet(spreadsheet.spreadsheetId)
                it?.let { bussr ->
                    val item = bussr
                    _onSuccessUpload.call()
                } ?: run {
                    Log.d(
                        "AttendanceOverviewViewModel Failed to write to spreadSheet",
                        "Check permissions"
                    )
                }
            }
        }
    }

    private fun getSpreadsheet(spreadsheetId: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                sheetsRepository.getSheet(spreadsheetId)
            }.onSuccess {
                classroomSpreadsheet = it
            }.onFailure { error ->
                when (error) {
                    is CancellationException -> throw error
                    is GoogleJsonResponseException -> {
                        _onSpreadSheetError.value = error
                    }
                    else -> {
                        val copy = error
                        Log.d(
                            " AttendanceOverviewViewModel Failed to get spreadsheet",
                            error.message.toString()
                        )
                    }
                }
            }
        }
    }
//    var sheetId = it.sheets.first().properties.sheetId
//    var data = sheetsRepository.updateSheet(sheetId, it.spreadsheetId)
//    var copy = data
//    data = sheetsRepository.addSheet(it.spreadsheetId)
//    copy = data
//    item = sheetsRepository.getSheet(it.spreadsheetId)
//    sheetId = item.sheets.last().properties.sheetId
//    data = sheetsRepository.writeToSheet(sheetId, it.spreadsheetId, listOf())
//    copy = data
}