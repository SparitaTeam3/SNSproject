package com.android.hikers.extention

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import com.android.hikers.R

fun TextView.showErrMsg(msg: String, editText: EditText) {
    setText(msg)
    visibility = View.VISIBLE
    editText.background = context.getDrawable(R.drawable.edit_text_background_error)

}

fun EditText.showError(msg: String, errMsg: TextView) {
    val animShake = AnimationUtils.loadAnimation(context, R.anim.shake_error)

    this.requestFocus()
    this.background = context.getDrawable(R.drawable.edit_text_background_error)

    with(errMsg) {
        visibility = android.view.View.VISIBLE
        text = msg
        startAnimation(animShake)
    }
}

fun EditText.leaveEmpty(errMsg: TextView, msg: String) {
    setOnFocusChangeListener { v, hasFocus ->
        val context = v.context
        if (!hasFocus) {
            if (text.isEmpty()) {
                errMsg.showErrMsg(msg, this)
            }
        }
    }
}
