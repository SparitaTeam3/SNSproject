package com.android.hikers.data

import android.net.Uri
import android.os.Build
import com.android.hikers.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.*

class PostManager private constructor(){

    private val postList = mutableListOf<Post>()
    private var nextPostID = 0

    companion object{
        private var instance: PostManager? = null

        fun newInstance(): PostManager {
            if(instance == null) instance = PostManager()
            return instance!!
        }
    }

    init{
        //이미 작성된 게시물 3개 생성
        addNewPost("겨울 등산 준비물 도와주세요",
            "안녕하세요, 이번에 겨울 등산에 다녀올 예정입니다.\n겨울 등산에 필요한 준비물이 무엇이 있을까요?\n감사합니다!",
            "hong_gildong",
            location = "설악산")

        addNewPost("히말라야 트래킹에 다녀왔어요",
            "5박6일 히말라야 트래킹에 다녀왔어요.\n풍경도 좋고 걸으며 마음이 많이 편해졌답니다.\n걷는 걸 좋아하시는 분들은 꼭 한 번 다녀오시길 바랍니다!",
            "kim_chulsoo",
            location = "히말라야",
            image = Uri.parse("android.resource://com.android.hikers/"+R.drawable.post_img_example1))
            //image = Uri.parse("drawable://" + R.drawable.post_img_example1))

        addNewPost("여기가 어느 산인지 아시는 분 계신가요?",
            "인터넷에서 본 사진인데요 여기가 어느 산인지 모르겠어요.ㅜㅜ\n여기가 어딘지 아시는 분 있다면 꼭 댓글 달아주세요!\n",
            "lee_younghee",
            image = Uri.parse("android.resource://com.android.hikers/"+R.drawable.post_img_example2))
            //image = Uri.parse("drawable://" + R.drawable.post_img_example2))
    }

    private fun getCurrentDateTime():String{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var currentDateTime = LocalDateTime.now()
            return currentDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
        }
        return ""
    }

    fun addNewPost(title:String, body:String, writerId:String, image:Uri? = null, location:String = "위치 정보 없음"){

        //게시물 ID 붙이기
        val postID = nextPostID++
        //게시물 게시된 시간 구하기
        var writtenTime = getCurrentDateTime()
        //새 게시물 생성하기
        postList.add(Post(postID, title, body, writerId, writtenTime, image = image, location = location))
    }

    //최신에 작성된 n개의 게시물 List 반환
    fun getRecentPostList(n:Int):List<Post>{
        val recentPostList = mutableListOf<Post>()
        for(i in 0 until min(n, postList.size)){
            recentPostList.add(postList[postList.size -1 -i])
        }
        return recentPostList.toList()
    }

    //작성된 게시물 중 가장 최신 게시물 ID 반환
    fun getMostRecentPostID():Int?{
        if(postList.isEmpty()) return null
        return postList[postList.size -1].postID
    }
    //최신에 작성된 n개의 게시물 중 마지막 최신 게시물 ID 반환
    fun getLeastRecentPostID(n:Int):Int?{
        if((n == 0) || postList.isEmpty()) return null

        return if(postList.size < n) postList[0].postID
        else postList[postList.size - n].postID
    }
    fun findPostByID(postID: Int): Post? {
        return postList.find { it.postID == postID }
    }
}