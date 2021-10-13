package com.ionutv.classroomplus.utils

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object DateUtils {
    val timeZone: ZoneId = ZoneId.systemDefault()
    fun getDateTimeFromTimeStamp(timeStamp: Long): ZonedDateTime {
        return Instant.ofEpochMilli(timeStamp)
            .atZone(timeZone)
    }

    fun getFormattedDateFromDateTime(dateTime: ZonedDateTime) : String{
        return dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
    }

    fun getFormattedTimeFromDateTime(dateTime: ZonedDateTime) : String{
        return dateTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
    }
}