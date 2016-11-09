package com.firstry.gmase.quantilife.model

import java.text.DateFormat
import java.util.*


/**
 * Created by Guille2 on 28/08/2016.
 */
object AppDay {
    var installation: Long = 0
    //todo
    val SEGUNDOS_DIA = 60 * 60 * 24

    fun today(): Int {
        return Math.floor(((System.currentTimeMillis() / 1000.0) - installation) / SEGUNDOS_DIA).toInt()
    }

    fun dayToShow(day: Int): String {
        // You can specify styles if you want
        val format: DateFormat = DateFormat.getDateInstance()
// Set time zone information if you want.
        val date = Date(installation * 1000 + day * SEGUNDOS_DIA * 1000)
        val text = format.format(date)
        return text
    }
}