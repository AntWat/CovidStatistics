package com.ant_waters.covidstatistics.Utils

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt

class Utils() {
    companion object {
        fun ShowToast(context: Context?, msg: String, length: Int = Toast.LENGTH_SHORT,
                      gravity: Int = -1, textColor: Int = Color.BLACK ) {

            val toast = Toast.makeText(context, msg, length)
            if(gravity >= 0) { toast.setGravity(gravity, 0, 0) }
            val view = toast.view

            val text: TextView = view!!.findViewById(android.R.id.message)
            text.setTextColor(textColor)
            toast.show();
        }
    }
}