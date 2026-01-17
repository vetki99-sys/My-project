package com.smartcalculator.core

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * محرك الحسابات الزمنية
 * يدعم عمليات الوقت والتاريخ، الفرق بين التواريخ، إضافة/طرح الزمن
 */
@Singleton
class TimeEngine @Inject constructor() {

    private val precision = 8
    private val roundingMode = RoundingMode.HALF_UP

    /**
     * حساب الفرق بين تاريخين
     */
    fun calculateDateDifference(
        startDate: LocalDate,
        endDate: LocalDate
    ): DateDifference {
        val days = ChronoUnit.DAYS.between(startDate, endDate)
        val weeks = days / 7
        val months = ChronoUnit.MONTHS.between(startDate, endDate)
        val years = ChronoUnit.YEARS.between(startDate, endDate)

        // حساب الأشهر والأيام المتبقية بدقة
        val totalMonths = (years * 12) + startDate.until(endDate, ChronoUnit.MONTHS)
        val remainingDays = startDate.plusMonths(totalMonths).until(endDate, ChronoUnit.DAYS)

        return DateDifference(
            years = years.toInt(),
            months = totalMonths.toInt(),
            weeks = (days / 7).toInt(),
            days = days.toInt(),
            hours = 0,
            minutes = 0,
            totalDays = days,
            totalHours = days * 24,
            totalMinutes = days * 24 * 60
        )
    }

    /**
     * حساب الفرق بين تاريخين مع الوقت
     */
    fun calculateDateTimeDifference(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): DateTimeDifference {
        val days = ChronoUnit.DAYS.between(startDateTime, endDateTime)
        val hours = ChronoUnit.HOURS.between(startDateTime, endDateTime)
        val minutes = ChronoUnit.MINUTES.between(startDateTime, endDateTime)
        val seconds = ChronoUnit.SECONDS.between(startDateTime, endDateTime)

        val startDate = startDateTime.toLocalDate()
        val endDate = endDateTime.toLocalDate()

        val years = ChronoUnit.YEARS.between(startDate, endDate)
        val totalMonths = (years * 12) + startDate.until(endDate, ChronoUnit.MONTHS)
        val remainingDays = startDate.plusMonths(totalMonths).until(endDate, ChronoUnit.DAYS)

        val startTime = startDateTime.toLocalTime()
        val endTime = endDateTime.toLocalTime()

        var hourDiff = ChronoUnit.HOURS.between(startTime, endTime)
        var minuteDiff = ChronoUnit.MINUTES.between(startTime, endTime) % 60
        var secondDiff = ChronoUnit.SECONDS.between(startTime, endTime) % 60

        // تصحيح الساعات إذا لزم الأمر
        if (minuteDiff < 0) {
            minuteDiff += 60
            hourDiff -= 1
        }
        if (secondDiff < 0) {
            secondDiff += 60
            minuteDiff -= 1
        }

        return DateTimeDifference(
            years = years.toInt(),
            months = totalMonths.toInt(),
            days = remainingDays.toInt(),
            hours = hourDiff.toInt(),
            minutes = minuteDiff.toInt(),
            seconds = secondDiff.toInt(),
            totalDays = days,
            totalHours = hours,
            totalMinutes = minutes,
            totalSeconds = seconds
        )
    }

    /**
     * إضافة فترة إلى تاريخ
     */
    fun addToDate(
        date: LocalDate,
        years: Int = 0,
        months: Int = 0,
        weeks: Int = 0,
        days: Int = 0
    ): LocalDate {
        var result = date
            .plusYears(years.toLong())
            .plusMonths(months.toLong())
            .plusWeeks(weeks.toLong())
            .plusDays(days.toLong())
        return result
    }

    /**
     * طرح فترة من تاريخ
     */
    fun subtractFromDate(
        date: LocalDate,
        years: Int = 0,
        months: Int = 0,
        weeks: Int = 0,
        days: Int = 0
    ): LocalDate {
        var result = date
            .minusYears(years.toLong())
            .minusMonths(months.toLong())
            .minusWeeks(weeks.toLong())
            .minusDays(days.toLong())
        return result
    }

    /**
     * إضافة وقت إلى تاريخ ووقت
     */
    fun addToDateTime(
        dateTime: LocalDateTime,
        hours: Int = 0,
        minutes: Int = 0,
        seconds: Int = 0
    ): LocalDateTime {
        return dateTime
            .plusHours(hours.toLong())
            .plusMinutes(minutes.toLong())
            .plusSeconds(seconds.toLong())
    }

    /**
     * طرح وقت من تاريخ ووقت
     */
    fun subtractFromDateTime(
        dateTime: LocalDateTime,
        hours: Int = 0,
        minutes: Int = 0,
        seconds: Int = 0
    ): LocalDateTime {
        return dateTime
            .minusHours(hours.toLong())
            .minusMinutes(minutes.toLong())
            .minusSeconds(seconds.toLong())
    }

    /**
     * تحويل الثواني إلى تنسيق مقروء
     */
    fun formatSeconds(totalSeconds: Long): FormattedTime {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return FormattedTime(
            hours = hours.toInt(),
            minutes = minutes.toInt(),
            seconds = seconds.toInt(),
            totalSeconds = totalSeconds,
            formatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        )
    }

    /**
     * تحويل الدقائق إلى تنسيق مقروء
     */
    fun formatMinutes(totalMinutes: Long): FormattedDuration {
        val days = totalMinutes / (24 * 60)
        val hours = (totalMinutes % (24 * 60)) / 60
        val minutes = totalMinutes % 60

        return FormattedDuration(
            days = days.toInt(),
            hours = hours.toInt(),
            minutes = minutes.toInt(),
            totalMinutes = totalMinutes,
            formatted = buildString {
                if (days > 0) append("${days}d ")
                if (hours > 0) append("${hours}h ")
                append("${minutes}m")
            }.trim()
        )
    }

    /**
     * تحويل الوقت إلى ثواني
     */
    fun timeToSeconds(hours: Int, minutes: Int, seconds: Int): Long {
        return (hours * 3600L) + (minutes * 60L) + seconds
    }

    /**
     * حساب عدد الأيام في شهر معين
     */
    fun daysInMonth(year: Int, month: Int): Int {
        return YearMonth.of(year, month).lengthOfMonth()
    }

    /**
     * حساب اليوم من الأسبوع
     */
    fun dayOfWeek(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("EEEE")
        return date.format(formatter)
    }

    /**
     * حساب عدد أيام السنة
     */
    fun daysInYear(year: Int): Int {
        return if (Year.isLeap(year)) 366 else 365
    }

    /**
     * حساب تاريخ بداية ونهاية الأسبوع
     */
    fun weekBounds(date: LocalDate): Pair<LocalDate, LocalDate> {
        val startOfWeek = date.minusDays(date.dayOfWeek.value.toLong() - 1)
        val endOfWeek = startOfWeek.plusDays(6)
        return Pair(startOfWeek, endOfWeek)
    }

    /**
     * حساب تاريخ بداية ونهاية الشهر
     */
    fun monthBounds(date: LocalDate): Pair<LocalDate, LocalDate> {
        val yearMonth = YearMonth.from(date)
        return Pair(
            yearMonth.atDay(1),
            yearMonth.atEndOfMonth()
        )
    }

    /**
     * حساب تاريخ بداية ونهاية السنة
     */
    fun yearBounds(date: LocalDate): Pair<LocalDate, LocalDate> {
        val year = Year.from(date)
        return Pair(
            year.atDay(1),
            year.atDay(daysInYear(year.value))
        )
    }

    /**
     * تحويل المنطقة الزمنية
     */
    fun convertTimeZone(
        dateTime: LocalDateTime,
        fromZone: ZoneId,
        toZone: ZoneId
    ): ZonedDateTime {
        val instant = dateTime.atZone(fromZone).toInstant()
        return instant.atZone(toZone)
    }

    /**
     * حساب الفرق بين منطقتين زمنيتين
     */
    fun timeZoneDifference(zone1: ZoneId, zone2: ZoneId): Int {
        val now = Instant.now()
        val offset1 = now.atZone(zone1).offset.totalSeconds
        val offset2 = now.atZone(zone2).offset.totalSeconds
        return (offset2 - offset1) / 3600
    }

    /**
     * التحقق من صحة الوقت
     */
    fun isValidTime(hours: Int, minutes: Int, seconds: Int = 0): Boolean {
        return hours in 0..23 && minutes in 0..59 && seconds in 0..59
    }

    /**
     * التحقق من صحة التاريخ
     */
    fun isValidDate(year: Int, month: Int, day: Int): Boolean {
        return try {
            LocalDate.of(year, month, day)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * حساب عدد أيام العمل بين تاريخين
     */
    fun businessDaysBetween(
        startDate: LocalDate,
        endDate: LocalDate,
        excludeWeekends: Boolean = true,
        holidays: Set<LocalDate> = emptySet()
    ): Int {
        var days = 0
        var current = startDate

        while (current.isBefore(endDate) || current.isEqual(endDate)) {
            val isWeekend = current.dayOfWeek == DayOfWeek.SATURDAY ||
                    current.dayOfWeek == DayOfWeek.SUNDAY

            if (!excludeWeekends || !isWeekend) {
                if (!holidays.contains(current)) {
                    days++
                }
            }

            current = current.plusDays(1)
        }

        return days
    }

    /**
     * حساب تاريخ الاستحقاق (إضافة أيام عمل)
     */
    fun addBusinessDays(
        startDate: LocalDate,
        daysToAdd: Int,
        holidays: Set<LocalDate> = emptySet()
    ): LocalDate {
        var current = startDate
        var added = 0

        while (added < daysToAdd) {
            current = current.plusDays(1)
            val isWeekend = current.dayOfWeek == DayOfWeek.SATURDAY ||
                    current.dayOfWeek == DayOfWeek.SUNDAY

            if (!isWeekend && !holidays.contains(current)) {
                added++
            }
        }

        return current
    }

    /**
     * تنسيق التاريخ بطريقة محلية
     */
    fun formatDate(date: LocalDate, pattern: String = "yyyy-MM-dd"): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return date.format(formatter)
    }

    /**
     * تنسيق الوقت بطريقة محلية
     */
    fun formatTime(time: LocalTime, pattern: String = "HH:mm:ss"): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return time.format(formatter)
    }

    /**
     * تنسيق التاريخ والوقت بطريقة محلية
     */
    fun formatDateTime(dateTime: LocalDateTime, pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return dateTime.format(formatter)
    }

    /**
     * تحليل سلسلة تاريخ
     */
    fun parseDate(dateString: String, pattern: String = "yyyy-MM-dd"): LocalDate? {
        return try {
            val formatter = DateTimeFormatter.ofPattern(pattern)
            LocalDate.parse(dateString, formatter)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * تحليل سلسلة وقت
     */
    fun parseTime(timeString: String, pattern: String = "HH:mm:ss"): LocalTime? {
        return try {
            val formatter = DateTimeFormatter.ofPattern(pattern)
            LocalTime.parse(timeString, formatter)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * فئات النتائج
     */
    data class DateDifference(
        val years: Int,
        val months: Int,
        val weeks: Int,
        val days: Int,
        val hours: Int,
        val minutes: Int,
        val totalDays: Long,
        val totalHours: Long,
        val totalMinutes: Long
    )

    data class DateTimeDifference(
        val years: Int,
        val months: Int,
        val days: Int,
        val hours: Int,
        val minutes: Int,
        val seconds: Int,
        val totalDays: Long,
        val totalHours: Long,
        val totalMinutes: Long,
        val totalSeconds: Long
    )

    data class FormattedTime(
        val hours: Int,
        val minutes: Int,
        val seconds: Int,
        val totalSeconds: Long,
        val formatted: String
    )

    data class FormattedDuration(
        val days: Int,
        val hours: Int,
        val minutes: Int,
        val totalMinutes: Long,
        val formatted: String
    )
}
