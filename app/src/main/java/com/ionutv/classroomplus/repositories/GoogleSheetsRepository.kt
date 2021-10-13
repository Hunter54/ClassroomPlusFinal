package com.ionutv.classroomplus.repositories

import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.ionutv.classroomplus.models.AttendanceEntry
import com.ionutv.classroomplus.services.GoogleSheetsService

class GoogleSheetsRepository(private val classroomService: GoogleSheetsService) {

    suspend fun createSpreadSheet(spreadsheet: Spreadsheet): Spreadsheet {
        return classroomService.createSpreadSheet(spreadsheet)
    }

    suspend fun getSheet(spreadsheetId: String): Spreadsheet {
        return classroomService.getSpreadSheet(spreadsheetId)
    }

    suspend fun updateSheet(
        sheetId: Int,
        spreadSheetId: String,
        newSheetName: String
    ): BatchUpdateSpreadsheetResponse? {
        return classroomService.renameSheet(sheetId, spreadSheetId, newSheetName)
    }

    suspend fun addSheet(
        spreadSheetId: String,
        sheetTitle: String
    ): BatchUpdateSpreadsheetResponse? {
        return classroomService.addSheet(spreadSheetId, sheetTitle)
    }

    suspend fun writeToSheet(
        writeToSheetId: Int,
        spreadsheetId: String,
        data: List<AttendanceEntry>
    ): BatchUpdateSpreadsheetResponse? {
        return classroomService.writeToSheet(writeToSheetId, spreadsheetId, data)
    }
}