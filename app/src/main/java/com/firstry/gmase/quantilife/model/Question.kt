package com.firstry.gmase.quantilife.model

import java.util.*

/**
 * Created by Guille2 on 18/08/2016.
 */
class Question(var id: Int, var text: String = "NA", var textResult: String = "", var resultTime: Int = 0, var scope: Scope, var category: Category, var result: Int = -1, val weight: Float = 1f, var type: Type = Type.YESNO, var visible: Boolean = true, val expDays: Float, val answers: List<Answer>?, var tagBlock: Boolean = false, val depend: ArrayList<Tag>? = null, var dirty: Boolean = false) {

    fun getCurrentAnswer(): Answer {
        var output = answers!![0]
        for (a in answers) {
            if (a.value == result) {
                output = a
                break
            }
        }
        return output
    }

    fun getTemptingAnswer(positionSlider: Int): Answer {
        val numAnswers = answers!!.count()
        val div = positionSlider.toDouble() / (100.5 / numAnswers)
        val j = Math.floor(div)
        return answers[j.toInt()]
    }

    fun getAnswerValue(answerName: String): Int {
        if (answers != null) {
            for (a in answers) {
                if (answerName == a.text) {
                    return a.value
                }
            }
        }
        return 0
    }

    fun getSliderPosition(): Int {
        val numAnswers = answers!!.count()
        var i = 1
        var output = 0
        for (a in answers) {
            if (a.value == result) {
                output = (i * (100.0 / numAnswers)).toInt()
                break
            }
            i++
        }
        return output
    }

    /*
    Visibility conditions:
    1. Not answered or expired
    2. No active dependency tag
    3. Day limit

    //Todo si el result =-2 hay un tag que evita que salga la pregunta

     */
    //day es igual al resultDay de la question normalmente
    fun visibility(day: Int): Int {
        visible = true
        if (result != -1 && AppDay.today() - day < expDays) {
            visible = false
        }
        //Condicion para el tag other
        if (result == -2 && AppDay.today() - day < expDays * 4) {
            visible = false
        }
        if (tagBlock)
            visible = false
//Si fue respondida hoy o es visible devuelve 1
        if ((AppDay.today() - day < 1 && result != -1) or visible)
            return 1
        else return 0
    }

    //TODO si no se ha respondido pero por culpa de un tag no
    fun visibilityReview(day: Int) {
        if (result != -1 && AppDay.today() - day < 1) {
            visible = true
        } else visible = false
//        } else if (AppDay.today() - day < expDays) {
//            visible = false
//        }

//        if (result == -1 && depend != null && depend.size > 0) {
//            //TODO no funciona
//            for (i in depend)
//                if (i.state == TagDictionary.get(i.tagId)!!.state) {
//                    //TODO hay que hacer que caduquen los tags
//                    //visible = false
//                    break
//                }
//        }
//        if (result == -1)
//            visible = false
    }
}