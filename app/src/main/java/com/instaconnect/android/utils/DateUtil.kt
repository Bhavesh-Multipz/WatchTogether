package com.instaconnect.android.utils

import android.text.format.DateFormat
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class DateUtil {

    companion object {
        private val LAST_TIME_MS = AtomicLong()

        fun formatByDay(sdate: String): String {
            return try {
                val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                val value = formatter.parse(sdate)
                val dateFormatter = SimpleDateFormat("MMM dd, yyyy") //this format changeable
                dateFormatter.timeZone = TimeZone.getDefault()
                val date = formatter.parse(sdate)
                var time = SimpleDateFormat("hh:mm aa").format(date)
                val days = (Date().time - date.time) / 86400000
                time = time.replaceFirst("^0+(?!$)".toRegex(), "")
                if (days == 0L) "Today $time" else if (days == 1L) "Yesterday $time" else if (days < 7) DateFormat.format(
                    "EEEE",
                    date
                ).toString() + " " + time else dateFormatter.format(date) + " " + time
            } catch (e: Exception) {
                e.printStackTrace()
                sdate
            }
        }

        fun uniqueCurrentTimeMS(): Long {
            var now = System.currentTimeMillis()
            while (true) {
                val lastTime = LAST_TIME_MS.get()
                if (lastTime >= now) now = lastTime + 1
                if (LAST_TIME_MS.compareAndSet(lastTime, now)) return now
            }
        }

        fun getDaysAgo(date: Date): String {
            val days = (Date().time - date.time) / 86400000
            var time = SimpleDateFormat("hh:mm aa").format(date)
            time = time.replaceFirst("^0+(?!$)".toRegex(), "")
            return if (days == 0L) "Today at $time" else if (days == 1L) "Yesterday at $time" else {
                SimpleDateFormat("yyyy-MM-dd").format(date) + " " + time
            }
        }

        fun getDaysAgo(sdate: String): String {
            return try {
                val date = SimpleDateFormat("dd-MMM-yyyy hh:mm").parse(sdate)
                val days = (Date().time - date.time) / 86400000
                var time = SimpleDateFormat("hh:mm aa").format(date)
                val dateFormatter = SimpleDateFormat("MMM dd, yyyy") //this format changeable
                dateFormatter.timeZone = TimeZone.getDefault()
                time = time.replaceFirst("^0+(?!$)".toRegex(), "")
                if (days == 0L) "Today at $time" else if (days == 1L) "Yesterday $time" else if (days < 7) DateFormat.format(
                    "EEEE",
                    date
                ).toString() + " " + time else dateFormatter.format(date) + " " + time
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                sdate
            }
        }
    }

    val date: String
        get() {
            val c = Calendar.getInstance()
            val df = SimpleDateFormat("dd-MMM-yyyy hh:mm")
            return df.format(c.time)
        }

    @get:Synchronized
    val timeStamp: String
        get() {
            val s = DateFormat.format("yyyy-MM-dd HH:mm:ss.SSS", uniqueCurrentTimeMS())
            return s.toString()
        }

    fun dateToTimeStamp(timeStamp: Long): String {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = timeStamp * 1000L
        return DateFormat.format("yyyy-MM-dd'T'HH:mm:ss", cal).toString()
    }

    fun covertTimeToAgoText(dataDate: String?): String? {
        var convTime: String? = null
        val prefix = ""
        val suffix = "Ago"
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val pasTime = dateFormat.parse(dataDate)
            val nowTime = Date()
            val dateDiff = nowTime.time - pasTime.time
            var second = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day = TimeUnit.MILLISECONDS.toDays(dateDiff)
            if (second < 60) {
                if (second == 0L) second = 1
                convTime = "$second Seconds $suffix"
            } else if (minute < 60) {
                convTime = "$minute Minutes $suffix"
            } else if (hour < 24) {
                convTime = "$hour Hours $suffix"
            } else if (day >= 7) {
                convTime = if (day > 360) {
                    (day / 360).toString() + " Years " + suffix
                } else if (day > 30) {
                    (day / 30).toString() + " Months " + suffix
                } else {
                    (day / 7).toString() + " Week " + suffix
                }
            } else if (day < 7) {
                convTime = "$day Days $suffix"
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("ConvTimeE", e.message!!)
        }
        return convTime
    }
}