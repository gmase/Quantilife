package com.firstry.gmase.quantilife.view

import android.support.v7.widget.RecyclerView
import android.view.View
import com.firstry.gmase.quantilife.R

/*
  Source: http://www.littlerobots.nl/blog/Handle-Android-RecyclerView-Clicks/
  USAGE:

  ItemClickSupport.Companion.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
      @Override
      public void onItemClicked(RecyclerView recyclerView, int position, View v) {
          // do it
      }
  });
*/
class ItemClickSupport() {

    private var mRecyclerView: RecyclerView? = null
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnItemLongClickListener: OnItemLongClickListener? = null

    constructor(mRecyclerView: RecyclerView) : this() {
        mRecyclerView.setTag(R.id.item_click_support, this)
        mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener)
    }

//    private val mOnClickListener = object : View.OnClickListener {
//        override fun onClick(v: View) {
//            if (mOnItemClickListener != null) {
//                val holder = mRecyclerView!!.getChildViewHolder(v)
//                mOnItemClickListener!!.onItemClicked(mRecyclerView!!, holder.adapterPosition, v)
//            }
//        }
//    }

    private val mOnClickListener = View.OnClickListener { v ->
        if (mOnItemClickListener != null) {
            val holder = mRecyclerView?.getChildViewHolder(v)
            mOnItemClickListener?.onItemClicked(mRecyclerView, holder?.adapterPosition, v)
        }
    }

    private val mOnLongClickListener = View.OnLongClickListener { v ->
        if (mOnItemLongClickListener != null) {
            val holder = mRecyclerView?.getChildViewHolder(v)
            return@OnLongClickListener mOnItemLongClickListener!!.onItemLongClicked(mRecyclerView, holder?.adapterPosition, v)
        }
        false
    }
    private val mAttachListener = object : RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewAttachedToWindow(view: View) {
            if (mOnItemClickListener != null) {
                view.setOnClickListener(mOnClickListener)
            }
            if (mOnItemLongClickListener != null) {
                view.setOnLongClickListener(mOnLongClickListener)
            }
        }

        override fun onChildViewDetachedFromWindow(view: View) {

        }
    }

    //Para los metodos static de java
    companion object {
        fun addTo(view: RecyclerView): ItemClickSupport? {
            var support: ItemClickSupport? = view.getTag(R.id.item_click_support) as ItemClickSupport?
            if (support == null) {
                support = ItemClickSupport(view)
            }
            return support
        }

        fun removeFrom(view: RecyclerView): ItemClickSupport? {
            var support: ItemClickSupport? = view.getTag(R.id.item_click_support) as ItemClickSupport?
            if (support != null) {
                support.detach(view)
            }
            return support
        }

    }

    //    fun setOnItemClickListener(listener: com.firstry.gmase.quantilife.View.OnItemClickListener): ItemClickSupport {
//        mOnItemClickListener = listener
//        return this
//    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener): ItemClickSupport {
        mOnItemLongClickListener = listener
        return this
    }

    private fun detach(view: RecyclerView) {
        view.removeOnChildAttachStateChangeListener(mAttachListener)
        view.setTag(R.id.item_click_support, null)
    }

    interface OnItemClickListener {
        fun onItemClicked(recyclerView: RecyclerView?, position: Int?, v: View)
    }

    interface OnItemLongClickListener {
        fun onItemLongClicked(recyclerView: RecyclerView?, position: Int?, v: View): Boolean
    }
}