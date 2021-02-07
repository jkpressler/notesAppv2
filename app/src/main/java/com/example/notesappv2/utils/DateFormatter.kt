package com.example.notesappv2.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {

    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a")

    fun formatDate(date : Date) : String {
        return dateFormat.format(date)
    }
}