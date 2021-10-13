package com.ionutv.classroomplus.services

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.*
import com.ionutv.classroomplus.models.Attendance
import com.ionutv.classroomplus.models.AttendanceEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class GoogleSheetsService private constructor(mCredential: GoogleAccountCredential) {
    private var service: Sheets

    private fun convertFromListToRowDataList(data: List<AttendanceEntry>): List<RowData> {
        val firstRowList = listOf(
            CellData().setUserEnteredValue(ExtendedValue().setStringValue("Student Name")),
            CellData().setUserEnteredValue(ExtendedValue().setStringValue("Student Email"))
        )
        val firstRowData = RowData().setValues(firstRowList)
        val listRowData = mutableListOf(firstRowData)
        data.forEach {
            val rowDataList = listOf(
                CellData().setUserEnteredValue(ExtendedValue().setStringValue(it.name)),
                CellData().setUserEnteredValue(ExtendedValue().setStringValue(it.email))
            )
            val rowData = RowData().setValues(rowDataList)
            listRowData.add(rowData)
        }
        return listRowData
    }

    init {
        val json_factory = JacksonFactory.getDefaultInstance()
        mCredential.selectedAccountName = GoogleSignInService.account?.email
        mCredential.selectedAccount = GoogleSignInService.account?.account
        val transport = com.google.api.client.http.javanet.NetHttpTransport()
        service = Sheets.Builder(transport, json_factory, mCredential)
            .setApplicationName("ClassroomPlus")
            .build()
    }

    suspend fun createSpreadSheet(spreadSheet: Spreadsheet): Spreadsheet =
        withContext(Dispatchers.IO) {
            val uploadedSpreadsheet = service.spreadsheets().create(spreadSheet)
                .setFields("spreadsheetId")
                .execute()
            uploadedSpreadsheet
        }

    suspend fun getSpreadSheet(spreadSheetId: String): Spreadsheet =
        withContext(Dispatchers.IO) {
            service.spreadsheets().get(spreadSheetId)
                .execute()
        }

    suspend fun renameSheet(
        writeToSheetId: Int,
        spreadSheetId: String,
        newSheetName:String
    ): BatchUpdateSpreadsheetResponse? =
        withContext(Dispatchers.IO) {
            val sheetProperties = SheetProperties().apply {
                title = newSheetName
                sheetId = writeToSheetId
            }
            val updateSpreadsheetRequest =
                UpdateSheetPropertiesRequest().setProperties(sheetProperties).setFields("Title")
            val request = Request().setUpdateSheetProperties(updateSpreadsheetRequest)
            val requests = mutableListOf(request)
            val batchUpdateSpreadsheetRequest =
                BatchUpdateSpreadsheetRequest().setRequests(requests)
            service.spreadsheets().batchUpdate(spreadSheetId, batchUpdateSpreadsheetRequest)
                .execute()
        }

    suspend fun addSheet(
        spreadSheetId: String,
        sheetTitle: String
    ): BatchUpdateSpreadsheetResponse? =
        withContext(Dispatchers.IO) {
            val addSheetRequest = AddSheetRequest().setProperties(
                SheetProperties().apply {
                    title = sheetTitle
                    index = 1
                }
            )
            val request = Request().setAddSheet(addSheetRequest)
            val requests = mutableListOf(request)
            val batchUpdateSpreadsheetRequest =
                BatchUpdateSpreadsheetRequest().setRequests(requests)
            service.spreadsheets().batchUpdate(spreadSheetId, batchUpdateSpreadsheetRequest)
                .execute()
        }

    suspend fun writeToSheet(
        writeToSheetId: Int,
        spreadsheetId: String,
        writeData: List<AttendanceEntry>
    ): BatchUpdateSpreadsheetResponse? =
        withContext(Dispatchers.IO) {
            val listRowData = convertFromListToRowDataList(writeData)
            val requests = mutableListOf<Request>()
            requests.add(
                Request().setUpdateCells(
                    UpdateCellsRequest()
                        .setRows(listRowData)
                        .setFields("userEnteredValue")
                        .setRange(
                            GridRange()
                                .setSheetId(writeToSheetId)
                                .setStartRowIndex(0)
                                .setStartColumnIndex(0)
                        )
                )
            )
            val body = BatchUpdateSpreadsheetRequest()
                .setRequests(requests)
            service.spreadsheets().batchUpdate(spreadsheetId, body).execute()
        }


    companion object {
        @Volatile
        private var INSTANCE: GoogleSheetsService? = null

        fun getInstance(mCredential: GoogleAccountCredential): GoogleSheetsService =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: GoogleSheetsService(mCredential).also { INSTANCE = it }
            }
    }

}