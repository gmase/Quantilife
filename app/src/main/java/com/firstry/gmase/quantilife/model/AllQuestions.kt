package com.firstry.gmase.quantilife.model

import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * Created by Guille2 on 21/08/2016
 * Have fun
 */
object AllQuestions {
    private val MAX_VISIBLE_QUESTIONS = 7
    private val MAX_VISIBLE_DAY_0 = 12
    var questions: MutableList<Question>
    var totalWork: Int
    var totalHealth: Int
    var totalRelationships: Int
    var totalEthics: Int

    var formerWork: Int
    var formerHealth: Int
    var formerRelationships: Int
    var formerEthics: Int

    var total: Int
    var formerTotal: Int

    init {
        questions = ArrayList()
        totalWork = 0
        totalHealth = 0
        totalRelationships = 0
        totalEthics = 0
        total = 0

        formerWork = 0
        formerHealth = 0
        formerRelationships = 0
        formerEthics = 0
        formerTotal = 0
    }

    fun computeTotals() {
        totalWork = 0
        totalHealth = 0
        totalRelationships = 0
        totalEthics = 0

        var tempGWork = 0
        var tempGHealth = 0
        var tempGRelationships = 0
        var tempGEthics = 0
        var tempFWork = 0.0
        var tempFHealth = 0.0
        var tempFRelationships = 0.0
        var tempFEthics = 0.0

        var answeredRFacts = 0.0
        var answeredHFacts = 0.0
        var answeredWFacts = 0.0
        var answeredEFacts = 0.0

        var unAnsweredRFacts = 0.0
        var unAnsweredHFacts = 0.0
        var unAnsweredWFacts = 0.0
        var unAnsweredEFacts = 0.0

        val questionsFiltradas = questionsFiltradasTags()
        for (q in questionsFiltradas) {
            //-1 no respondida
            //-2 no aplica por un tag
            if (q.result < 0) {
                if (q.category == Category.FACT)
                    if (q.result == -1)
                        when (q.scope) {
                            Scope.ETHICS -> unAnsweredEFacts += q.weight
                            Scope.HEALTH -> unAnsweredHFacts += q.weight
                            Scope.RELATIONSHIPS -> unAnsweredRFacts += q.weight
                            Scope.WORK -> unAnsweredWFacts += q.weight
                        }
                //else los -2 los ignoramos
            } else {
                if (q.category == Category.FACT) {
                    when (q.scope) {
                        Scope.ETHICS -> {
                            tempFEthics += q.result * q.weight
                            answeredEFacts += q.weight
                        }
                        Scope.HEALTH -> {
                            tempFHealth += q.result * q.weight
                            answeredHFacts += q.weight
                        }
                        Scope.RELATIONSHIPS -> {
                            tempFRelationships += q.result * q.weight
                            answeredRFacts += q.weight
                        }
                        Scope.WORK -> {
                            tempFWork += q.result * q.weight
                            answeredWFacts += q.weight
                        }
                    }
                }
                //Los goals suman un maximo de 30%
                else {
                    when (q.scope) {
                        Scope.ETHICS -> tempGEthics += q.result
                        Scope.HEALTH -> tempGHealth += q.result
                        Scope.RELATIONSHIPS -> tempGRelationships += q.result
                        Scope.WORK -> tempGWork += q.result
                    }
                }
            }
        }
        //Los facts hacemos la media  (mas o menos)
        //se debe poder llegar al 100% solo con facts
        //Para que se pueda alcanzar el maximo este valor debe ser 1000
        val maxFactScore = 950
        val factorExp = 0.8

        val maxGoals = 100.0

        if (tempFWork > 0)
            totalWork = (maxFactScore * Math.pow(tempFWork, factorExp) / Math.pow((unAnsweredWFacts + answeredWFacts) * 100, factorExp)).toInt()
        //totalWork=1000*(tempFWork^factorExp/((unAnsweredWFacts + answeredWFacts)*100)^factorExp).toInt()
        //totalWork = (1000 * ((Math.log(tempFWork / factorMult)) / (Math.log((unAnsweredWFacts + answeredWFacts).toDouble() / factorMult * 100.0)))).toInt()
        if (tempFHealth > 0)
            totalHealth = (maxFactScore * Math.pow(tempFHealth, factorExp) / Math.pow((unAnsweredHFacts + answeredHFacts) * 100, factorExp)).toInt()
        if (tempFRelationships > 0)
            totalRelationships = (maxFactScore * Math.pow(tempFRelationships, factorExp) / Math.pow((unAnsweredRFacts + answeredRFacts) * 100, factorExp)).toInt()
        if (tempFEthics > 0)
            totalEthics = (maxFactScore * Math.pow(tempFEthics, factorExp) / Math.pow((unAnsweredEFacts + answeredEFacts) * 100, factorExp)).toInt()

        //Los goals suman un maximo de maxGoals pero sin pasarnos de 1000
        totalWork += min(tempGWork.toDouble(), maxGoals).toInt()
        totalWork = min(totalWork.toDouble(), 1000.0).toInt()

        totalRelationships += min(tempGRelationships.toDouble(), maxGoals).toInt()
        totalRelationships = min(totalRelationships.toDouble(), 1000.0).toInt()

        totalHealth += min(tempGHealth.toDouble(), maxGoals).toInt()
        totalHealth = min(totalHealth.toDouble(), 1000.0).toInt()

        totalEthics += min(tempGEthics.toDouble(), maxGoals).toInt()
        totalEthics = min(totalEthics.toDouble(), 1000.0).toInt()


        total = totalWork + totalHealth + totalRelationships + totalEthics
    }

    private fun min(number: Double, number2: Double): Double {
        if (number > number2)
            return number2
        else return number
    }

    fun updateFormers() {
        formerWork = totalWork
        formerHealth = totalHealth
        formerRelationships = totalRelationships
        formerEthics = totalEthics
        formerTotal = totalWork + totalHealth + totalRelationships + totalEthics
    }

    fun add(q: Question) {
        questions.add(q)
    }

    fun getId(id: Int): Question {
        for (q in questions) {
            if (q.id == id)
                return q
        }
        return questions[0]
    }

    fun getVisible(): MutableList<Question> {
        val temp: MutableList<Question>
        temp = ArrayList()
        for (q in questions) {
            if (q.visible)
                temp.add(q)
        }
        return temp
    }

    fun setResult(id: Int, res: Int, text: String, time: Int) {
        for (q in questions) {
            if (q.id == id) {
                q.result = res
                q.textResult = text
                q.resultTime = time
                q.visible = false
            }
        }
    }

    fun dirties(): List<Question> {
        val output = ArrayList<Question>()
        for (q in questions)
            if (q.dirty) {
                output.add(q)
                q.dirty = false
            }
        return output
    }
    fun loadFromJSON(items: JSONArray) {
        questions.removeAll(questions)
        var ans: JSONArray? = null
        var dependencies: JSONArray? = null
        var i = 0
        var obj: JSONObject
        var tipo: Type
        var answerList: MutableList<Answer>?
        var tagList: ArrayList<Tag>? = null
        while (i < items.length()) {
            obj = items[i] as JSONObject
            when (obj.getString("type")) {
                "YESNO" -> tipo = Type.YESNO
                "SLIDER" -> tipo = Type.SLIDER
                else -> tipo = Type.YESNO
            }

            var item: JSONObject
            //Answers
            try {
                ans = obj.getJSONArray("answers")
            } catch (e: Exception) {
            }
            answerList = null
            if (ans != null) {
                var j = 0
                answerList = ArrayList<Answer>()
                while (j < ans.length()) {
                    item = ans[j] as JSONObject
                    var tagsPlus: ArrayList<String>? = null
                    var tagsMinus: ArrayList<String>? = null
                    var tags: JSONArray? = null
                    //TODO guardar los affectedTags

                    try {
                        tags = item.getJSONArray("plusTags")
                    } catch (e: Exception) {
                    }

                    if (tags != null) {
                        tagsPlus = ArrayList()
                        var h = 0
                        while (h < tags.length()) {
                            tagsPlus.add(tags[h].toString())
                            h++
                        }
                    }
                    tags = null
                    try {
                        tags = item.getJSONArray("minusTags")
                    } catch (e: Exception) {
                    }
                    if (tags != null) {
                        tagsMinus = ArrayList()
                        var h = 0
                        while (h < tags.length()) {
                            tagsMinus.add(tags[h].toString())
                            h++
                        }
                    }


                    answerList.add(Answer(text = item.getString("text"), value = item.getInt("value"), plusTags = tagsPlus, minusTags = tagsMinus))
                    j++
                }
            }
            try {
                dependencies = obj.getJSONArray("dependencies")
            } catch (e: Exception) {
            }
            if (dependencies != null) {
                var j = 0
                tagList = ArrayList<Tag>()
                while (j < dependencies.length()) {
                    val state = Integer.parseInt((dependencies[j] as String).substring(0, 2))
                    //val state = (dependencies[j] as String).substring(0, 2).toInt()

                    //Log.d("MyApp",(dependencies[j] as String).substring(0, 2))
                    //Log.d("MyApp",(state.toString()))
                    val id = (dependencies[j] as String).substring(2)
                    tagList.add(Tag(tagId = id, state = state, YesPhrase = TagDictionary.get(id)!!.YesPhrase, NoPhrase = TagDictionary.get(id)!!.NoPhrase))
                    j++
                }
            }

            questions.add(i, Question(id = obj.getInt("id"), depend = tagList, weight = obj.getDouble("weight").toFloat(), text = obj.getString("question"), type = tipo, answers = answerList, scope = Scope.valueOf(obj.getString("scope")), expDays = obj.getDouble("expDays").toFloat(), category = Category.valueOf(obj.getString("category"))))
            //todo anadir peso
            i++
        }
    }

    fun reviewVisibility() {
        questionsFiltradasTags()
        var max: Int
        if (AppDay.today() < 1)
            max = MAX_VISIBLE_DAY_0
        else max = MAX_VISIBLE_QUESTIONS

        for (q in questions) {
            if (max > 0)
                max -= q.visibility(q.resultTime)
            else q.visible = false
        }
    }

    fun reviewVisibilityToday() {
        //questionsFiltradas = questionsFiltradasTags()
        for (q in questions) {
            q.visibilityReview(q.resultTime)
        }
    }

    fun resetAll() {
        for (q in questions)
            q.result = -1
    }

    // A todas las question que no apliquen por culpa de un tag,les ponemos un resultado -2
    fun questionsFiltradasTags(): List<Question> {
        val output: MutableList<Question>
        output = ArrayList()
        var add: Boolean
        for (q in questions) {
            add = true
            if (q.depend != null && q.depend.size > 0) {
                for (i: Tag in q.depend) {
                    when (i.state) {
                        -1 -> {
                            if (TagDictionary.get(i.tagId)!!.state == 1) {
                                add = false
                            }
                        }
                        1 -> {
                            if (TagDictionary.get(i.tagId)!!.state == -1) {
                                add = false
                            }
                        }
                    }
                    if (!add)
                        break
                }
            }
            if (add) {
                output.add(q)
            } else q.tagBlock = true
        }
        return output
    }


}