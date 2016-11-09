package com.firstry.gmase.quantilife.model

import android.content.Context
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import com.firstry.gmase.quantilife.R

/**
 * Created by Guille2 on 18/09/2016
 * Have fun
 */
class TagListAdapter : ListAdapter {
    private var _context: Context? = null
    private var _elementos: List<String>? = null


    constructor(context: Context, elementos: List<String>) {
        this._context = context
        this._elementos = elementos
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var convertView = p1

        if (convertView == null) {
            val infalInflater = this._context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = infalInflater.inflate(R.layout.tag_item, null)
        }

        val txtListChild = convertView!!.findViewById(R.id.tagItem) as TextView
        txtListChild.text = _elementos!![p0]
        return convertView
    }

    override fun registerDataSetObserver(p0: DataSetObserver?) {

    }

    override fun getItemViewType(p0: Int): Int {
        return 1
    }

    override fun getItem(p0: Int): Any {
        return _elementos!![p0]
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun unregisterDataSetObserver(p0: DataSetObserver?) {

    }

    override fun getCount(): Int {
        return _elementos!!.count()
    }

    override fun isEnabled(p0: Int): Boolean {
        return true
    }

    override fun areAllItemsEnabled(): Boolean {
        return true
    }

    override fun isEmpty(): Boolean {
        return false
    }
}