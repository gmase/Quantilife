package com.firstry.gmase.quantilife.view

import android.content.res.Resources
import android.graphics.Color
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firstry.gmase.quantilife.R
import com.firstry.gmase.quantilife.controler.MyModeldApter
import com.firstry.gmase.quantilife.model.*
import com.firstry.gmase.quantilife.modelView.ViewHolderSlider
import com.firstry.gmase.quantilife.modelView.ViewHolderYesNo

/**
 * Created by Guille2 on 05/08/2016.
 */
class CardsAdapter(val dbAdapter: MyModeldApter?, val fm: FragmentManager) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dataToShow = AllQuestions.getVisible()
    private var res: Resources? = null

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(parent.context)
        res = parent.resources
        when (viewType) {
            Type.YESNO.ordinal -> {
                val v1 = inflater.inflate(R.layout.question_yesno, parent, false)
                viewHolder = ViewHolderYesNo(parent.context, v1, dataToShow, this, fm, res!!.getString(R.string.other))
            }
            Type.SLIDER.ordinal -> {
                val v2 = inflater.inflate(R.layout.question_slider, parent, false)
                viewHolder = ViewHolderSlider(parent.context, v2, dataToShow, this, fm, res!!.getString(R.string.other))
            }
            else -> {
                throw UnsupportedOperationException("Tipo Question no definido")
            }
        }
        return viewHolder
    }// set the view's size, margins, paddings and layout parameters

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            Type.YESNO.ordinal -> {
                val vh1 = holder as ViewHolderYesNo
                configureViewHolderYesNo(vh1, position)
            }
            Type.SLIDER.ordinal -> {
                val vh2 = holder as ViewHolderSlider
                configureViewHolderSlider(vh2, position)
            }
            else -> {
                throw UnsupportedOperationException("Tipo Question no definido")
            }
        }
    }

    override fun getItemCount(): Int {
        return dataToShow.size
    }

    override fun getItemViewType(position: Int): Int {
        return dataToShow[position].type.ordinal
    }

    private fun configureViewHolderYesNo(vh1: ViewHolderYesNo, position: Int) {
        val question = dataToShow[position]
        vh1.labelQuestion.text = question.text
        vh1.yesButtom.text = question.answers!![0].text
        vh1.noButtom.text = question.answers[1].text
        if (question.textResult != "") {
            vh1.lastAnswer.visibility = View.VISIBLE
            vh1.lastAnswer.text = res!!.getString(R.string.said1) + '"' + question.textResult + '"' + res!!.getString(R.string.said2) + AppDay.dayToShow(question.resultTime)
        } else vh1.lastAnswer.visibility = View.INVISIBLE

        //todo por alguna razon no puedo coger los colores de R.color asi que los meto a mano
        when (question.scope) {
            Scope.WORK -> vh1.questionLayout.setBackgroundColor(Color.parseColor("#cda800"))
            Scope.RELATIONSHIPS -> vh1.questionLayout.setBackgroundColor(Color.parseColor("#ee6150"))
            Scope.ETHICS -> vh1.questionLayout.setBackgroundColor(Color.parseColor("#888888"))
            Scope.HEALTH -> vh1.questionLayout.setBackgroundColor(Color.parseColor("#3c8fc8"))
        }
    }

    private fun configureViewHolderSlider(vh2: ViewHolderSlider, position: Int) {
        val question = dataToShow[position]
        vh2.labelQuestion.text = question.text
        vh2.slider.progress = dataToShow[position].getSliderPosition()
        vh2.textSlider.text = AllQuestions.getId(dataToShow[position].id).getCurrentAnswer().text
        if (question.textResult != "") {
            vh2.lastAnswer.visibility = View.VISIBLE
            vh2.lastAnswer.text = res!!.getString(R.string.said1) + '"' + question.textResult + '"' + res!!.getString(R.string.said2) + AppDay.dayToShow(question.resultTime)
        } else vh2.lastAnswer.visibility = View.INVISIBLE
        when (question.scope) {
            Scope.WORK -> vh2.questionLayout.setBackgroundColor(Color.parseColor("#cda800"))
            Scope.RELATIONSHIPS -> vh2.questionLayout.setBackgroundColor(Color.parseColor("#ee6150"))
            Scope.ETHICS -> vh2.questionLayout.setBackgroundColor(Color.parseColor("#888888"))
            Scope.HEALTH -> vh2.questionLayout.setBackgroundColor(Color.parseColor("#3c8fc8"))
        }
    }

    fun delete(position: Int) {
        dbAdapter!!.saveResult(dataToShow[position].id, dataToShow[position].result, dataToShow[position].textResult)

        //Guardo los tags asociados a la respuesta marcada
        if (dataToShow[position].getCurrentAnswer().minusTags != null) {
            for (i in dataToShow[position].getCurrentAnswer().minusTags!!) {
                TagDictionary.get(i)!!.state = -1
                dbAdapter.saveTag(TagDictionary.get(i)!!)
            }
        }
        if (dataToShow[position].getCurrentAnswer().plusTags != null) {
            for (i in dataToShow[position].getCurrentAnswer().plusTags!!) {
                TagDictionary.get(i)!!.state = +1
                dbAdapter.saveTag(TagDictionary.get(i)!!)
            }
        }
        dataToShow.removeAt(position)
        notifyItemRemoved(position)
    }

    fun deleteByTag(position: Int, tag: Tag) {
        dbAdapter!!.saveResult(dataToShow[position].id, -2, res!!.getString(R.string.said_not_for_me))
        dbAdapter.saveTag(tag)
        dataToShow.removeAt(position)
        notifyItemRemoved(position)
    }

}
