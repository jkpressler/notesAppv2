package com.example.notesappv2.model

import android.os.Parcelable
import com.example.notesappv2.utils.getRandomNumber
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class NoteModel(
        val id : Int = getRandomNumber(),
        val title : String = "",
        val note : String = "",
        val color : String = "#ffffff",
        val creationDate : Date = Date(),
        val modifiedDate : Date = Date(),
) : Parcelable