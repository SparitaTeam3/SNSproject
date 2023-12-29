package com.android.hikers.messages

import android.content.Context
import com.android.hikers.R

enum class ErrorMsg(var msg: List<Int>) {
    // 0 = 비어있을때 1 = 로그인시 사용
    ID(
        listOf(
            R.string.plz_input_id,
            R.string.no_exist_id,
            R.string.already_exist_id,
            R.string.id_use_eng_num
        )
    ),
    PW(
        listOf(
            R.string.plz_input_pw,
            R.string.not_match_pw,
            R.string.pw_more_eight,
            R.string.pw_must_have_special,
            R.string.plz_check_pw, // 4부터는 비밀번호 확인용
            R.string.diffrent_pw_check
        )
    ),
    NAME(
        listOf(
            R.string.plz_input_name
        )
    );

    fun show(context: Context, idx: Int): String {
        return context.getString(msg[idx])
    }
}