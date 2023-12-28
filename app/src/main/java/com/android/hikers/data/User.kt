package com.android.hikers.data

import android.net.Uri

data class User(
    val ID: String,
    val password: String,
    var name: String, // 이름은 변경될 수 있어서 var로 변경했습니다.
    var profileImage: Uri? = null,
    var introduction: String = "",
    var character: MutableList<String> = mutableListOf<String>(), // 성격보다 키워드에 어울리는거같아 character로 변경했습니다.
    val writtenPostIDList: MutableList<Int> = mutableListOf<Int>(),
    val likedPostIDList: MutableList<Int> = mutableListOf<Int>()
) {
    fun addWrittenPostID(postID: Int) {
        writtenPostIDList.add(postID)
    }

    fun isInLikedPostIDList(postID: Int): Boolean {
        return likedPostIDList.contains(postID)
    }

    fun addLikedPostID(postID: Int) {
        likedPostIDList.add(postID)
    }

    fun deleteLikedPostID(postID: Int) {
        likedPostIDList.remove(postID)
    }
}