package com.example.routinechecks.utils

import android.content.Context
import com.example.routinechecks.R
import java.util.ArrayList

object ColorUtil {

    fun getColorCode(context: Context): ArrayList<Int> {
        val typedArray = context.resources.obtainTypedArray(R.array.progress_color_codes)
        return ArrayList(Helper.convertTypedArrayToIntegerArrayList(typedArray))
    }

    fun getColor(position: Int): Int {
        val colorCodes = App.colorCodes
        return colorCodes!![position % colorCodes.size]
    }

}