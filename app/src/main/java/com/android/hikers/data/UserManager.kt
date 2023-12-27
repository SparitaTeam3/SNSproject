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
        addNewUser("hong_gildong", "aaaaaaaa!", "홍길동")
        addNewUser("kim_chulsoo", "aaaaaaaa!", "김철수")
        addNewUser("lee_younghee", "aaaaaaaa!", "이영희")
    }

    fun addNewUser(
        ID: String, password: String, name: String,
        image: Uri? = null,
        introduction: String = "",
        personality: MutableList<String> = mutableListOf<String>()
    ) {
        userList.add(User(ID, password, name, image, introduction, personality))
    }

    fun findUserByID(ID: String): User? {
        return userList.find { it.ID == ID }
    }

    fun checkUserExist(id: String): Boolean {
        return userList.any { it.ID == id }
    }

    fun isLoginSuccess(ID: String, password: String): Boolean {
        userList.forEach {
            if ((it.ID == ID) && (it.password == password)) return true
        }
        return false
    }
}