package com.android.hikers.extention

import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.android.hikers.R

fun TextView.showErrMsg(msg: String) {
    setText(msg)
    visibility = View.VISIBLE
}

fun EditText.leaveEmpty(errMsg: TextView, string: String) {
    setOnFocusChangeListener { v, hasFocus ->
        val context = v.context
        if (!hasFocus) {
            if (text.isEmpty()) {
                errMsg.showErrMsg(string)
                background = context.getDrawable(R.drawable.edit_text_background_error)
            }
        }
    }
}
