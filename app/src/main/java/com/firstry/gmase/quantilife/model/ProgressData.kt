package com.firstry.gmase.quantilife.model

import com.github.mikephil.charting.data.Entry

/**
 * Created by Guille2 on 30/08/2016.
 */
class ProgressData(val days: Int) {

    var red: MutableList<Entry>
    var blue: MutableList<Entry>
    var yellow: MutableList<Entry>
    var grey: MutableList<Entry>

    init {
        red = mutableListOf()
        blue = mutableListOf()
        yellow = mutableListOf()
        grey = mutableListOf()
    }
}