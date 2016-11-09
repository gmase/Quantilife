package com.firstry.gmase.quantilife.view

/**
 * Created by Guille2 on 21/08/2016.
 */
interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)

    fun onItemDismiss(position: Int)
}