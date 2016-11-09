package com.firstry.gmase.quantilife.Http

import com.firstry.gmase.quantilife.model.Question
import java.util.*

/**
 * Created by Guille2 on 08/11/2016
 * Have fun
 */
class SentData {
    var questionList: MutableList<Question>

    init {
        questionList = ArrayList<Question>()
    }

    fun add(q: Question) {
        questionList.add(q)
    }
}