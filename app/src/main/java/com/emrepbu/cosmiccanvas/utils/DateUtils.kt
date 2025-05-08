package com.emrepbu.cosmiccanvas.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private const val NASA_DATE_FORMAT = "yyyy-MM-dd"
    private const val DISPLAY_DATE_FORMAT = "MMMM d, yyyy"

    private val nasaDateFormatter = SimpleDateFormat(NASA_DATE_FORMAT, Locale.US)
    private val displayDateFormatter = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault())

    fun formatToNasaDate(date: Date): String {
        return nasaDateFormatter.format(date)
    }

    fun formatToDisplayDate(date: Date): String {
        return displayDateFormatter.format(date)
    }

    fun parseNasaDate(dateString: String): Date {
        return nasaDateFormatter.parse(dateString) ?: Date()
    }

    fun getTodayDate(): Date {
        return Calendar.getInstance().time
    }

    fun getTodayDateString(): String {
        return formatToNasaDate(getTodayDate())
    }

    fun getRecentDates(count: Int): List<String> {
        val calendar = Calendar.getInstance()
        return List(count) { index ->
            calendar.add(Calendar.DAY_OF_YEAR, -index)
            val date = formatToNasaDate(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, index) // Reset calendar
            calendar.add(Calendar.DAY_OF_YEAR, -1) // Go back one day for next iteration
            date
        }
    }

    fun getStartDateForCalendar(): Calendar {
        // NASA APOD started on June 16, 1995
        return Calendar.getInstance().apply {
            set(1995, 5, 16, 0, 0, 0)
        }
    }
}