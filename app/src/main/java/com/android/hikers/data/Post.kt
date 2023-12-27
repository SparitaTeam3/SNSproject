package com.android.hikers.data

import android.net.Uri

data class Post(val postID:Int,
                val title:String,
                val body:String,
                val writerId:String,
                val writtenTime:String,
                var heartCount:Int = 0,
                val image: Uri? = null,
                val location:String = "위치 정보 없음"
                ) {
}