package com.android.hikers.data

import android.net.Uri

data class User(val ID:String,
                val password:String,
                val name:String,
                var profileImage: Uri? = null,
                var introduction:String = "",
                val personality:MutableList<String> = mutableListOf<String>(),
                val writtenPostIDList:MutableList<Int> = mutableListOf<Int>(),
                val likedPostIDList:MutableList<Int> = mutableListOf<Int>()){
}