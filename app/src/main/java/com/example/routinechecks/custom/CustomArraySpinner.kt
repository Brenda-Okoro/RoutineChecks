package com.example.routinechecks.custom

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.routinechecks.R

class CustomArraySpinner(private val mContext: Context, private val resourceId: Int, data: ArrayList<String>) : ArrayAdapter<String>(mContext, resourceId, data) {
    private var data = ArrayList<String>()
    private var unSelectablePosition: Int = 0
    private var unSelectableItemColor: Int = 0
    private var selectedItemColor: Int = 0
    private var selectedItemTextSize: Float = 0.toFloat()

    init {
        this.data = data

        //set default values
        setUnselectablePosition(R.color.black)
        setSelectedItemColor(R.color.black)
        setSelectedItemTextSize(12f)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        try {

            val view = super.getView(position, convertView, parent) as TextView

            view.textSize = selectedItemTextSize
            if (position == unSelectablePosition)
                view.setTextColor(ContextCompat.getColor(mContext, unSelectableItemColor))
            else
                view.setTextColor(ContextCompat.getColor(mContext, selectedItemColor))

            return view

        } catch (ex: Exception) {
            ex.printStackTrace()
            return super.getView(position, convertView, parent)
        }

    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {

        try {

            val textView = super.getDropDownView(position, convertView, parent) as TextView

            if (position == unSelectablePosition) {
                // Set the hint text color gray
                textView.setTextColor(ContextCompat.getColor(mContext, unSelectableItemColor))
            } else {
                textView.setTextColor(ContextCompat.getColor(mContext, selectedItemColor))
            }

            return textView

        } catch (ex: Exception) {
            ex.printStackTrace()
            return super.getDropDownView(position, convertView, parent)
        }

    }

    override fun isEnabled(position: Int): Boolean {
        return position != unSelectablePosition && super.isEnabled(position)
    }

    fun setUnselectablePosition(unSelectablePosition: Int) {
        this.unSelectablePosition = unSelectablePosition
    }

    fun setUnselectableItemColor(unSelectableItemColor: Int) {
        this.unSelectableItemColor = unSelectableItemColor
    }

    fun setSelectedItemColor(selectedItemColor: Int) {
        this.selectedItemColor = selectedItemColor
    }

    fun setSelectedItemTextSize(selectedItemTextSize: Float) {
        this.selectedItemTextSize = selectedItemTextSize
    }
}
