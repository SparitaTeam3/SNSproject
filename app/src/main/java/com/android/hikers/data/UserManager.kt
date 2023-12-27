package com.android.hikers.data

import android.net.Uri

class UserManager private constructor() {
    private val userList = mutableListOf<User>()

    companion object {
        private var instance: UserManager? = null

        fun newInstance(): UserManager {
            if (instance == null) instance = UserManager()
            return instance!!
        }
    }

    init {
        //이미 회원가입 된 회원 3명 추가

    }

    fun addNewUser(ID: String, password: String, name: String) {
        userList
    }
}