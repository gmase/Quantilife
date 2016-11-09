package com.firstry.gmase.quantilife.activities

import android.app.Activity
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.firstry.gmase.quantilife.R
import com.firstry.gmase.quantilife.model.TagListAdapter
import java.util.*

/**
 * Created by Guille2 on 18/09/2016
 * Have fun
 */
class QuestionDialog() : DialogFragment() {

    var mListener: OnTagSelectedListener? = null
    var position: Int? = null
    var list: ArrayList<String>? = null

    interface OnTagSelectedListener {
        fun OnTagSelectedListener(position: Int, InputTag: String)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            mListener = activity as OnTagSelectedListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement OnArticleSelectedListener")
        }
    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        list = args!!["lista"]!! as ArrayList<String>
        //list!!.add(list!!.lastIndex + 1,args)
        position = args["position"]!! as Int
    }

    override fun onCreateView(inflater: LayoutInflater?, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        //super.onCreate(savedInstanceState, persistentState)
        //setContentView(R.layout.question_dialog)
        val view = inflater!!.inflate(R.layout.question_dialog, container)
        //val backButton = view.findViewById(R.id.backButton) as Button
        val tagList = view.findViewById(R.id.tagList) as ListView

        val listAdapter = TagListAdapter(context, list!!)
        // setting list adapter
        tagList.adapter = listAdapter

        tagList.setOnItemClickListener { listAdapter, view, i, l ->
            Toast.makeText(context, (view as TextView).text, Toast.LENGTH_SHORT).show()

            mListener!!.OnTagSelectedListener(position!!, view.text as String)
            dismiss()
        }
        return view
    }

}