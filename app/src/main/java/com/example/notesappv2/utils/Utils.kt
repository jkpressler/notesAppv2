package com.example.notesappv2.utils

import java.util.*


fun getRandomNumber() : Int {

    val random = Random()
    return random.nextInt(999999)
}