package com.firstry.gmase.quantilife.modelView

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
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
//ItemTouchHelperAdapter
class ViewHolderSlider(var mView: View, var rv: CardsAdapter) : RecyclerView.ViewHolder(mView), View.OnClickListener {
    var labelQuestion: TextView
    var slider: SeekBar
    var done: ImageButton
    var context: Context? = null
    var questions: MutableList<Question>
    var textSlider: TextView
    var sliderValue: Int
    val optionsButtom: Button
    val questionLayout: FrameLayout
    val lastAnswer: TextView
    var default: String? = null

    init {
        questions = ArrayList<Question>()
        questionLayout = mView.findViewById(R.id.question_layout) as FrameLayout
        labelQuestion = mView.findViewById(R.id.question) as TextView
        slider = mView.findViewById(R.id.seekBar) as SeekBar
        done = mView.findViewById(R.id.done) as ImageButton
        textSlider = mView.findViewById(R.id.answer) as TextView
        optionsButtom = mView.findViewById(R.id.optionsButtom) as Button
        sliderValue = 0
        lastAnswer = mView.findViewById(R.id.lastAnswerSlider) as TextView
    }

    constructor(context: Context, mView: View, dataShow: MutableList<Question>, rv: CardsAdapter, fm: FragmentManager, defaultAnswer: String) : this(mView, rv) {
        default = defaultAnswer
        this.context = context
        questions = dataShow
        done.setOnClickListener(this)

        slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                sliderValue = p1
                textSlider.text = AllQuestions.getId(questions.get(index = layoutPosition).id).getTemptingAnswer(sliderValue).text
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
        })
        optionsButtom.setOnClickListener {
            //todo
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
            //lista = questions.get(index = position).depend?.getPhrases()

            lista.add(default as String)
            args.putStringArrayList("lista", lista as ArrayList<String>?)
            args.putInt("position", position)
            yourDialog.arguments = args
            yourDialog.show(fm, "some_optional_tag")

        }
    }

    override fun onClick(view: View) {
        val position: Int = layoutPosition // gets item position
        AllQuestions.setResult(questions.get(index = position).id, questions.get(index = position).getAnswerValue(textSlider.text.toString()), textSlider.text.toString(), AppDay.today())
        AllQuestions.computeTotals()
        rv.delete(position)
    }

}


