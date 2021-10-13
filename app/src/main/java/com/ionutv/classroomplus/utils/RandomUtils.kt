package com.ionutv.classroomplus.utils

object RandomUtils {

    private val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun generateRandomString (length : Int) :String {
        return List(length) { alphabet.random()}.joinToString("")
    }
}