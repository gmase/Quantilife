package com.firstry.gmase.quantilife.modelView

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import com.firstry.gmase.quantilife.R
import com.firstry.gmase.quantilife.activities.QuestionDialog
import com.firstry.gmase.quantilife.model.AllQuestions
import com.firstry.gmase.quantilife.model.AppDay
import com.firstry.gmase.quantilife.model.Question
import com.firstry.gmase.quantilife.view.CardsAdapter
import java.util.*

/**
 * Created by Guille2 on 19/08/2016.
 */
class ViewHolderYesNo(var mView: View, var rv: CardsAdapter) : RecyclerView.ViewHolder(mView) {
    var labelQuestion: TextView
    val questionLayout: FrameLayout
    var context: Context? = null
    var questions: MutableList<Question>
    val yesButtom: Button
    val noButtom: Button
    val optionsButtom: Button
    val lastAnswer: TextView
    var default: String? = null

    init {
        questions = ArrayList<Question>()
        labelQuestion = mView.findViewById(R.id.question) as TextView
        questionLayout = mView.findViewById(R.id.question_layout) as FrameLayout
        yesButtom = mView.findViewById(R.id.yesButtom) as Button
        noButtom = mView.findViewById(R.id.noButtom) as Button
        optionsButtom = mView.findViewById(R.id.optionsButtom) as Button
        lastAnswer = mView.findViewById(R.id.lastAnswerYN) as TextView
    }

    constructor(context: Context, mView: View, dataShow: MutableList<Question>, rv: CardsAdapter, fm: FragmentManager, defaultAnswer: String) : this(mView, rv) {
        default = defaultAnswer
        this.context = context
        questions = dataShow

        yesButtom.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val position: Int = layoutPosition // gets item position
                val q = AllQuestions.getId(questions.get(index = position).id)
                val yes = q.answers!![0].text!!
                AllQuestions.setResult(questions.get(index = position).id, q.getAnswerValue(yes), yes, AppDay.today())
                AllQuestions.computeTotals()
                rv.delete(position)
            }
        })
        noButtom.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val position: Int = layoutPosition // gets item position
                val q = AllQuestions.getId(questions.get(index = position).id)
                val no = q.answers!![1].text!!
                AllQuestions.setResult(questions.get(index = position).id, q.getAnswerValue(no), no, AppDay.today())
                AllQuestions.computeTotals()
                rv.delete(position)
            }
        })
        optionsButtom.setOnClickListener {
            //todo

            //Context.parent.supportFragmentManager
            val yourDialog = QuestionDialog()
            val args = Bundle()

            val lista: List<String>?
            lista = ArrayList<String>()
            for (i in questions.get(index = position).depend!!) {
                if (i.state == -1)
                    lista.add(i.YesPhrase)
                else
                    lista.add(i.NoPhrase)
            }
            lista.add(default as String)
            args.putStringArrayList("lista", lista as ArrayList<String>?)
            args.putInt("position", position)
            yourDialog.arguments = args
            yourDialog.show(fm, "some_optional_tag")
        }
    }
}